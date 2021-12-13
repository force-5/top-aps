package com.force5solutions.care

import com.force5solutions.care.common.CareConstants
import com.force5solutions.care.cp.ConfigProperty
import com.force5solutions.care.ldap.SecurityRole
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.force5solutions.care.aps.EntitlementRole
import com.force5solutions.care.cc.WorkerEntitlementRole
import com.force5solutions.care.workflow.ApsWorkflowTask
import com.force5solutions.care.workflow.WorkflowTaskStatus
import com.force5solutions.care.cc.WorkerEntitlementArchive
import com.force5solutions.care.cc.EntitlementPolicy
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsAnnotationConfiguration
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor
import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventListener
import com.force5solutions.care.workflow.ApsWorkflowTaskType
import com.force5solutions.care.common.EntitlementStatus
import com.force5solutions.care.workflow.WorkflowTask
import com.force5solutions.care.workflow.CentralWorkflowTask
import com.force5solutions.care.workflow.CentralWorkflowTaskType
import com.force5solutions.care.aps.Entitlement
import org.grails.plugins.versionable.VersioningContext
import com.force5solutions.care.workflow.ApsWorkflowUtilService
import org.springframework.web.context.request.RequestContextHolder

class UtilService {

    boolean transactional = true

    def versioningService
    def entitlementRoleService
    def fixtureLoader

    public static String getCareCentralUrl() {
        return ConfigurationHolder.config.careCentral.webService.url
    }

    static Set<String> getEntitlementsToProvideAccess(Long workerEntitlementRoleId) {
        Set<String> entitlementIds = []
        WorkerEntitlementRole workerEntitlementRole = WorkerEntitlementRole.get(workerEntitlementRoleId)
        EntitlementRole role = EntitlementRole.findById(workerEntitlementRole.entitlementRole.id)
        entitlementIds = role.getEntitlementsForProvisioning(workerEntitlementRole)*.id as Set
        return entitlementIds
    }

    static Set<String> getEntitlementsToRevokeAccess(Long workerEntitlementRoleId) {
        Set<String> entitlementIds = []
        WorkerEntitlementRole workerEntitlementRole = WorkerEntitlementRole.get(workerEntitlementRoleId)
        EntitlementRole role = EntitlementRole.findById(workerEntitlementRole.entitlementRole.id)
        entitlementIds = role.getEntitlementsForRevocation(workerEntitlementRole)*.id as Set
        return entitlementIds
    }

    def updateAndGetDefaultSizeOfListViewInConfig(Map params) {
        def defaultSize = null
        def session = RequestContextHolder?.currentRequestAttributes()?.getSession()
        if (session && !session.rowCount) {
            session.rowCount = ConfigurationHolder?.config?.defaultSizeOfListView
            defaultSize = session.rowCount.toString().equalsIgnoreCase('Unlimited') ? session.rowCount.toString() : session.rowCount.toInteger()
        } else {
            if (params.rowCount && !params?.rowCount?.toString()?.equalsIgnoreCase('Unlimited')) {
                session.rowCount = params?.rowCount?.toInteger()
                defaultSize = session.rowCount
            } else if (params?.max && !params?.max?.toString()?.equalsIgnoreCase('Unlimited')) {
                session.rowCount = params.max.toInteger()
                defaultSize = session.rowCount
            } else if (params?.rowCount?.toString()?.equalsIgnoreCase('Unlimited')) {
                session.rowCount = params?.rowCount
                defaultSize = session.rowCount
            } else {
                defaultSize = session.rowCount ? (session.rowCount?.toString()?.equalsIgnoreCase('Unlimited') ? session.rowCount : session.rowCount?.toInteger()) : 10
            }
        }
        return defaultSize
    }

    void preloadInitialValuesInWorkerEntitlementArchiveFromApsWorkflowTask() {
        populateTheApproximateActionDatesInApsWorkflowTask()
        deleteWorkerEntitlementArchiveRecords()
        populateWorkerEntitlementArchiveRecordsFromApsWorkflowTask()
    }

