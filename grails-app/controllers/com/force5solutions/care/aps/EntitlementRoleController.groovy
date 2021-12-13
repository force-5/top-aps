package com.force5solutions.care.aps

import com.force5solutions.care.common.EntitlementStatus
import com.force5solutions.care.ldap.Permission
import com.force5solutions.care.common.Secured
import com.force5solutions.care.ldap.SecurityRole

class EntitlementRoleController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def entitlementRoleService
    def versioningService
    def utilService

    def index = {
        redirect(action: "list", params: params)
    }

    @Secured(value = Permission.READ_ENTITLEMENT_ROLE)
    def list = {
        Integer offset = params.offset ? params.offset.toInteger() : 0
        def maxRes = utilService.updateAndGetDefaultSizeOfListViewInConfig(params)
        String orderBy = params.order ?: 'asc'
        String sort = params.sort ?: 'name'

        def entitlementRoleCommand = session?.filterEntitlementRoleCommand
        List<EntitlementRole> entitlementRoleList = entitlementRoleService.getEntitlementRoles(sort, orderBy, entitlementRoleCommand)

        def entitlementRoleTotal = null
        if (!maxRes?.toString()?.equalsIgnoreCase('Unlimited')) {
            entitlementRoleTotal = entitlementRoleList.size()
            if (entitlementRoleTotal) {
                Integer lastIndex = offset + maxRes - 1
                if (lastIndex >= entitlementRoleTotal) {
                    lastIndex = entitlementRoleTotal - 1
                }
                entitlementRoleList = entitlementRoleList.getAt(offset..lastIndex)
            }
        }
        if (params?.ajax?.equalsIgnoreCase('true')) {
            render(template: 'entitlementRolesTable', model: [entitlementRoleList: entitlementRoleList, entitlementRoleTotal: entitlementRoleTotal, offset: offset, max: maxRes, order: orderBy, sort: sort])
        } else {
            [entitlementRoleList: entitlementRoleList, entitlementRoleTotal: entitlementRoleTotal, offset: offset, max: maxRes, order: orderBy, sort: sort]
        }
    }

    @Secured(value = Permission.CREATE_ENTITLEMENT_ROLE)
    def create = {
        def entitlementRole = new EntitlementRole()
        entitlementRole.properties = params
        return [entitlementRole: entitlementRole, statuses: EntitlementStatus.values(), remainingActiveRoles: EntitlementRole.listApproved()]
    }

    def save = {
        if (params.status) {
            params.status = EntitlementStatus."${params.status}"
        }
        def entitlementRole = new EntitlementRole()
        if (params.gatekeepers) {
            entitlementRole.gatekeepers = SecurityRole.getAll(params.list("gatekeepers"))
            params.remove("gatekeepers")
        }
        if (params.roles) {
            entitlementRole.roles = EntitlementRole.findAllByIdInList(params.list("roles"))
            params.remove("roles")
        }
        List<Entitlement> entitlements = []
        if (params.entitlementIds) {
            entitlements = Entitlement.findAllByIdInList(params.list("entitlementIds"))
            params.remove("entitlementIds")
        }

        entitlementRole.entitlements = entitlements
        entitlementRole.properties = params
        if (entitlementRoleService.save(entitlementRole)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'entitlementRole.label', default: 'Entitlement Role'), entitlementRole.name])}"
            redirect(action: "show", id: entitlementRole.id)
        } else {
            render(view: "create", model: [entitlementRole: entitlementRole, statuses: EntitlementStatus.values(), remainingActiveRoles: (EntitlementRole.listApproved() - entitlementRole.roles)])
        }
    }

    @Secured(value = Permission.READ_ENTITLEMENT_ROLE)
    def show = {
        def entitlementRole = EntitlementRole.findById(params.id)
        if (!entitlementRole) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'entitlementRole.label', default: 'Entitlement Role'), params.id])}"
            redirect(action: "list")
        } else {
            String gatekeepersString = entitlementRole.inheritedGatekeepers*.toString()?.join(", ")
            String standardsString = entitlementRole.inheritedStandards*.toString()?.join(", ")
            String entitlementPoliciesString = entitlementRole.inheritedEntitlementPolicies*.toString()?.join(", ")
            return [entitlementRole: entitlementRole, gatekeepersString: gatekeepersString, standardsString: standardsString, entitlementPoliciesString: entitlementPoliciesString]
        }
    }

    @Secured(value = Permission.UPDATE_ENTITLEMENT_ROLE)
    def edit = {
        EntitlementRole entitlementRole = EntitlementRole.findById(params.id, [fetch: [gatekeepers: 'join', entitlements: 'join', roles: 'join']])
        entitlementRole = versioningService.getCurrentObject(entitlementRole)
        if (!entitlementRole) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'entitlementRole.label', default: 'Entitlement Role'), params.id])}"
            redirect(action: "list")
        } else {
            return [entitlementRole: entitlementRole, statuses: EntitlementStatus.values(), remainingActiveRoles: (EntitlementRole.listApproved() - entitlementRole.roles)]
        }
    }

    @Secured(value = Permission.UPDATE_ENTITLEMENT_ROLE)
    def update = {
        if (params.status) {
            params.status = EntitlementStatus."${params.status}"
        }
        def entitlementRole = EntitlementRole.findById(params.id)
        if (entitlementRole) {
            if (params.version) {
                def version = params.version.toLong()
                if (entitlementRole.version > version) {

                    entitlementRole.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'entitlementRole.label', default: 'Entitlement Role')] as Object[], "Another user has updated this EntitlementRole while you were editing")
                    render(view: "edit", model: [entitlementRole: entitlementRole, statuses: EntitlementStatus.values(), remainingActiveRoles: (EntitlementRole.listApproved() - entitlementRole.roles)])
                    return
                }
            }
            if (params.gatekeepers) {
                entitlementRole.gatekeepers = SecurityRole.getAll(params.list("gatekeepers"))
                params.remove("gatekeepers")
            } else {
                entitlementRole.gatekeepers = []
            }
            if (params.roles) {
                entitlementRole.roles = EntitlementRole.findAllByIdInList(params.list("roles"))
                params.remove("roles")
            } else {
                entitlementRole.roles = []
            }

            Set<Entitlement> entitlements = []
            if (params.entitlementIds) {
                entitlements = Entitlement.findAllByIdInList(params.list("entitlementIds"))
                params.remove("entitlementIds")
                params.remove("entitlements")
            }
            entitlementRole.entitlements = entitlements as List
            entitlementRole.properties = params
            if (!entitlementRole.hasErrors() && entitlementRoleService.save(entitlementRole)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'entitlementRole.label', default: 'Entitlement Role'), entitlementRole.name])}"
                if (versioningService.hasPendingChanges(entitlementRole)) {
                    redirect(action: "showUnapprovedChanges", id: entitlementRole.id)
                } else {
                    redirect(action: "show", id: entitlementRole.id)
                }
            } else {
                render(view: "edit", model: [entitlementRole: entitlementRole, statuses: EntitlementStatus.values(), remainingActiveRoles: (EntitlementRole.listApproved() - entitlementRole.roles)])
            }
        } else {
            render(view: "edit", model: [entitlementRole: entitlementRole, statuses: EntitlementStatus.values(), remainingActiveRoles: (EntitlementRole.listApproved() - entitlementRole.roles)])
        }
    }

    @Secured(value = Permission.UPDATE_ENTITLEMENT_ROLE)
    def resubmitApprovalRequest = {
        EntitlementRole entitlementRole = EntitlementRole.findById(params.id)
        if (entitlementRole) {
            entitlementRoleService.triggerUpdateRoleWorkflow(entitlementRole)
        }
        redirect(action: unapprovedEntitlementRoles)
    }

    @Secured(value = Permission.DELETE_ENTITLEMENT_ROLE)
    def delete = {
        def entitlementRole = EntitlementRole.findById(params.id)
        String name = entitlementRole.name
        if (!entitlementRole?.deletable) {
            flash.message = "Can not delete Entitlement Role: ${name}. The role might be Active or there is a pending task involving the role in the Inbox."
            redirect(action: 'list')
        } else {
            if (entitlementRole && entitlementRoleService.delete(entitlementRole)) {
                try {
                    flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'entitlementRole.label', default: 'Entitlement Role'), name])}"
                    redirect(action: "list")
                }
                catch (org.springframework.dao.DataIntegrityViolationException e) {
                    flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'entitlementRole.label', default: 'Entitlement Role'), name])}"
                    redirect(action: "show", id: params.id)
                }
            } else {
                flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'entitlementRole.label', default: 'Entitlement Role'), name])}"
                redirect(action: "list")
            }
        }
    }

    def filterList = { EntitlementRoleCO entitlementRoleCO ->
        session.filterEntitlementRoleCommand = entitlementRoleCO
        redirect(action: 'list')
    }

    def filterDialog = {
        def entitlementRoleCommand = session.filterEntitlementRoleCommand

        List<EntitlementRole> roles = EntitlementRole.findAllByIsApproved(true).sort {
            it.toString().toLowerCase()
        }
        List<Gatekeeper> gatekeepers = Gatekeeper.list().sort {
            it.toString().toLowerCase()
        }
        List<Entitlement> entitlements = Entitlement.list().sort {
            it.toString().toLowerCase()
        }
        List<RoleOwner> owners = RoleOwner.list().sort {
            it.toString().toLowerCase()
        }
        render(template: 'entitlementRoleFilter',
                model: [entitlementRoleCO: entitlementRoleCommand, entitlements: entitlements, gatekeepers: gatekeepers, roles: roles, owners: owners])
    }

    def showAllEntitlementRole = {
        session.filterEntitlementRoleCommand = null
        redirect(action: 'list')
    }

    def unapprovedEntitlementRoles = {
        List<EntitlementRole> currentRoles = entitlementRoleService.getUnapprovedEntitlementRoles()
        currentRoles.each {
            it = versioningService.getCurrentObject(it)
        }
        render(view: 'unapprovedEntitlementRoles', model: [entitlementRoleList: currentRoles])
    }

    def showUnapprovedChanges = {
        EntitlementRole entitlementRole = EntitlementRole.findById(params.id, [fetch: [gatekeepers: 'join', entitlements: 'join', roles: 'join']])
        entitlementRole = versioningService.getCurrentObject(entitlementRole)
        [entitlementRole: entitlementRole]
    }
}
