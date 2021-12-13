import com.force5solutions.care.aps.Entitlement
import com.force5solutions.care.ldap.SecurityRole
import com.force5solutions.care.workflow.ApsWorkflowUtilService
import com.force5solutions.care.workflow.ApsWorkflowTask
import org.grails.plugins.versionable.VersioningContext
import com.force5solutions.care.aps.RoleOwner
import com.force5solutions.care.aps.EntitlementRole
import org.codehaus.groovy.grails.commons.ApplicationHolder


pre {
    if (!EntitlementRole.count()) {
        createEntitlementRoles()
    }
}

fixture {}

void createEntitlementRoles() {
    def entitlementRoleService = ApplicationHolder.getApplication().getMainContext().getBean('entitlementRoleService')
    List<String> tagList = ['Tag-1', 'Tag-2', 'Tag-3', 'Tag-4', 'Tag-5']
    List<String> names = ['Physical ER', 'Cyber ER', 'Both ER']
    (1..3).each {
        int index = new Random().nextInt(4)
        EntitlementRole role = new EntitlementRole()
        role.name = (names.get(it - 1))
        role.isExposed = true
        role.owner = RoleOwner.count() ? RoleOwner.get(new Random().nextInt(RoleOwner.count()) + 1) : null
        role.gatekeepers = SecurityRole.getAll([2l])
        List<Entitlement> approvedEntitlements = Entitlement.listApproved()
        role.entitlements = [approvedEntitlements.get(new Random().nextInt(approvedEntitlements.size()))]
        role.notes = "Entitlement Role with Entitlements - ${role.entitlements?.join(', ')}"
        role.tags = tagList[index] + ', ' + tagList[index + 1]
        entitlementRoleService.save(role)
        sleep(1000)
        EntitlementRole.withSession {
            VersioningContext.set(UUID.randomUUID().toString())
            ApsWorkflowTask.list().each { task ->
                Map responseElements = ['accessJustification': 'Approved by Central System during bootstrap', 'userAction': 'APPROVE']
                ApsWorkflowUtilService.sendResponseElements(task, responseElements)
            }
        }
    }
}