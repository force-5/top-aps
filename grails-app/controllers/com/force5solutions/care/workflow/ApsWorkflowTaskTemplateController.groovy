package com.force5solutions.care.workflow

import com.force5solutions.care.aps.ApsApplicationRole
import com.force5solutions.care.cc.PeriodUnit

class ApsWorkflowTaskTemplateController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def utilService

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        Integer offset = params.offset ? params.offset.toInteger() : 0
        def maxRes = utilService.updateAndGetDefaultSizeOfListViewInConfig(params)
        def order = params.order ?: 'asc'
        def sort = params.sort ?: 'id'
        List<ApsWorkflowTaskTemplate> apsWorkflowTaskTemplateList = ApsWorkflowTaskTemplate.list([order: order, sort: sort])
        List<ApsWorkflowTaskTemplate> filteredTemplateList = []

        apsWorkflowTaskTemplateList.each {
            if (!(it in (apsWorkflowTaskTemplateList*.escalationTemplate))) {
                filteredTemplateList.add(it)
            }
        }
        filteredTemplateList.remove {it.id.equalsIngoreCase("INITIAL_TASK_TEMPLATE")}
        filteredTemplateList.removeAll {
            (it?.actorApplicationRoles?.size() == 0) &&
                    (it?.toNotificationApplicationRoles?.size() == 0) &&
                    (it?.ccNotificationApplicationRoles?.size() == 0) &&
                    (it?.actorSlids?.size() == 0)
        }

        def filteredTemplateListCount = null
        if (!maxRes?.toString()?.equalsIgnoreCase('Unlimited')) {
            filteredTemplateListCount = filteredTemplateList.size()
            if (filteredTemplateListCount) {
                Integer lastIndex = offset + maxRes - 1
                if (lastIndex >= filteredTemplateListCount) {
                    lastIndex = filteredTemplateListCount - 1
                }
                filteredTemplateList = filteredTemplateList.getAt(offset..lastIndex)
            }
        }
        if (params?.ajax?.equalsIgnoreCase('true')) {
            render(template: 'apsWorkflowTaskTemplatesTable', model: [filteredTemplateList: filteredTemplateList, filteredTemplateListCount: filteredTemplateListCount, offset: offset, max: maxRes, order: order, sort: sort])
        } else {
            [filteredTemplateList: filteredTemplateList, filteredTemplateListCount: filteredTemplateListCount, offset: offset, max: maxRes, order: order, sort: sort]
        }
    }

    def listAll = {
        render(view: 'list', model: [filteredTemplateList: ApsWorkflowTaskTemplate.list()])
    }

    def create = {
        def apsWorkflowTaskTemplate = new ApsWorkflowTaskTemplate()
        apsWorkflowTaskTemplate.properties = params
        return [apsWorkflowTaskTemplate: apsWorkflowTaskTemplate]
    }

    def save = {
        def apsWorkflowTaskTemplate = new ApsWorkflowTaskTemplate()
        bindData(apsWorkflowTaskTemplate, params, ['actorApplicationRoles', 'toNotificationApplicationRoles', 'ccNotificationApplicationRoles', 'periodUnit'])

        if (params.actorApplicationRoles) {
            List<String> roles = (params.actorApplicationRoles as String)?.replace('[', '')?.replace(']', '')?.tokenize(',')*.trim()*.toString()
            roles = roles.findAll {it.length()}
            roles.each {
                ApsApplicationRole apsApplicationRole = ApsApplicationRole.get(it)
                apsWorkflowTaskTemplate.addToActorApplicationRoles(apsApplicationRole)
            }
        }

        if (params.toNotificationApplicationRoles) {
            List<String> roles = (params.toNotificationApplicationRoles as String)?.replace('[', '')?.replace(']', '')?.tokenize(',')*.trim()*.toString()
            roles = roles.findAll {it.length()}
            roles.each {
                ApsApplicationRole apsApplicationRole = ApsApplicationRole.get(it)
                apsWorkflowTaskTemplate.addToToNotificationApplicationRoles(apsApplicationRole)
            }
        }

        if (params.ccNotificationApplicationRoles) {
            List<String> roles = (params.ccNotificationApplicationRoles as String)?.replace('[', '')?.replace(']', '')?.tokenize(',')*.trim()*.toString()
            roles = roles.findAll {it.length()}
            roles.each {
                ApsApplicationRole apsApplicationRole = ApsApplicationRole.get(it)
                apsWorkflowTaskTemplate.addToCcNotificationApplicationRoles(apsApplicationRole)
            }
        }

        if (params.periodUnit) {
            List<String> periodUnits = (params.periodUnit as String)?.replace('[', '')?.replace(']', '')?.tokenize(',')*.trim()*.toString()
            periodUnits.each {
                PeriodUnit unit = PeriodUnit.get(it)
                apsWorkflowTaskTemplate.periodUnit = unit
            }
        }

        apsWorkflowTaskTemplate.id = params.templateName

        if (apsWorkflowTaskTemplate.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'apsWorkflowTaskTemplate.label', default: 'ApsWorkflowTaskTemplate'), apsWorkflowTaskTemplate.id])}"
            redirect(action: "show", id: apsWorkflowTaskTemplate.id)
        }
        else {
            render(view: "create", model: [apsWorkflowTaskTemplate: apsWorkflowTaskTemplate])
        }
    }

    def show = {
        def apsWorkflowTaskTemplate = ApsWorkflowTaskTemplate.get(params.id)
        if (!apsWorkflowTaskTemplate) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'apsWorkflowTaskTemplate.label', default: 'ApsWorkflowTaskTemplate'), params.id])}"
            redirect(action: "list")
        }
        else {
            [apsWorkflowTaskTemplate: apsWorkflowTaskTemplate]
        }
    }

    def edit = {
        def apsWorkflowTaskTemplate = ApsWorkflowTaskTemplate.findById(params.id)
        if (!apsWorkflowTaskTemplate) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'apsWorkflowTaskTemplate.label', default: 'ApsWorkflowTaskTemplate'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [apsWorkflowTaskTemplate: apsWorkflowTaskTemplate]
        }
    }

    def update = {
        def apsWorkflowTaskTemplate = ApsWorkflowTaskTemplate.findById(params.templateName)
        if (apsWorkflowTaskTemplate) {
            if (params.version) {
                def version = params.version.toLong()
                if (apsWorkflowTaskTemplate.version > version) {

                    apsWorkflowTaskTemplate.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'apsWorkflowTaskTemplate.label', default: 'ApsWorkflowTaskTemplate')] as Object[], "Another user has updated this ApsWorkflowTaskTemplate while you were editing")
                    render(view: "edit", model: [apsWorkflowTaskTemplate: apsWorkflowTaskTemplate])
                    return
                }
            }

            apsWorkflowTaskTemplate.actorSecurityRoles.clear()
            bindData(apsWorkflowTaskTemplate, params, ['actorApplicationRoles', 'toNotificationApplicationRoles', 'ccNotificationApplicationRoles', 'periodUnit'])
            apsWorkflowTaskTemplate.actorApplicationRoles.clear()
            apsWorkflowTaskTemplate.toNotificationApplicationRoles.clear()
            apsWorkflowTaskTemplate.ccNotificationApplicationRoles.clear()

            if (params.actorApplicationRoles) {
                List<String> roles = (params.actorApplicationRoles as String)?.replace('[', '')?.replace(']', '')?.tokenize(',')*.trim()*.toString()
                roles = roles.findAll {it.length()}
                roles.each {
                    ApsApplicationRole apsApplicationRole = ApsApplicationRole.get(it)
                    apsWorkflowTaskTemplate.addToActorApplicationRoles(apsApplicationRole)
                }
            }

            if (params.toNotificationApplicationRoles) {
                List<String> roles = (params.toNotificationApplicationRoles as String)?.replace('[', '')?.replace(']', '')?.tokenize(',')*.trim()*.toString()
                roles = roles.findAll {it.length()}
                roles.each {
                    ApsApplicationRole apsApplicationRole = ApsApplicationRole.get(it)
                    apsWorkflowTaskTemplate.addToToNotificationApplicationRoles(apsApplicationRole)
                }
            }

            if (params.ccNotificationApplicationRoles) {
                List<String> roles = (params.ccNotificationApplicationRoles as String)?.replace('[', '')?.replace(']', '')?.tokenize(',')*.trim()*.toString()
                roles = roles.findAll {it.length()}
                roles.each {
                    ApsApplicationRole apsApplicationRole = ApsApplicationRole.get(it)
                    apsWorkflowTaskTemplate.addToCcNotificationApplicationRoles(apsApplicationRole)
                }
            }

            if (params.periodUnit) {
                List<String> periodUnits = (params.periodUnit as String)?.replace('[', '')?.replace(']', '')?.tokenize(',')*.trim()*.toString()
                periodUnits.each {
                    PeriodUnit unit = PeriodUnit.get(it)
                    apsWorkflowTaskTemplate.periodUnit = unit
                }
            }

            apsWorkflowTaskTemplate.id = params.templateName

            if (!apsWorkflowTaskTemplate.hasErrors() && apsWorkflowTaskTemplate.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'apsWorkflowTaskTemplate.label', default: 'ApsWorkflowTaskTemplate'), apsWorkflowTaskTemplate.id])}"
                redirect(action: "show", id: apsWorkflowTaskTemplate.id)
            }
            else {
                render(view: "edit", model: [apsWorkflowTaskTemplate: apsWorkflowTaskTemplate])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'apsWorkflowTaskTemplate.label', default: 'ApsWorkflowTaskTemplate'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def apsWorkflowTaskTemplate = ApsWorkflowTaskTemplate.get(params.id)
        if (apsWorkflowTaskTemplate) {
            try {
                apsWorkflowTaskTemplate.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'apsWorkflowTaskTemplate.label', default: 'ApsWorkflowTaskTemplate'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'apsWorkflowTaskTemplate.label', default: 'ApsWorkflowTaskTemplate'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'apsWorkflowTaskTemplate.label', default: 'ApsWorkflowTaskTemplate'), params.id])}"
            redirect(action: "list")
        }
    }

    def cloneTemplate = {
        def apsWorkflowTaskTemplate = ApsWorkflowTaskTemplate.findById(params.id)
        ApsWorkflowTaskTemplate clonedApsWorkflowTaskTemplate = new ApsWorkflowTaskTemplate()
        clonedApsWorkflowTaskTemplate.properties = apsWorkflowTaskTemplate.properties
        clonedApsWorkflowTaskTemplate.id = "CLONE_OF_" + apsWorkflowTaskTemplate.id
        render(view: 'create', model: [apsWorkflowTaskTemplate: clonedApsWorkflowTaskTemplate])
    }
}
