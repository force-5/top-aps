package com.force5solutions.care.workflow

import com.force5solutions.care.common.WorkflowTaskTemplate
import com.force5solutions.care.ldap.SecurityRole
import com.force5solutions.care.aps.ApsApplicationRole
import com.force5solutions.care.aps.ApsMessageTemplate

class ApsWorkflowTaskTemplate extends WorkflowTaskTemplate {

    ApsMessageTemplate messageTemplate
    String id
    Set<String> actions = []
    Set<SecurityRole> actorSecurityRoles = []
    Set<ApsApplicationRole> actorApplicationRoles = []
    Set<ApsApplicationRole> toNotificationApplicationRoles = []
    Set<ApsApplicationRole> ccNotificationApplicationRoles = []
    ApsWorkflowTaskTemplate escalationTemplate
    ApsWorkflowTaskType workflowTaskType

    static hasMany = [actorApplicationRoles: ApsApplicationRole, actorSecurityRoles: SecurityRole,
            toNotificationApplicationRoles: ApsApplicationRole,
            ccNotificationApplicationRoles: ApsApplicationRole,
            actions: String]

    static constraints = {
        id()
        messageTemplate(nullable: true)
        responseForm(nullable: true)
        period(nullable: true)
        periodUnit(nullable: true)
        workflowTaskType(nullable: true)
        actions()
        actorSlids(nullable: true)
        toNotificationSlids(nullable: true)
        toNotificationEmails(nullable: true)
        ccNotificationSlids(nullable: true)
        ccNotificationEmails(nullable: true)
        escalationTemplate(nullable: true)
        respectExclusionList(nullable: true)
    }

    static mapping = {
        id generator: 'assigned'
    }

    String toString() {
        return id
    }

}
