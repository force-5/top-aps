package com.force5solutions.care.workflow

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class WorkflowRequestJob {
    def apsWorkflowTaskService;
    def concurrent = false;

    static triggers = {
        cron name: 'workflowRequestCronTrigger',
                group: 'topJobsGroup',
                cronExpression: ConfigurationHolder.config.farAheadCronExpression
    }

    def execute() {
        if (!ConfigurationHolder.config.bootStrapMode) {
            log.info "Executing Workflow Request Job at ${new Date()}"
            apsWorkflowTaskService.processNewTasks()
        }
    }
}
