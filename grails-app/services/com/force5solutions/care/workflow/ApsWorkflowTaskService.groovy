package com.force5solutions.care.workflow

import com.force5solutions.care.cc.UploadedFile
import org.codehaus.groovy.grails.commons.ConfigurationHolder

import com.force5solutions.care.aps.*
import com.force5solutions.care.cc.Person
import com.force5solutions.care.ldap.TopUser
import com.force5solutions.care.cc.Worker
import com.force5solutions.care.cc.EmployeeSupervisor
import com.force5solutions.care.cc.CcEntitlementRole
import com.force5solutions.care.cc.Employee
import com.force5solutions.care.cc.PeriodUnit
import groovy.time.TimeCategory
import com.force5solutions.care.cc.WorkerEntitlementRole
import com.force5solutions.care.common.CareConstants
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.force5solutions.care.cc.EntitlementRoleAccessStatus
import org.grails.plugins.versionable.VersionHistory

//TODO : Looks like there is a lot of repetition in the CentralWorkflowTaskService classes for APS and Central
class ApsWorkflowTaskService {

    static transactional = true
    def securityService
    def careWebService
    def apsUtilService
    def versioningService
    def entitlementService

    static config = ConfigurationHolder.config

    private Boolean validateIfRoleOwnerOwns(String slid, EntitlementRole entitlementRole) {
        return (entitlementRole.owner.slid == slid)
    }

    private Boolean validateIfRoleOwnerOwnsEntitlement(String slid, Entitlement entitlement) {
        return (entitlement.owner.slid == slid)
    }

    private Boolean validateIfGatekeeperOwns(String slid, EntitlementRole entitlementRole) {
        return (slid in entitlementRole.gatekeepers*.slid)
    }

    private Boolean validateIfProvisionerOwns(String slid, Entitlement entitlement) {
        return (slid in entitlement.provisioners*.slid)
    }

    private List<ApsApplicationRole> getApplicationRoles(String slid) {
        List<ApsApplicationRole> roles = []
        ApsPerson person = ApsPerson.findBySlid(slid)
        if (person) {
            if (Gatekeeper.countByPerson(person)) {
                roles.add(ApsApplicationRole.GATEKEEPER)
            }
            if (RoleOwner.countByPerson(person)) {
                roles.add(ApsApplicationRole.ROLE_OWNER)
            }
            if (Provisioner.countByPerson(person)) {
                roles.add(ApsApplicationRole.PROVISIONER)
            }
            if (DeProvisioner.countByPerson(person)) {
                roles.add(ApsApplicationRole.DEPROVISIONER)
            }
        }
        return roles
    }

    private void processNewTask(CentralWorkflowTask task) {
        try {
            String entitlementRoleId = null
            EntitlementRole entitlementRole = null
            careWebService.changeCentralWorkflowTaskStatusToPending(task.id)
            if (task.workflowType != CentralWorkflowType.ACCESS_VERIFICATION) {
                entitlementRoleId = task.entitlementRole.id
                entitlementRole = EntitlementRole.findById(entitlementRoleId)
            }
            switch (task.workflowType) {
                case CentralWorkflowType.EMPLOYEE_PUBLIC_ACCESS_REQUEST:
                    ApsWorkflowUtilService.startRoleAccessRequestWorkflow(entitlementRole, task)
                    break;
                case CentralWorkflowType.EMPLOYEE_ACCESS_REQUEST:
                    ApsWorkflowUtilService.startRoleAccessRequestWorkflow(entitlementRole, task)
                    break;
                case CentralWorkflowType.CONTRACTOR_ACCESS_REQUEST:
                    ApsWorkflowUtilService.startRoleAccessRequestWorkflow(entitlementRole, task)
                    break;
                case CentralWorkflowType.EMPLOYEE_ACCESS_REVOKE:
                    ApsWorkflowUtilService.startRoleRevokeRequestWorkflow(entitlementRole, task)
                    break;
                case CentralWorkflowType.CONTRACTOR_ACCESS_REVOKE:
                    ApsWorkflowUtilService.startRoleRevokeRequestWorkflow(entitlementRole, task)
                    break;
                case CentralWorkflowType.EMPLOYEE_CANCEL_ACCESS_APPROVAL:
                    ApsWorkflowUtilService.cancelAccessApprovalWorkflow(entitlementRole, task)
                    break;
                case CentralWorkflowType.CONTRACTOR_CANCEL_ACCESS_APPROVAL:
                    ApsWorkflowUtilService.cancelAccessApprovalWorkflow(entitlementRole, task)
                    break;
                case CentralWorkflowType.EMPLOYEE_CANCEL_ACCESS_REVOCATION:
                    ApsWorkflowUtilService.cancelAccessRevocationWorkflow(entitlementRole, task)
                    break;
                case CentralWorkflowType.CONTRACTOR_CANCEL_ACCESS_REVOCATION:
                    ApsWorkflowUtilService.cancelAccessRevocationWorkflow(entitlementRole, task)
                    break;
                case CentralWorkflowType.EMPLOYEE_TERMINATION:
                    ApsWorkflowUtilService.startRoleTerminationWorkflow(entitlementRole, task)
                    break;
                case CentralWorkflowType.CONTRACTOR_TERMINATION:
                    ApsWorkflowUtilService.startRoleTerminationWorkflow(entitlementRole, task)
                    break;
                case CentralWorkflowType.ACCESS_VERIFICATION:
                    ApsWorkflowUtilService.startAccessVerificationWorkflow(task)
                    break;
                case CentralWorkflowType.PROVISIONER_DEPROVISIONER_TASKS_ON_ROLE_UPDATE:
                    ApsWorkflowUtilService.startProvisionerDeprovisionerTaskOnRoleUpdateWorkflow(task)
                    break;
            }
        } catch (Throwable t) {
            t.printStackTrace()
            //TODO : What else to do?
        }
    }

