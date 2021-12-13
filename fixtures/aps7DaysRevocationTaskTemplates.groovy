import com.force5solutions.care.cc.PeriodUnit
import com.force5solutions.care.common.CareConstants
import com.force5solutions.care.aps.ApsMessageTemplate
import com.force5solutions.care.ldap.SecurityRole
import com.force5solutions.care.workflow.ApsWorkflowTaskType
import com.force5solutions.care.workflow.ApsWorkflowTaskTemplate
import com.force5solutions.care.aps.ApsApplicationRole
import com.force5solutions.care.common.SessionUtils

pre {

    ApsWorkflowTaskTemplate template;

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_7_DAYS_ESCALATE_VP_TASK_TEMPLATE)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_7_DAYS_ESCALATE_VP_TASK_TEMPLATE) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.APS_REVOKE_7_DAYS_ESCALATE_VP_TASK_TEMPLATE
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_7D_REVOKE_REQUEST_NOTIFICATION)
            toNotificationSlids = 'vp-1'
        }
        template.s()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_7_DAYS_ESCALATE_DIRECTOR_TASK_TEMPLATE)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_7_DAYS_ESCALATE_DIRECTOR_TASK_TEMPLATE) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.APS_REVOKE_7_DAYS_ESCALATE_DIRECTOR_TASK_TEMPLATE
            period = 6
            periodUnit = PeriodUnit.DAYS
            escalationTemplate = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_7_DAYS_ESCALATE_VP_TASK_TEMPLATE)
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_7D_REVOKE_REQUEST_NOTIFICATION)
            toNotificationSlids = 'director-1'
        }
        template.s()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_7_DAYS_ESCALATE_COMPLIANCE_LEAD_TASK_TEMPLATE)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_7_DAYS_ESCALATE_COMPLIANCE_LEAD_TASK_TEMPLATE) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.APS_REVOKE_7_DAYS_ESCALATE_COMPLIANCE_LEAD_TASK_TEMPLATE
            period = 5
            periodUnit = PeriodUnit.DAYS
            escalationTemplate = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_7_DAYS_ESCALATE_DIRECTOR_TASK_TEMPLATE)
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_7D_REVOKE_REQUEST_NOTIFICATION)
            toNotificationSlids = 'complianceLead-1'
            toNotificationEmails = "care.force5+toNotification-5@gmail.com"
            toNotificationApplicationRoles = [ApsApplicationRole.ROLE_OWNER] as Set
        }
        template.s()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_7_DAYS_ESCALATE_GM_TASK_TEMPLATE)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_7_DAYS_ESCALATE_GM_TASK_TEMPLATE) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.APS_REVOKE_7_DAYS_ESCALATE_GM_TASK_TEMPLATE
            period = 4
            periodUnit = PeriodUnit.DAYS
            escalationTemplate = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_7_DAYS_ESCALATE_COMPLIANCE_LEAD_TASK_TEMPLATE)
            actions = ['CONFIRM', 'REJECT']
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_7D_REVOKE_REQUEST_ESCALATION)
            workflowTaskType = ApsWorkflowTaskType.HUMAN
            responseForm = 'revocationUserResponse'
            actorSlids = 'gm-1'
            toNotificationSlids = 'gm-1'
            toNotificationEmails = "care.force5+toNotification-3@gmail.com, care.force5+toNotification-4gmail.com"
            toNotificationApplicationRoles = [ApsApplicationRole.DEPROVISIONER] as Set
        }
        template.s()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_7_DAYS_PROVISIONER_TASK_TEMPLATE)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_7_DAYS_PROVISIONER_TASK_TEMPLATE) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.APS_REVOKE_7_DAYS_PROVISIONER_TASK_TEMPLATE
            period = 3
            periodUnit = PeriodUnit.DAYS
            escalationTemplate = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_7_DAYS_ESCALATE_GM_TASK_TEMPLATE)
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_7D_REVOKE_REQUEST)
            actions = ['CONFIRM', 'REJECT']
            workflowTaskType = ApsWorkflowTaskType.HUMAN
            responseForm = 'revocationUserResponse'
            actorApplicationRoles = [ApsApplicationRole.DEPROVISIONER] as Set
            toNotificationApplicationRoles = [ApsApplicationRole.DEPROVISIONER] as Set
            actorSecurityRoles = [SecurityRole.findByName(CareConstants.CAREADMIN)] as Set
            toNotificationEmails = "care.force5+toNotification-1@gmail.com, care.force5+toNotification-2gmail.com"
            ccNotificationApplicationRoles = [ApsApplicationRole.GATEKEEPER] as Set
        }
        template.s()
    }
}

fixture {
}
