package com.force5solutions.care.aps.specs

import com.force5solutions.care.aps.pages.securityrole.*
import com.force5solutions.care.aps.pages.common.ApsLoginPage
import com.force5solutions.care.ldap.SecurityRole
import grails.plugin.remotecontrol.RemoteControl

class SecurityRoleCRUDSpec extends BaseGebSpec {

    def "creating a test security role"() {
        given:
        goTo(ApsLoginPage)

        when:
        loginModule.loginAs "admin", "admin"
        goTo(SecurityRoleListPage)
        createSecurityRoleLink.click()
        goTo(SecurityRoleCreatePage)
        createSecurityRole(UUID.randomUUID().toString(), UUID.randomUUID().toString())

        def remote = new RemoteControl()
        def objectId = remote {
            SecurityRole.list().last().id
        }
        goTo('securityRole/show/' + objectId, false)

        then:
        title == "Show Security Role"
    }

    def "editing a test security role"() {
        given:
        goTo(ApsLoginPage)

        when:
        loginModule.loginAs "admin", "admin"
        goTo(SecurityRoleListPage)
        createSecurityRoleLink.click()
        goTo(SecurityRoleCreatePage)
        createSecurityRole(UUID.randomUUID().toString(), UUID.randomUUID().toString())
        def remote = new RemoteControl()
        def objectId = remote {
            SecurityRole.list().last().id
        }
        goTo('securityRole/show/' + objectId, false)
        goTo(SecurityRoleListPage)
        showSecurityRoleLink.click()
        remote = new RemoteControl()
        objectId = remote {
            SecurityRole.list().last().id
        }
        goTo('securityRole/show/' + objectId, false)
        assert at(SecurityRoleShowPage)
        editSecurityRoleButton.click()
        remote = new RemoteControl()
        objectId = remote {
            SecurityRole.list().last().id
        }
        goTo('securityRole/edit/' + objectId, false)
        assert at(SecurityRoleEditPage)
        String newName = UUID.randomUUID().toString()
        editSecurityRole(newName, UUID.randomUUID().toString())

        remote = new RemoteControl()
        objectId = remote {
            SecurityRole.list().last().id
        }
        goTo('securityRole/show/' + objectId, false)

        then:
        title == "Show Security Role"

        and:
        nameEntry.text() == newName
    }
}
