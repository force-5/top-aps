package com.force5solutions.care.workflow

import com.force5solutions.care.aps.EntitlementInfoFromFeed
import com.force5solutions.care.aps.EntitlementRole
import com.force5solutions.care.aps.Origin
import com.force5solutions.care.cc.UploadedFile
import com.force5solutions.care.common.SessionUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.drools.KnowledgeBaseFactory
import org.drools.base.MapGlobalResolver
import org.drools.builder.KnowledgeBuilderFactory
import org.drools.builder.ResourceType
import org.drools.compiler.DialectConfiguration
import org.drools.compiler.PackageBuilderConfiguration
import org.drools.io.ResourceFactory
import org.drools.persistence.gorm.GORMKnowledgeService
import org.drools.rule.builder.dialect.java.JavaDialectConfiguration
import org.drools.runtime.EnvironmentName
import org.drools.runtime.StatefulKnowledgeSession
import static com.force5solutions.care.common.CareConstants.*
import com.force5solutions.care.aps.Entitlement
import com.force5solutions.care.cc.PeriodUnit
import com.force5solutions.care.aps.ApsDataFile
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.force5solutions.care.cc.WorkerEntitlementRole
import com.force5solutions.care.aps.ApsUtilService
import org.apache.commons.logging.LogFactory
import java.text.SimpleDateFormat
import com.force5solutions.care.aps.EntitlementAttribute
import com.force5solutions.care.aps.RoleOwner
import com.force5solutions.care.aps.ApsPerson
import com.force5solutions.care.cc.EntitlementPolicy
import com.force5solutions.care.common.CareConstants

class ApsWorkflowUtilService extends WorkflowUtilService {

    static transactional = true
    static config = ConfigurationHolder.config

    private static final log = LogFactory.getLog(this)

    static void sendNotificationForWorkerEntitlementRole(String taskTemplate, Long workerEntitlementRoleId) {
        def applicationContext = ApplicationHolder.getApplication().getMainContext()
        def apsUtilService = applicationContext.getBean('apsUtilService')
        ApsWorkflowTaskTemplate apsWorkflowTaskTemplate = ApsWorkflowTaskTemplate.findById(taskTemplate)
        if (apsWorkflowTaskTemplate) {
            WorkerEntitlementRole workerEntitlementRole = WorkerEntitlementRole.get(workerEntitlementRoleId)
            apsUtilService.sendNotification(apsWorkflowTaskTemplate, apsWorkflowTaskTemplate.messageTemplate, workerEntitlementRole, ApsUtilService.getParametersForMessageTemplate(workerEntitlementRole))
        }
    }

    static void abortWorkflow(String workflowGuid) {
        List<ApsWorkflowTask> tasks = ApsWorkflowTask.findAllByWorkflowGuidAndStatusInList(workflowGuid, [WorkflowTaskStatus.NEW, WorkflowTaskStatus.PENDING])
        tasks.each { task ->
            task.status = WorkflowTaskStatus.CANCELLED
            task.actorSlid = APS_SYSTEM_USER_ID
            task.s()
        }
    }

    static void startCancelWorkflow(String workflowGuid) {
        ApsWorkflowTask initialTask = ApsWorkflowTask.findByWorkflowGuid(workflowGuid)
        switch (initialTask.workflowType) {
            default:
                log.debug "No cancel workflow present "
        }
    }

    static void registerAllWorkItemHandlers(StatefulKnowledgeSession knowledgeSession) {
        knowledgeSession.getWorkItemManager().registerWorkItemHandler("APS Workflow Task", new WorkflowTaskHandler())
    }

