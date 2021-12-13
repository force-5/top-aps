package com.force5solutions.care.aps

import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.force5solutions.care.ldap.Permission
import com.force5solutions.care.common.Secured

class DeProvisionerController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def employeeUtilService
    def utilService

    def index = {
        redirect(action: "list", params: params)
    }

    @Secured(value = Permission.READ_DEPROVISIONER)
    def list = {
        List<DeProvisioner> deProvisionerList = []
        Integer deProvisionerTotal = null
        Integer offset = params.offset ? params.offset.toInteger() : 0
        params.max = utilService.updateAndGetDefaultSizeOfListViewInConfig(params)
        params.order = params.order ?: 'asc'
        params.sort = params.sort ?: 'firstName'
        if (params.sort == 'name') {
            deProvisionerList = DeProvisioner.list().sort {it.person.name}
            if (!(params.order == 'asc')) {
                deProvisionerList = deProvisionerList.reverse()
            }
        } else {
            deProvisionerList = DeProvisioner.createCriteria().list {
                person {
                    order(params.sort.toString(), params.order)

                }
            }
        }
        if (!params?.max?.toString()?.equalsIgnoreCase('Unlimited')) {
            deProvisionerTotal = deProvisionerList.size()
            if (deProvisionerList) {
                Integer lastIndex = offset + params.max - 1
                if (lastIndex >= deProvisionerTotal) {
                    lastIndex = deProvisionerTotal - 1
                }
                deProvisionerList = deProvisionerList.getAt(offset..lastIndex)
            }
        }

        if (params?.ajax?.equalsIgnoreCase('true')) {
            render template: 'deProvisionersTable', model: [deProvisionerList: deProvisionerList, deProvisionerTotal: deProvisionerTotal, offset: offset, max: params.max, order: params.order, sort: params.sort]
        } else {
            [deProvisionerList: deProvisionerList, deProvisionerTotal: deProvisionerTotal, offset: offset, max: params.max, order: params.order, sort: params.sort]
        }
    }

    @Secured(value = Permission.CREATE_DEPROVISIONER)
    def create = {
        def deProvisioner = new DeProvisioner()
        deProvisioner.properties = params
        return [deProvisioner: deProvisioner]
    }

    def save = {
        def deProvisioner = new DeProvisioner(params)
        deProvisioner.properties = params
        ApsPerson person = ApsPerson.findBySlid(params.person.slid)
        if (!person) {
            if (ConfigurationHolder.config.isEmployeeEditable == 'true') {
                person = new ApsPerson()
            } else {
                person = employeeUtilService.findPerson(params.person.slid)
                if (!person) {
                    render(view: "create", model: [deProvisioner: deProvisioner, fail: 'true'])
                    return false
                }
            }
        }
        deProvisioner.person = person
        deProvisioner.properties = params

        if (deProvisioner.person.s() && deProvisioner.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'deProvisioner.label', default: 'Deprovisioner'), deProvisioner.id])}"
            redirect(action: "show", id: deProvisioner.id)
        }
        else {
            render(view: "create", model: [deProvisioner: deProvisioner])
        }
    }

    @Secured(value = Permission.READ_DEPROVISIONER)
    def show = {
        def deProvisioner = DeProvisioner.get(params.id)
        if (!deProvisioner) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'deProvisioner.label', default: 'Deprovisioner'), params.id])}"
            redirect(action: "list")
        }
        else {
            [deProvisioner: deProvisioner]
        }
    }

    @Secured(value = Permission.UPDATE_DEPROVISIONER)
    def edit = {
        def deProvisioner = DeProvisioner.get(params.id)
        if (!deProvisioner) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'deProvisioner.label', default: 'Deprovisioner'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [deProvisioner: deProvisioner]
        }
    }

    @Secured(value = Permission.UPDATE_DEPROVISIONER)
    def update = {
        def deProvisioner = DeProvisioner.get(params.id)
        deProvisioner.properties = params
        if (deProvisioner) {
            if (params.version) {
                def version = params.version.toLong()
                if (deProvisioner.version > version) {

                    deProvisioner.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'deProvisioner.label', default: 'Deprovisioner')] as Object[], "Another user has updated this Deprovisioner while you were editing")
                    render(view: "edit", model: [deProvisioner: deProvisioner])
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
                        render(view: "edit", model: [deProvisioner: deProvisioner])
                        return false
                    }
                }
            }
            deProvisioner.person = person
            deProvisioner.properties = params

            if (deProvisioner.person.s() && !deProvisioner.hasErrors() && deProvisioner.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'deProvisioner.label', default: 'Deprovisioner'), deProvisioner.id])}"
                redirect(action: "show", id: deProvisioner.id)
            }
            else {
                render(view: "edit", model: [deProvisioner: deProvisioner])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'deProvisioner.label', default: 'Deprovisioner'), params.id])}"
            redirect(action: "list")
        }
    }

    @Secured(value = Permission.DELETE_DEPROVISIONER)
    def delete = {
        def deProvisioner = DeProvisioner.get(params.id)
        if (deProvisioner) {
            try {
                deProvisioner.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'deProvisioner.label', default: 'Deprovisioner'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'deProvisioner.label', default: 'Deprovisioner'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'deProvisioner.label', default: 'Deprovisioner'), params.id])}"
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
