package com.force5solutions.care.aps

import com.force5solutions.care.common.EntitlementStatus
import com.force5solutions.care.cc.Worker
import com.force5solutions.care.cc.WorkerEntitlementRole
import com.force5solutions.care.ldap.SecurityRole
import com.force5solutions.care.workflow.ApsWorkflowTask
import com.force5solutions.care.cc.EntitlementPolicy
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.force5solutions.care.workflow.WorkflowTaskStatus
import com.force5solutions.care.cc.Employee
import com.force5solutions.care.workflow.ApsWorkflowType
import com.force5solutions.care.cc.CcEntitlementRole
import com.force5solutions.care.cc.EntitlementRoleAccessStatus
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class EntitlementRole {

    def entitlementService

    static requireApprovalObjects = ['entitlements', 'roles', 'status', 'name', 'sortedPolicyTypes']

    String id
    String name
    Boolean isExposed = false
    Boolean isPropagated = false
    Boolean isApproved = false
    EntitlementStatus status = EntitlementStatus.INACTIVE
    String notes
    RoleOwner owner
    Date dateCreated
    Date lastUpdated
    List<Entitlement> entitlements = []
    List<EntitlementRole> roles = []
    List<SecurityRole> gatekeepers = []
    String tags
    String sortedPolicyTypes

    static hasMany = [gatekeepers: SecurityRole, entitlements: Entitlement, roles: EntitlementRole]

    static transients = ['rolesThatRequireApproval', 'deletable']

    static mapping = {
        id generator: 'uuid'
    }

    static List<EntitlementRole> listApproved() {
        return EntitlementRole.findAllByIsApproved(true, [sort: 'name'])
    }

    static constraints = {
        name(unique: true)
        isExposed()
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
        notes(maxSize: 5000, nullable: true, blank: true)
        tags(nullable: true, blank: true)
        sortedPolicyTypes(nullable: true, blank: true)
        entitlements(nullable: true, blank: true, validator: { val, obj ->
            if (!(obj.entitlements || obj.roles)) {
                return "default.blank.message"
            }
        })
        roles(nullable: true, blank: true, validator: { val, obj ->
            if (!(obj.entitlements || obj.roles)) {
                return "default.blank.message"
            }
        })
    }

    def beforeUpdate() {
        sortedPolicyTypes = populateSortedEntitlementPolicyTypes()
        checkAndAssignDefaultGatekeeper()
    }

    def beforeInsert() {
        sortedPolicyTypes = populateSortedEntitlementPolicyTypes()
        checkAndAssignDefaultGatekeeper()
    }

    def checkAndAssignDefaultGatekeeper() {
        if (!(gatekeepers || inheritedGatekeepers)) {
            SecurityRole gateKeeperSecurityRole = ConfigurationHolder.config.defaultEntitlementRoleGatekeeperSecurityGroup ? SecurityRole.findByName(ConfigurationHolder.config.defaultEntitlementRoleGatekeeperSecurityGroup.toString()) : SecurityRole.list().first()
            gatekeepers = [gateKeeperSecurityRole]
        }
    }

    public String populateSortedEntitlementPolicyTypes() {
        return allEntitlements*.type.unique().sort().toString()
    }

    String toString() {
        return name
    }

    boolean equals(o) {
        if (this.is(o)) return true;
        if (!(o.instanceOf(EntitlementRole.class))) return false;
        EntitlementRole pg = (EntitlementRole) o;
        return (this.ident() == pg.ident())
    }

    Set<EntitlementRole> getRolesThatRequireApproval() {
        Set<EntitlementRole> requiredRoles = []
        if (gatekeepers) {
            requiredRoles.add(this)
        } else {
            roles.each {
                requiredRoles.addAll(it.rolesThatRequireApproval)
            }
        }
        return requiredRoles
    }

    public Set<Entitlement> getEntitlementsThatRequireApprovalForWorker(WorkerEntitlementRole workerEntitlementRole, boolean isRevocationWorkflow = false, ApsWorkflowType apsWorkflowType) {
        Worker worker = workerEntitlementRole.worker
        Set<Entitlement> requiredEntitlements = entitlementsThatRequireApproval

        if (!isRevocationWorkflow) {
            Set<Entitlement> activeEntitlements = entitlementService.getActiveEntitlementsForWorker(worker)
            if (activeEntitlements) {
                requiredEntitlements.removeAll(activeEntitlements)
            }
        }
        List<ApsWorkflowTask> entitlementTasks = ApsWorkflowTask.findAllByEntitlementIdInListAndStatusNotEqual(requiredEntitlements*.id, WorkflowTaskStatus.COMPLETE)
        entitlementTasks = entitlementTasks.findAll { it.workflowType.equals(apsWorkflowType) }
        if (entitlementTasks) {
            entitlementTasks = entitlementTasks.findAll { it.worker == worker }
            requiredEntitlements = requiredEntitlements.findAll { !(it in entitlementTasks*.entitlement) }
        }
        return requiredEntitlements
    }

    Set<Entitlement> getEntitlementsForProvisioning(WorkerEntitlementRole workerEntitlementRole) {
        Set<Entitlement> provisioningEntitlements = allEntitlements
        Worker worker = workerEntitlementRole.worker
        provisioningEntitlements.removeAll(entitlementService.getActiveEntitlementsForWorker(worker))
        def entitlementService = ApplicationHolder.getApplication().getMainContext().getBean('entitlementService')
        List<Entitlement> alreadyApprovedFeedEntitlements = (worker instanceof Employee) ? entitlementService.getEntitlementsForWorkerFromFeed(worker) : []
        provisioningEntitlements = provisioningEntitlements.findAll { !(it in alreadyApprovedFeedEntitlements) }
        return provisioningEntitlements
    }

    Set<Entitlement> getEntitlementsForRevocation(WorkerEntitlementRole workerEntitlementRole) {
        Set<Entitlement> entitlementsToBeRevoked = allEntitlements
        Worker worker = workerEntitlementRole.worker
        Set<Entitlement> activeEntitlements = entitlementService.getActiveEntitlementsForWorker(worker)
        if (activeEntitlements) {
            entitlementsToBeRevoked.removeAll(activeEntitlements)
        }
        def entitlementService = ApplicationHolder.getApplication().getMainContext().getBean('entitlementService')
        List<Entitlement> alreadyApprovedFeedEntitlements = (worker instanceof Employee) ? entitlementService.getEntitlementsForWorkerFromFeed(worker) : []
        entitlementsToBeRevoked = entitlementsToBeRevoked.findAll { !(it in alreadyApprovedFeedEntitlements) }
        return entitlementsToBeRevoked
    }

    Set<Entitlement> getEntitlementsThatRequireApproval() {
        Set<Entitlement> requiredEntitlements = []
        if (!gatekeepers) {
            entitlements.each { Entitlement entitlement ->
                if (entitlement.gatekeepers) {
                    requiredEntitlements.add(entitlement)
                }
            }
            roles.each { EntitlementRole role ->
                requiredEntitlements.addAll(role.entitlementsThatRequireApproval)
            }
        }
        return requiredEntitlements
    }

    Set<SecurityRole> getInheritedGatekeepers() {
        Set<SecurityRole> inheritedGatekeepers = []
        entitlements?.each { Entitlement entitlement ->
            entitlement?.gatekeepers?.each { gatekeeper ->
                if (gatekeeper instanceof SecurityRole) {
                    inheritedGatekeepers.add(gatekeeper)
                }
            }
        }
        roles.each { EntitlementRole role ->
            role.gatekeepers?.each { gatekeeper ->
                if (gatekeeper instanceof SecurityRole) {
                    inheritedGatekeepers.add(gatekeeper)
                }
            }
            role.entitlements?.each { Entitlement entitlement ->
                entitlement.gatekeepers?.each { gatekeeper ->
                    if (gatekeeper instanceof SecurityRole) {
                        inheritedGatekeepers.add(gatekeeper)
                    }
                }
            }
        }
        return inheritedGatekeepers
    }

    Set<Entitlement> getAllEntitlements() {
        Set<Entitlement> subEntitlements = []
        subEntitlements.addAll(entitlements)
        roles.each {
            subEntitlements.addAll(it.getAllEntitlements())
        }
        subEntitlements = (subEntitlements.findAll { it } as Set)
        return subEntitlements
    }

    Set<Entitlement> getInheritedEntitlements() {
        Set<Entitlement> myInheritedEntitlements = []
        roles.each {
            myInheritedEntitlements.addAll(it.getInheritedEntitlements())
        }
        return (myInheritedEntitlements.findAll { it } as Set)
    }

    Set<String> getInheritedStandards() {
        Set<String> inheritedStandards = []
        Set<EntitlementPolicy> types = inheritedEntitlementPolicies
        if (types) {
            inheritedStandards = (types*.standards)?.flatten() as Set
        }
        return inheritedStandards
    }

    Set<EntitlementPolicy> getInheritedEntitlementPolicies() {
        Set<EntitlementPolicy> inheritedEntitlementPolicies = []
        entitlements?.each { Entitlement entitlement ->
            inheritedEntitlementPolicies?.add(EntitlementPolicy?.get(entitlement?.type))
        }
        roles.each { EntitlementRole role ->
            role.entitlements?.each { Entitlement entitlement ->
                inheritedEntitlementPolicies?.add(EntitlementPolicy?.get(entitlement?.type))
            }
        }
        return inheritedEntitlementPolicies
    }

    static void approveEntitlementRole(String entitlementRoleId) {
        EntitlementRole entitlementRole = EntitlementRole.findById(entitlementRoleId)
        entitlementRole.isApproved = true
        def entitlementRoleService = ApplicationHolder.getApplication().getMainContext().getBean('entitlementRoleService')
        entitlementRoleService.save(entitlementRole)
    }

    static void updateEntitlementRole(String entitlementRoleId) {
        EntitlementRole entitlementRole = EntitlementRole.findById(entitlementRoleId)
        def versioningService = ApplicationHolder.application.mainContext.getBean('versioningService')
        versioningService.approvePendingChanges(entitlementRole)
    }

    public boolean hasPendingAddOrUpdateTask() {
        ApsWorkflowTask.createCriteria().list {
            inList('workflowType', [ApsWorkflowType.ADD_ROLE, ApsWorkflowType.UPDATE_ROLE])
            eq('status', WorkflowTaskStatus.NEW)
            eq('entitlementRoleId', id)
        }.size() ? true : false
    }

    public boolean isDeletable() {
        boolean canBeDeleted
        if (hasPendingAddOrUpdateTask()) {
            canBeDeleted = false
        } else {
            String ccEntitlementRoleId = this.id
            CcEntitlementRole ccEntitlementRole = CcEntitlementRole.findById(ccEntitlementRoleId)
            WorkerEntitlementRole workerEntitlementRole = WorkerEntitlementRole.findByEntitlementRoleAndStatusInList(ccEntitlementRole, EntitlementRoleAccessStatus.statusesToBeCheckedWhileDeletionOfRoles)
            canBeDeleted = workerEntitlementRole ? false : true
        }
        return canBeDeleted
    }
}
