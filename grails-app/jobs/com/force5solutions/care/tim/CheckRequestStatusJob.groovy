package com.force5solutions.care.tim

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class CheckRequestStatusJob {

    def timService
    def concurrent = false

    static triggers = {
        cron name: 'checkRequestStatusCronTrigger',
                group: 'topJobsGroup',
                cronExpression: ConfigurationHolder.config.farAheadCronExpression
    }

    def execute() {
        log.info "Executing Check Request Status Job at ${new Date()}"
        timService.checkRequestStatus()
    }
}
