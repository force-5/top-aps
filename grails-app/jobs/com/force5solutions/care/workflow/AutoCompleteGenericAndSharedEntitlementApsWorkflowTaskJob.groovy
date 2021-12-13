package com.force5solutions.care.workflow

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class AutoCompleteGenericAndSharedEntitlementApsWorkflowTaskJob {

    def apsWorkflowTaskService
    def concurrent = false;

    static triggers = {
        cron name: 'autoCompleteGenericAndSharedEntitlementApsWorkflowTaskJobTrigger',
                group: 'topJobsGroup',
                cronExpression: ConfigurationHolder.config.farAheadCronExpression
    }

    def execute() {
        if (!ConfigurationHolder.config.bootStrapMode) {
            log.info "Executing Auto complete Generic And Shared Entitlement ApsWorkflow Task Job at ${new Date()}"
            apsWorkflowTaskService.autoCompleteGenericAndSharedEntitlementApsWorkflowTasks()
        }
    }
}
