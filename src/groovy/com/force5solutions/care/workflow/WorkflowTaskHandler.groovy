package com.force5solutions.care.workflow

import com.force5solutions.care.aps.ApsApplicationRole
import com.force5solutions.care.aps.GenericAndSharedEntitlementApsWorkflowTask
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.drools.persistence.gorm.GORMKnowledgeService
import org.drools.runtime.StatefulKnowledgeSession
import org.drools.runtime.process.WorkItem
import org.drools.runtime.process.WorkItemManager
import com.force5solutions.care.aps.ApsUtilService
import org.apache.commons.logging.LogFactory
import com.force5solutions.care.aps.Entitlement
import com.force5solutions.care.common.CareConstants

class WorkflowTaskHandler implements org.drools.runtime.process.WorkItemHandler {

    def applicationContext = ApplicationHolder.getApplication().getMainContext()
    static config = ConfigurationHolder.config
    def apsWorkflowTaskService = applicationContext.getBean('apsWorkflowTaskService')
    def apsUtilService = applicationContext.getBean('apsUtilService')
    private static final log = LogFactory.getLog(this)

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

        log.debug "Executing NEW APS WORKFLOW TASK HANDLER : " + workItem.parameters

        ApsWorkflowType workflowType = workItem.parameters.workflowType
        String filePath = workflowType.workflowFilePath

        def (kbase, env) = ApsWorkflowUtilService.createKnowledgeBaseForFlows(filePath)
        StatefulKnowledgeSession knowledgeSession = GORMKnowledgeService.loadStatefulKnowledgeSession(workItem.parameters.droolsSessionId.toInteger(), kbase, null, env);

        ApsWorkflowTask task = new ApsWorkflowTask()
        task.workItemId = workItem.id
        task.workflowType = workflowType
        task.workerEntitlementRoleId = workItem.parameters.workerEntitlementRoleId
        task.droolsSessionId = workItem.parameters.droolsSessionId
        task.effectiveStartDate = workItem.parameters.effectiveStartDate ?: new Date()
        task.workflowGuid = workItem.parameters.workflowGuid
        task.entitlementRoleId = workItem.parameters.entitlementRoleId
        task.entitlementId = workItem.parameters.entitlementId
        task.workerId = workItem.parameters.workerId
        task.entitlementInfoFromFeedId = workItem.parameters.entitlementInfoFromFeedId
        task.actorSlid = workItem.parameters.actorSlid
        task.message = workItem.parameters.message
        task.nodeName = knowledgeSession.getProcessInstance(workItem.processInstanceId).getNodeInstances()[0].nodeName
        task.nodeId = knowledgeSession.getProcessInstance(workItem.processInstanceId).getNodeInstances()[0].nodeId
        task.provisionerDeprovisionerTaskOnRoleUpdateGuid = workItem.parameters.provisionerDeprovisionerTaskOnRoleUpdateGuid

        String messageTemplateName

