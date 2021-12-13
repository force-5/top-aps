package com.force5solutions.care.workflow

import com.force5solutions.care.cc.WorkerEntitlementRole
import com.force5solutions.care.aps.Entitlement
import com.force5solutions.care.aps.EntitlementRole
import com.force5solutions.care.common.CareConstants
import com.force5solutions.care.aps.ApsDataFile
import java.beans.XMLEncoder
import com.force5solutions.care.cc.Worker
import com.force5solutions.care.aps.ApsPerson
import com.force5solutions.care.common.SessionUtils

class ApsWorkflowTask extends WorkflowTask {

    ApsWorkflowType workflowType
    String entitlementRoleId
    Long workerEntitlementRoleId
    String entitlementId
    Long workerId
    Long timRequestId
    Long entitlementInfoFromFeedId
    ApsWorkflowTaskType type = ApsWorkflowTaskType.HUMAN

    List<String> actions = []
    Set<ApsDataFile> documents = []
    Set<String> securityRoles = []
    Set<ApsWorkflowTaskPermittedSlid> permittedSlids = []
    String provisionerDeprovisionerTaskOnRoleUpdateGuid
    Boolean isAutoProvisionedDeprovisionedTask = false

    static hasMany = [documents: ApsDataFile, actions: String, permittedSlids: ApsWorkflowTaskPermittedSlid, securityRoles: String]

    static transients = ['gatekeeperTask', 'provisionerOrDeprovisionerTask', 'provisionerTask', 'deprovisionerTask', 'worker', 'entitlement', 'entitlementRole', 'workerEntitlementRole', 'workflowFilePath', 'workflowProcessId', 'parametersForMessageTemplate', 'abbreviatedCodeForGroupResponse', 'accessOrRevokeRequestApprover', 'workflowTypeName']

    static constraints = {
        escalationTemplateId(nullable: true)
        period(nullable: true)
        periodUnit(nullable: true)
        actorSlid(nullable: true)
        timRequestId(nullable: true)
        workerId(nullable: true)
        response(nullable: true)
        message(nullable: true, maxSize: 8000)
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
        responseForm(nullable: true)
        entitlementId(nullable: true)
        entitlementInfoFromFeedId(nullable: true)
        entitlementRoleId(nullable: true)
        workerEntitlementRoleId(nullable: true)
        responseElements(nullable: true)
        actionDate(nullable: true)
        provisionerDeprovisionerTaskOnRoleUpdateGuid(nullable: true)
        isAutoProvisionedDeprovisionedTask(nullable: true)
    }

    static mapping = {
        responseElements type: 'text'
    }

    static List<ApsWorkflowTask> getPermittedTasks() {
        List<ApsWorkflowTask> tasks = ApsWorkflowTask.findAllByTypeAndStatus(ApsWorkflowTaskType.HUMAN, WorkflowTaskStatus.NEW, [fetch: [permittedSlids: 'eager', securityRoles: 'eager']])
        tasks = tasks.findAll { !it.isAutoProvisionedDeprovisionedTask }
        List<ApsWorkflowTask> permittedTasksList = []
        tasks.each { ApsWorkflowTask task ->
            if (hasPermission(task)) {
                permittedTasksList.add(task)
            }
        }
        return permittedTasksList
    }

    static List<ApsWorkflowTask> getPermittedTasksCompleted() {
        List<ApsWorkflowTask> tasks = ApsWorkflowTask.findAllByTypeAndStatus(ApsWorkflowTaskType.HUMAN, WorkflowTaskStatus.COMPLETE, [fetch: [permittedSlids: 'eager', securityRoles: 'eager']])
        List<ApsWorkflowTask> permittedTasksList = []
        tasks.each { ApsWorkflowTask task ->
            if (hasPermissionForCompletedTasks(task)) {
                permittedTasksList.add(task)
            }
        }
        return permittedTasksList
    }

    static Boolean hasPermission(String slidString = null, ApsWorkflowTask task) {
        String slid = slidString ?: SessionUtils.getSession()?.loggedUser
        if (!slid) {
            return false
        }
        if (task.permittedSlids.find { it.slid == slid && !it.isArchived }) {
            return true
        }
        Collection userSecurityRoles = SessionUtils.session?.roles
        if (userSecurityRoles.any { it in task.securityRoles } && (task.status == WorkflowTaskStatus.NEW)) {
            return true
        }
        return false
    }

