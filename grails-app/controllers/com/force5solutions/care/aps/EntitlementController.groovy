package com.force5solutions.care.aps

import com.force5solutions.care.common.EntitlementStatus
import com.force5solutions.care.common.CustomPropertyType

import com.force5solutions.care.cc.EntitlementPolicy
import com.force5solutions.care.cc.CcCustomPropertyValue
import com.force5solutions.care.cc.CcCustomProperty
import com.force5solutions.care.ldap.SecurityRole

import static com.force5solutions.care.aps.Origin.*
import com.force5solutions.care.ldap.Permission
import com.force5solutions.care.common.Secured

class EntitlementController {

    def entitlementService
    def versioningService
    def utilService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    @Secured(value = Permission.READ_ENTITLEMENT)
    def list = {
        Integer offset = params.offset ? params.offset.toInteger() : 0
        def maxRes = utilService.updateAndGetDefaultSizeOfListViewInConfig(params)
        String orderBy = params.order ?: 'asc'
        String sort = params.sort ?: 'name'

        def entitlementCommand = session?.filterEntitlementCommand
        List<Entitlement> entitlementList = entitlementService.getEntitlements(sort, orderBy, entitlementCommand)

        def entitlementTotal = null
        if (!maxRes?.toString()?.equalsIgnoreCase('Unlimited')) {
            entitlementTotal = entitlementList.size()
            if (entitlementTotal) {
                Integer lastIndex = offset + maxRes - 1
                if (lastIndex >= entitlementTotal) {
                    lastIndex = entitlementTotal - 1
                }
                entitlementList = entitlementList.getAt(offset..lastIndex)
            }
        }
        if (params?.ajax?.equalsIgnoreCase('true')) {
            render(template: 'entitlementsTable', model: [entitlementList: entitlementList, entitlementTotal: entitlementTotal, offset: offset, max: maxRes, order: orderBy, sort: sort])
        } else {
            [entitlementList: entitlementList, entitlementTotal: entitlementTotal, offset: offset, max: maxRes, order: orderBy, sort: sort]
        }
    }

    @Secured(value = Permission.CREATE_ENTITLEMENT)
    def create = {
        def entitlement = new Entitlement()
        entitlement.properties = params
        return [entitlement: entitlement, statuses: EntitlementStatus.values(), remainingActiveTypes: EntitlementPolicy.listApproved()]
    }

