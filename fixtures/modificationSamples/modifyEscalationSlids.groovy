import com.force5solutions.care.aps.ApsMessageTemplate
import com.force5solutions.care.common.CareConstants
import com.force5solutions.care.workflow.ApsWorkflowTaskTemplate
import com.force5solutions.care.workflow.ApsWorkflowTaskType

pre {
    ApsWorkflowTaskTemplate templateToBeModified = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_7_DAYS_ESCALATE_VP_TASK_TEMPLATE)
    templateToBeModified.with {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_7D_REVOKE_REQUEST_ESCALATION)
        actions = ['CONFIRM']
        workflowTaskType = ApsWorkflowTaskType.HUMAN
        responseForm = 'userResponse'
        actorSlids = 'vp-1, vp-2, vp-3'
    }
}