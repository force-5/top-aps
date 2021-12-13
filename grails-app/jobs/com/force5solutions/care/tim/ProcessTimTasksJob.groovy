package com.force5solutions.care.tim

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ProcessTimTasksJob {

    def timService;
    def concurrent = false;

    static triggers = {
        cron name: 'processTimTasksCronTrigger',
                group: 'topJobsGroup',
                cronExpression: ConfigurationHolder.config.farAheadCronExpression
    }

    def execute() {
        log.info "Executing Process TIM Tasks Job at ${new Date()}"
        timService.processNewTasks()
    }
}