    static void sendResponseElements(ApsWorkflowTask workflowTask, Map responseElements, List<UploadedFile> uploadedFiles = [], boolean groupResponse = false) {
        def workerEntitlementArchiveService = ApplicationHolder.getApplication().getMainContext().getBean('workerEntitlementArchiveService')
        def (kbase, env) = createKnowledgeBaseForFlows(workflowTask.workflowType.workflowFilePath)
        StatefulKnowledgeSession knowledgeSession = GORMKnowledgeService.loadStatefulKnowledgeSession(workflowTask.droolsSessionId.toInteger(), kbase, null, env);
        Long workItemId = workflowTask.workItemId
        if (!groupResponse) {
            uploadedFiles?.each { UploadedFile uploadedFile ->
                workflowTask.addToDocuments(new ApsDataFile(uploadedFile))
            }
        }
        workflowTask.status = WorkflowTaskStatus.COMPLETE
        workflowTask.response = responseElements['userAction'] ?: 'CONFIRM'
        workflowTask.message = responseElements['accessJustification']
        if (responseElements['actionDate'] && (responseElements['actionDate'].toString().length() > 0)) {
            SimpleDateFormat format = new SimpleDateFormat('MM/dd/yyyy hh:mm a')
            workflowTask.actionDate = format.parse(responseElements['actionDate'].toString())
        }
        workflowTask.responseElements = ApsWorkflowTask.serializeResponseEmplements(responseElements)
        if (SessionUtils.getSession()?.loggedUser) {
            workflowTask.actorSlid = SessionUtils.getSession()?.loggedUser
            ApsWorkflowTaskPermittedSlid.markArchived(workflowTask, SessionUtils.getSession()?.loggedUser);
        }
        if (workflowTask.response.toString().equals('AUTO CONFIRM')) {
            workflowTask.actorSlid = APS_SYSTEM_USER_ID
        }
        workflowTask.s()
        registerAllWorkItemHandlers(knowledgeSession)
        knowledgeSession.getWorkItemManager().completeWorkItem(workItemId, ['responseElements': responseElements])
        workerEntitlementArchiveService.createWorkerEntitlementEntry(workflowTask, responseElements)
        completeAutoProvisioningDeprovisioningTasksOnGettingTIMResponse(workflowTask, responseElements, groupResponse)
    }

    private static void completeAutoProvisioningDeprovisioningTasksOnGettingTIMResponse(ApsWorkflowTask workflowTask, Map responseElements, boolean groupResponse) {
        ApsWorkflowTaskService apsWorkflowTaskService = ApplicationHolder.getApplication().getMainContext().getBean('apsWorkflowTaskService')

        if (workflowTask.type.equals(ApsWorkflowTaskType.SYSTEM_TIM) && !(workflowTask.response in ['FAILURE', 'REJECT'])) {
            List<ApsWorkflowTask> genericAndSharedEntitlementTasksHavingSameWorkflowGuid = apsWorkflowTaskService.fetchAutoProvisionedDeprovisionedTasksForSameWorkflow(workflowTask)
            if (genericAndSharedEntitlementTasksHavingSameWorkflowGuid) {
                apsWorkflowTaskService.sendResponseForSharedAndGenericEntitlementProvisionerTasks(responseElements, genericAndSharedEntitlementTasksHavingSameWorkflowGuid, groupResponse)
            }
        }
    }

    void startAddRoleWorkflow(String entitlementRoleId) {
        log.debug "Starting Add Role Workflow: " + entitlementRoleId
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("entitlementRoleId", entitlementRoleId)
        startWorkflow(parameterMap, ApsWorkflowType.ADD_ROLE)
    }

    void startUpdateRoleWorkflow(String entitlementRoleId) {
        log.debug "Starting Update Role Workflow: " + entitlementRoleId
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("entitlementRoleId", entitlementRoleId)
        startWorkflow(parameterMap, ApsWorkflowType.UPDATE_ROLE)
    }

    static void startAddEntitlementWorkflow(String entitlementId) {
        log.debug "Starting Add Entitlement Workflow: " + entitlementId
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("entitlementId", entitlementId)
        startWorkflow(parameterMap, ApsWorkflowType.ADD_ENTITLEMENT)
    }

    static void startAccountPasswordChangeWorkflow(String entitlementId) {
        log.debug "Starting Account Password Change Workflow: " + entitlementId
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("entitlementId", entitlementId)
        startWorkflow(parameterMap, ApsWorkflowType.ACCOUNT_PASSWORD_CHANGE)
    }

    static void startUpdateEntitlementWorkflow(String entitlementId) {
        log.debug "Starting Update Entitlement Workflow: " + entitlementId
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("entitlementId", entitlementId)
        startWorkflow(parameterMap, ApsWorkflowType.UPDATE_ENTITLEMENT)
    }

