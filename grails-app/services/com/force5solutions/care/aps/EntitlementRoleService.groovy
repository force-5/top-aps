package com.force5solutions.care.aps

import com.force5solutions.care.common.EntitlementStatus

import com.force5solutions.care.workflow.ApsWorkflowTask
import com.force5solutions.care.workflow.WorkflowTaskStatus
import com.force5solutions.care.workflow.ApsWorkflowType

class EntitlementRoleService {

    def versioningService
    def apsWorkflowUtilService
    def careWebService
    static transaction = true

    public Boolean save(EntitlementRole role) {
        Date date = new Date()
        String type, description;
        if (role.id) {
            type = "Update"
        } else {
            type = "Create"
        }
        if (!role.validate() && role.hasErrors()) {
            role?.errors?.allErrors?.each { log.error it }
            return false
        }
        log.info "*****ER Accesses: " + role.entitlements
        versioningService.saveVersionableObject(role, true)
        if (type == "Create") {
            apsWorkflowUtilService.startAddRoleWorkflow(role.id)
        } else {
            if (versioningService.hasPendingChanges(role)) {
                triggerUpdateRoleWorkflow(role)
            }
        }
        return true
    }

    public void triggerUpdateRoleWorkflow(EntitlementRole role) {
        Integer alreadyPendingTaskCount = ApsWorkflowTask.createCriteria().count {
            eq('entitlementRoleId', role.id)
            ne('status', WorkflowTaskStatus.COMPLETE)
            inList('workflowType', [ApsWorkflowType.ADD_ROLE, ApsWorkflowType.UPDATE_ROLE])
        }
        if (!alreadyPendingTaskCount) {
            apsWorkflowUtilService.startUpdateRoleWorkflow(role.id)
        }
    }

    public List<EntitlementRole> getEntitlementRoles(String sort, String orderBy, EntitlementRoleCO entitlementRoleCommand) {
        def entitlementRoles = EntitlementRole.createCriteria().list {
            and {
                if (entitlementRoleCommand?.name) ilike("name", "%" + entitlementRoleCommand.name + "%")
                if (entitlementRoleCommand?.notes) ilike("notes", "%" + entitlementRoleCommand.notes + "%")
                if (entitlementRoleCommand?.status) eq("status", EntitlementStatus.(entitlementRoleCommand.status))
                if (entitlementRoleCommand?.isExposed != null) eq("isExposed", entitlementRoleCommand.isExposed)
                if (entitlementRoleCommand?.isPropagated != null) eq("isPropagated", entitlementRoleCommand.isPropagated)

                if (entitlementRoleCommand?.owner) {
                    owner {
                        eq("id", entitlementRoleCommand.owner)
                    }
                }
                if (entitlementRoleCommand?.role) {
                    roles {
                        eq("id", entitlementRoleCommand.role)
                    }
                }
                if (entitlementRoleCommand?.gatekeeper) {
                    gatekeepers {
                        eq("id", entitlementRoleCommand.gatekeeper)
                    }
                }
                if (entitlementRoleCommand?.entitlement) {
                    entitlements {
                        eq("id", entitlementRoleCommand.entitlement)
                    }
                }
                eq("isApproved", true)
            }
            order(sort, orderBy)
        }
        return entitlementRoles
    }

    public List<EntitlementRole> getUnapprovedEntitlementRoles() {
        List<EntitlementRole> unapprovedEntitlementRoles = []
        EntitlementRole.list([fetch: [gatekeepers: 'join', entitlements: 'join', roles: 'join']]).each { entitlementRole ->
            if (!entitlementRole.isApproved) {
                unapprovedEntitlementRoles << entitlementRole
            } else if (entitlementRole.isApproved && (versioningService.hasPendingChanges(entitlementRole))) {
                unapprovedEntitlementRoles << entitlementRole
            }
        }
        unapprovedEntitlementRoles
    }

    public void updateSortedPolicyTypesOfRolesContainingEntitlement(Entitlement entitlement) {
        List<EntitlementRole> entitlementRoleList = []
        EntitlementRole.findAllByStatus(EntitlementStatus.ACTIVE).each { EntitlementRole entitlementRole ->
            if (entitlementRole.allEntitlements.contains(entitlement)) {
                entitlementRoleList.add(entitlementRole)
            }
        }
        entitlementRoleList.each { EntitlementRole entitlementRole ->
            if (entitlementRole?.sortedPolicyTypes != entitlementRole.populateSortedEntitlementPolicyTypes()) {
                entitlementRole.sortedPolicyTypes = entitlementRole.populateSortedEntitlementPolicyTypes()
                save(entitlementRole)
            }
        }
    }

    public Boolean delete(EntitlementRole entitlementRole) {
        boolean status = false
        try {
            status = careWebService.markCcEntitlementRoleAsDeleted(entitlementRole.id)
            if (status) {
                entitlementRole.delete(flush: true)
            }
        } catch (Exception e) {
            e.printStackTrace()
            status = false
        }
        log.info "***** Deleting the Entitlment Role : " + entitlementRole
        return status
    }
}