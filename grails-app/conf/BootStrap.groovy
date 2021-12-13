import com.force5solutions.care.aps.Entitlement
import com.force5solutions.care.aps.EntitlementRole
import com.force5solutions.care.aps.RoleOwner
import com.force5solutions.care.cc.EntitlementPolicy
import com.force5solutions.care.cc.EntitlementRoleAccessStatus
import com.force5solutions.care.cc.Worker
import com.force5solutions.care.cc.WorkerEntitlementRole
import com.force5solutions.care.common.MetaClassHelper
import com.force5solutions.care.common.SessionUtils
import com.force5solutions.care.ldap.TopUser
import grails.util.Environment
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.force5solutions.care.common.CannedResponse

class BootStrap {

    def fixtureLoader
//    def timService
    private static final log = LogFactory.getLog(this)

    def init = { servletContext ->
        ConfigurationHolder.config.bootStrapMode = true
        MetaClassHelper.enrichClasses();

        fixtureLoader.load "configProperties"
        if (!TopUser.count()) {
            fixtureLoader.load "master"
        }

        if ((Environment.currentEnvironment != Environment.PRODUCTION) && (Environment.currentEnvironment.name != "qa") && (Environment.currentEnvironment.name != "demo")) {
            log.info "Bootstrapping Test Data"
            bootstrapDummyData()
        }

        if (!CannedResponse.count()) {
            fixtureLoader.load "cannedResponses"
        }

        if ((Environment.currentEnvironment == Environment.TEST)) {
            if (!EntitlementPolicy.count()) {
                fixtureLoader.load "entitlementPolicies"
            }
        }

//        populateTimDummyImplementationMaps()

        fixtureLoader.load "customTags"
        fixtureLoader.load "messageTemplates"
        fixtureLoader.load "apsWorkflowTaskTemplates"

        ConfigurationHolder.config.bootStrapMode = false
        SessionUtils.setSession(null)
    }

    public void bootstrapDummyData() {
        Map mockSession = new HashMap()
        mockSession.isContractorImageUpdated = false
        mockSession.loggedUser = "Test User"
        SessionUtils.setSession(mockSession)

        if (!RoleOwner.count()) {
            fixtureLoader.load "dummyData"
        }
    }


//    public void populateTimDummyImplementationMaps() {
//        if ((ApplicationHolder.getApplication().getMainContext().getBean('timApiWrapper').getClass().getName() == 'com.force5solutions.tim.wrapper.TimApiWrapperDummyImpl')) {
//            log.info "Populating TIM Dummy Implementation Maps"
//            List<String> timRoles = timService.getRoles()
//            Worker.list().each { Worker worker ->
//                Set<WorkerEntitlementRole> workerEntitlementRoles = worker.entitlementRoles?.findAll { it.status in [EntitlementRoleAccessStatus.active, EntitlementRoleAccessStatus.pendingRevocation, EntitlementRoleAccessStatus.pendingTermination, EntitlementRoleAccessStatus.error] }
//                List<Entitlement> workerTimEntitlements = []
//                if (workerEntitlementRoles) {
//                    workerEntitlementRoles.each { WorkerEntitlementRole workerEntitlementRole ->
//                        EntitlementRole role = EntitlementRole.findById(workerEntitlementRole.entitlementRole.id)
//                        role?.entitlements?.findAll { it?.name in timRoles }?.each {
//                            workerTimEntitlements?.add(it)
//                        }
//                    }
//                }
//                if (workerTimEntitlements) {
//                    timService.createPerson(worker, workerTimEntitlements*.name)
//                }
//            }
//        }
//    }

    def destroy = {
    }
}