    static ApsWorkflowType getCorrespondingApsWorkflowType(CentralWorkflowTask centralWorkflowTask) {
        ApsWorkflowType apsWorkflowType = null
        switch (centralWorkflowTask.workflowType) {
            case CentralWorkflowType.EMPLOYEE_ACCESS_REQUEST:
                apsWorkflowType = ApsWorkflowType.ROLE_ACCESS_REQUEST
                break
            case CentralWorkflowType.EMPLOYEE_PUBLIC_ACCESS_REQUEST:
                apsWorkflowType = ApsWorkflowType.ROLE_ACCESS_REQUEST
                break
            case CentralWorkflowType.EMPLOYEE_CANCEL_ACCESS_APPROVAL:
                apsWorkflowType = ApsWorkflowType.CANCEL_ACCESS_APPROVAL
                break
            case CentralWorkflowType.CONTRACTOR_ACCESS_REQUEST:
                apsWorkflowType = ApsWorkflowType.ROLE_ACCESS_REQUEST_FOR_CONTRACTOR
                break
            case CentralWorkflowType.CONTRACTOR_CANCEL_ACCESS_APPROVAL:
                apsWorkflowType = ApsWorkflowType.CANCEL_ACCESS_APPROVAL
                break
            case CentralWorkflowType.EMPLOYEE_ACCESS_REVOKE:
                apsWorkflowType = ApsWorkflowType.ROLE_REVOKE_REQUEST
                break
            case CentralWorkflowType.EMPLOYEE_CANCEL_ACCESS_REVOCATION:
                apsWorkflowType = ApsWorkflowType.CANCEL_ACCESS_REVOCATION
                break
            case CentralWorkflowType.CONTRACTOR_ACCESS_REVOKE:
                apsWorkflowType = ApsWorkflowType.ROLE_REVOKE_REQUEST
                break
            case CentralWorkflowType.CONTRACTOR_CANCEL_ACCESS_REVOCATION:
                apsWorkflowType = ApsWorkflowType.CANCEL_ACCESS_REVOCATION
                break
            default:
                break
        }
        return apsWorkflowType
    }

    static void startRoleAccessRequestWorkflow(EntitlementRole entitlementRole, CentralWorkflowTask centralWorkflowTask) {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        List<String> otherEntitlementRoles = ((centralWorkflowTask.worker.activeEntitlementRoles*.entitlementRole.id as List) - (entitlementRole.id))
        Set<String> entitlementRoleIds = ((entitlementRole.rolesThatRequireApproval*.id as List) - (otherEntitlementRoles)) as Set
        Set<String> entitlementThatRequireApprovalIds = (entitlementRole.getEntitlementsThatRequireApprovalForWorker(centralWorkflowTask.workerEntitlementRole, false, getCorrespondingApsWorkflowType(centralWorkflowTask))*.id) as Set
        if (entitlementRoleIds || entitlementThatRequireApprovalIds) {
            parameterMap.put("workerEntitlementRoleId", centralWorkflowTask.workerEntitlementRoleId)
            parameterMap.put("entitlementThatRequireApprovalIds", entitlementThatRequireApprovalIds)
            parameterMap.put("entitlementIds", [] as Set)
            parameterMap.put("careCentralTaskId", centralWorkflowTask.id)
            parameterMap.put("workflowGuid", centralWorkflowTask.workflowGuid)
            parameterMap.put("entitlementRoleIds", entitlementRoleIds)
            if (centralWorkflowTask.worker.isEmployee()) {
                startWorkflow(parameterMap, ApsWorkflowType.ROLE_ACCESS_REQUEST)
            } else {
                startWorkflow(parameterMap, ApsWorkflowType.ROLE_ACCESS_REQUEST_FOR_CONTRACTOR)
            }
        } else {
            ApsWorkflowTaskService.createResponseForCareCentral(centralWorkflowTask.id, ["userAction": "APPROVE", "accessJustification": "No valid roles or entitlements to create Gatekeeper tasks."]);
        }
    }

