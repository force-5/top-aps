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
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_24_HOURS_ESCALATE_VP_TASK_TEMPLATE)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_24_HOURS_ESCALATE_VP_TASK_TEMPLATE) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.APS_REVOKE_24_HOURS_ESCALATE_VP_TASK_TEMPLATE
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_24H_REVOKE_REQUEST_NOTIFICATION)
            toNotificationSlids = 'vp-1'
        }
        template.s()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_24_HOURS_ESCALATE_DIRECTOR_TASK_TEMPLATE)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_24_HOURS_ESCALATE_DIRECTOR_TASK_TEMPLATE) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.APS_REVOKE_24_HOURS_ESCALATE_DIRECTOR_TASK_TEMPLATE
            period = 18
            periodUnit = PeriodUnit.HOURS
            escalationTemplate = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_24_HOURS_ESCALATE_VP_TASK_TEMPLATE)
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_24H_REVOKE_REQUEST_NOTIFICATION)
            toNotificationSlids = 'director-1'
        }
        template.s()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_24_HOURS_ESCALATE_COMPLIANCE_LEAD_TASK_TEMPLATE)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_24_HOURS_ESCALATE_COMPLIANCE_LEAD_TASK_TEMPLATE) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.APS_REVOKE_24_HOURS_ESCALATE_COMPLIANCE_LEAD_TASK_TEMPLATE
            period = 14
            periodUnit = PeriodUnit.HOURS
            escalationTemplate = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_24_HOURS_ESCALATE_DIRECTOR_TASK_TEMPLATE)
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_24H_REVOKE_REQUEST_NOTIFICATION)
            toNotificationSlids = 'complianceLead-1'
            toNotificationEmails = "care.force5+toNotification-5@gmail.com"
            toNotificationApplicationRoles = [ApsApplicationRole.ROLE_OWNER] as Set
        }
        template.s()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_24_HOURS_ESCALATE_GM_TASK_TEMPLATE)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_24_HOURS_ESCALATE_GM_TASK_TEMPLATE) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.APS_REVOKE_24_HOURS_ESCALATE_GM_TASK_TEMPLATE
            period = 6
            periodUnit = PeriodUnit.HOURS
            escalationTemplate = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_24_HOURS_ESCALATE_COMPLIANCE_LEAD_TASK_TEMPLATE)
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_24H_REVOKE_REQUEST_ESCALATION)
            actions = ['CONFIRM', 'REJECT']
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
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_24_HOURS_PROVISIONER_TASK_TEMPLATE)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_24_HOURS_PROVISIONER_TASK_TEMPLATE) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.APS_REVOKE_24_HOURS_PROVISIONER_TASK_TEMPLATE
            period = 4
            periodUnit = PeriodUnit.HOURS
            escalationTemplate = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_24_HOURS_ESCALATE_GM_TASK_TEMPLATE)
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_24H_REVOKE_REQUEST)
            actions = ['CONFIRM', 'REJECT']
            workflowTaskType = ApsWorkflowTaskType.HUMAN
            responseForm = 'revocationUserResponse'
            actorApplicationRoles = [ApsApplicationRole.DEPROVISIONER] as Set
            toNotificationApplicationRoles = [ApsApplicationRole.DEPROVISIONER] as Set
            actorSecurityRoles = [SecurityRole.findByName(CareConstants.CAREADMIN)] as Set
            toNotificationEmails = "care.force5+toNotification-1@gmail.com, care.force5+toNotification-2@gmail.com"
            ccNotificationApplicationRoles = [ApsApplicationRole.GATEKEEPER] as Set
        }
        template.s()
    }
}

fixture {
}