    private void populateWorkerEntitlementArchiveRecordsFromApsWorkflowTask() {
        List<WorkflowTask> workflowTaskList = ApsWorkflowTask.findAllByNodeNameInListAndStatus(['Pending Approval from Entitlement Provisioner', 'Entitlement Revoke Request', 'Provisioner Task', 'Deprovisioner Task'], WorkflowTaskStatus.COMPLETE)
        workflowTaskList.addAll(CentralWorkflowTask.findAllByNodeNameAndStatus("Access Granted By Feed", WorkflowTaskStatus.COMPLETE))
        workflowTaskList.addAll(CentralWorkflowTask.findAllByNodeNameAndStatus("Access Revoked By Feed", WorkflowTaskStatus.COMPLETE))
        workflowTaskList = workflowTaskList.findAll { it.type in [ApsWorkflowTaskType.HUMAN, CentralWorkflowTaskType.SYSTEM_CENTRAL] }.sort { it.lastUpdated }
        workflowTaskList.each { WorkflowTask workflowTask ->
            if (workflowTask.class.equals(ApsWorkflowTask.class)) {
                createWorkerEntitlementArchiveEntryWithOldTimeStamp(workflowTask)
            } else {
                createWorkerEntitlementArchiveEntryFromFeedTask(workflowTask)
            }
        }
    }

    private void createWorkerEntitlementArchiveEntryFromFeedTask(WorkflowTask workflowTask) {
        EntitlementRole entitlementRole = EntitlementRole.findById((workflowTask as CentralWorkflowTask).workerEntitlementRole.entitlementRole.id)
        entitlementRole.allEntitlements*.id
        entitlementRole.roles*.id
        entitlementRole = versioningService.getObjectOnDate(entitlementRole, workflowTask.effectiveStartDate) as EntitlementRole
        Set<Entitlement> entitlements = entitlementRole.allEntitlements
        entitlements.each { Entitlement entitlement ->
            createWorkerEntitlementArchiveEntryWithOldTimeStamp(workflowTask, entitlement)
        }
    }

    private void createWorkerEntitlementArchiveEntryWithOldTimeStamp(WorkflowTask workflowTask, Entitlement entitlement = null) {
        changeTimeStamping(new WorkerEntitlementArchive(), false)
        WorkerEntitlementArchive workerEntitlementArchive = new WorkerEntitlementArchive()
        workerEntitlementArchive.workerId = workflowTask?.workerEntitlementRole?.worker?.id
        if (workflowTask instanceof ApsWorkflowTask) {
            workerEntitlementArchive.apsWorkflowTaskId = workflowTask.id
        } else if (workflowTask instanceof CentralWorkflowTask) {
            workerEntitlementArchive.centralWorkflowTaskId = workflowTask.id
        }
        workerEntitlementArchive.entitlementId = entitlement ? entitlement.id : workflowTask?.entitlement?.id
        workerEntitlementArchive.workerFirstName = workflowTask?.workerEntitlementRole?.worker?.firstName
        workerEntitlementArchive.workerMiddleName = workflowTask?.workerEntitlementRole?.worker?.middleName
        workerEntitlementArchive.workerLastName = workflowTask?.workerEntitlementRole?.worker?.lastName
        workerEntitlementArchive.workerSlid = workflowTask?.workerEntitlementRole?.worker?.slid
        workerEntitlementArchive.entitlementName = entitlement ? entitlement.name : workflowTask?.entitlement?.name
        workerEntitlementArchive.entitlementAlias = entitlement ? entitlement.alias : workflowTask?.entitlement?.alias
        workerEntitlementArchive.userResponse = workflowTask?.response ?: 'CONFIRM'
        workerEntitlementArchive.entitlementOrigin = entitlement ? entitlement.origin.name : workflowTask?.entitlement?.origin?.name
        workerEntitlementArchive.entitlementPolicyType = EntitlementPolicy.get(entitlement ? entitlement.type : workflowTask?.entitlement?.type)?.name
        workerEntitlementArchive.notes = workflowTask?.message
        if (workflowTask?.nodeName?.equalsIgnoreCase('Access Granted By Feed') || workflowTask.isProvisionerTask()) {
            workerEntitlementArchive.entitlementProvisionerSlid = workflowTask?.actorSlid
            workerEntitlementArchive.actionType = CareConstants.ACCESS_REQUEST
        } else if (workflowTask?.nodeName?.equalsIgnoreCase('Access Revoked By Feed') || workflowTask.isDeprovisionerTask()) {
            workerEntitlementArchive.entitlementDeProvisionerSlid = workflowTask?.actorSlid
            workerEntitlementArchive.actionType = CareConstants.REVOKE_REQUEST
        }
        workerEntitlementArchive.actionDate = workflowTask?.actionDate ?: workflowTask?.effectiveStartDate
        workerEntitlementArchive.dateCreated = workflowTask?.lastUpdated
        workerEntitlementArchive.lastUpdated = workflowTask?.lastUpdated
        workerEntitlementArchive.s()
        changeTimeStamping(new WorkerEntitlementArchive(), true)
    }