    static Boolean hasPermissionForCompletedTasks(String slidString = null, ApsWorkflowTask task) {
        String slid = slidString ?: SessionUtils.getSession()?.loggedUser
        if (!slid) {
            return false
        }
        if (task.permittedSlids.find { it.slid == slid && !it.isArchived }) {
            return true
        }
        Collection userSecurityRoles = SessionUtils.session?.roles
        if (userSecurityRoles.any { it in task.securityRoles } && (task.status == WorkflowTaskStatus.COMPLETE)) {
            return true
        }
        return false
    }

    def getWorkerName() {
        if (workerEntitlementRoleId) {
            return worker?.name
        } else {
            return ''
        }
    }

    def getWorker() {
        Worker worker = workerEntitlementRoleId ? workerEntitlementRole.worker : (workerId ? Worker.get(workerId) : null)
        return worker
    }

    def getWorkerEntitlementRole() {
        if (workerEntitlementRoleId) {
            return WorkerEntitlementRole.get(workerEntitlementRoleId)
        }
        return null
    }

    def getEntitlement() {
        return ((entitlementId) ? Entitlement.findById(entitlementId) : null)
    }

    def getEntitlementRole() {
        return ((entitlementRoleId) ? EntitlementRole.findById(entitlementRoleId) : (workerEntitlementRoleId ? EntitlementRole.findById(workerEntitlementRole.entitlementRole.id) : null))
    }

    String getWorkflowFilePath() {
        return workflowType.workflowFilePath
    }

    String getWorkflowProcessId() {
        return workflowType.workflowProcessId
    }

    static getInitialTask(String workflowGuid) {
        WorkflowTask task = CentralWorkflowTask.getInitialTask(workflowGuid)
        if (!task) {
            task = ApsWorkflowTask.findByWorkflowGuid(workflowGuid)
        }
        return task
    }

    void createEscalationLogTask() {
        ApsWorkflowTask escalationLogTask = new ApsWorkflowTask();
        escalationLogTask.with {
            workflowGuid = this.workflowGuid;
            nodeName = this.nodeName
            nodeId = this.nodeId
            workItemId = this.workItemId
            actorSlid = CareConstants.APS_SYSTEM_USER_ID
            droolsSessionId = this.droolsSessionId
            response = "ESCALATED"
            status = WorkflowTaskStatus.COMPLETE
            type = ApsWorkflowTaskType.SYSTEM_APS
            workflowType = this.workflowType
        }
        escalationLogTask.s();
    }

