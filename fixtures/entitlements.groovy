import com.force5solutions.care.aps.Entitlement
import com.force5solutions.care.ldap.SecurityRole
import com.force5solutions.care.workflow.ApsWorkflowUtilService
import com.force5solutions.care.workflow.ApsWorkflowTask
import org.grails.plugins.versionable.VersioningContext
import com.force5solutions.care.aps.RoleOwner
import com.force5solutions.care.aps.Origin
import org.codehaus.groovy.grails.commons.ApplicationHolder


pre {
    if (!Entitlement.count()) {
        createEntitlementData()
    }
}
fixture {}

void createEntitlementData() {
    def entitlementService = ApplicationHolder.getApplication().getMainContext().getBean('entitlementService')

    Origin manual = Origin.findByName(Origin.MANUAL)

    List<String> names = ['Physical Entitlement', 'Cyber Entitlement', 'Both Entitlements']
    (1..3).each {
        Entitlement entitlement = new Entitlement(isExposed: true, isApproved: true)
        entitlement.name = names.get(it-1)
        entitlement.alias = names.get(it-1)
        entitlement.origin = manual
        entitlement.owner = RoleOwner.count() ? RoleOwner.get(new Random().nextInt(RoleOwner.count()) + 1) : null
        entitlement.gatekeepers = SecurityRole.getAll([1l])
        entitlement.provisioners = SecurityRole.getAll([1l])
        entitlement.deProvisioners = SecurityRole.getAll([1l])
        entitlement.type = (new Random().nextInt(2) + 1)
        entitlementService.saveEntitlement(entitlement)
        sleep(1000)
        Entitlement.withSession {
            VersioningContext.set(UUID.randomUUID().toString())
            ApsWorkflowTask.list().each { task ->
                Map responseElements = ['accessJustification': 'Approved by Central System during bootstrap', 'userAction': 'APPROVE']
                ApsWorkflowUtilService.sendResponseElements(task, responseElements)
            }
        }
    }
}
