package com.force5solutions.care.workflow

import com.force5solutions.care.common.MetaClassHelper
import grails.plugin.spock.UnitSpec
import com.force5solutions.care.cc.PeriodUnit

class EscalationServiceSpec extends UnitSpec {

    EscalationService escalationService
    ApsWorkflowTaskService apsWorkflowTaskService
    def setupSpec() {
        MetaClassHelper.enrichClasses();
    }

    def setup() {
        mockDomain(ApsWorkflowTask)
        mockLogging(EscalationService)
        apsWorkflowTaskService = Mock(ApsWorkflowTaskService)
        escalationService = new EscalationService()
        escalationService.apsWorkflowTaskService=apsWorkflowTaskService
    }

    def "No escalations should happen when no tasks exist"() {
        when:
        escalationService.escalateTasks(new Date())

        then:
        0 * apsWorkflowTaskService.escalateWorkflowTask(_)
    }

    def "No escalations should happen for tasks which do not have escalation template"() {
        setup:
        ApsWorkflowTask workflowTask = new ApsWorkflowTask();
        workflowTask.with {
            droolsSessionId = 1
            workflowType = ApsWorkflowType.ROLE_REVOKE_REQUEST
            workflowGuid = "abcd"
            nodeName = "blah"
            workItemId = 5
            nodeId = 35
        }
        workflowTask.s()

        when:
        escalationService.escalateTasks(new Date()+1000)


        then:
        0 * apsWorkflowTaskService.escalateWorkflowTask(_)
    }

    def "No escalations should happen before time"() {
        setup:
        ApsWorkflowTask workflowTask = new ApsWorkflowTask();
        workflowTask.with {
            droolsSessionId = 1
            workflowType = ApsWorkflowType.ROLE_REVOKE_REQUEST
            workflowGuid = "abcd"
            nodeName = "blah"
            workItemId = 5
            nodeId = 35
            period = 1
            periodUnit = PeriodUnit.DAYS
            escalationTemplateId = "A"
        }
        workflowTask.s()

        when:
        escalationService.escalateTasks()

        then:
        0 * apsWorkflowTaskService.escalateWorkflowTask(_)
    }

    def "Escalations should happen when time has passed"() {
        setup:
        ApsWorkflowTask workflowTask = new ApsWorkflowTask();
        workflowTask.with {
            droolsSessionId = 1
            workflowType = ApsWorkflowType.ROLE_REVOKE_REQUEST
            workflowGuid = "abcd"
            nodeName = "blah"
            workItemId = 5
            nodeId = 35
            period = 1
            periodUnit = PeriodUnit.DAYS
            escalationTemplateId = "A"
        }
        workflowTask.s()

        when:
        escalationService.escalateTasks(new Date()+2)

        then:
        1 * apsWorkflowTaskService.escalateWorkflowTask(_)
    }

}