    private void changeTimeStamping(Object domainObjectInstance, boolean shouldTimestamp) {
        GrailsAnnotationConfiguration configuration = ApplicationHolder.getApplication().getMainContext().getBean("&sessionFactory").configuration
        ClosureEventTriggeringInterceptor interceptor = configuration.getEventListeners().saveOrUpdateEventListeners[0]
        ClosureEventListener listener = interceptor.findEventListener(domainObjectInstance)
        listener.shouldTimestamp = shouldTimestamp
    }

    private void deleteWorkerEntitlementArchiveRecords() {
        WorkerEntitlementArchive.executeUpdate('delete from WorkerEntitlementArchive')
    }

    private void populateTheApproximateActionDatesInApsWorkflowTask() {
        List<ApsWorkflowTask> apsWorkflowTaskList = ApsWorkflowTask.findAllByNodeNameInListAndStatus(['Pending Approval from Entitlement Provisioner', 'Entitlement Revoke Request', 'Provisioner Task', 'Deprovisioner Task'], WorkflowTaskStatus.COMPLETE)
        Map<String, List<ApsWorkflowTask>> tasksGroupedByGuid = apsWorkflowTaskList ? apsWorkflowTaskList.groupBy { it.workflowGuid } : [:]
        tasksGroupedByGuid.each { workflowGuid, tasks ->
            if (tasks.any { it.actionDate }) {
                Date actionDate = tasks?.max { it?.actionDate }?.actionDate
                tasks.each { ApsWorkflowTask task ->
                    if (!task.actionDate) {
                        task.actionDate = actionDate
                    }
                }
            } else {
                tasks.each { ApsWorkflowTask task ->
                    task.actionDate = task.lastUpdated
                }
            }
            tasks*.save()
        }
    }

    //TODO: Remove the else part later; We can also remove this utility and te corresponding once it is run on prod.
    public void populateSortedPolicyTypeForRoles() {
        EntitlementRole.findAllByStatus(EntitlementStatus.ACTIVE).each { EntitlementRole entitlementRole ->
            if (entitlementRole.entitlements || entitlementRole.roles) {
                entitlementRole.sortedPolicyTypes = entitlementRole.populateSortedEntitlementPolicyTypes()
                entitlementRoleService.save(entitlementRole)
                sleep(1000)
                if (entitlementRole) {
                    autoApproveRoleApsWorkflowTask(entitlementRole)
                }
            } else {
                println "Skipping the role: " + entitlementRole
            }
        }
    }

    private void autoApproveRoleApsWorkflowTask(EntitlementRole role) {
        List<ApsWorkflowTask> apsWorkflowTasks = ApsWorkflowTask.findAllByEntitlementRoleIdAndNodeName(role?.id, "Get approval from Role Owner")
        apsWorkflowTasks = apsWorkflowTasks.findAll { it.status.equals(WorkflowTaskStatus.NEW) }
        if (apsWorkflowTasks) {
            VersioningContext.set(UUID.randomUUID().toString())
            Map responseElements = ['accessJustification': 'Approved by system during update of sortedPolicyTypes of the role', 'userAction': 'APPROVE']
            ApsWorkflowUtilService.sendResponseElements(apsWorkflowTasks.last(), responseElements)
        }
    }

