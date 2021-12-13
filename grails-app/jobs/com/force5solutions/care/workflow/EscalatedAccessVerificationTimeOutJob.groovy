package com.force5solutions.care.workflow

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class EscalatedAccessVerificationTimeOutJob {

    def apsWorkflowTaskService
    def concurrent = false;

    static triggers = {
        cron name: 'escalatedAccessVerificationTimeOutCronTrigger',
                group: 'topJobsGroup',
                cronExpression: ConfigurationHolder.config.farAheadCronExpression
    }

    def execute() {
        if (!ConfigurationHolder.config.bootStrapMode) {
            log.info "Executing Escalated Access Verification Job at ${new Date()}"
            apsWorkflowTaskService.autoConfirmAccessVerificationTasks(new Date())
        }
    }
}
