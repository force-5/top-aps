package com.force5solutions.care.aps

import com.force5solutions.care.cc.EntitlementRoleAccessStatus
import com.force5solutions.care.cc.WorkerEntitlementRole
import com.force5solutions.care.common.EntitlementStatus
import com.force5solutions.care.workflow.ApsWorkflowTask
import groovy.time.TimeCategory
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.force5solutions.care.cc.EntitlementPolicy
import com.force5solutions.care.cc.Worker
import groovy.sql.Sql
import groovy.sql.GroovyRowResult
import com.force5solutions.care.feed.HrInfo
import com.force5solutions.care.workflow.WorkflowTaskStatus
import com.force5solutions.care.workflow.ApsWorkflowType

import java.text.SimpleDateFormat

class EntitlementService {

    def grailsApplication
    def versioningService
    def apsWorkflowUtilService

    static boolean transaction = false

    public List<Entitlement> getEntitlementsForWorkerFromFeed(Worker worker) {
        List<Entitlement> entitlements = []

        /*String url = ConfigurationHolder.config.feed.ppFeed.url
        String driver = ConfigurationHolder.config.feed.ppFeed.driver
        String query = ConfigurationHolder.config.feed.ppFeed.entitlementAccessForWorkerQuery

        HrInfo hrInfo = HrInfo.findBySlid(worker.slid?.toString())
        Long pernr = 0
        if (hrInfo) {
            pernr = hrInfo.pernr
        }
        if (pernr || worker.badgeNumber) {
            Sql sql = Sql.newInstance(url, driver)
            List<GroovyRowResult> rows = sql.rows(query, ['PERSONNEL_NUMBER': pernr, 'BADGE_NUMBER': worker.badgeNumber ?: 0])
            log.info "Rows: " + rows
            List<String> entitlementNames = []
            rows?.each { GroovyRowResult row ->
                entitlementNames.add(row.getProperty('CATEGORY') as String)
            }
            log.info "Entitlement Names: " + entitlementNames
            if (entitlementNames) {
                entitlements = Entitlement.findAllByNameInList(entitlementNames)
            }
        }*/
        log.info "Entitlements For Worker From Feed: " + entitlements
        return entitlements
    }

    public List<Entitlement> createPPEntitlements(List<String> timRoles, Origin origin) {
        String ppOwnerString = ConfigurationHolder.config.ppOwner
        String ppRoleTypeString = ConfigurationHolder.config.ppPolicy
        def entitlementsCreated = []

        RoleOwner ppOwner = RoleOwner.findBySlid(ppOwnerString)
        EntitlementPolicy entitlementPolicy = EntitlementPolicy.findByName(ppRoleTypeString)
        timRoles.each { String timRole ->
            if (!Entitlement.findByName(timRole)) {
                entitlementsCreated.add(createEntitlement(timRole, origin, ppOwner, entitlementPolicy))
            }
        }
        return entitlementsCreated
    }

    public List<Entitlement> createTimEntitlements(List<String> timRoles, Origin origin) {
        String timOwnerString = ConfigurationHolder.config.timOwner
        String timRoleTypeString = ConfigurationHolder.config.timRoleType
        def entitlementsCreated = []

        RoleOwner timOwner = RoleOwner.findByPerson(ApsPerson.findBySlid(timOwnerString))
        EntitlementPolicy entitlementPolicy = EntitlementPolicy.findByName(timRoleTypeString)
        timRoles.each { String timRole ->
            if (!Entitlement.findByName(timRole)) {
                entitlementsCreated.add(createEntitlement(timRole, origin, timOwner, entitlementPolicy))
            }
        }
        return entitlementsCreated
    }

    public Entitlement createEntitlement(String entitlementName, Origin origin, RoleOwner owner, EntitlementPolicy entitlementPolicy) {
        Entitlement entitlement = new Entitlement(name: entitlementName, alias: entitlementName, origin: origin, owner: owner, type: entitlementPolicy.id, isApproved: true).s()
        log.info "Created entitlement : " + entitlement
        return entitlement
    }

    public Boolean saveEntitlement(Entitlement entitlement) {
        String type, description;
        if (entitlement.id) {
            type = "Update"
        } else {
            type = "Create"
        }
        versioningService.saveVersionableObject(entitlement, true)
        entitlement = Entitlement.findById(entitlement.id, [cache: false])
        if (type == "Create") {
            if (!entitlement) {
                entitlement?.errors?.allErrors?.each { log.error it }
                return false
            }
            apsWorkflowUtilService.startAddEntitlementWorkflow(entitlement.id)
        } else {
            if (versioningService.hasPendingChanges(entitlement)) {
                triggerUpdateEntitlementWorkflow(entitlement)
            }
        }
        if (entitlement.hasErrors()) {
            entitlement?.errors?.allErrors?.each { log.error it }
            return false
        }
        return true
    }

    public void triggerUpdateEntitlementWorkflow(Entitlement entitlement) {
        Integer alreadyPendingTaskCount = ApsWorkflowTask.createCriteria().count {
            eq('entitlementId', entitlement.id)
            ne('status', WorkflowTaskStatus.COMPLETE)
            inList('workflowType', [ApsWorkflowType.ADD_ENTITLEMENT, ApsWorkflowType.UPDATE_ENTITLEMENT])
        }
        if (!alreadyPendingTaskCount) {
            apsWorkflowUtilService.startUpdateEntitlementWorkflow(entitlement.id)
        }
    }


