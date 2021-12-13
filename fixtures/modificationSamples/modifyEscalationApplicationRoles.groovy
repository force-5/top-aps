import com.force5solutions.care.aps.ApsApplicationRole
import com.force5solutions.care.aps.ApsMessageTemplate
import com.force5solutions.care.common.CareConstants
import com.force5solutions.care.ldap.SecurityRole
import com.force5solutions.care.workflow.ApsWorkflowTaskTemplate
import com.force5solutions.care.workflow.ApsWorkflowTaskType

pre {
    ApsWorkflowTaskTemplate templateToBeModified = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_7_DAYS_PROVISIONER_TASK_TEMPLATE)
    templateToBeModified.with {
        period = 3
        periodUnit = com.force5solutions.care.cc.PeriodUnit.DAYS
        escalationTemplate = ApsWorkflowTaskTemplate.findById(CareConstants.APS_REVOKE_7_DAYS_ESCALATE_GM_TASK_TEMPLATE)
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_7D_REVOKE_REQUEST)
        actions = ['CONFIRMx']
        workflowTaskType = ApsWorkflowTaskType.HUMAN
        responseForm = 'userResponse'
        actorApplicationRoles = [ApsApplicationRole.DEPROVISIONER, ApsApplicationRole.ROLE_OWNER] as Set
        actorSecurityRoles = [SecurityRole.findByName(CareConstants.CAREADMIN)] as Set
    }
}