    void processNewTasks() {
        List<AbortCentralWorkflow> centralWorkflowsToBeAborted = AbortCentralWorkflow.findAllByIsAborted(false)
        centralWorkflowsToBeAborted.each {
            ApsWorkflowUtilService.abortWorkflow(it.workflowGuid)
            careWebService.markWorkflowAsAborted(it.workflowGuid)
        }
        List<CentralWorkflowTask> newTasks = CentralWorkflowTask.findAllByStatusAndType(WorkflowTaskStatus.NEW, CentralWorkflowTaskType.SYSTEM_APS)
        newTasks.each { CentralWorkflowTask task ->
            processNewTask(task)
        }
    }

    List getEmailRecipients(ApsWorkflowTask task) {
        Set<String> recipients = []

        List<String> slids = (TopUser.list()*.slid + Person.list()*.slid).unique()
        slids.each { String slid ->
            if (slid && ApsWorkflowTask.hasPermission(slid, task)) {
                recipients.add(slid + config.emailDomain)
            }
        }
        return recipients as List
    }

    static void abortWorkItem(def workflowTask) {
        workflowTask.status = WorkflowTaskStatus.CANCELLED
        workflowTask.s()
    }

    static Set<String> getEntitlementsAlreadyRequestedForRevocation(EntitlementRole role, Worker worker) {
        Set<String> entitlements = role.allEntitlements*.id as Set
        Set<ApsWorkflowTask> tasks = ApsWorkflowTask.findAllByEntitlementIdInListAndStatus(entitlements as List, WorkflowTaskStatus.PENDING)
        tasks = tasks.findAll { it.worker == worker } as Set
        entitlements = entitlements.findAll { it in tasks*.entitlementId } as Set
        return entitlements
    }

    static boolean doesExistApsWorkflowTaskWithResponse(String workflowGuid, List nodeIds, String response) {
        nodeIds = nodeIds*.toLong()
        boolean hasResponse = ApsWorkflowTask.createCriteria().count {
            and {
                eq('workflowGuid', workflowGuid)
                eq('response', response)
                'in'('nodeId', nodeIds)
            }
        } as boolean
        return hasResponse
    }

    static void createResponseForCareCentral(Long careCentralTaskId, Map response) {
        new CareCentralResponse(careCentralTaskId, response).s()
    }

    public void escalateWorkflowTask(ApsWorkflowTask taskToBeEscalated) {
        log.debug "Escalating Task : " + taskToBeEscalated.id
        taskToBeEscalated.createEscalationLogTask()
        ApsWorkflowTaskTemplate escalationTemplate = ApsWorkflowTaskTemplate.findById(taskToBeEscalated.escalationTemplateId)
        Collection<String> slids = ApsUtilService.getSecurityRolesOrSlidsByApplicationRole(escalationTemplate.actorApplicationRoles, taskToBeEscalated.entitlement ?: (taskToBeEscalated.entitlementRole ?: taskToBeEscalated.workerEntitlementRole), escalationTemplate.respectExclusionList)
        if (escalationTemplate.actorSlids) {
            slids.addAll(escalationTemplate.actorSlids.tokenize(', '))
        }

        slids?.each { String slid ->
            if (!ApsWorkflowTaskPermittedSlid.countByTaskAndSlid(taskToBeEscalated, slid)) {
                taskToBeEscalated.addToPermittedSlids(new ApsWorkflowTaskPermittedSlid(slid: slid))
            }
        }
        taskToBeEscalated.securityRoles += escalationTemplate.actorSecurityRoles*.name
        if (escalationTemplate.escalationTemplate) {
            taskToBeEscalated.escalationTemplateId = escalationTemplate.escalationTemplate.id
            taskToBeEscalated.period = escalationTemplate.period
            taskToBeEscalated.periodUnit = escalationTemplate.periodUnit
        } else {
            taskToBeEscalated.escalationTemplateId = null;
            taskToBeEscalated.period = null
            taskToBeEscalated.periodUnit = null
        }

        taskToBeEscalated.s();

        // Send a notification to the new recipients
        Object object = taskToBeEscalated.entitlement ?: (taskToBeEscalated.entitlementRole ?: taskToBeEscalated.workerEntitlementRole)
        if (escalationTemplate && object) {
            Map parameters = ApsUtilService.getParametersForMessageTemplate(object)
            if (taskToBeEscalated.workerEntitlementRole) {
                parameters.putAll(populateWorkflowParametersFromWorkerEntitlementRole(taskToBeEscalated.workerEntitlementRole, parameters))
            }
            parameters.putAll(populateWorkflowParametersForRevocationTable(taskToBeEscalated, taskToBeEscalated.workflowGuid, parameters))
            if (taskToBeEscalated?.entitlementId) {
                parameters['entitlement'] = Entitlement.findById(taskToBeEscalated.entitlementId)
            }
            apsUtilService.sendNotification(escalationTemplate, escalationTemplate.messageTemplate, object, parameters)
        }
    }

