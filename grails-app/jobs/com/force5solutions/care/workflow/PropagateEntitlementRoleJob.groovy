package com.force5solutions.care.workflow

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class PropagateEntitlementRoleJob {

    def careWebService
    def concurrent = false;

    static triggers = {
        cron name: 'propogateEntitlementRoleCronTrigger',
                group: 'topJobsGroup',
                cronExpression: ConfigurationHolder.config.farAheadCronExpression
    }

    def execute() {
        if (!ConfigurationHolder.config.bootStrapMode) {
            log.info "Executing Propagate Entitlement Roles Job at ${new Date()}"
            careWebService.propagateEntitlementRoles()
        }
    }
}
