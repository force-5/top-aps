package com.force5solutions.care.workflow

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class EscalationJob {

    def escalationService
    def concurrent = false;

    static triggers = {
        cron name: 'escalationTrigger',
                group: 'topJobsGroup',
                cronExpression: ConfigurationHolder.config.farAheadCronExpression
    }

    def execute() {
        log.info "Executing Escalation Job at ${new Date()}"
        escalationService.escalateTasks(new Date())
    }
}