    static void startRoleRevokeRequestWorkflow(EntitlementRole entitlementRole, CentralWorkflowTask centralWorkflowTask) {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        Set<String> otherActiveEntitlementRoleIds = centralWorkflowTask.worker.activeEntitlementRoles*.entitlementRole.id - entitlementRole.id
        Set<String> entitlementRoleIds = ((entitlementRole.rolesThatRequireApproval*.id as List) - (otherActiveEntitlementRoleIds)) as Set
        Set<String> entitlementThatRequireApprovalIds = (entitlementRole.getEntitlementsThatRequireApprovalForWorker(centralWorkflowTask.workerEntitlementRole, true, getCorrespondingApsWorkflowType(centralWorkflowTask))*.id) as Set
        if (entitlementRoleIds || entitlementThatRequireApprovalIds) {
            Set<String> otherActiveEntitlementIds = (EntitlementRole.findAllByIdInList(otherActiveEntitlementRoleIds)*.allEntitlements*.id).flatten() as Set
            Set<String> entitlementsAlreadyRequestedForRevocation = ApsWorkflowTaskService.getEntitlementsAlreadyRequestedForRevocation(entitlementRole, centralWorkflowTask.worker)
            Set<String> entitlementIds = ((entitlementRole.allEntitlements*.id - otherActiveEntitlementIds) as Set) - entitlementsAlreadyRequestedForRevocation
            parameterMap.put("workerEntitlementRoleId", centralWorkflowTask.workerEntitlementRoleId)
            parameterMap.put("entitlementIds", entitlementIds)
            //TODO: Should have some better mechanism to identify the template to be used
            String taskTemplate = (centralWorkflowTask.periodUnit == PeriodUnit.DAYS) ? APS_REVOKE_7_DAYS_PROVISIONER_TASK_TEMPLATE : APS_REVOKE_24_HOURS_PROVISIONER_TASK_TEMPLATE
            parameterMap.put("revocationWorkflowTaskTemplate", taskTemplate)
            parameterMap.put("effectiveStartDate", centralWorkflowTask.effectiveStartDate)
            parameterMap.put("careCentralTaskId", centralWorkflowTask.id)
            parameterMap.put("workflowGuid", centralWorkflowTask.workflowGuid)
            CentralWorkflowTask initialTask = CentralWorkflowTask.getInitialTask(centralWorkflowTask.workflowGuid)
            parameterMap.put("message", initialTask?.message)
            parameterMap.put("entitlementThatRequireApprovalIds", entitlementThatRequireApprovalIds)
            parameterMap.put("entitlementRoleIds", entitlementRoleIds)
            startWorkflow(parameterMap, ApsWorkflowType.ROLE_REVOKE_REQUEST)
        } else {
            ApsWorkflowTaskService.createResponseForCareCentral(centralWorkflowTask.id, ["userAction": "CONFIRM", "accessJustification": "No valid roles or entitlements to create Gatekeeper tasks."]);
        }
    }

    static void cancelAccessApprovalWorkflow(EntitlementRole entitlementRole, CentralWorkflowTask centralWorkflowTask) {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        Set<String> otherActiveEntitlementRoleIds = centralWorkflowTask.worker.activeEntitlementRoles*.entitlementRole.id - entitlementRole.id
        Set<String> entitlementRoleIds = ((entitlementRole.rolesThatRequireApproval*.id as List) - (otherActiveEntitlementRoleIds)) as Set
        Set<String> entitlementThatRequireApprovalIds = (entitlementRole.getEntitlementsThatRequireApprovalForWorker(centralWorkflowTask.workerEntitlementRole, true, getCorrespondingApsWorkflowType(centralWorkflowTask))*.id) as Set
        if (entitlementRoleIds || entitlementThatRequireApprovalIds) {
            Set<String> otherActiveEntitlementIds = (EntitlementRole.findAllByIdInList(otherActiveEntitlementRoleIds)*.allEntitlements*.id).flatten() as Set
            Set<String> entitlementsAlreadyRequestedForRevocation = ApsWorkflowTaskService.getEntitlementsAlreadyRequestedForRevocation(entitlementRole, centralWorkflowTask.worker)
            Set<String> entitlementIds = ((entitlementRole.allEntitlements*.id - otherActiveEntitlementIds) as Set) - entitlementsAlreadyRequestedForRevocation
            parameterMap.put("workerEntitlementRoleId", centralWorkflowTask.workerEntitlementRoleId)
            parameterMap.put("entitlementIds", entitlementIds)
            //TODO: Should have some better mechanism to identify the template to be used
            String taskTemplate = (centralWorkflowTask.periodUnit == PeriodUnit.DAYS) ? APS_REVOKE_7_DAYS_PROVISIONER_TASK_TEMPLATE : APS_REVOKE_24_HOURS_PROVISIONER_TASK_TEMPLATE
            parameterMap.put("revocationWorkflowTaskTemplate", taskTemplate)
            parameterMap.put("effectiveStartDate", centralWorkflowTask.effectiveStartDate)
            parameterMap.put("careCentralTaskId", centralWorkflowTask.id)
            parameterMap.put("workflowGuid", centralWorkflowTask.workflowGuid)
            CentralWorkflowTask initialTask = CentralWorkflowTask.getInitialTask(centralWorkflowTask.workflowGuid)
            parameterMap.put("message", initialTask?.message)
            parameterMap.put("entitlementThatRequireApprovalIds", entitlementThatRequireApprovalIds)
            parameterMap.put("entitlementRoleIds", entitlementRoleIds)
            startWorkflow(parameterMap, ApsWorkflowType.CANCEL_ACCESS_APPROVAL)
        } else {
            ApsWorkflowTaskService.createResponseForCareCentral(centralWorkflowTask.id, ["userAction": "CONFIRM", "accessJustification": "No valid roles or entitlements to create Gatekeeper tasks."]);
        }
    }

