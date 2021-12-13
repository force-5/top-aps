package com.force5solutions.care.workflow

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class WorkflowResponseJob {

    def careWebService
    def concurrent = false;

    static triggers = {
        cron name: 'workflowResponseCronTrigger',
                group: 'topJobsGroup',
                cronExpression: ConfigurationHolder.config.farAheadCronExpression
    }

    def execute() {
        if (!ConfigurationHolder.config.bootStrapMode) {
            log.info "Executing Workflow Response Job at ${new Date()}"
            careWebService.sendWorkflowResponsesToCareCentral()
        }
    }
}
