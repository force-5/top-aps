package com.force5solutions.care.workflow

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class TriggerProvisionerDeprovisionerTaskOnRoleUpdateJob {

    def careWebService
    def concurrent = false;

    static triggers = {
        cron name: 'triggerProvisionerDeprovisionerTaskOnRoleUpdateCronTrigger',
                group: 'topJobsGroup',
                cronExpression: ConfigurationHolder.config.farAheadCronExpression
    }

    def execute() {
        if (!ConfigurationHolder.config.bootStrapMode) {
            log.info "Executing TriggerProvisioner Deprovisioner Task On Role Update Job at ${new Date()}"
            careWebService.triggerProvisionerDeprovisionerTasksOnRoleUpdate()
        }
    }
}
