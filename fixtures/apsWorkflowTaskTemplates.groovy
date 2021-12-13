import com.force5solutions.care.common.CareConstants
import com.force5solutions.care.aps.ApsMessageTemplate
import com.force5solutions.care.ldap.SecurityRole
import com.force5solutions.care.workflow.ApsWorkflowTaskType
import com.force5solutions.care.workflow.ApsWorkflowTaskTemplate
import com.force5solutions.care.aps.ApsApplicationRole
import com.force5solutions.care.common.SessionUtils

load "aps24HoursRevocationTaskTemplates"
load "aps7DaysRevocationTaskTemplates"

pre {
    ApsWorkflowTaskTemplate template;

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_INITIAL_TASK)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_INITIAL_TASK) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.WORKFLOW_TASK_TEMPLATE_INITIAL_TASK
            workflowTaskType = ApsWorkflowTaskType.SYSTEM_APS
        }
        template.s()
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_TIM_SYSTEM_TASK)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_TIM_SYSTEM_TASK) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.WORKFLOW_TASK_TEMPLATE_TIM_SYSTEM_TASK
            workflowTaskType = ApsWorkflowTaskType.SYSTEM_TIM
        }
        template.s()
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_ACCESS_APPROVAL_BY_GATEKEEPER)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_ACCESS_APPROVAL_BY_GATEKEEPER) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.WORKFLOW_TASK_TEMPLATE_ACCESS_APPROVAL_BY_GATEKEEPER
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ACCESS_REQUEST)
            actions = ['APPROVE', 'REJECT']
            workflowTaskType = ApsWorkflowTaskType.HUMAN
            responseForm = 'gatekeeperResponse'
            actorApplicationRoles = [ApsApplicationRole.GATEKEEPER] as Set
            toNotificationApplicationRoles = [ApsApplicationRole.GATEKEEPER] as Set
            actorSecurityRoles = [SecurityRole.findByName(CareConstants.CAREADMIN)] as Set
        }
        template.s()
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_REVOKE_APPROVAL_BY_GATEKEEPER)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_REVOKE_APPROVAL_BY_GATEKEEPER) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.WORKFLOW_TASK_TEMPLATE_REVOKE_APPROVAL_BY_GATEKEEPER
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_REVOKE_REQUEST_APPROVAL_GATEKEEPER)
            actions = ['APPROVE', 'REJECT']
            workflowTaskType = ApsWorkflowTaskType.HUMAN
            responseForm = 'revocationUserResponse'
            actorApplicationRoles = [ApsApplicationRole.GATEKEEPER] as Set
            toNotificationApplicationRoles = [ApsApplicationRole.GATEKEEPER] as Set
            actorSecurityRoles = [SecurityRole.findByName(CareConstants.CAREADMIN)] as Set
        }
        template.s()
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_ACCESS_CONFIRM_BY_PROVISIONER)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_ACCESS_CONFIRM_BY_PROVISIONER) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.WORKFLOW_TASK_TEMPLATE_ACCESS_CONFIRM_BY_PROVISIONER
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ACCESS_REQUEST_APPROVED)
            actions = ['CONFIRM', 'REJECT']
            workflowTaskType = ApsWorkflowTaskType.HUMAN
            responseForm = 'roleAccessRequest'
            actorApplicationRoles = [ApsApplicationRole.PROVISIONER] as Set
            toNotificationApplicationRoles = [ApsApplicationRole.PROVISIONER] as Set
            actorSecurityRoles = [SecurityRole.findByName(CareConstants.CAREADMIN)] as Set
        }
        template.s()
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_REVOKE_CONFIRM_BY_PROVISIONER)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_REVOKE_CONFIRM_BY_PROVISIONER) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.WORKFLOW_TASK_TEMPLATE_REVOKE_CONFIRM_BY_PROVISIONER
            actions = ['CONFIRM', 'REJECT']
            workflowTaskType = ApsWorkflowTaskType.HUMAN
            responseForm = 'revocationUserResponse'
            actorApplicationRoles = [ApsApplicationRole.PROVISIONER] as Set
            toNotificationApplicationRoles = [ApsApplicationRole.PROVISIONER] as Set
            actorSecurityRoles = [SecurityRole.findByName(CareConstants.CAREADMIN)] as Set
        }
        template.s()
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_TERMINATE_CONFIRM_BY_PROVISIONER)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_TERMINATE_CONFIRM_BY_PROVISIONER) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.WORKFLOW_TASK_TEMPLATE_TERMINATE_CONFIRM_BY_PROVISIONER
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_TERMINATE_REQUEST)
            actions = ['CONFIRM']
            workflowTaskType = ApsWorkflowTaskType.HUMAN
            responseForm = 'userResponse'
            actorApplicationRoles = [ApsApplicationRole.PROVISIONER] as Set
            toNotificationApplicationRoles = [ApsApplicationRole.PROVISIONER] as Set
            actorSecurityRoles = [SecurityRole.findByName(CareConstants.CAREADMIN)] as Set
        }
        template.s()
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_ADD_ROLE)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_ADD_ROLE) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.WORKFLOW_TASK_TEMPLATE_ADD_ROLE
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ENTITLEMENT_ROLE_CREATION)
            actions = ['APPROVE', 'REJECT']
            workflowTaskType = ApsWorkflowTaskType.HUMAN
            responseForm = 'approveNewRole'
            actorApplicationRoles = [ApsApplicationRole.ROLE_OWNER] as Set
            toNotificationApplicationRoles = [ApsApplicationRole.ROLE_OWNER] as Set
            actorSecurityRoles = [SecurityRole.findByName(CareConstants.CAREADMIN)] as Set
        }
        template.s()
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_UPDATE_ROLE)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_UPDATE_ROLE) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.WORKFLOW_TASK_TEMPLATE_UPDATE_ROLE
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ENTITLEMENT_ROLE_UPDATION)
            actions = ['APPROVE', 'REJECT']
            workflowTaskType = ApsWorkflowTaskType.HUMAN
            responseForm = 'approveNewRole'
            actorApplicationRoles = [ApsApplicationRole.ROLE_OWNER] as Set
            toNotificationApplicationRoles = [ApsApplicationRole.ROLE_OWNER] as Set
            actorSecurityRoles = [SecurityRole.findByName(CareConstants.CAREADMIN)] as Set
        }
        template.s()
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_ADD_ENTITLEMENT)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_ADD_ENTITLEMENT) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.WORKFLOW_TASK_TEMPLATE_ADD_ENTITLEMENT
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ENTITLEMENT_CREATION)
            actions = ['APPROVE', 'REJECT']
            workflowTaskType = ApsWorkflowTaskType.HUMAN
            responseForm = 'approveNewEntitlement'
            actorApplicationRoles = [ApsApplicationRole.ROLE_OWNER] as Set
            toNotificationApplicationRoles = [ApsApplicationRole.ROLE_OWNER] as Set
            actorSecurityRoles = [SecurityRole.findByName(CareConstants.CAREADMIN)] as Set
        }
        template.s()
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_ACCOUNT_PASSWORD_CHANGE)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_ACCOUNT_PASSWORD_CHANGE) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.WORKFLOW_TASK_TEMPLATE_ACCOUNT_PASSWORD_CHANGE
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ACCOUNT_PASSWORD_CHANGE)
            actions = ['APPROVE', 'REJECT']
            workflowTaskType = ApsWorkflowTaskType.HUMAN
            responseForm = 'accountPasswordChange'
            actorApplicationRoles = [ApsApplicationRole.ROLE_OWNER] as Set
            toNotificationApplicationRoles = [ApsApplicationRole.ROLE_OWNER] as Set
            actorSecurityRoles = [SecurityRole.findByName(CareConstants.CAREADMIN)] as Set
        }
        template.s()
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_UPDATE_ENTITLEMENT)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_UPDATE_ENTITLEMENT) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.WORKFLOW_TASK_TEMPLATE_UPDATE_ENTITLEMENT
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ENTITLEMENT_UPDATION)
            actions = ['APPROVE', 'REJECT']
            workflowTaskType = ApsWorkflowTaskType.HUMAN
            responseForm = 'approveNewEntitlement'
            actorApplicationRoles = [ApsApplicationRole.ROLE_OWNER] as Set
            toNotificationApplicationRoles = [ApsApplicationRole.ROLE_OWNER] as Set
            actorSecurityRoles = [SecurityRole.findByName(CareConstants.CAREADMIN)] as Set
        }
        template.s()
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_APS_ACCESS_VERIFICATION)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_APS_ACCESS_VERIFICATION) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.WORKFLOW_TASK_TEMPLATE_APS_ACCESS_VERIFICATION
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ACCESS_VERIFICATION)
            toNotificationEmails = 'care.force5+bcc@gmail.com'
            actions = ['CONFIRM', 'REVOKE ALL']
            workflowTaskType = ApsWorkflowTaskType.HUMAN
            responseForm = 'adminAccessVerification'
            actorSecurityRoles = [SecurityRole.findByName(CareConstants.CAREADMIN)] as Set
        }
        template.s()
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.GATEKEEPER_REJECTION_NOTIFICATION_EMAIL_APS_SYSTEM_TASK)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.GATEKEEPER_REJECTION_NOTIFICATION_EMAIL_APS_SYSTEM_TASK) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.GATEKEEPER_REJECTION_NOTIFICATION_EMAIL_APS_SYSTEM_TASK
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_GATEKEEPER_REJECTION_NOTIFICATION)
            workflowTaskType = ApsWorkflowTaskType.SYSTEM_APS
            toNotificationApplicationRoles = [ApsApplicationRole.WORKER, ApsApplicationRole.SUPERVISOR] as Set
            ccNotificationEmails = 'care.force5+bcc@gmail.com'
        }
        template.s()
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.GATEKEEPER_REJECTION_NOTIFICATION_EMAIL_APS_SYSTEM_TASK_FOR_CONTRACTOR)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.GATEKEEPER_REJECTION_NOTIFICATION_EMAIL_APS_SYSTEM_TASK_FOR_CONTRACTOR) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.GATEKEEPER_REJECTION_NOTIFICATION_EMAIL_APS_SYSTEM_TASK_FOR_CONTRACTOR
            messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_GATEKEEPER_REJECTION_NOTIFICATION)
            workflowTaskType = ApsWorkflowTaskType.SYSTEM_APS
            toNotificationApplicationRoles = [ApsApplicationRole.WORKER, ApsApplicationRole.BUSINESS_UNIT_REQUESTER] as Set
            ccNotificationEmails = 'care.force5+bcc@gmail.com'
        }
        template.s()
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_UPDATE_ENTITLEMENT_FEED_EXCEPTION)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_UPDATE_ENTITLEMENT_FEED_EXCEPTION) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.WORKFLOW_TASK_TEMPLATE_UPDATE_ENTITLEMENT_FEED_EXCEPTION
            workflowTaskType = ApsWorkflowTaskType.HUMAN
            actions = ['APPROVE', 'REJECT']
            responseForm = 'approveNewEntitlement'
            actorSecurityRoles = [SecurityRole.findByName(CareConstants.CAREADMIN)] as Set
        }
        template.s()
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_CREATE_ENTITLEMENT_FEED_EXCEPTION)))
    if (overrideOrCreate) {
        template = ApsWorkflowTaskTemplate.findById(CareConstants.WORKFLOW_TASK_TEMPLATE_CREATE_ENTITLEMENT_FEED_EXCEPTION) ?: new ApsWorkflowTaskTemplate();
        template.with {
            id = CareConstants.WORKFLOW_TASK_TEMPLATE_CREATE_ENTITLEMENT_FEED_EXCEPTION
            workflowTaskType = ApsWorkflowTaskType.HUMAN
            actions = ['APPROVE', 'REJECT']
            responseForm = 'approveNewEntitlement'
            actorSecurityRoles = [SecurityRole.findByName(CareConstants.CAREADMIN)] as Set
        }
        template.s()
    }
}

fixture {
}
