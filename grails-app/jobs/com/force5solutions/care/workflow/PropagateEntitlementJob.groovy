package com.force5solutions.care.workflow

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class PropagateEntitlementJob {

    def careWebService
    def concurrent = false;

    static triggers = {
        cron name: 'propogateEntitlementCronTrigger',
                group: 'topJobsGroup',
                cronExpression: ConfigurationHolder.config.farAheadCronExpression
    }

    def execute() {
        if (!ConfigurationHolder.config.bootStrapMode) {
            log.info "Executing Propagate Entitlements Job at ${new Date()}"
            careWebService.propagateEntitlements()
        }
    }
}