    static void cancelAccessRevocationWorkflow(EntitlementRole entitlementRole, CentralWorkflowTask centralWorkflowTask) {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        List<String> otherEntitlementRoles = ((centralWorkflowTask.worker.activeEntitlementRoles*.entitlementRole.id as List) - (entitlementRole.id))
        Set<String> entitlementRoleIds = ((entitlementRole.rolesThatRequireApproval*.id as List) - (otherEntitlementRoles)) as Set
        Set<String> entitlementThatRequireApprovalIds = (entitlementRole.getEntitlementsThatRequireApprovalForWorker(centralWorkflowTask.workerEntitlementRole, false, getCorrespondingApsWorkflowType(centralWorkflowTask))*.id) as Set
        if (entitlementRoleIds || entitlementThatRequireApprovalIds) {
            parameterMap.put("workerEntitlementRoleId", centralWorkflowTask.workerEntitlementRoleId)
            parameterMap.put("entitlementThatRequireApprovalIds", entitlementThatRequireApprovalIds)
            parameterMap.put("entitlementIds", [] as Set)
            parameterMap.put("careCentralTaskId", centralWorkflowTask.id)
            parameterMap.put("workflowGuid", centralWorkflowTask.workflowGuid)
            parameterMap.put("entitlementRoleIds", entitlementRoleIds)
            startWorkflow(parameterMap, ApsWorkflowType.CANCEL_ACCESS_REVOCATION)
        } else {
            ApsWorkflowTaskService.createResponseForCareCentral(centralWorkflowTask.id, ["userAction": "APPROVE", "accessJustification": "No valid roles or entitlements to create Gatekeeper tasks."]);
        }
    }

    static void startRoleTerminationWorkflow(EntitlementRole entitlementRole, CentralWorkflowTask centralWorkflowTask) {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("workerEntitlementRoleId", centralWorkflowTask.workerEntitlementRoleId)
        Set<String> otherActiveEntitlementRoleIds = centralWorkflowTask.worker.activeEntitlementRoles*.entitlementRole.id - entitlementRole.id
        Set<String> otherActiveEntitlementIds = (EntitlementRole.findAllByIdInList(otherActiveEntitlementRoleIds)*.allEntitlements*.id).flatten() as Set
        Set<String> entitlementsAlreadyRequestedForRevocation = ApsWorkflowTaskService.getEntitlementsAlreadyRequestedForRevocation(entitlementRole, centralWorkflowTask.worker)
        Set<String> entitlementIds = ((entitlementRole.allEntitlements*.id - otherActiveEntitlementIds) as Set) - entitlementsAlreadyRequestedForRevocation
        Origin timOrigin = Origin.findByName(Origin.TIM_FEED)
        boolean hasTimEntitlements = entitlementRole.entitlements.any { it.origin == timOrigin }
        if (entitlementIds) {
            List<Entitlement> entitlements = Entitlement.findAllByIdInList(entitlementIds)
            entitlements.removeAll { it.origin == timOrigin }
            entitlementIds = entitlements*.id
        }
        parameterMap.put("entitlementIds", entitlementIds)
        parameterMap.put("hasTimEntitlements", hasTimEntitlements)
        parameterMap.put("careCentralTaskId", centralWorkflowTask.id)
        parameterMap.put("workflowGuid", centralWorkflowTask.workflowGuid)
        startWorkflow(parameterMap, ApsWorkflowType.TERMINATE_REQUEST)
    }

