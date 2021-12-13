package com.force5solutions.care

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.force5solutions.care.aps.*
import com.force5solutions.care.ldap.*
import com.force5solutions.care.workflow.ApsWorkflowTask

import com.force5solutions.care.cc.Certification
import com.force5solutions.care.cc.Worker
import com.force5solutions.care.workflow.WorkflowVO
import com.force5solutions.care.workflow.CentralWorkflowTask
import com.force5solutions.care.common.CannedResponse
import com.force5solutions.care.common.CustomTag

class CareTagLib {

    def permissionService
    def entitlementService
    def rssFeedService
    def versioningService
    def apsWorkflowTaskService
    static config = ConfigurationHolder.config
    static namespace = "care"

    def historyByWorkerEntitlementRoleId = { attrs ->
        Long workerEntitlementRoleId = attrs['workerEntitlementRoleId']
        List<WorkflowVO> workflowVOs = []

        List<CentralWorkflowTask> centralTasks = CentralWorkflowTask.findAllByWorkerEntitlementRoleId(workerEntitlementRoleId)
        List<ApsWorkflowTask> apsTasks = ApsWorkflowTask.findAllByWorkerEntitlementRoleId(workerEntitlementRoleId)
        centralTasks.each { CentralWorkflowTask centralWorkflowTask ->
            workflowVOs << new WorkflowVO(centralWorkflowTask, "Central")
        }
        apsTasks.each { ApsWorkflowTask apsWorkflowTask ->
            workflowVOs << new WorkflowVO(apsWorkflowTask, "APS")
        }
        workflowVOs = workflowVOs.sort { it.taskCreated }

        out << render(template: '/workerEntitlementRole/workerRoleHistoryItems', model: [workflowVOs: workflowVOs])

    }

    def inbox = {
        List tasks = ApsWorkflowTask.getPermittedTasks()
        out << g.render(template: '/layouts/inbox', model: [total: (tasks) ? tasks.size() : 0])

    }

    def headerNews = {
        List<News> headerNews = News.listOrderByDateCreated(order: 'desc')
        String rssFeedUrl = config.rssFeedUrl
        out << g.render(template: "/layouts/headerNews", model: [headerNews: headerNews, rssFeedUrl: rssFeedUrl])
    }

    def hasPermission = { attrs, body ->
        Permission permission = attrs['permission']
        if (permission && permissionService.hasPermission(permission)) {
            out << true
        }

    }

    def isPermissionChecked = { attrs, body ->
        SecurityRole role = attrs['role']
        String permission = attrs['permission']
        Long value = attrs['value']
        PermissionLevel permissionLevel = (role?.permissionLevels?.find { it?.permission?.name == permission })
        Boolean isChecked = (permissionLevel && ((permissionLevel?.level % value) == 0))
        if (isChecked) {
            out << true
        }
    }
    def rssFeed = {
        List<RssFeedsVO> rssFeedsVOs = rssFeedService.getRssFeeds()
        String rssFeedUrl = config.rssFeedUrl
        out << g.render(template: "/news/rssFeed", model: [rssFeedsVOs: rssFeedsVOs, rssFeedUrl: rssFeedUrl])
    }

    def fullName = { attrs ->
        String slid = attrs['slid']
        log.info "Slid passed is ${slid}"
        ApsPerson p = ApsPerson.findBySlid(slid)
        if (p) {
            log.debug "found a person"
            out << p.name
        } else {
            log.debug "Did not find a person"
            out << slid
        }
    }

    def missingCertificationsForWorkflow = { attrs ->
        Worker worker = attrs['worker']
        List<Certification> missingCertifications = worker.missingCertifications.sort { it.name } //to maintain order
        out << g.render(template: "/workerCertification/missingCertificationForWorkflow", model: [worker: worker, missingCertifications: missingCertifications])
    }

    def popupBox = { attrs ->

        String height = attrs['height']
        String width = attrs['width']
        String opacity = ((attrs['splashTransparency']) ? attrs['splashTransparency'] : "0.80")
    }

    def cannedResponse = { attrs ->
        String targetId = attrs['targetId']
        String taskDesc = attrs['taskDescription']
        List<CannedResponse> cannedResponses = CannedResponse?.findAllByTaskDescription(taskDesc)
        cannedResponses = cannedResponses.sort { it.priority }

        Map model = [:]
        model.put('target', targetId)
        model.put('cannedResponses', cannedResponses)
        out << g.render(template: "/cannedResponseTemplates/responseTemplate", model: model)
    }

    def messagePreview = { attrs ->
        def messageTemplateCO = attrs['messageTemplate']
        ApsMessageTemplate messageTemplate = ApsMessageTemplate.get(messageTemplateCO.id)
        String text = '<div style="font-size: 14px;font-weight: bold;text-decoration:underline;">SUBJECT:</div>' + CustomTag.replaceTagsWithDummyCode(messageTemplate.subjectTemplate)
        text = text + '<div style="font-size: 14px;font-weight: bold;text-decoration:underline;">BODY:</div>' + CustomTag.replaceTagsWithDummyCode(messageTemplate.bodyTemplate)
        out << text
    }

    def listProvisionerDeprovisionerTasks = { attrs ->
        List<ApsWorkflowTask> apsWorkflowTasks = (attrs['tasks'] ?: attrs['task']) as List<ApsWorkflowTask>
        List<ProvisionerDeprovisionerTaskVO> provisionerDeprovisionerTaskVOList = apsWorkflowTaskService.populateProvisionerDeprovisionerTaskVOs(apsWorkflowTasks)
        out << g.render(template: '/apsWorkflowTask/showProvisionerDeprovisionerTasks', model: [taskVOs: provisionerDeprovisionerTaskVOList])
    }

    def showSharedAccountsAndProvisionedWorkers = { attrs ->
        List<ApsWorkflowTask> apsWorkflowTasks = attrs['tasks']
        boolean isRevocation = attrs['isRevocation'] as boolean ?: false
        List<Entitlement> entitlements = apsWorkflowTasks*.entitlement
        List<SharedAccountsAndProvisionedWorkersVO> sharedAccountsAndProvisionedWorkersVOs = []
        entitlements = entitlements.findAll { it?.hasSharedAccountAttributeTrue() || it?.hasGenericAccountAttributeTrue() }.unique()
        entitlements.each { Entitlement entitlement ->
            SharedAccountsAndProvisionedWorkersVO sharedAccountsAndProvisionedWorkersVO = new SharedAccountsAndProvisionedWorkersVO()
            sharedAccountsAndProvisionedWorkersVO.entitlement = entitlement
            String sharedAccountsAttribute = entitlement?.entitlementAttributes?.find { it?.keyName?.equalsIgnoreCase('Account List') }?.value
            if (sharedAccountsAttribute) {
                sharedAccountsAndProvisionedWorkersVO.sharedAccounts = sharedAccountsAttribute?.tokenize(',')*.trim()
            }
            sharedAccountsAndProvisionedWorkersVO.provisionedWorkers = entitlementService.getActiveWorkersWithEntitlement(entitlement)
            sharedAccountsAndProvisionedWorkersVOs.add(sharedAccountsAndProvisionedWorkersVO)
        }
        if (sharedAccountsAndProvisionedWorkersVOs) {
            out << g.render(template: '/entitlement/sharedAccountsAndActiveWorkers', model: [sharedAccountsAndProvisionedWorkersVOs: sharedAccountsAndProvisionedWorkersVOs, isRevocation: isRevocation])
        }
    }
}