    def save = {
        if (params.status) {
            params.status = EntitlementStatus."${params.status}"
        }
        def entitlement = new Entitlement(origin: Origin.findByName('Manual'))
        if (params.gatekeepers) {
            entitlement.gatekeepers = SecurityRole.getAll(params.list("gatekeepers"))
            params.remove("gatekeepers")
        }
        if (params.customPropertyId) {
            Long propertyId
            Map customPropertyMap = params.findAll { key, value ->
                (key.startsWith("customPropertyValue")) && (!(key.contains("_")) || (key.contains("_value"))) && (value != 'struct')
            }
            def keys = customPropertyMap.keySet()?.findAll { it.contains("_value") }
            customPropertyMap = customPropertyMap.findAll { !((it.key + "_value") in keys) }

            customPropertyMap.each { String key, value ->
                String t = key - "customPropertyValue";
                def x = t?.split('_') as List
                propertyId = x.first().toLong()
                CcCustomPropertyValue propertyValue = new CcCustomPropertyValue()
                CcCustomProperty customProperty = CcCustomProperty.get(propertyId)
                propertyValue.value = value
                propertyValue.customProperty = customProperty
                entitlement.addToCustomPropertyValues(propertyValue)
            }
        }
        params.remove("customPropertyId")
        List<CcCustomPropertyValue> customPropertyValues = entitlement.customPropertyValues as List
        List<CcCustomProperty> missingValueCustomProperties = []
        customPropertyValues.each {
            if ((it.customProperty.isRequired) && !(it.value)) {
                missingValueCustomProperties.add(it.customProperty)
            }
            if ((it.customProperty.propertyType == CustomPropertyType.NUMBER) && !(it.value.isNumber()) && (it.value)) {
                missingValueCustomProperties.add(it.customProperty)
            }
            if ((it.customProperty.size) && (it.customProperty.propertyType == CustomPropertyType.NUMBER) && (it.value.isNumber()) && (it.value) && (it.customProperty.size.toLong() < it.value.toLong())) {
                missingValueCustomProperties.add(it.customProperty)
            }
        }

        if (params.list('attributeIds')) {
            List<Long> entitlementAttributeIds = params.list('attributeIds')
            entitlementAttributeIds.each {
                EntitlementAttribute entitlementAttribute = EntitlementAttribute.get(it)
                entitlementAttribute.keyName = params.get('updateAttributeKey-' + it)
                entitlementAttribute.value = params.get('updateAttributeValue-' + it)
                entitlementAttribute.save()
            }
        }

        if (params.keySet().any { it.toString().startsWith("newAttributeKey-") }) {
            Map newAttributeKeys = params.findAll { it.key.toString().startsWith("newAttributeKey-") }
            newAttributeKeys.each { key, value ->
                EntitlementAttribute entitlementAttribute = new EntitlementAttribute()
                entitlementAttribute.keyName = value
                entitlementAttribute.value = params[key.toString().replace('Key', 'Value')]
                entitlement.addToEntitlementAttributes(entitlementAttribute)
            }
        }

        entitlement.properties = params
        if (!missingValueCustomProperties) {
            if (entitlementService.saveEntitlement(entitlement)) {
                flash.message = "${message(code: 'default.created.message', args: [message(code: 'entitlement.label', default: 'Entitlement'), entitlement.name])}"
                redirect(action: "show", id: entitlement.id)
            } else {
                render(view: "create", model: [entitlement: entitlement, statuses: EntitlementStatus.values(), missingCustomProperties: missingValueCustomProperties, remainingActiveTypes: (EntitlementPolicy.listApproved() - entitlement.type)])
            }
        } else {
            render(view: "create", model: [entitlement: entitlement, statuses: EntitlementStatus.values(), missingCustomProperties: missingValueCustomProperties, remainingActiveTypes: (EntitlementPolicy.listApproved() - entitlement.type)])

        }
    }

