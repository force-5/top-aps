package com.force5solutions.care.aps

import com.force5solutions.care.common.Secured
import com.force5solutions.care.ldap.Permission
import com.force5solutions.care.cc.TransportType
import com.force5solutions.care.cc.UploadedFile
import com.force5solutions.care.cc.AppUtil
import com.force5solutions.care.common.CustomTag

class ApsMessageTemplateController {

    def utilService

    def index = { redirect(action: list, params: params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    @Secured(value = Permission.READ_MESSAGE_TEMPLATE)
    def list = {
        List<ApsMessageTemplate> apsMessageTemplateList = []
        Integer apsMessageTemplateTotal = null
        params.order = params.order ?: 'asc'
        params.sort = params.sort ?: 'name'
        Integer offset = params.offset ? params.offset.toInteger() : 0
        params.max = utilService.updateAndGetDefaultSizeOfListViewInConfig(params)
        if (!params?.max?.toString()?.equalsIgnoreCase('Unlimited')) {
            apsMessageTemplateList = ApsMessageTemplate.list(params)
            apsMessageTemplateTotal = ApsMessageTemplate.count()
        } else {
            apsMessageTemplateList = ApsMessageTemplate.list()
        }
        if (params?.ajax?.equalsIgnoreCase('true')) {
            render template: 'apsMessageTemplatesTable', model: [apsMessageTemplateList: apsMessageTemplateList, apsMessageTemplateTotal: apsMessageTemplateTotal, offset: offset, max: params.max, order: params.order, sort: params.sort]
        } else {
            [apsMessageTemplateList: apsMessageTemplateList, apsMessageTemplateTotal: apsMessageTemplateTotal, offset: offset, max: params.max, order: params.order, sort: params.sort]
        }
    }


    @Secured(value = Permission.READ_MESSAGE_TEMPLATE)
    def show = {
        ApsMessageTemplate messageTemplate = ApsMessageTemplate.get(params?.id)

        if (!messageTemplate) {
            flash.message = "Message Template not found"
            redirect(action: list)
        }
        else {
            return ['messageTemplate': createmessageTemplateCommand(messageTemplate), 'attachments': messageTemplate.attachments]
        }
    }

    @Secured(value = Permission.DELETE_MESSAGE_TEMPLATE)
    def delete = {
        ApsMessageTemplate messageTemplate = ApsMessageTemplate.get(params.id)
        if (messageTemplate) {
            String name = messageTemplate.name
            try {
                messageTemplate.delete(flush: true)
                flash.message = "MessageTemplate ${name} deleted"
                redirect(action: list)
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "MessageTemplate ${name} could not be deleted"
                redirect(action: show, id: params.id)
            }
        }
        else {
            flash.message = "MessageTemplate not found with id ${params.id}"
            redirect(action: list)
        }
    }

    @Secured(value = Permission.CREATE_MESSAGE_TEMPLATE)
    def create = {
        ApsMessageTemplate messageTemplate = new ApsMessageTemplate()
        messageTemplate.properties = params
        return ['messageTemplate': createmessageTemplateCommand(messageTemplate),
                tagsMap: CustomTag.tagsMap(),
        ]
    }

    def save = {MessageTemplateCO messageTemplateCO ->
        if (messageTemplateCO.validate()) {
            ApsMessageTemplate messageTemplate = createmessageTemplateObject(messageTemplateCO)
            if (!messageTemplate.hasErrors()) {
                AppUtil.populateAttachments(params).each {UploadedFile uploadedFile ->
                    ApsDataFile attachment = new ApsDataFile(uploadedFile)
                    messageTemplate.addToAttachments(attachment)
                }
                if (messageTemplate.s()) {
                    flash.message = "Message Template ${messageTemplate.name} created successfully"
                    redirect(action: 'show', id: messageTemplate.id)
                    return false;
                }
            }
        }
        flash.errormessage = "Please enter valid values in required fields"
        render(view: 'create', model: ['messageTemplate': messageTemplateCO, tagsMap: CustomTag.tagsMap()])
    }

    @Secured(value = Permission.UPDATE_MESSAGE_TEMPLATE)
    def edit = {
        ApsMessageTemplate messageTemplate = ApsMessageTemplate.get(params.id)
        if (!messageTemplate) {
            flash.message = "Message Template not found "
            redirect(action: list)
        }
        else {
            return [messageTemplate: createmessageTemplateCommand(messageTemplate),
                    tagsMap: CustomTag.tagsMap(),
                    attachments: messageTemplate.attachments
            ]
        }
    }

    @Secured(value = Permission.UPDATE_MESSAGE_TEMPLATE)
    def update = {MessageTemplateCO messageTemplateCO ->
        if (!messageTemplateCO.hasErrors()) {
            ApsMessageTemplate messageTemplate = createmessageTemplateObject(messageTemplateCO)
            List attachmentsToRemove = messageTemplate.attachments*.id - messageTemplateCO.remainingAttachments
            if (attachmentsToRemove) {
                ApsDataFile.getAll(attachmentsToRemove)?.each {ApsDataFile attachment ->
                    messageTemplate.removeFromAttachments(attachment)
                    attachment.delete(flush: true)
                }
            }

            if (messageTemplate && !messageTemplate.hasErrors()) {
                AppUtil.populateAttachments(params).each {UploadedFile uploadedFile ->
                    ApsDataFile attachment = new ApsDataFile(uploadedFile)
                    messageTemplate.addToAttachments(attachment)
                }


                if (messageTemplate.s()) {
                    flash.message = "Message Template ${messageTemplate.name} updated successfully"
                    redirect(action: show, id: messageTemplate.id)
                    return false
                }
            }
        }

        Set<ApsDataFile> attachments = ApsMessageTemplate.get(messageTemplateCO?.id)?.attachments
        render(view: 'edit', model: [messageTemplate: messageTemplateCO,
                tagsMap: CustomTag.tagsMap(), attachments: attachments
        ])
    }


    def validateName = {
        String isNameValid = "true"
        Long id = params.id?.length() > 0 ? params.id?.toLong() : null
        if (params.name) {
            ApsMessageTemplate messageTemplate = ApsMessageTemplate.findByName(params.name)
            if (messageTemplate && messageTemplate.id != id) {
                isNameValid = "false"
            } else {
                isNameValid = "true"
            }
        } else {
            isNameValid = "false"
        }
        render isNameValid
    }

    private MessageTemplateCO createmessageTemplateCommand(ApsMessageTemplate messageTemplate) {
        MessageTemplateCO messageTemplateCO = new MessageTemplateCO()
        messageTemplateCO.with {
            id = messageTemplate?.id
            name = messageTemplate?.name
            transportType = messageTemplate?.transportType?.toString()
            subjectTemplate = messageTemplate?.subjectTemplate
            bodyTemplate = messageTemplate?.bodyTemplate
        }
        return messageTemplateCO
    }

    private ApsMessageTemplate createmessageTemplateObject(MessageTemplateCO messageTemplateCO) {
        ApsMessageTemplate messageTemplate
        if (messageTemplateCO.id) {
            messageTemplate = ApsMessageTemplate.get(messageTemplateCO.id.toLong())
        } else {
            messageTemplate = new ApsMessageTemplate()
        }

        messageTemplate.name = messageTemplateCO.name
        messageTemplate.subjectTemplate = messageTemplateCO.subjectTemplate
        messageTemplate.transportType = TransportType.getTransportType(messageTemplateCO.transportType)
        messageTemplate.bodyTemplate = messageTemplateCO.bodyTemplate
        return messageTemplate
    }
}

class MessageTemplateCO {
    String id
    String name
    String transportType
    String subjectTemplate
    String bodyTemplate
    def remainingAttachments = []

    static constraints = {
        subjectTemplate(blank: false)
        bodyTemplate(blank: false)
        name(blank: false, validator: {val, obj ->
            if (val) {
                List<ApsMessageTemplate> messageTemplates = ApsMessageTemplate.findAllByName(obj.name)
                if (obj.id) {
                    messageTemplates = messageTemplates.findAll {!it.id == obj?.id?.toLong()}
                }
                if (messageTemplates.size() > 0) {
                    return "default.not.unique.message"
                }
            }
        })
    }

    void setRemainingAttachments(def attachments) {
        remainingAttachments = [attachments].flatten()*.toLong()
    }
}