    public void escalateProvisionerDeprovisionerWorkflowTasks(List<ApsWorkflowTask> provisionerDeprovisionerTasks) {
        def applicationContext = ApplicationHolder.getApplication().getMainContext()
        def apsUtilService = applicationContext.getBean('apsUtilService')
        Collection<String> toRecipients = []
        Collection<String> ccRecipients = []

        Map tasksGroupedByWorkflowGuid = provisionerDeprovisionerTasks.groupBy { it.workflowGuid }
        tasksGroupedByWorkflowGuid.each { key, value ->
            ApsWorkflowTask gatekeeperTask = ApsWorkflowTask.findByWorkflowGuidAndNodeNameInList(key.toString(), ['Pending Approval by Entitlement Role Gatekeeper', 'Pending Revocation by Entitlement Role Gatekeeper'])
            ApsWorkflowTaskTemplate escalationTemplate = ApsWorkflowTaskTemplate.findById(value?.toList()?.first()?.escalationTemplateId)

            if (escalationTemplate) {
                Map parameters = [employeeListLink: "${ConfigurationHolder.config.grails.serverURL}/employee/list",
                        link: ConfigurationHolder.config.grails.serverURL]
                if (gatekeeperTask.workerEntitlementRole) {
                    parameters.putAll(populateWorkflowParametersFromWorkerEntitlementRole(gatekeeperTask.workerEntitlementRole, parameters))
                }
                parameters.putAll(populateWorkflowParametersForRevocationTable(gatekeeperTask, gatekeeperTask.workflowGuid, parameters))

                String emailSubject = apsUtilService.prepareEmailSubject(escalationTemplate.messageTemplate, parameters)
                String emailBody = apsUtilService.prepareEmailBody(escalationTemplate.messageTemplate, parameters)

                if (value) {
                    value.toList()*.entitlement.each { Entitlement entitlement ->
                        toRecipients.addAll(apsUtilService.getRecipients(escalationTemplate.toNotificationEmails, escalationTemplate.toNotificationSlids, escalationTemplate.toNotificationApplicationRoles, entitlement, escalationTemplate.respectExclusionList))
                        ccRecipients.addAll(apsUtilService.getRecipients(escalationTemplate.ccNotificationEmails, escalationTemplate.ccNotificationSlids, escalationTemplate.ccNotificationApplicationRoles, entitlement, escalationTemplate.respectExclusionList))
                    }
                    toRecipients = toRecipients as Set
                    ccRecipients = ccRecipients as Set

                    try {
                        apsUtilService.sendEmail(toRecipients, ccRecipients, emailSubject, emailBody)
                    } catch (Throwable t) {
                        // TODO : What else to do?
                        t.printStackTrace();
                    }
                }
            }
        }

        provisionerDeprovisionerTasks.each { ApsWorkflowTask taskToBeEscalated ->
            log.debug "Escalating Provisioner Task : " + taskToBeEscalated.id
            taskToBeEscalated.createEscalationLogTask()
            ApsWorkflowTaskTemplate escalationTemplate = ApsWorkflowTaskTemplate.findById(taskToBeEscalated.escalationTemplateId)
            Collection<String> slids = ApsUtilService.getSecurityRolesOrSlidsByApplicationRole(escalationTemplate.actorApplicationRoles, taskToBeEscalated.entitlement ?: (taskToBeEscalated.entitlementRole ?: taskToBeEscalated.workerEntitlementRole), escalationTemplate.respectExclusionList)
            if (escalationTemplate.actorSlids) {
                slids.addAll(escalationTemplate.actorSlids.tokenize(', '))
            }

            slids?.each { String slid ->
                if (!ApsWorkflowTaskPermittedSlid.countByTaskAndSlid(taskToBeEscalated, slid)) {
                    taskToBeEscalated.addToPermittedSlids(new ApsWorkflowTaskPermittedSlid(slid: slid))
                }
            }
            taskToBeEscalated.securityRoles += escalationTemplate.actorSecurityRoles*.name
            if (escalationTemplate.escalationTemplate) {
                taskToBeEscalated.escalationTemplateId = escalationTemplate.escalationTemplate.id
                taskToBeEscalated.period = escalationTemplate.period
                taskToBeEscalated.periodUnit = escalationTemplate.periodUnit
            } else {
                taskToBeEscalated.escalationTemplateId = null;
                taskToBeEscalated.period = null
                taskToBeEscalated.periodUnit = null
            }
            taskToBeEscalated.s();
        }
    }

    public Map accessVerificationReport(String slid) {
        EmployeeSupervisor employeeSupervisor = EmployeeSupervisor.findBySlid(slid)
        Map<CcEntitlementRole, List<Worker>> activeWorkersGroupByEntitlementRoles = employeeSupervisor?.getActiveWorkersGroupByEntitlementRole()


        Map rolesMap = new HashMap()
        activeWorkersGroupByEntitlementRoles?.each { entitlementRole, workers ->
            List<EmpVO> empVOs = []
            workers.each { Worker worker ->
                empVOs << new EmpVO(worker.slid)
            }

            rolesMap.put(entitlementRole, empVOs)
        }
        return rolesMap
    }

