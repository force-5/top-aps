import com.force5solutions.care.workflow.ApsWorkflowTaskTemplate
import com.force5solutions.care.common.CareConstants
import com.force5solutions.care.aps.ApsMessageTemplate
import com.force5solutions.care.workflow.ApsWorkflowTaskType
import com.force5solutions.care.aps.ApsApplicationRole

ApsWorkflowTaskTemplate workflowTaskTemplate = new ApsWorkflowTaskTemplate()
 workflowTaskTemplate.with {
    id = CareConstants.GATEKEEPER_REJECTION_NOTIFICATION_EMAIL_APS_SYSTEM_TASK
    messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_GATEKEEPER_REJECTION_NOTIFICATION)
    workflowTaskType = ApsWorkflowTaskType.SYSTEM_APS
    toNotificationApplicationRoles = [ApsApplicationRole.WORKER, ApsApplicationRole.SUPERVISOR] as Set
    ccNotificationEmails = 'care.force5+bcc@gmail.com'
}

workflowTaskTemplate.s()