    public List<Entitlement> getEntitlements(String sort, String orderBy, EntitlementCO entitlementCommand) {
        def entitlements = Entitlement.createCriteria().list {
            and {
                if (entitlementCommand?.name) ilike("name", "%" + entitlementCommand.name + "%")
                if (entitlementCommand?.alias) ilike("alias", "%" + entitlementCommand.alias + "%")
                if (entitlementCommand?.notes) ilike("notes", "%" + entitlementCommand.notes + "%")
                if (entitlementCommand?.entitlementPolicy) eq("type", entitlementCommand.entitlementPolicy)
                if (entitlementCommand?.origin) {
                    origin {
                        eq("id", entitlementCommand.origin)
                    }
                }
                if (entitlementCommand?.status) {
                    eq("status", EntitlementStatus.(entitlementCommand.status))
                }
                if (entitlementCommand?.provisioner) {
                    provisioners {
                        eq("id", entitlementCommand.provisioner)
                    }
                }
                if (entitlementCommand?.deProvisioner) {
                    deProvisioners {
                        eq("id", entitlementCommand.deProvisioner)
                    }
                }
                if (entitlementCommand?.gatekeeper) {
                    gatekeepers {
                        eq("id", entitlementCommand.gatekeeper)
                    }
                }
                if (entitlementCommand?.owner) {
                    owner {
                        eq("id", entitlementCommand.owner)
                    }
                }
                eq("isApproved", true)
            }
            if (sort == 'origin') {
                origin {
                    order("name", orderBy)
                }
            } else if (sort == 'entitlementPolicy') {
                type {
                    order("name", orderBy)
                }
            } else {
                order(sort, orderBy)
            }
            order("id", orderBy)
        }
        return entitlements
    }


    public List<Entitlement> getUnapprovedEntitlements() {
        List<Entitlement> unapprovedEntitlements = []
        Entitlement.list([fetch: [gatekeepers: 'join', provisioners: 'join', deProvisioners: 'join', customPropertyValues: 'join']]).each { entitlement ->
            if (!entitlement.isApproved) {
                unapprovedEntitlements << entitlement
            } else if (entitlement.isApproved && (versioningService.hasPendingChanges(entitlement))) {
                unapprovedEntitlements << entitlement
            }
        }
        unapprovedEntitlements
    }

    public Set<Entitlement> getActiveEntitlementsForWorker(Worker worker) {
        def activeRoles = []
        if (worker.entitlementRoles) {
            def activeCcEntitlementRoles = worker.activeEntitlementRoles*.entitlementRole
            def activeRoleIds = activeCcEntitlementRoles ? activeCcEntitlementRoles*.id : []
            activeRoles = EntitlementRole.findAllByIdInList(activeRoleIds.flatten())
        }
        Set activeEntitlements = (activeRoles ? activeRoles*.allEntitlements.flatten() : [])
        return activeEntitlements
    }

    public List<Worker> getActiveWorkersWithEntitlement(Entitlement entitlement) {
        List<Worker> workers = []
        List activeWorkerEntitlementRoles = WorkerEntitlementRole.findAllByStatus(EntitlementRoleAccessStatus.ACTIVE)
        activeWorkerEntitlementRoles.each { WorkerEntitlementRole workerEntitlementRole ->
            EntitlementRole entitlementRole = EntitlementRole.findById(workerEntitlementRole.entitlementRole.id)
            if (entitlementRole && entitlementRole?.allEntitlements?.contains(entitlement)) {
                workers.add(workerEntitlementRole.worker)
            }
        }
        workers = workers.unique()
        return workers
    }

    public List<Entitlement> getAllEntitlementsWithSharedAccountTrue() {
        return Entitlement.list().findAll { it.hasSharedAccountAttributeTrue() || it.hasGenericAccountAttributeTrue() }
    }

    public List<Entitlement> getAllSharedOrGenericAccountEntitlementsRequiringAPasswordChange() {
        List<Entitlement> entitlements = allEntitlementsWithSharedAccountTrue
        List<Entitlement> filteredEntitlements = []
        String lastPassWordChangeDate
        Date currentDate = new Date()
        entitlements.each { Entitlement entitlement ->
            lastPassWordChangeDate = entitlement.lastPasswordChangeAttributeValue
            if ((lastPassWordChangeDate && isAdjustedDateLessThanCurrentDate(lastPassWordChangeDate, currentDate)) || isAdjustedDateLessThanCurrentDate(entitlement.lastUpdated.format('MM/dd/yyyy'), currentDate) || isAdjustedDateLessThanCurrentDate(entitlement.dateCreated.format('MM/dd/yyyy'), currentDate)) {
                filteredEntitlements.add(entitlement)
            }
        }
        return filteredEntitlements
    }

    boolean isAdjustedDateLessThanCurrentDate(String lastPasswordChangeDate, Date currentDate) {
        boolean passwordChangeRequired = false
        int passwordChangeTimeoutPeriodInMonths = (grailsApplication.config.accountPasswordChangeTimeoutPeriodInMonths ?: '9') as int
        use(TimeCategory) {
            passwordChangeRequired = ((new Date().parse('MM/dd/yyyy', lastPasswordChangeDate) + passwordChangeTimeoutPeriodInMonths.months) as Date) < currentDate
        }
        return passwordChangeRequired
    }

    void setLastPasswordChangeAttribute(List<Entitlement> entitlements, String actionDateString) {
        Date actionDate = actionDateString ? new SimpleDateFormat('MM/dd/yyyy hh:mm a').parse(actionDateString) : new Date()
        actionDateString = actionDate.format('MM/dd/yyyy')
        entitlements.each { Entitlement entitlement ->
            entitlement.saveLastPasswordChangeAttribute(actionDateString)
        }
    }
}