    public def autoConfirmAccessVerificationTasks(Date date) {
        Integer hours = ConfigurationHolder.config.escalatedAccessVerificationWaitPeriodInHours ? ConfigurationHolder.config.escalatedAccessVerificationWaitPeriodInHours?.toInteger() : 8
        List<ApsWorkflowTask> tasks = ApsWorkflowTask.findAllWhere(workflowType: ApsWorkflowType.ACCESS_VERIFICATION, status: WorkflowTaskStatus.NEW, escalationTemplateId: null)
        use(TimeCategory) {
            tasks = tasks.findAll { (((it.effectiveStartDate + hours.hours) as Date) < date) }
        }
        tasks.each { ApsWorkflowTask task ->
            ApsWorkflowUtilService.sendResponseElements(task, ['userAction': 'AUTO CONFIRM'])
        }
    }

    public def autoConfirmGatekeeperResponseForAccessRevocationTasks(Date date) {
        List<ApsWorkflowTask> autoConfirmTasks = []
        List<ApsWorkflowTask> tasks = ApsWorkflowTask.findAllWhere(workflowType: ApsWorkflowType.ROLE_REVOKE_REQUEST, status: WorkflowTaskStatus.NEW, nodeName: 'Pending Revocation by Entitlement Role Gatekeeper')
        Integer hours
        tasks.each { ApsWorkflowTask task ->
            CentralWorkflowTask centralWorkflowTask = CentralWorkflowTask.findAllByWorkflowGuid(task.workflowGuid).max { it.dateCreated }
            if (centralWorkflowTask.periodUnit == PeriodUnit.DAYS) {
                hours = ConfigurationHolder.config.gatekeeperAutoConfirmWaitPeriodFor7DaysRevocationInHours ? ConfigurationHolder.config.gatekeeperAutoConfirmWaitPeriodFor7DaysRevocationInHours?.toInteger() : 24
            } else {
                hours = ConfigurationHolder.config.gatekeeperAutoConfirmWaitPeriodFor24HoursRevocationInHours ? ConfigurationHolder.config.gatekeeperAutoConfirmWaitPeriodFor24HoursRevocationInHours?.toInteger() : 2
            }
            use(TimeCategory) {
                if (((task.effectiveStartDate + hours.hours) as Date) < date) {
                    autoConfirmTasks.add(task)
                }
            }
        }
        autoConfirmTasks.each { ApsWorkflowTask task ->
            ApsWorkflowUtilService.sendResponseElements(task, ['userAction': 'AUTO CONFIRM'])
        }
    }

    public def autoCompleteGenericAndSharedEntitlementApsWorkflowTasks() {
        List<GenericAndSharedEntitlementApsWorkflowTask> genericAndSharedEntitlementApsWorkflowTaskList = GenericAndSharedEntitlementApsWorkflowTask.findAllByIsProcessed(false)
        Boolean processGenericAndSharedEntitlementApsWorkflowTask = false
        Map responseElements = [:]

        genericAndSharedEntitlementApsWorkflowTaskList.each { GenericAndSharedEntitlementApsWorkflowTask genericAndSharedEntitlementApsWorkflowTask ->
            Long workerEntitlementRoleId = ApsWorkflowTask.findByWorkflowGuid(genericAndSharedEntitlementApsWorkflowTask.workflowGuid).workerEntitlementRoleId
            List<ApsWorkflowTask> apsWorkflowTasks = ApsWorkflowTask.findAllByWorkerEntitlementRoleIdAndStatus(workerEntitlementRoleId, WorkflowTaskStatus.NEW)
            List<ApsWorkflowTask> filteredApsWorkflowTasks = apsWorkflowTasks.findAll { (it.isGatekeeperTask()) || (it.isProvisionerOrDeprovisionerTask() && !it.isAutoProvisionedDeprovisionedTask) }
            if (filteredApsWorkflowTasks) {
                List<Long> oldTaskIds = genericAndSharedEntitlementApsWorkflowTask?.taskIdsOnWhichResponseIsDependentOn?.tokenize(',')
                List<Long> newTaskIds = filteredApsWorkflowTasks*.id
                String taskIdsToBeAdded = oldTaskIds ? (',' + oldTaskIds?.plus(newTaskIds)?.join(',')) : (',' + newTaskIds?.join(','))
                genericAndSharedEntitlementApsWorkflowTask.taskIdsOnWhichResponseIsDependentOn = genericAndSharedEntitlementApsWorkflowTask.taskIdsOnWhichResponseIsDependentOn ? genericAndSharedEntitlementApsWorkflowTask.taskIdsOnWhichResponseIsDependentOn + taskIdsToBeAdded : taskIdsToBeAdded
                genericAndSharedEntitlementApsWorkflowTask.taskIdsOnWhichResponseIsDependentOn = genericAndSharedEntitlementApsWorkflowTask?.taskIdsOnWhichResponseIsDependentOn?.tokenize(',')?.unique()?.join(',')
                genericAndSharedEntitlementApsWorkflowTask.s()
            }
        }

        genericAndSharedEntitlementApsWorkflowTaskList.each { GenericAndSharedEntitlementApsWorkflowTask genericAndSharedEntitlementApsWorkflowTask ->
            processGenericAndSharedEntitlementApsWorkflowTask = false
            if (!genericAndSharedEntitlementApsWorkflowTask.taskIdsOnWhichResponseIsDependentOn) {
                responseElements.put('accessJustification', 'Auto-approval of Generic and Shared entitlement by APS system')
                responseElements.put('userAction', 'CONFIRM')
                processGenericAndSharedEntitlementApsWorkflowTask = true
            } else {
                List<String> taskIds = genericAndSharedEntitlementApsWorkflowTask?.taskIdsOnWhichResponseIsDependentOn?.tokenize(',')
                List<ApsWorkflowTask> apsWorkflowTasks = ApsWorkflowTask.findAllByIdInListAndNodeNameNotEqual(taskIds.collect { it.toLong() }, 'Initial Task')
                apsWorkflowTasks = apsWorkflowTasks.findAll { it.isGatekeeperTask() || !it.isAutoProvisionedDeprovisionedTask }
                if (apsWorkflowTasks.every { it.status.equals(WorkflowTaskStatus.COMPLETE) && it.response.equals('REJECT') }) {
                    responseElements.put('accessJustification', 'Auto-rejection of Generic and Shared entitlement by APS system')
                    responseElements.put('userAction', 'REJECT')
                    processGenericAndSharedEntitlementApsWorkflowTask = true
                } else if (apsWorkflowTasks.every { it.status.equals(WorkflowTaskStatus.COMPLETE) && it.response in ['CONFIRM', 'APPROVE'] }) {
                    responseElements.put('accessJustification', 'Auto-approval of Generic and Shared entitlement by APS system')
                    responseElements.put('userAction', 'CONFIRM')
                    processGenericAndSharedEntitlementApsWorkflowTask = true
                }
            }
            if (processGenericAndSharedEntitlementApsWorkflowTask) {
                List<ApsWorkflowTask> apsWorkflowTasks = ApsWorkflowTask.findAllByWorkflowGuidAndNodeNameNotEqual(genericAndSharedEntitlementApsWorkflowTask.workflowGuid, 'Initial Task')
                apsWorkflowTasks.each { ApsWorkflowTask apsWorkflowTask ->
                    ApsWorkflowUtilService.sendResponseElements(apsWorkflowTask, responseElements)
                }
                List<GenericAndSharedEntitlementApsWorkflowTask> genericAndSharedEntitlementApsWorkflowTasks = genericAndSharedEntitlementApsWorkflowTaskList.findAll { it.workflowGuid.equals(genericAndSharedEntitlementApsWorkflowTask.workflowGuid) }
                genericAndSharedEntitlementApsWorkflowTasks*.isProcessed = true
                genericAndSharedEntitlementApsWorkflowTaskList*.s()
            }
        }
    }

