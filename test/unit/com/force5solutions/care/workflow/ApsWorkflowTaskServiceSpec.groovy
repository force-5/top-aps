package com.force5solutions.care.workflow

import com.force5solutions.care.cc.PeriodUnit
import com.force5solutions.care.cc.WorkerCertification
import com.force5solutions.care.cc.WorkerEntitlementRole
import com.force5solutions.care.common.CareConstants
import com.force5solutions.care.aps.ApsMessageTemplate
import com.force5solutions.care.common.MetaClassHelper
import com.force5solutions.care.ldap.SecurityRole
import grails.plugin.spock.UnitSpec
import com.force5solutions.care.aps.ApsApplicationRole
import com.force5solutions.care.cc.TransportType
import com.force5solutions.care.aps.ApsUtilService
import ru.perm.kefir.asynchronousmail.AsynchronousMailService

class ApsWorkflowTaskServiceSpec extends UnitSpec {

    ApsWorkflowTaskService service;

    def setupSpec() {
        MetaClassHelper.enrichClasses();
    }

    def setup() {
        mockDomain(ApsWorkflowTask)
        mockDomain(SecurityRole)
        mockDomain(ApsWorkflowTaskTemplate)
        mockDomain(ApsMessageTemplate)
        mockDomain(WorkerEntitlementRole)
        mockDomain(WorkerCertification)
        mockDomain(ApsWorkflowTaskPermittedSlid)
        def apsUtilService = Mock(ApsUtilService)
        mockLogging(ApsWorkflowTaskService)
        service = new ApsWorkflowTaskService();
        def asynchrounousMailService = Mock(AsynchronousMailService)
        asynchrounousMailService.sendAsynchronousMail(_)  >> {}
        apsUtilService.asynchronousMailService = asynchrounousMailService
        mockConfig('''
            grails.serverURL = 'http://www.google.com'
            '''
        )
    }

    def "Basic Test"() {
        setup:

        ApsMessageTemplate messageTemplate = new ApsMessageTemplate();
        messageTemplate.with {
            name = "Dummy Template"
            subjectTemplate = "subject"
            transportType = TransportType.EMAIL
            bodyTemplate = "body"
            s()
        }

        SecurityRole careEditor = new SecurityRole()
        careEditor.name = CareConstants.CAREEDITOR
        careEditor.description = "description"
        careEditor.s();

        ApsWorkflowTask taskToBeEscalated = new ApsWorkflowTask();
        ApsWorkflowTaskTemplate escalationTemplate = new ApsWorkflowTaskTemplate();
        escalationTemplate.messageTemplate = messageTemplate
        escalationTemplate.with {
            id = "defaultEscalationTemplate"
            periodUnit = PeriodUnit.DAYS
            period = 2
            responseForm = "response.gsp"
            workflowTaskType = ApsWorkflowTaskType.HUMAN
            actorSlids = 'slid1, slid2'
            actions = ['CONFIRM'] as Set
            actorApplicationRoles = [ApsApplicationRole.GATEKEEPER] as Set
            actorSecurityRoles = [SecurityRole.findByName(CareConstants.CAREEDITOR)]
            s()
        }

        taskToBeEscalated.with {
            escalationTemplateId = escalationTemplate.id
            droolsSessionId = 1
            workflowType = ApsWorkflowType.ROLE_REVOKE_REQUEST
            workflowGuid = "guid1"
            nodeName = "nodeName1"
            workItemId = 5
            nodeId = 35
        }
        taskToBeEscalated.s()

        when:

        service.escalateWorkflowTask(taskToBeEscalated)
        taskToBeEscalated = taskToBeEscalated.refresh()

        then:
        messageTemplate.id != null
        escalationTemplate.id != null
        ApsWorkflowTaskTemplate.count() == 1
        ApsMessageTemplate.count() == 1
        escalationTemplate.messageTemplate != null
        ApsWorkflowTask.count() == 2
        taskToBeEscalated.permittedSlids.size() == 2
        CareConstants.CAREEDITOR in taskToBeEscalated.securityRoles
    }
}