    public static String serializeResponseEmplements(Map responseElements) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream()
        XMLEncoder xmlEncoder = new XMLEncoder(bos)
        xmlEncoder.writeObject(responseElements)
        xmlEncoder.flush()
        return bos.toString()
    }

    public String getAbbreviatedCodeForGroupResponse() {
        String abbreviatedCode = ""
        List<String> tokenizedWorkflowType = workflowType.name.tokenize(' ')
        tokenizedWorkflowType.each { String s ->
            abbreviatedCode = abbreviatedCode + s.substring(0, 1)
        }
        List<String> tokenizedCurrentNode = nodeName.tokenize(' ')
        tokenizedCurrentNode.each { String s ->
            abbreviatedCode = abbreviatedCode + s.substring(0, 1)
        }
        return abbreviatedCode
    }

    public boolean hasAnyProvisionerConfirmed() {
        boolean alreadyProvisioned = false
        if (entitlement && (isProvisionerTask() && (workflowType in [ApsWorkflowType.ROLE_ACCESS_REQUEST, ApsWorkflowType.ROLE_ACCESS_REQUEST_FOR_CONTRACTOR, ApsWorkflowType.PROVISIONER_DEPROVISIONER_TASKS_ON_ROLE_UPDATE])) || (isDeprovisionerTask() && (workflowType in [ApsWorkflowType.ROLE_REVOKE_REQUEST, ApsWorkflowType.PROVISIONER_DEPROVISIONER_TASKS_ON_ROLE_UPDATE]))) {
            List<ApsWorkflowTask> apsWorkflowTaskList = ApsWorkflowTask.createCriteria().list {
                eq('workflowGuid', workflowGuid)
                eq('nodeName', nodeName)
            }
            alreadyProvisioned = apsWorkflowTaskList ? apsWorkflowTaskList.any { it?.response?.equalsIgnoreCase('CONFIRM') } : false
        }
        return alreadyProvisioned
    }

    public static boolean hasAnyProvisionerConfirmedInTheTaskList(List<ApsWorkflowTask> apsWorkflowTaskList) {
        boolean alreadyProvisioned = false
        Map tasksGroupedByWorkflowGuid = apsWorkflowTaskList?.groupBy { it?.workflowGuid }
        tasksGroupedByWorkflowGuid.each { key, value ->
            ApsWorkflowTask firstTaskFromTheList = value.toList().first()
            if (!alreadyProvisioned && firstTaskFromTheList.entitlement && ((firstTaskFromTheList.nodeName.equalsIgnoreCase('Pending Approval from Entitlement Provisioner') && (firstTaskFromTheList.workflowType in [ApsWorkflowType.ROLE_ACCESS_REQUEST, ApsWorkflowType.ROLE_ACCESS_REQUEST_FOR_CONTRACTOR])) || (firstTaskFromTheList.nodeName.equalsIgnoreCase('Entitlement Revoke Request') && (firstTaskFromTheList.workflowType in [ApsWorkflowType.ROLE_REVOKE_REQUEST])))) {
                List<ApsWorkflowTask> workflowTaskList = ApsWorkflowTask.createCriteria().list {
                    eq('workflowGuid', firstTaskFromTheList.workflowGuid)
                    eq('nodeName', firstTaskFromTheList.nodeName)
                }
                alreadyProvisioned = workflowTaskList ? workflowTaskList.any { it?.response?.equalsIgnoreCase('CONFIRM') } : false
            }
        }
        return alreadyProvisioned
    }

    public ApsPerson getAccessOrRevokeRequestApprover() {
        List<ApsWorkflowTask> apsWorkflowTasks = ApsWorkflowTask.createCriteria().list {
            eq('workerEntitlementRoleId', workerEntitlementRoleId)
            eq('type', ApsWorkflowTaskType.HUMAN)
            eq('workflowGuid', workflowGuid)
            inList('nodeName', ['Pending Approval by Entitlement Role Gatekeeper', 'Pending Revocation by Entitlement Role Gatekeeper'])
            eq('status', WorkflowTaskStatus.COMPLETE)
        }
        return apsWorkflowTasks ? ApsPerson.findBySlid(apsWorkflowTasks?.last()?.actorSlid?.trim() ?: "") : null
    }

    public String getWorkflowTypeName() {
        String workflowType
        if (this.workflowType.equals(ApsWorkflowType.PROVISIONER_DEPROVISIONER_TASKS_ON_ROLE_UPDATE) && nodeName.equalsIgnoreCase("Deprovisioner Task")) {
            workflowType = "ROLE CHANGE - ACCESS REMOVAL REQUIRED"
        } else if (this.workflowType.equals(ApsWorkflowType.PROVISIONER_DEPROVISIONER_TASKS_ON_ROLE_UPDATE) && nodeName.equalsIgnoreCase("Provisioner Task")) {
            workflowType = "ROLE CHANGE - ADDITIONAL ACCESS REQUIRED"
        } else {
            workflowType = this.workflowType
        }
        return workflowType
    }

    public boolean isGatekeeperTask() {
        return nodeName in ['Pending Approval by Entitlement Role Gatekeeper', 'Pending Approval by Entitlement Gatekeeper']
    }

    public boolean isProvisionerTask() {
        return nodeName in ['Pending Approval from Entitlement Provisioner', 'Provisioner Task']
    }

    public boolean isDeprovisionerTask() {
        return nodeName in ['Entitlement Revoke Request', 'Deprovisioner Task']
    }

    public boolean isProvisionerOrDeprovisionerTask() {
        return (provisionerTask || deprovisionerTask)
    }
}