    public void sendResponse(ApsWorkflowTask task, Map responseElements, List<UploadedFile> uploadedFiles = [], boolean groupResponse = false) {
        List<ApsWorkflowTask> similarEntitlementProvisionerTasks = []
        List<ApsWorkflowTask> entitlementProvisionerTasksHavingSameWorkflowGuid = []
        List<ApsWorkflowTask> genericAndSharedEntitlementTasksHavingSameWorkflowGuid = []
        String userAction = responseElements['userAction']?.toString()
        if (task.workerEntitlementRoleId && task.nodeName in ['Pending Approval by Entitlement Role Gatekeeper', 'Pending Approval by Entitlement Gatekeeper'] && userAction?.equalsIgnoreCase('APPROVE')) {
            WorkerEntitlementRole workerEntitlementRole = task.workerEntitlementRole
            EntitlementRole role = EntitlementRole.findById(workerEntitlementRole.entitlementRole.id)
            Set<Entitlement> entitlements = role.getEntitlementsForProvisioning(workerEntitlementRole)
            if (entitlements.every { it.toBeAutoProvisioned }) {
                entitlements.each { Entitlement entitlement ->
                    new GenericAndSharedEntitlementApsWorkflowTask(workflowGuid: task.workflowGuid, entitlementId: entitlement.id).s()
                }
            }
        } else if (task.workerEntitlementRoleId && task.nodeName in ['Pending Revocation by Entitlement Role Gatekeeper', 'Pending Revocation by Entitlement Gatekeeper'] && userAction?.equalsIgnoreCase('APPROVE')) {
            WorkerEntitlementRole workerEntitlementRole = task.workerEntitlementRole
            EntitlementRole role = EntitlementRole.findById(workerEntitlementRole.entitlementRole.id)
            Set<Entitlement> entitlements = role.getEntitlementsForRevocation(workerEntitlementRole)
            if (entitlements.every { it.toBeAutoDeprovisioned }) {
                entitlements.each { Entitlement entitlement ->
                    new GenericAndSharedEntitlementApsWorkflowTask(workflowGuid: task.workflowGuid, entitlementId: entitlement.id).s()
                }
            }
        }

        if (task.entitlement && ((task.nodeName.equalsIgnoreCase('Pending Approval from Entitlement Provisioner') && (task.workflowType in [ApsWorkflowType.ROLE_ACCESS_REQUEST, ApsWorkflowType.ROLE_ACCESS_REQUEST_FOR_CONTRACTOR, ApsWorkflowType.CANCEL_ACCESS_REVOCATION])) || (task.nodeName.equalsIgnoreCase('Entitlement Revoke Request') && (task.workflowType in [ApsWorkflowType.ROLE_REVOKE_REQUEST, ApsWorkflowType.CANCEL_ACCESS_APPROVAL])))) {
            if (userAction?.equalsIgnoreCase('CONFIRM')) {
                similarEntitlementProvisionerTasks = ApsWorkflowTask.createCriteria().list {
                    eq('type', task.type)
                    eq('entitlementId', task.entitlementId)
                    eq('workflowType', task.workflowType)
                    eq('nodeName', task.nodeName)
                    eq('status', task.status)
                }
                similarEntitlementProvisionerTasks = similarEntitlementProvisionerTasks.findAll { it.workerEntitlementRole.worker == task.workerEntitlementRole.worker }
                similarEntitlementProvisionerTasks.remove(task)
                genericAndSharedEntitlementTasksHavingSameWorkflowGuid = fetchAutoProvisionedDeprovisionedTasksForSameWorkflow(task)
            } else if (userAction?.equalsIgnoreCase('REJECT')) {
                entitlementProvisionerTasksHavingSameWorkflowGuid = ApsWorkflowTask.createCriteria().list {
                    eq('type', task.type)
                    eq('workflowGuid', task.workflowGuid)
                    eq('workflowType', task.workflowType)
                    eq('nodeName', task.nodeName)
                    eq('status', task.status)
                }
                entitlementProvisionerTasksHavingSameWorkflowGuid.remove(task)
            }
        }

        if (task.workflowType.equals(ApsWorkflowType.UPDATE_ROLE) && (userAction.toUpperCase() in ['CONFIRM', 'APPROVE'])) {
            deletePreviousProvisionerDeprovisionerTasksOnRoleUpdate(task)
            createProvisionerDeprovisionerTasksOnRoleUpdate(task)
        }

        ApsWorkflowUtilService.sendResponseElements(task, responseElements, uploadedFiles, groupResponse)
        if (similarEntitlementProvisionerTasks) {
            sendResponseForOtherEntitlementProvisionerTasks(task, similarEntitlementProvisionerTasks, responseElements, uploadedFiles, groupResponse)
        }
        if (entitlementProvisionerTasksHavingSameWorkflowGuid) {
            sendResponseForOtherEntitlementProvisionerTasks(task, entitlementProvisionerTasksHavingSameWorkflowGuid, responseElements, uploadedFiles, groupResponse)
        }
        if (genericAndSharedEntitlementTasksHavingSameWorkflowGuid) {
            sendResponseForSharedAndGenericEntitlementProvisionerTasks(responseElements, genericAndSharedEntitlementTasksHavingSameWorkflowGuid, groupResponse)
        }
    }

