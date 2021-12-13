package com.force5solutions.care.tim

import com.force5solutions.care.aps.Origin
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class CreateEntitlementsJob {

    def timService
    def entitlementService
    def concurrent = false;

    static triggers = {
        cron name: 'createEntitlementsCronTrigger',
                group: 'topJobsGroup',
                cronExpression: ConfigurationHolder.config.farAheadCronExpression
    }

    def execute() {
        log.info "Executing Create TIM Entitlements Job at ${new Date()}"
        Origin origin = Origin.findByName(Origin.TIM_FEED)
        entitlementService.createTimEntitlements(timService.getRoles(), origin)
    }
}