        String workflowTaskTemplateName = workItem.parameters.workflowTaskTemplate ?: workItem.parameters.revocationWorkflowTaskTemplate
        ApsWorkflowTaskTemplate taskTemplate
        if (workflowTaskTemplateName) {
            taskTemplate = ApsWorkflowTaskTemplate.findById(workflowTaskTemplateName)
            if (taskTemplate) {
                task.period = taskTemplate.period
                task.periodUnit = taskTemplate.periodUnit
                task.actions = taskTemplate.actions as List
                task.securityRoles = taskTemplate.actorSecurityRoles*.name
                Collection<String> securityRoles = ApsUtilService.getSecurityRolesOrSlidsByApplicationRole(taskTemplate.actorApplicationRoles.findAll { it in [ApsApplicationRole.GATEKEEPER, ApsApplicationRole.PROVISIONER, ApsApplicationRole.DEPROVISIONER] }, task.entitlement ?: (task.entitlementRole ?: task.workerEntitlementRole), taskTemplate.respectExclusionList)
                Collection<String> slids = ApsUtilService.getSecurityRolesOrSlidsByApplicationRole(taskTemplate.actorApplicationRoles.findAll { it in [ApsApplicationRole.WORKER, ApsApplicationRole.BUSINESS_UNIT_REQUESTER, ApsApplicationRole.ROLE_OWNER, ApsApplicationRole.SUPERVISOR] }, task.entitlement ?: (task.entitlementRole ?: task.workerEntitlementRole), taskTemplate.respectExclusionList)
                if (securityRoles) {
                    task.securityRoles.addAll(securityRoles)
                }
                if (slids) {
                    if (taskTemplate.actorSlids) {
                        slids.addAll(taskTemplate.actorSlids.tokenize(', '))
                    }
                    slids?.each {
                        task.addToPermittedSlids(new ApsWorkflowTaskPermittedSlid(slid: it))
                    }
                }
                task.responseForm = taskTemplate.responseForm
                task.type = taskTemplate.workflowTaskType
                task.escalationTemplateId = taskTemplate.escalationTemplate?.id
                messageTemplateName = taskTemplate.messageTemplate?.name
            }
        }
        if (task.workflowType.equals(ApsWorkflowType.PROVISIONER_DEPROVISIONER_TASKS_ON_ROLE_UPDATE)) {
            task.actions = ['CONFIRM']
            task.nodeName = task?.responseForm ? (task.responseForm.contains("revocation")) ? "Deprovisioner Task" : "Provisioner Task" : task.nodeName
        }

        if (task?.entitlementId) {
            Entitlement entitlement = Entitlement.findById(task.entitlementId)
            if (task?.nodeName in ['Pending Approval from Entitlement Provisioner', 'Provisioner Task'] && entitlement?.toBeAutoProvisioned) {
                task.isAutoProvisionedDeprovisionedTask = true
                if (task.workflowType.equals(ApsWorkflowType.PROVISIONER_DEPROVISIONER_TASKS_ON_ROLE_UPDATE)) {
                    new GenericAndSharedEntitlementApsWorkflowTask(workflowGuid: task.workflowGuid, entitlementId: entitlement.id).s()
                }
            } else if (task?.nodeName in ['Entitlement Revoke Request', 'Deprovisioner Task'] && entitlement?.toBeAutoDeprovisioned) {
                task.isAutoProvisionedDeprovisionedTask = true
                if (task.workflowType.equals(ApsWorkflowType.PROVISIONER_DEPROVISIONER_TASKS_ON_ROLE_UPDATE)) {
                    new GenericAndSharedEntitlementApsWorkflowTask(workflowGuid: task.workflowGuid, entitlementId: entitlement.id).s()
                }
            }
        }

        String taskStatus = workItem.parameters.taskStatus
        if (taskStatus) {
            task.status = WorkflowTaskStatus."${taskStatus}"
        }
        task.s()

        Object object = task.entitlement ?: (task.workerEntitlementRole ?: (task.entitlementRole ?: task.worker))
        if (!(taskTemplate.id in [CareConstants.WORKFLOW_TASK_TEMPLATE_ACCESS_CONFIRM_BY_PROVISIONER, CareConstants.APS_REVOKE_7_DAYS_PROVISIONER_TASK_TEMPLATE, CareConstants.APS_REVOKE_24_HOURS_PROVISIONER_TASK_TEMPLATE]) && taskTemplate.messageTemplate && object) {
            Map parameters = ApsUtilService.getParametersForMessageTemplate(object)
            if (task.workerEntitlementRole) {
                parameters.putAll(apsWorkflowTaskService.populateWorkflowParametersFromWorkerEntitlementRole(task.workerEntitlementRole, parameters))
            }
            parameters.putAll(apsWorkflowTaskService.populateWorkflowParametersForRevocationTable(task, task.workflowGuid, parameters))
            if (task?.entitlementId) {
                parameters['entitlement'] = Entitlement.findById(task.entitlementId)
            }
            apsUtilService.sendNotification(taskTemplate, taskTemplate.messageTemplate, object, parameters)
        }

        if (task.status == WorkflowTaskStatus.COMPLETE) {
            manager.completeWorkItem(workItem.id, null);
        }
    }

    void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