    static List<ApsWorkflowTask> fetchAutoProvisionedDeprovisionedTasksForSameWorkflow(ApsWorkflowTask task) {
        List<ApsWorkflowTask> genericAndSharedEntitlementTasksHavingSameWorkflowGuid = ApsWorkflowTask.createCriteria().list {
            eq('workflowGuid', task.workflowGuid)
            eq('isAutoProvisionedDeprovisionedTask', true)
            eq('status', WorkflowTaskStatus.NEW)
        }
        return genericAndSharedEntitlementTasksHavingSameWorkflowGuid
    }

    static void sendResponseForSharedAndGenericEntitlementProvisionerTasks(Map responseElements, List<ApsWorkflowTask> genericAndSharedEntitlementTasksHavingSameWorkflowGuid, boolean groupResponse) {
        responseElements.put('accessJustification', 'Auto-approval of Generic and Shared entitlement by APS system')
        responseElements.put('userAction', 'CONFIRM')
        genericAndSharedEntitlementTasksHavingSameWorkflowGuid.each { ApsWorkflowTask apsWorkflowTask ->
            ApsWorkflowUtilService.sendResponseElements(apsWorkflowTask, responseElements, [], groupResponse)
        }
    }

    public void sendResponseForOtherEntitlementProvisionerTasks(ApsWorkflowTask apsWorkflowTask, List<ApsWorkflowTask> apsWorkflowTaskList, Map responseElements, List<UploadedFile> uploadedFiles = [], boolean groupResponse = false) {
        List<ApsDataFile> apsDataFiles = []
        apsWorkflowTask.documents.each { ApsDataFile apsDataFile ->
            apsDataFiles.add(apsDataFile)
        }
        apsWorkflowTaskList.each { ApsWorkflowTask task ->
            task.documents = apsDataFiles as Set
            ApsWorkflowUtilService.sendResponseElements(task, responseElements, [], groupResponse)
        }
    }

    public void autoArchiveCompletedWorkflowTask() {
        String autoArchiveCompletedWorkflowTaskDaysInterval = ConfigurationHolder.config.autoArchiveCompletedWorkflowTaskDaysInterval ?: '30'
        List<ApsWorkflowTask> tasks = ApsWorkflowTask.createCriteria().list {
            and {
                le("dateCreated", (new Date() - autoArchiveCompletedWorkflowTaskDaysInterval.toInteger()))
                eq("status", WorkflowTaskStatus.COMPLETE)
                eq("type", ApsWorkflowTaskType.HUMAN)
                'permittedSlids' {
                    eq('isArchived', false)
                }
            }
        }
        tasks.each { ApsWorkflowTask task ->
            List<ApsWorkflowTaskPermittedSlid> permittedSlids = ApsWorkflowTaskPermittedSlid.findAllByTask(task)
            if (task in permittedSlids*.task) {
                permittedSlids.each { ApsWorkflowTaskPermittedSlid permittedSlid ->
                    ApsWorkflowTaskPermittedSlid.markArchived(permittedSlid.task, permittedSlid.slid)
                }
            }
        }
    }

