package com.force5solutions.care.aps

import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class RoleOwnerController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def employeeUtilService
    def utilService

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        Integer roleOwnerTotal = null
        Integer offset = params.offset ? params.offset.toInteger() : 0
        List<RoleOwner> roleOwnerList = []
        params.max = utilService.updateAndGetDefaultSizeOfListViewInConfig(params)
        params.order = params.order ?: 'asc'
        params.sort = params.sort ?: "firstName"
        if (params.sort == 'name') {
            roleOwnerList = RoleOwner.list().sort {it.person.name}
            if (!(params.order == 'asc')) {
                roleOwnerList = roleOwnerList.reverse()
            }
        } else {
            roleOwnerList = RoleOwner.createCriteria().list {
                person {
                    order(params.sort.toString(), params.order)

                }
            }
        }
        if (!params?.max?.toString()?.equalsIgnoreCase('Unlimited')) {
            roleOwnerTotal = roleOwnerList.size()
            if (roleOwnerTotal) {
                Integer lastIndex = offset + params.max - 1
                if (lastIndex >= roleOwnerTotal) {
                    lastIndex = roleOwnerTotal - 1
                }
                roleOwnerList = roleOwnerList.getAt(offset..lastIndex)
            }
        }
        if (params?.ajax?.equalsIgnoreCase('true')) {
            render template: 'roleOwnersTable', model: [roleOwnerList: roleOwnerList, roleOwnerTotal: roleOwnerTotal, offset: offset, max: params.max, order: params.order, sort: params.sort]
        } else {
            [roleOwnerList: roleOwnerList, roleOwnerTotal: roleOwnerTotal, offset: offset, max: params.max, order: params.order, sort: params.sort]
        }
    }

    def create = {
        def roleOwner = new RoleOwner()
        roleOwner.properties = params
        return [roleOwner: roleOwner]
    }

    def save = {
        def roleOwner = new RoleOwner()
        roleOwner.properties = params
        ApsPerson person = ApsPerson.findBySlid(params.person.slid)
        if (!person) {
            if (ConfigurationHolder.config.isEmployeeEditable == 'true') {
                person = new ApsPerson()
            } else {
                person = employeeUtilService.findPerson(params.person.slid)
                if (!person) {
                    render(view: "create", model: [roleOwner: roleOwner, fail: 'true'])
                    return false
                }
            }
        }
        roleOwner.person = person
        roleOwner.properties = params
        if (roleOwner.person.s() && roleOwner.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'roleOwner.label', default: 'Role Owner'), roleOwner.name])}"
            redirect(action: "show", id: roleOwner.id)
        }
        else {
            render(view: "create", model: [roleOwner: roleOwner])
        }
    }

    def show = {
        def roleOwner = RoleOwner.get(params.id)
        if (!roleOwner) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'roleOwner.label', default: 'Role Owner'), params.id])}"
            redirect(action: "list")
        }
        else {
            [roleOwner: roleOwner]
        }
    }

    def edit = {
        def roleOwner = RoleOwner.get(params.id)
        if (!roleOwner) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'roleOwner.label', default: 'Role Owner'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [roleOwner: roleOwner]
        }
    }

    def update = {
        def roleOwner = RoleOwner.get(params.id)
        roleOwner.properties = params

        if (roleOwner) {
            if (params.version) {
                def version = params.version.toLong()
                if (roleOwner.version > version) {

                    roleOwner.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'roleOwner.label', default: 'Role Owner')] as Object[], "Another user has updated this RoleOwner while you were editing")
                    render(view: "edit", model: [roleOwner: roleOwner])
                    return
                }
            }
            ApsPerson person = ApsPerson.findBySlid(params.person.slid)
            if (!person) {
                if (ConfigurationHolder.config.isEmployeeEditable == 'true') {
                    person = new ApsPerson()
                } else {
                    person = employeeUtilService.findPerson(params.person.slid)
                    if (!person) {
                        render(view: "edit", model: [roleOwner: roleOwner])
                        return false
                    }
                }
            }
            roleOwner.person = person
            roleOwner.properties = params
            if (roleOwner.person.s() && !roleOwner.hasErrors() && roleOwner.s()) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'roleOwner.label', default: 'Role Owner'), roleOwner.name])}"
                redirect(action: "show", id: roleOwner.id)
            }
            else {
                render(view: "edit", model: [roleOwner: roleOwner])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'roleOwner.label', default: 'Role Owner'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def roleOwner = RoleOwner.get(params.id)
        if (roleOwner) {
            try {
                roleOwner.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'roleOwner.label', default: 'Role Owner'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'roleOwner.label', default: 'Role Owner'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'roleOwner.label', default: 'RoleOwner'), params.id])}"
            redirect(action: "list")
        }
    }

    def checkSlid = {
        ApsPerson person = employeeUtilService.findPerson(params.slid)
        if (person) {
            render person as JSON
        } else {
            render "{'fail':'true'}"
        }
    }
}