    static void startAccessVerificationWorkflow(CentralWorkflowTask centralWorkflowTask) {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("workerId", centralWorkflowTask.workerId)
        parameterMap.put("careCentralTaskId", centralWorkflowTask.id)
        parameterMap.put("workflowGuid", centralWorkflowTask.workflowGuid)
        startWorkflow(parameterMap, ApsWorkflowType.ACCESS_VERIFICATION)
    }

    static void startProvisionerDeprovisionerTaskOnRoleUpdateWorkflow(CentralWorkflowTask centralWorkflowTask) {
        List<ProvisionerDeprovisionerTaskOnRoleUpdate> provisionerDeprovisionerTaskOnRoleUpdateList = ProvisionerDeprovisionerTaskOnRoleUpdate.findAllByWorkerEntitlementRoleIdAndGuid(centralWorkflowTask.workerEntitlementRoleId, centralWorkflowTask.provisionerDeprovisionerTaskOnRoleUpdateGuid)
        Map<String, Object> parameterMap = new HashMap<String, Object>()
        parameterMap.put("provisionerDeprovisionerTaskOnRoleUpdateIds", provisionerDeprovisionerTaskOnRoleUpdateList*.id)
        parameterMap.put("careCentralTaskId", centralWorkflowTask.id)
        parameterMap.put("workerEntitlementRoleId", centralWorkflowTask.workerEntitlementRoleId)
        parameterMap.put("provisionerDeprovisionerTaskOnRoleUpdateGuid", centralWorkflowTask.provisionerDeprovisionerTaskOnRoleUpdateGuid)
        String taskTemplate = (centralWorkflowTask.periodUnit == PeriodUnit.DAYS) ? APS_REVOKE_7_DAYS_PROVISIONER_TASK_TEMPLATE : APS_REVOKE_24_HOURS_PROVISIONER_TASK_TEMPLATE
        parameterMap.put("revocationWorkflowTaskTemplate", taskTemplate)
        parameterMap.put("effectiveStartDate", centralWorkflowTask.effectiveStartDate)
        parameterMap.put("workflowGuid", centralWorkflowTask.workflowGuid)
        startWorkflow(parameterMap, ApsWorkflowType.PROVISIONER_DEPROVISIONER_TASKS_ON_ROLE_UPDATE)
    }

    private static void startWorkflow(Map<String, Object> parameterMap, ApsWorkflowType apsWorkflowType) {
        log.debug "Starting workflow ${apsWorkflowType} now, with the parameterMap : ${parameterMap}"
        parameterMap.put("workflowType", apsWorkflowType)
        String filePath = apsWorkflowType.workflowFilePath
        String processId = apsWorkflowType.workflowProcessId
        def (kbase, env) = createKnowledgeBaseForFlows(filePath)
        StatefulKnowledgeSession knowledgeSession = createOrLoadKnowledgeSession(kbase, env);

        parameterMap.put("actorSlid", SessionUtils.getSession()?.loggedUser ?: APS_SYSTEM_USER_ID)
        parameterMap.put("droolsSessionId", knowledgeSession.id.toLong())
        if (!parameterMap.containsKey('workflowGuid')) {
            parameterMap.put("workflowGuid", UUID.randomUUID().toString())
        }
        registerAllWorkItemHandlers(knowledgeSession)
        knowledgeSession.startProcess(processId, parameterMap)
    }

    static def createKnowledgeBaseForFlows(String flowFileName) {
        PackageBuilderConfiguration pkgBuilderCfg = new PackageBuilderConfiguration()
        DialectConfiguration javaConf = pkgBuilderCfg.getDialectConfiguration("java")
        javaConf.setCompiler(JavaDialectConfiguration.JANINO)

        // create knowledge base
        def knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(pkgBuilderCfg)
        knowledgeBuilder.add(ResourceFactory.newClassPathResource(flowFileName), ResourceType.DRF);
        def knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase()
        if (knowledgeBuilder.hasErrors()) {
            def error_str = knowledgeBuilder.getErrors().toString()
            throw new IllegalArgumentException("Could not parse knowledge: $error_str")
        }
        knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages())

