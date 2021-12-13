package com.force5solutions.care.workflow

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class AutoConfirmGatekeeperRevocationResponseTimeOutJob {

    def apsWorkflowTaskService
    def concurrent = false;

    static triggers = {
        cron name: 'autoConfirmGatekeeperRevocationResponseTimeOutCronTrigger',
                group: 'topJobsGroup',
                cronExpression: ConfigurationHolder.config.farAheadCronExpression
    }

    def execute() {
        if (!ConfigurationHolder.config.bootStrapMode) {
            log.info "Executing Auto Confirm Gatekeeper Revocation Response Time Out Job at ${new Date()}"
            apsWorkflowTaskService.autoConfirmGatekeeperResponseForAccessRevocationTasks(new Date())
        }
    }
}
