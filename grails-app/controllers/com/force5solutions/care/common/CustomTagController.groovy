package com.force5solutions.care.common

class CustomTagController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def utilService

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        List<CustomTag> customTagList = []
        Integer customTagTotal = null
        Integer offset = params.offset ? params.offset.toInteger() : 0
        params.order = params.order ?: 'asc'
        params.sort = params.sort ?: 'name'
        params.max = utilService.updateAndGetDefaultSizeOfListViewInConfig(params)
        if (!params?.max?.toString()?.equalsIgnoreCase('Unlimited')) {
            customTagList = CustomTag.list(params)
            customTagTotal = CustomTag.count()
        } else {
            customTagList = CustomTag.list()
        }
        if (params?.ajax?.equalsIgnoreCase('true')) {
            render template: 'customTagsTable', model: [customTagList: customTagList, customTagTotal: customTagTotal, offset: offset, max: params.max, order: params.order, sort: params.sort]
        } else {
            [customTagList: customTagList, customTagTotal: customTagTotal, offset: offset, max: params.max, order: params.order, sort: params.sort]
        }
    }

    def create = {
        def customTag = new CustomTag()
        customTag.properties = params
        return [customTag: customTag]
    }

    def save = {
        def customTag = new CustomTag(params)
        if (customTag.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'customTag.label', default: 'CustomTag'), customTag.id])}"
            redirect(action: "show", id: customTag.id)
        }
        else {
            render(view: "create", model: [customTag: customTag])
        }
    }

    def show = {
        def customTag = CustomTag.get(params.id)
        if (!customTag) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'customTag.label', default: 'CustomTag'), params.id])}"
            redirect(action: "list")
        }
        else {
            [customTag: customTag]
        }
    }

    def edit = {
        def customTag = CustomTag.get(params.id)
        if (!customTag) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'customTag.label', default: 'CustomTag'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [customTag: customTag]
        }
    }

    def update = {
        def customTag = CustomTag.get(params.id)
        if (customTag) {
            if (params.version) {
                def version = params.version.toLong()
                if (customTag.version > version) {

                    customTag.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'customTag.label', default: 'CustomTag')] as Object[], "Another user has updated this CustomTag while you were editing")
                    render(view: "edit", model: [customTag: customTag])
                    return
                }
            }
            customTag.properties = params
            if (!customTag.hasErrors() && customTag.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'customTag.label', default: 'CustomTag'), customTag.id])}"
                redirect(action: "show", id: customTag.id)
            }
            else {
                render(view: "edit", model: [customTag: customTag])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'customTag.label', default: 'CustomTag'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def customTag = CustomTag.get(params.id)
        if (customTag) {
            try {
                customTag.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'customTag.label', default: 'CustomTag'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'customTag.label', default: 'CustomTag'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'customTag.label', default: 'CustomTag'), params.id])}"
            redirect(action: "list")
        }
    }
}
