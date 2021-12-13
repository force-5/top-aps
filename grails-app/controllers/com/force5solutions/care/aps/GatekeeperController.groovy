package com.force5solutions.care.aps

import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.force5solutions.care.ldap.Permission
import com.force5solutions.care.common.Secured

class GatekeeperController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def employeeUtilService
    def utilService

    def index = {
        redirect(action: "list", params: params)
    }

    @Secured(value = Permission.READ_GATEKEEPER)
    def list = {
        List<Gatekeeper> gatekeeperList = []
        Integer gatekeeperTotal = null
        Integer offset = params.offset ? params.offset.toInteger() : 0
        def orderBy = params.order ?: 'asc'
        def sort = params.sort ?: 'firstName'
        params.max = utilService.updateAndGetDefaultSizeOfListViewInConfig(params)
        if (sort == 'name') {
            gatekeeperList = Gatekeeper.list().sort {it.person.name}
            if (!(params.order == 'asc')) {
                gatekeeperList = gatekeeperList.reverse()
            }
        } else {
            gatekeeperList = Gatekeeper.createCriteria().list {
                person {
                    order(sort.toString(), orderBy)
                }
            }
        }
        if (!params?.max?.toString()?.equalsIgnoreCase('Unlimited')) {
            gatekeeperTotal = gatekeeperList.size()
            if (gatekeeperTotal) {
                Integer lastIndex = offset + params.max - 1
                if (lastIndex >= gatekeeperTotal) {
                    lastIndex = gatekeeperTotal - 1
                }
                gatekeeperList = gatekeeperList.getAt(offset..lastIndex)
            }
        }
        if (params?.ajax?.equalsIgnoreCase('true')) {
            render template: 'gatekeepersTable', model: [gatekeeperList: gatekeeperList, gatekeeperTotal: gatekeeperTotal, offset: offset, max: params.max, order: orderBy, sort: sort]
        } else {
            [gatekeeperList: gatekeeperList, gatekeeperTotal: gatekeeperTotal, offset: offset, max: params.max, order: orderBy, sort: sort]
        }
    }

    @Secured(value = Permission.CREATE_GATEKEEPER)
    def create = {
        def gatekeeper = new Gatekeeper()
        gatekeeper.properties = params
        return [gatekeeper: gatekeeper]
    }

    def save = {
        def gatekeeper = new Gatekeeper(params)
        gatekeeper.properties = params
        ApsPerson person = ApsPerson.findBySlid(params.person.slid)
        if (!person) {
            if (ConfigurationHolder.config.isEmployeeEditable == 'true') {
                person = new ApsPerson()
            } else {
                person = employeeUtilService.findPerson(params.person.slid)
                if (!person) {
                    render(view: "create", model: [gatekeeper: gatekeeper, fail: 'true'])
                    return false
                }
            }
        }
        gatekeeper.person = person
        gatekeeper.properties = params

        if (gatekeeper.person.s() && gatekeeper.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'gatekeeper.label', default: 'Gatekeeper'), gatekeeper.id])}"
            redirect(action: "show", id: gatekeeper.id)
        }
        else {
            render(view: "create", model: [gatekeeper: gatekeeper])
        }
    }

    @Secured(value = Permission.READ_GATEKEEPER)
    def show = {
        def gatekeeper = Gatekeeper.get(params.id)
        if (!gatekeeper) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'gatekeeper.label', default: 'Gatekeeper'), params.id])}"
            redirect(action: "list")
        }
        else {
            [gatekeeper: gatekeeper]
        }
    }

    @Secured(value = Permission.UPDATE_GATEKEEPER)
    def edit = {
        def gatekeeper = Gatekeeper.get(params.id)
        if (!gatekeeper) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'gatekeeper.label', default: 'Gatekeeper'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [gatekeeper: gatekeeper]
        }
    }

    @Secured(value = Permission.UPDATE_GATEKEEPER)
    def update = {
        def gatekeeper = Gatekeeper.get(params.id)
        gatekeeper.properties = params

        if (gatekeeper) {
            if (params.version) {
                def version = params.version.toLong()
                if (gatekeeper.version > version) {

                    gatekeeper.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'gatekeeper.label', default: 'Gatekeeper')] as Object[], "Another user has updated this Gatekeeper while you were editing")
                    render(view: "edit", model: [gatekeeper: gatekeeper])
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
                        render(view: "edit", model: [gatekeeper: gatekeeper])
                        return false
                    }
                }
            }
            gatekeeper.person = person
            gatekeeper.properties = params

            if (gatekeeper.person.s() && !gatekeeper.hasErrors() && gatekeeper.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'gatekeeper.label', default: 'Gatekeeper'), gatekeeper.id])}"
                redirect(action: "show", id: gatekeeper.id)
            }
            else {
                render(view: "edit", model: [gatekeeper: gatekeeper])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'gatekeeper.label', default: 'Gatekeeper'), params.id])}"
            redirect(action: "list")
        }
    }

    @Secured(value = Permission.DELETE_GATEKEEPER)
    def delete = {
        def gatekeeper = Gatekeeper.get(params.id)
        if (gatekeeper) {
            try {
                gatekeeper.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'gatekeeper.label', default: 'Gatekeeper'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'gatekeeper.label', default: 'Gatekeeper'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'gatekeeper.label', default: 'Gatekeeper'), params.id])}"
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