        def env = KnowledgeBaseFactory.newEnvironment()
        env.set(EnvironmentName.GLOBALS, new MapGlobalResolver())
        return [knowledgeBase, env]
    }

    public static StatefulKnowledgeSession createOrLoadKnowledgeSession(def kbase, def env) {
        StatefulKnowledgeSession knowledgeSession;
        knowledgeSession = GORMKnowledgeService.newStatefulKnowledgeSession(kbase, null, env)
        return knowledgeSession;
    }

    void startUpdateEntitlementExceptionFromFeedWorkFlow(EntitlementInfoFromFeed entitlementInfoFromFeed) {
        log.debug "Starting Update Entitlement Exception From Feed Role Workflow For Entitlement: " + entitlementInfoFromFeed?.entitlementId
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("entitlementInfoFromFeedId", entitlementInfoFromFeed?.id)
        startWorkflow(parameterMap, ApsWorkflowType.UPDATE_ENTITLEMENT_EXCEPTION_FROM_FEED)
    }

    void startCreateEntitlementExceptionFromFeedWorkFlow(EntitlementInfoFromFeed entitlementInfoFromFeed) {
        log.debug """Starting Create Entitlement Exception From Feed Role Workflow For Category Name: """ + entitlementInfoFromFeed?.entitlementName
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("entitlementInfoFromFeedId", entitlementInfoFromFeed?.id)
        startWorkflow(parameterMap, ApsWorkflowType.CREATE_ENTITLEMENT_EXCEPTION_FROM_FEED)
    }

    static void createOrUpdateEntitlementFromFeedDetail(Long entitlementInfoFromFeedId) {
        EntitlementInfoFromFeed entitlementInfoFromFeed = EntitlementInfoFromFeed.get(entitlementInfoFromFeedId)
        if (entitlementInfoFromFeed) {
            Entitlement entitlement = entitlementInfoFromFeed?.workflowType == CareConstants.WORKFLOW_TYPE_FOR_UPDATE_ENTITLEMENT_FROM_FILE_FEED ? Entitlement.findById(entitlementInfoFromFeed?.entitlementId) :
                createEntitlementFromEntitlementInfoFeed(entitlementInfoFromFeed)
            entitlement.s()
            addOrUpdateAttributesToEntitlementFromEntitlementInfoFeed(entitlement, entitlementInfoFromFeed)
            entitlementInfoFromFeed.isProcessed = true
            entitlementInfoFromFeed.s()
        }
    }

    static Entitlement createEntitlementFromEntitlementInfoFeed(EntitlementInfoFromFeed entitlementInfoFromFeed) {
        RoleOwner owner = RoleOwner.findByPerson(ApsPerson.findBySlid((ConfigurationHolder.config.ppOwner as String).toUpperCase()))
        EntitlementPolicy entitlementPolicy = EntitlementPolicy.findAllByNameInList((ConfigurationHolder.config.ppPolicy as String).tokenize(',')*.trim()).first()
        Origin origin = Origin.findByName(ConfigurationHolder.config.ppOrigin as String)
        return (new Entitlement(name: entitlementInfoFromFeed?.entitlementName,
                alias: entitlementInfoFromFeed?.entitlementName,
                type: entitlementPolicy?.id, origin: origin, owner: owner, isApproved: true))

    }

    static void addOrUpdateAttributesToEntitlementFromEntitlementInfoFeed(Entitlement entitlement, EntitlementInfoFromFeed entitlementInfoFromFeed) {
        entitlement.entitlementAttributes.clear()
        entitlementInfoFromFeed.areaAttributes.each { String areaAttributeValue ->
            entitlement.addToEntitlementAttributes(new EntitlementAttribute(keyName: 'Area', value: areaAttributeValue))
        }
        entitlementInfoFromFeed.readerAttributes.each { String readerAttributeValue ->
            entitlement.addToEntitlementAttributes(new EntitlementAttribute(keyName: 'Reader', value: readerAttributeValue))
        }
        entitlement.s()
    }

    static void abortCreateOrUpdateEntitlementWorkflowForCategoryAreaFileFeed(Long entitlementInfoFromFeedId) {
        EntitlementInfoFromFeed entitlementInfoFromFeed = EntitlementInfoFromFeed.get(entitlementInfoFromFeedId)
        if (entitlementInfoFromFeed) {
            entitlementInfoFromFeed.isProcessed = true
            entitlementInfoFromFeed.s()
        }
    }
}
