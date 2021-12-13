package com.force5solutions.care.aps

import com.force5solutions.care.ldap.Permission
import com.force5solutions.care.common.Secured

class OriginController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def utilService

    def index = {
        redirect(action: "list", params: params)
    }

    @Secured(value = Permission.READ_ORIGIN)
    def list = {
        List<Origin> originList = []
        Integer originTotal = null
        Integer offset = params.offset ? params.offset.toInteger() : 0
        params.order = params.order ?: 'asc'
        params.sort = params.sort ?: 'name'
        params.max = utilService.updateAndGetDefaultSizeOfListViewInConfig(params)
        if (!params?.max?.toString()?.equalsIgnoreCase('Unlimited')) {
            originList = Origin.list(params)
            originTotal = Origin.count()
        } else {
            originList = Origin.list()
        }
        if (params?.ajax?.equalsIgnoreCase('true')) {
            render template: 'originsTable', model: [originList: originList, originTotal: originTotal, offset: offset, max: params.max, order: params.order, sort: params.sort]
        } else {
            [originList: originList, originTotal: originTotal, offset: offset, max: params.max, order: params.order, sort: params.sort]
        }
    }

    @Secured(value = Permission.CREATE_ORIGIN)
    def create = {
        def origin = new Origin()
        origin.properties = params
        return [origin: origin]
    }

    def save = {
        def origin = new Origin(params)
        if (origin.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'origin.label', default: 'Origin'), origin.name])}"
            redirect(action: "show", id: origin.id)
        }
        else {
            render(view: "create", model: [origin: origin])
        }
    }

    @Secured(value = Permission.READ_ORIGIN)
    def show = {
        def origin = Origin.get(params.id)
        if (!origin) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'origin.label', default: 'Origin'), params.id])}"
            redirect(action: "list")
        }
        else {
            [origin: origin]
        }
    }

    @Secured(value = Permission.UPDATE_ORIGIN)
    def edit = {
        def origin = Origin.get(params.id)
        if (!origin) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'origin.label', default: 'Origin'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [origin: origin]
        }
    }

    @Secured(value = Permission.UPDATE_ORIGIN)
    def update = {
        def origin = Origin.get(params.id)
        if (origin) {
            if (params.version) {
                def version = params.version.toLong()
                if (origin.version > version) {

                    origin.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'origin.label', default: 'Origin')] as Object[], "Another user has updated this Origin while you were editing")
                    render(view: "edit", model: [origin: origin])
                    return
                }
            }
            origin.properties = params
            if (!origin.hasErrors() && origin.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'origin.label', default: 'Origin'), origin.name])}"
                redirect(action: "show", id: origin.id)
            }
            else {
                render(view: "edit", model: [origin: origin])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'origin.label', default: 'Origin'), params.id])}"
            redirect(action: "list")
        }
    }

    @Secured(value = Permission.DELETE_ORIGIN)
    def delete = {
        def origin = Origin.get(params.id)
        String name = origin.name
        if (origin) {
            try {
                origin.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'origin.label', default: 'Origin'), name])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'origin.label', default: 'Origin'), name])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'origin.label', default: 'Origin'), name])}"
            redirect(action: "list")
        }
    }
}