    public void fixNodeNameOfInitialTaskForProvisionerDeprovisionerTasksOnRoleUpdate() {
        List<ApsWorkflowTask> apsWorkflowTaskList = ApsWorkflowTask.findAllByTypeAndNodeName(ApsWorkflowTaskType.SYSTEM_APS, 'Provisioner Task')
        apsWorkflowTaskList.each { ApsWorkflowTask apsWorkflowTask ->
            apsWorkflowTask.nodeName = "Initial Task"
            apsWorkflowTask.s()
        }
    }

    public void createConfigPropertiesForCategoryFileFeedServices() {
        createOrUpdateConfigProperty("updateEntitlementFeedExceptionWorkflow", "flows/update-entitlement-feed-exception.rf")
        createOrUpdateConfigProperty("createEntitlementFeedExceptionWorkflow", "flows/create-entitlement-feed-exception.rf")
        createOrUpdateConfigProperty("ppOrigin", "Picture Perfect Feed")
        createOrUpdateConfigProperty("isInitialLoadOfEntitlementsFromFileFeedService", "false")
        createOrUpdateConfigProperty("ppPolicy", "Physical", "ppRoleType")
        fixtureLoader.load 'apsWorkflowTaskTemplates'
    }

    void createOrUpdateConfigProperty(String newKey, String value, String oldKey = null) {
        ConfigProperty configProperty = !oldKey ? (ConfigProperty.findByName(newKey) ?: new ConfigProperty()) : (ConfigProperty.findByName(oldKey) ?: new ConfigProperty())
        configProperty.name = newKey
        configProperty.value = value
        configProperty.s()
    }

    void createCronTriggerConfigPropertyForCategoryJobs() {
        createOrUpdateConfigProperty("categoryAreReaderFileFeedServiceCronTrigger", "0 0 0 1 1 ? 2050")
        createOrUpdateConfigProperty("categoryWorkerFileFeedServiceCronTrigger", "0 0 0 1 1 ? 2050")
    }

    def createRunDatabaseFeedConfigPropertiesForJobs() {
        createOrUpdateConfigProperty("runDatabaseFeedForCategoryAreaReader", "false")
        createOrUpdateConfigProperty("runDatabaseFeedForCategoryWorker", "false")
    }

    def createConfigPropertiesForAccountPasswordChange() {
        createOrUpdateConfigProperty("accountPasswordChangeTrigger", "0 0 0 1 1 ? 2050", "sharedAccountPasswordChangeTrigger")
        createOrUpdateConfigProperty("accountPasswordChangeWorkflow", "flows/account-password-change.rf", 'sharedAccountPasswordChangeWorkflow')
        createOrUpdateConfigProperty("accountPasswordChangeTimeoutPeriodInMonths", "9", "sharedAccountPasswordChangeTimeoutPeriodInMonths")
    }

    def createConfigPropertiesForAutoCompleteGenericAndSharedEntitlementApsWorkflowTaskJobTrigger() {
        createOrUpdateConfigProperty("autoCompleteGenericAndSharedEntitlementApsWorkflowTaskJobTrigger", "0/30 * * * * ? *")
    }

    def createAnnualResetDateForAccountPasswordChangeConfigProperty() {
        createOrUpdateConfigProperty("annualResetDateForAccountPasswordChange", "10/08/2013")
    }

    def createDefaultEntitlementRoleGatekeeperSecurityGroupConfigProperty() {
        createOrUpdateConfigProperty("defaultEntitlementRoleGatekeeperSecurityGroup", "GATEKEEPER-G1")
    }

    def assignDefaultGatekeeperToRoles() {
        SecurityRole securityRole = SecurityRole.findByName(ConfigurationHolder.config.defaultEntitlementRoleGatekeeperSecurityGroup)
        if (securityRole) {
            EntitlementRole.list().each { EntitlementRole entitlementRole ->
                if (!entitlementRole.gatekeepers) {
                    entitlementRole.gatekeepers.add(securityRole)
                    entitlementRole.s()
                    println "${securityRole} added as gatekeeper to role: ${entitlementRole} "
                }
            }
        }
    }
}
