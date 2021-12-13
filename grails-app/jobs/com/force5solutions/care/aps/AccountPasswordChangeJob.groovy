package com.force5solutions.care.aps

import com.force5solutions.care.cp.ConfigProperty
import com.force5solutions.care.workflow.ApsWorkflowTask
import com.force5solutions.care.workflow.ApsWorkflowTaskType
import com.force5solutions.care.workflow.ApsWorkflowType
import com.force5solutions.care.workflow.WorkflowTaskStatus
import groovy.time.TimeCategory
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import java.text.SimpleDateFormat

class AccountPasswordChangeJob {

    def concurrent = false
    def entitlementService
    def apsWorkflowUtilService
    def grailsApplication

    static triggers = {
        cron name: 'accountPasswordChangeTrigger',
                group: 'topJobsGroup',
                cronExpression: ConfigurationHolder.config.farAheadCronExpression
    }

    def execute() {
        log.info "Executing Shared Account Entitlement Password Change Job at ${new Date()}"
        String annualResetDateForAccountPasswordChange = grailsApplication.config.annualResetDateForAccountPasswordChange
        List<Entitlement> entitlements = []
        Date annualResetDate = annualResetDateForAccountPasswordChange.length().equals(10) ? new SimpleDateFormat('MM/dd/yyyy').parse(annualResetDateForAccountPasswordChange) : null
        if (annualResetDate && (annualResetDate < new Date())) {
            entitlements = entitlementService.allEntitlementsWithSharedAccountTrue
            updateTheAnnualResetDateForAccountPasswordChangeConfigProperty(annualResetDate)
        } else if (!annualResetDate) {
            entitlements = entitlementService.allSharedOrGenericAccountEntitlementsRequiringAPasswordChange
        }
        entitlements.each { Entitlement entitlement ->
            if (!existingPasswordChangeTask(entitlement)) {
                log.info "Sending ${entitlement} to the account password change workflow"
                apsWorkflowUtilService.startAccountPasswordChangeWorkflow(entitlement.id)
            }
        }
    }

    boolean existingPasswordChangeTask(Entitlement entitlement) {
        ApsWorkflowTask apsWorkflowTask = ApsWorkflowTask.createCriteria().get {
            eq('type', ApsWorkflowTaskType.HUMAN)
            eq('workflowType', ApsWorkflowType.ACCOUNT_PASSWORD_CHANGE)
            eq('entitlementId', entitlement.id)
            eq('status', WorkflowTaskStatus.NEW)
        }
        return apsWorkflowTask ? true : false
    }

    void updateTheAnnualResetDateForAccountPasswordChangeConfigProperty(Date annualResetDate) {
        ConfigProperty configProperty = ConfigProperty.findByName('annualResetDateForAccountPasswordChange')
        if (configProperty) {
            use(TimeCategory) {
                configProperty.value = (annualResetDate + 1.year).format('MM/dd/yyyy')
            }
            configProperty.s()
        }
    }
}