    public static void sendEmailToProvisioners(Long workerEntitlementRoleId, String workflowGuid, String revocationWorkflowTaskTemplate = null) {
        Set<String> entitlementIds = []
        String taskTemplateName
        Collection<String> toRecipients = []
        Collection<String> ccRecipients = []
        ApsWorkflowTask gatekeeperTask = ApsWorkflowTask.findByWorkflowGuidAndNodeNameInList(workflowGuid, ['Pending Approval by Entitlement Gatekeeper', 'Pending Approval by Entitlement Role Gatekeeper', 'Pending Revocation by Entitlement Role Gatekeeper', 'Pending Revocation by Entitlement Gatekeeper'])
        def applicationContext = ApplicationHolder.getApplication().getMainContext()
        def utilService = applicationContext.getBean('utilService')
        def apsUtilService = applicationContext.getBean('apsUtilService')
        WorkerEntitlementRole workerEntitlementRole = WorkerEntitlementRole.get(workerEntitlementRoleId)

        if (gatekeeperTask.workflowType in [ApsWorkflowType.ROLE_ACCESS_REQUEST, ApsWorkflowType.ROLE_ACCESS_REQUEST_FOR_CONTRACTOR, ApsWorkflowType.CANCEL_ACCESS_REVOCATION]) {
            entitlementIds = utilService.getEntitlementsToProvideAccess(workerEntitlementRoleId)
            taskTemplateName = CareConstants.WORKFLOW_TASK_TEMPLATE_ACCESS_CONFIRM_BY_PROVISIONER
        } else {
            entitlementIds = utilService.getEntitlementsToRevokeAccess(workerEntitlementRoleId)
            taskTemplateName = revocationWorkflowTaskTemplate
        }
        List<Entitlement> entitlementList = Entitlement.findAllByIdInList(entitlementIds.toList())
        ApsWorkflowTaskTemplate taskTemplate = ApsWorkflowTaskTemplate.findById(taskTemplateName)

        Map parameters = [employeeListLink: "${ConfigurationHolder.config.grails.serverURL}/employee/list",
                link: ConfigurationHolder.config.grails.serverURL]
        if (workerEntitlementRole) {
            parameters.putAll(populateWorkflowParametersFromWorkerEntitlementRole(workerEntitlementRole, parameters))
        }
        parameters.putAll(populateWorkflowParametersForRevocationTable(gatekeeperTask, workflowGuid, parameters))

        String emailSubject = apsUtilService.prepareEmailSubject(taskTemplate.messageTemplate, parameters)
        String emailBody = apsUtilService.prepareEmailBody(taskTemplate.messageTemplate, parameters)

        if (entitlementList && taskTemplate) {
            entitlementList.each { Entitlement entitlement ->
                toRecipients.addAll(apsUtilService.getRecipients(taskTemplate.toNotificationEmails, taskTemplate.toNotificationSlids, taskTemplate.toNotificationApplicationRoles, entitlement, taskTemplate.respectExclusionList))
                ccRecipients.addAll(apsUtilService.getRecipients(taskTemplate.ccNotificationEmails, taskTemplate.ccNotificationSlids, taskTemplate.ccNotificationApplicationRoles, entitlement, taskTemplate.respectExclusionList))
            }
            toRecipients = toRecipients as Set
            ccRecipients = ccRecipients as Set

            try {
                apsUtilService.sendEmail(toRecipients, ccRecipients, emailSubject, emailBody)
            } catch (Throwable t) {
                // TODO : What else to do?
                t.printStackTrace();
            }
        }
    }

    public static Map populateWorkflowParametersFromWorkerEntitlementRole(WorkerEntitlementRole workerEntitlementRole, LinkedHashMap<String, GString> parameters) {
        parameters['worker'] = workerEntitlementRole?.worker
        parameters['workerAsSupervisor'] = (workerEntitlementRole?.worker ? EmployeeSupervisor.findBySlid(workerEntitlementRole?.worker?.slid) : null)
        parameters['entitlementRole'] = workerEntitlementRole?.entitlementRole
        parameters['workerEntitlementRole'] = workerEntitlementRole
        parameters['lastHumanActorName'] = ApsUtilService.getActorNameOfLastHumanTask(workerEntitlementRole)
        return parameters
    }

    public static Map populateWorkflowParametersForRevocationTable(ApsWorkflowTask task, String workflowGuid, LinkedHashMap<String, GString> parameters) {
        parameters['initialTask'] = ApsWorkflowTask.getInitialTask(workflowGuid)  // This is going for revocation table in the revocation email. See ###RevocationTable### in Custom Tags
        parameters['task'] = task
        parameters['effectiveStartDate'] = task.effectiveStartDate
        return parameters
    }

    public List<ProvisionerDeprovisionerTaskVO> populateProvisionerDeprovisionerTaskVOs(List<ApsWorkflowTask> apsWorkflowTasks) {
        List<ProvisionerDeprovisionerTaskVO> provisionerDeprovisionerTaskVOList = []
        apsWorkflowTasks.each { ApsWorkflowTask apsWorkflowTask ->
            provisionerDeprovisionerTaskVOList.addAll(checkAndCreateProvisionerDeprovisionerTaskVOs(apsWorkflowTask))
        }
        return provisionerDeprovisionerTaskVOList.flatten()
    }