    @Secured(value = Permission.READ_ENTITLEMENT)
    def show = {
        def entitlement = Entitlement.findById(params.id)
        if (!entitlement) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'entitlement.label', default: 'Entitlement'), params.id])}"
            redirect(action: "list")
        } else {
            [entitlement: entitlement]
        }
    }

    @Secured(value = Permission.UPDATE_ENTITLEMENT)
    def edit = {
        Entitlement entitlement = Entitlement.findById(params.id, [fetch: [gatekeepers: 'join', provisioners: 'join', deProvisioners: 'join', customPropertyValues: 'join', entitlementAttributes: 'join']])
        entitlement = versioningService.getCurrentObject(entitlement)
        if (entitlement.origin.name == PICTURE_PERFECT_FEED) {
            flash.message = "Cannot edit a Picture Perfect Feed Entitlement"
            redirect(action: "list")
        } else if (!entitlement) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'entitlement.label', default: 'Entitlement'), params.id])}"
            redirect(action: "list")
        } else {
            return [entitlement: entitlement, statuses: EntitlementStatus.values(), remainingActiveTypes: (EntitlementPolicy.listApproved() - entitlement.type)]
        }
    }

    @Secured(value = Permission.UPDATE_ENTITLEMENT)
    def update = {
        if (params.status) {
            params.status = EntitlementStatus."${params.status}"
        }
        def entitlement = Entitlement.findById(params.id)
        if (entitlement) {
            if (params.version) {
                def version = params.version.toLong()
                if (entitlement.version > version) {

                    entitlement.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'entitlement.label', default: 'Entitlement')] as Object[], "Another user has updated this Entitlement while you were editing")
                    render(view: "edit", model: [entitlement: entitlement, statuses: EntitlementStatus.values(), remainingActiveTypes: (EntitlementPolicy.listApproved() - entitlement.type)])
                    return
                }
            }
            if (params.gatekeepers) {
                entitlement.gatekeepers = SecurityRole.getAll(params.list("gatekeepers"))
                params.remove("gatekeepers")
            } else {
                entitlement.gatekeepers = []
            }
            if (params.provisioners) {
                entitlement.provisioners = SecurityRole.getAll(params.list("provisioners"))
                params.remove("provisioners")
            } else {
                entitlement.provisioners = []
            }
            if (params.deProvisioners) {
                entitlement.deProvisioners = SecurityRole.getAll(params.list("deProvisioners"))
                params.remove("deProvisioners")
            } else {
                entitlement.deProvisioners = []
            }
            List<CcCustomPropertyValue> propertiesToRemove = entitlement.customPropertyValues as List
            entitlement.customPropertyValues = []
            propertiesToRemove.each { it.delete() }

            if (params.customPropertyId) {
                Long propertyId
                Map customPropertyMap = params.findAll { key, value ->
                    (key.startsWith("customPropertyValue")) && (!(key.contains("_")) || (key.contains("_value"))) && (value != 'struct')
                }
                def keys = customPropertyMap.keySet()?.findAll { it.contains("_value") }
                customPropertyMap = customPropertyMap.findAll { !((it.key + "_value") in keys) }

                customPropertyMap.each { String key, value ->
                    String t = key - "customPropertyValue";
                    def x = t?.split('_') as List
                    propertyId = x.first().toLong()
                    CcCustomPropertyValue propertyValue = new CcCustomPropertyValue()
                    CcCustomProperty customProperty = CcCustomProperty.get(propertyId)
                    propertyValue.value = value
                    propertyValue.customProperty = customProperty
                    entitlement.addToCustomPropertyValues(propertyValue)
                }
            }
            params.remove("customPropertyId")
            List<CcCustomPropertyValue> customPropertyValues = entitlement.customPropertyValues as List
            List<CcCustomProperty> missingValueCustomProperties = []
            customPropertyValues.each {
                if ((it.customProperty.isRequired) && !(it.value)) {
                    missingValueCustomProperties.add(it.customProperty)
                }
                if ((it.customProperty.propertyType == CustomPropertyType.NUMBER) && !(it.value.isNumber()) && (it.value)) {
                    missingValueCustomProperties.add(it.customProperty)
                }
                if ((it.customProperty.size) && (it.customProperty.propertyType == CustomPropertyType.NUMBER) && (it.value.isNumber()) && (it.value) && (it.customProperty.size.toLong() < it.value.toLong())) {
                    missingValueCustomProperties.add(it.customProperty)
                }
            }

            if (params.list('attributeIds')) {
                List<Long> entitlementAttributeIds = (params.list('attributeIds') as List)*.toLong()
                List<Long> entitlementAttributeIdsToBeRemoved = (entitlement.entitlementAttributes*.id)
                entitlementAttributeIdsToBeRemoved.removeAll(entitlementAttributeIds)
                entitlementAttributeIds.each {
                    EntitlementAttribute entitlementAttribute = EntitlementAttribute.get(it)
                    entitlementAttribute.keyName = params.get('updateAttributeKey-' + it)
                    entitlementAttribute.value = params.get('updateAttributeValue-' + it)
                    entitlementAttribute.save()
                }
                entitlementAttributeIdsToBeRemoved.each { Long id ->
                    EntitlementAttribute entitlementAttribute = entitlement.entitlementAttributes.find { it?.id == id }
                    entitlement.removeFromEntitlementAttributes(entitlementAttribute)
                    entitlementAttribute.delete()
                }
            } else {
                entitlement.entitlementAttributes.clear()
                entitlement.entitlementAttributes*.delete()
                entitlement.save(flush: true)
            }

            if (params.keySet().any { it.toString().startsWith("newAttributeKey-") }) {
                Map newAttributeKeys = params.findAll { it.key.toString().startsWith("newAttributeKey-") }
                newAttributeKeys.each { key, value ->
                    EntitlementAttribute entitlementAttribute = new EntitlementAttribute()
                    entitlementAttribute.keyName = value
                    entitlementAttribute.value = params[key.toString().replace('Key', 'Value')]
                    entitlement.addToEntitlementAttributes(entitlementAttribute)
                }
            }

            entitlement.properties = params
            if (!missingValueCustomProperties) {

                if (!entitlement.hasErrors() && entitlementService.saveEntitlement(entitlement)) {
                    flash.message = "${message(code: 'default.updated.message', args: [message(code: 'entitlement.label', default: 'Entitlement'), entitlement.name])}"
                    if (versioningService.hasPendingChanges(entitlement)) {
                        redirect(action: "showUnapprovedChanges", id: entitlement.id)
                    } else {
                        redirect(action: "show", id: entitlement.id)
                    }
                } else {
                    render(view: "edit", model: [entitlement: entitlement, statuses: EntitlementStatus.values(), missingValueCustomProperties: missingValueCustomProperties, remainingActiveTypes: (EntitlementPolicy.listApproved() - entitlement.type)])
                }
            } else {
                render(view: "edit", model: [entitlement: entitlement, statuses: EntitlementStatus.values(), missingValueCustomProperties: missingValueCustomProperties, remainingActiveTypes: (EntitlementPolicy.listApproved() - entitlement.type)])
            }
        } else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'entitlement.label', default: 'Entitlement'), params.id])}"
            redirect(action: "list")
        }
    }

    @Secured(value = Permission.UPDATE_ENTITLEMENT)
    def resubmitApprovalRequest = {
        Entitlement entitlement = Entitlement.findById(params.id)
        if (entitlement) {
            entitlementService.triggerUpdateEntitlementWorkflow(entitlement)
        }
        redirect(action: unapprovedEntitlements)
    }

    @Secured(value = Permission.DELETE_ENTITLEMENT)
    def delete = {
        def entitlement = Entitlement.findById(params.id)
        String name = entitlement.name
        if (entitlement?.hasPendingAddOrUpdateTask()) {
            flash.message = "Can not delete Entitlement: ${name}. There is a pending ADD/UPDATE task in the Inbox."
            redirect(action: 'list')
        } else {
            if (entitlement) {
//            if (EntitlementAccess.countByEntitlement(entitlement)) {
//                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'entitlement.label', default: 'Entitlement'), params.id])}"
//                redirect(action: "show", id: params.id)
//                return
//            }
                try {
                    entitlement.delete(flush: true)
                    flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'entitlement.label', default: 'Entitlement'), name])}"
                    redirect(action: "list")
                }
                catch (org.springframework.dao.DataIntegrityViolationException e) {
                    flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'entitlement.label', default: 'Entitlement'), name])}"
                    redirect(action: "show", id: params.id)
                }
            } else {
                flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'entitlement.label', default: 'Entitlement'), name])}"
                redirect(action: "list")
            }
        }
    }

    def filterList = { EntitlementCO entitlementCO ->
        session.filterEntitlementCommand = entitlementCO
        redirect(action: 'list')
    }

    def filterDialog = {
        def entitlementCommand = session.filterEntitlementCommand
        List<Gatekeeper> gatekeepers = Gatekeeper.list().sort {
            it.toString().toLowerCase()
        }
        List<Origin> origins = Origin.list().sort {
            it.toString().toLowerCase()
        }
        List<Provisioner> provisioners = Provisioner.list().sort {
            it.toString().toLowerCase()
        }
        List<DeProvisioner> deProvisioners = DeProvisioner.list().sort {
            it.toString().toLowerCase()
        }
        List<EntitlementPolicy> entitlementPolicies = EntitlementPolicy.findAllByIsApproved(true).sort {
            it.toString().toLowerCase()
        }
        List<RoleOwner> owners = RoleOwner.list().sort {
            it.toString().toLowerCase()
        }
        render(template: 'entitlementFilter',
                model: [entitlementCO: entitlementCommand, gatekeepers: gatekeepers, provisioners: provisioners, origins: origins, entitlementPolicies: entitlementPolicies, owners: owners, deProvisioners: deProvisioners])
    }

    def showAllEntitlement = {
        session.filterEntitlementCommand = null
        redirect(action: 'list')
    }

    def unapprovedEntitlements = {
        List<Entitlement> currentEntitlements = entitlementService.getUnapprovedEntitlements()
        currentEntitlements.each {
            it = versioningService.getCurrentObject(it)
        }
        render(view: 'unapprovedEntitlements', model: [entitlementList: currentEntitlements])
    }

    def showUnapprovedChanges = {
        Entitlement entitlement = Entitlement.findById(params.id, [fetch: [gatekeepers: 'join', provisioners: 'join', deProvisioners: 'join', customPropertyValues: 'join']])
        entitlement = versioningService.getCurrentObject(entitlement)
        [entitlement: entitlement]
    }
}
