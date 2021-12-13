package com.force5solutions.care.workflow

import com.force5solutions.care.cc.AppUtil
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class EscalationService {

    boolean transactional = true
    def apsWorkflowTaskService

    def escalateTasks(Date currentDateTime = new Date()) {
        log.debug "Checking for tasks to be escalated at ${currentDateTime.myDateTimeFormat()}"
        List<ApsWorkflowTask> tasksToBeEscalated = findTasksToBeEscalated(currentDateTime)
        List<ApsWorkflowTask> provisionerDeprovisionerTasks = tasksToBeEscalated.findAll {it.nodeName in ['Pending Approval from Entitlement Provisioner', 'Entitlement Revoke Request']}
        tasksToBeEscalated.removeAll(provisionerDeprovisionerTasks)
        tasksToBeEscalated.each {ApsWorkflowTask taskToBeEscalated ->
            apsWorkflowTaskService.escalateWorkflowTask(taskToBeEscalated)
        }
        Integer outerCircleAccessLayer = (ConfigurationHolder.config.outerCircleAccessLayer && !ConfigurationHolder.config.outerCircleAccessLayer.toString().equalsIgnoreCase('null')) ? ConfigurationHolder.config.outerCircleAccessLayer.toString().toInteger() : null
        if (outerCircleAccessLayer) {
            provisionerDeprovisionerTasks = provisionerDeprovisionerTasks.findAll {((it?.entitlement?.accessLayer != null && (it.entitlement.accessLayer <= outerCircleAccessLayer)))}
        }
        if (provisionerDeprovisionerTasks) {
            apsWorkflowTaskService.escalateProvisionerDeprovisionerWorkflowTasks(provisionerDeprovisionerTasks)
        }
    }

    private List<ApsWorkflowTask> findTasksToBeEscalated(Date currentDateTime) {
        List<ApsWorkflowTask> tasksToBeEscalated = []
        List<ApsWorkflowTask> candidatesForEscalation = ApsWorkflowTask.findAllByStatusInListAndEscalationTemplateIdIsNotNull([WorkflowTaskStatus.NEW, WorkflowTaskStatus.PENDING])

        candidatesForEscalation.each {ApsWorkflowTask task ->
            if (AppUtil.getDatePlusPeriodUnit(task.effectiveStartDate, task.period, task.periodUnit) < currentDateTime) {
                tasksToBeEscalated.add(task)
            }
        }
        return tasksToBeEscalated
    }
}