    public List<ProvisionerDeprovisionerTaskVO> checkAndCreateProvisionerDeprovisionerTaskVOs(ApsWorkflowTask apsWorkflowTask) {
        List<ProvisionerDeprovisionerTaskVO> provisionerDeprovisionerTaskVOList = []
        List<WorkerEntitlementRole> workerEntitlementRoles = WorkerEntitlementRole.createCriteria().list {
            eq('entitlementRole', CcEntitlementRole.findById(apsWorkflowTask.entitlementRole.id))
            inList('status', [EntitlementRoleAccessStatus.ACTIVE, EntitlementRoleAccessStatus.PENDING_PROVISIONER_DEPROVISIONER_TASKS_ON_ROLE_UPDATE, EntitlementRoleAccessStatus.PENDING_ACCESS_APPROVAL, EntitlementRoleAccessStatus.PENDING_ACCESS_REVOCATION])
        }
        VersionHistory entitlementsChange = versioningService.getPendingChanges(apsWorkflowTask.entitlementRole as EntitlementRole).find { it.effectiveDate == null && it.propertyName.equalsIgnoreCase("entitlements") }
        if (entitlementsChange) {
            List<Entitlement> entitlementsToDeprovision = (entitlementsChange.oldValue - entitlementsChange.newValue) as List<Entitlement>
            List<Entitlement> entitlementsToProvision = (entitlementsChange.newValue - entitlementsChange.oldValue) as List<Entitlement>
            workerEntitlementRoles.each { WorkerEntitlementRole workerEntitlementRole ->
                entitlementsToDeprovision.each { Entitlement entitlement ->
                    List<ApsWorkflowTask> tasks = ApsWorkflowTask.createCriteria().list {
                        eq('entitlementId', entitlement.id)
                        eq('status', WorkflowTaskStatus.NEW)
                        eq('nodeName', 'Entitlement Revoke Request')
                    }
                    if (!tasks.any { it.workerEntitlementRole.worker == workerEntitlementRole.worker }) {
                        provisionerDeprovisionerTaskVOList.add(new ProvisionerDeprovisionerTaskVO(workerEntitlementRole, entitlement, 'De-provision'))
                    }
                }
                entitlementsToProvision.each { Entitlement entitlement ->
                    List<ApsWorkflowTask> tasks = ApsWorkflowTask.createCriteria().list {
                        eq('entitlementId', entitlement.id)
                        eq('status', WorkflowTaskStatus.NEW)
                        eq('nodeName', 'Pending Approval from Entitlement Provisioner')
                    }
                    Set<Entitlement> activeEntitlements = entitlementService.getActiveEntitlementsForWorker(workerEntitlementRole.worker)
                    if (!tasks.any { it.workerEntitlementRole.worker == workerEntitlementRole.worker } && !(entitlement in activeEntitlements)) {
                        provisionerDeprovisionerTaskVOList.add(new ProvisionerDeprovisionerTaskVO(workerEntitlementRole, entitlement, 'Provision'))
                    }
                }
            }
        }
        return provisionerDeprovisionerTaskVOList
    }


    public void createProvisionerDeprovisionerTasksOnRoleUpdate(ApsWorkflowTask apsWorkflowTask) {
        List<ProvisionerDeprovisionerTaskVO> provisionerDeprovisionerTaskVOList = checkAndCreateProvisionerDeprovisionerTaskVOs(apsWorkflowTask)
        String guid = UUID.randomUUID().toString()
        provisionerDeprovisionerTaskVOList.each { ProvisionerDeprovisionerTaskVO provisionerDeprovisionerTaskVO ->
            ProvisionerDeprovisionerTaskOnRoleUpdate provisionerDeprovisonerTaskOnRoleUpdate = new ProvisionerDeprovisionerTaskOnRoleUpdate(guid, provisionerDeprovisionerTaskVO.workerEntitlementRoleId, provisionerDeprovisionerTaskVO.entitlementId, provisionerDeprovisionerTaskVO.type)
            provisionerDeprovisonerTaskOnRoleUpdate.s()
        }
    }

    public void deletePreviousProvisionerDeprovisionerTasksOnRoleUpdate(ApsWorkflowTask apsWorkflowTask) {
        List<WorkerEntitlementRole> workerEntitlementRoles = WorkerEntitlementRole.createCriteria().list {
            eq('entitlementRole', CcEntitlementRole.findById(apsWorkflowTask.entitlementRole.id))
            inList('status', [EntitlementRoleAccessStatus.ACTIVE, EntitlementRoleAccessStatus.PENDING_ACCESS_APPROVAL, EntitlementRoleAccessStatus.PENDING_ACCESS_REVOCATION])
        }
        List<ProvisionerDeprovisionerTaskOnRoleUpdate> provisionerDeprovisonerTaskOnRoleUpdateList = ProvisionerDeprovisionerTaskOnRoleUpdate.findAllByWorkerEntitlementRoleIdInList(workerEntitlementRoles*.id)
        provisionerDeprovisonerTaskOnRoleUpdateList*.delete(flush: true)
    }
}

class EmpVO {
    String name
    String slid
    String badge

    EmpVO() {}

    EmpVO(String slidValue) {
        Employee employee = Employee.findBySlid(slidValue)
        name = employee.name
        slid = employee.slid
        badge = employee.badgeNumber
    }

}


