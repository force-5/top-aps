package com.force5solutions.care.aps

import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.force5solutions.care.ldap.Permission
import com.force5solutions.care.common.Secured

class ProvisionerController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def employeeUtilService
    def utilService

    def index = {
        redirect(action: "list", params: params)
    }

    @Secured(value = Permission.READ_PROVISIONER)
    def list = {
        List<Provisioner> provisionerList = []
        Integer provisionerTotal = null
        Integer offset = params.offset ? params.offset.toInteger() : 0
        params.max = utilService.updateAndGetDefaultSizeOfListViewInConfig(params)
        params.order = params.order ?: 'asc'
        params.sort = params.sort ?: 'firstName'
        if (params.sort == 'name') {
            provisionerList = Provisioner.list().sort {it.person.name}
            if (!(params.order == 'asc')) {
                provisionerList = provisionerList.reverse()
            }
        } else {
            provisionerList = Provisioner.createCriteria().list {
                person {
                    order(params.sort.toString(), params.order)

                }
            }
        }
        if (!params?.max?.toString()?.equalsIgnoreCase('Unlimited')) {
            provisionerTotal = provisionerList.size()
            if (provisionerTotal) {
                Integer lastIndex = offset + params.max - 1
                if (lastIndex >= provisionerTotal) {
                    lastIndex = provisionerTotal - 1
                }
                provisionerList = provisionerList.getAt(offset..lastIndex)
            }
        }
        if (params?.ajax?.equalsIgnoreCase('true')) {
            render template: 'provisionersTable', model: [provisionerList: provisionerList, provisionerTotal: provisionerTotal, offset: offset, max: params.max, order: params.order, sort: params.sort]
        } else {
            [provisionerList: provisionerList, provisionerTotal: provisionerTotal, offset: offset, max: params.max, order: params.order, sort: params.sort]
        }
    }

    @Secured(value = Permission.CREATE_PROVISIONER)
    def create = {
        def provisioner = new Provisioner()
        provisioner.properties = params
        return [provisioner: provisioner]
    }

    def save = {
        def provisioner = new Provisioner(params)
        provisioner.properties = params
        ApsPerson person = ApsPerson.findBySlid(params.person.slid)
        if (!person) {
            if (ConfigurationHolder.config.isEmployeeEditable == 'true') {
                person = new ApsPerson()
            } else {
                person = employeeUtilService.findPerson(params.person.slid)
                if (!person) {
                    render(view: "create", model: [provisioner: provisioner, fail: 'true'])
                    return false
                }
            }
        }
        provisioner.person = person
        provisioner.properties = params

        if (provisioner.person.s() && provisioner.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'provisioner.label', default: 'Provisioner'), provisioner.id])}"
            redirect(action: "show", id: provisioner.id)
        }
        else {
            render(view: "create", model: [provisioner: provisioner])
        }
    }

    @Secured(value = Permission.READ_PROVISIONER)
    def show = {
        def provisioner = Provisioner.get(params.id)
        if (!provisioner) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'provisioner.label', default: 'Provisioner'), params.id])}"
            redirect(action: "list")
        }
        else {
            [provisioner: provisioner]
        }
    }

    @Secured(value = Permission.UPDATE_PROVISIONER)
    def edit = {
        def provisioner = Provisioner.get(params.id)
        if (!provisioner) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'provisioner.label', default: 'Provisioner'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [provisioner: provisioner]
        }
    }

    @Secured(value = Permission.UPDATE_PROVISIONER)
    def update = {
        def provisioner = Provisioner.get(params.id)
        provisioner.properties = params
        if (provisioner) {
            if (params.version) {
                def version = params.version.toLong()
                if (provisioner.version > version) {

                    provisioner.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'provisioner.label', default: 'Provisioner')] as Object[], "Another user has updated this Provisioner while you were editing")
                    render(view: "edit", model: [provisioner: provisioner])
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
                        render(view: "edit", model: [provisioner: provisioner])
                        return false
                    }
                }
            }
            provisioner.person = person
            provisioner.properties = params

            if (provisioner.person.s() && !provisioner.hasErrors() && provisioner.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'provisioner.label', default: 'Provisioner'), provisioner.id])}"
                redirect(action: "show", id: provisioner.id)
            }
            else {
                render(view: "edit", model: [provisioner: provisioner])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'provisioner.label', default: 'Provisioner'), params.id])}"
            redirect(action: "list")
        }
    }

    @Secured(value = Permission.DELETE_PROVISIONER)
    def delete = {
        def provisioner = Provisioner.get(params.id)
        if (provisioner) {
            try {
                provisioner.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'provisioner.label', default: 'Provisioner'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'provisioner.label', default: 'Provisioner'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'provisioner.label', default: 'Provisioner'), params.id])}"
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
