package com.force5solutions.care.aps.specs

import com.force5solutions.care.aps.pages.roleowner.*
import com.force5solutions.care.aps.pages.common.ApsLoginPage
import com.force5solutions.care.aps.RoleOwner
import grails.plugin.remotecontrol.RemoteControl

class RoleOwnerCRUDSpec extends BaseGebSpec {

    def "creating a test role owner"() {
        given:
        goTo(ApsLoginPage)
        assert at(ApsLoginPage)

        when:
        loginModule.loginAs "admin", "admin"
        goTo(RoleOwnerListPage)
        assert at(RoleOwnerListPage)
        createRoleOwnerLink.click()
        goTo(RoleOwnerCreatePage)
        assert at(RoleOwnerCreatePage)
        personModule.fillDetailsAndCreate("firstName", "lastName", UUID.randomUUID().toString())

        def remote = new RemoteControl()
        def objectId = remote {
            RoleOwner.list().last().id
        }
        goTo('roleOwner/show/' + objectId, false)

        then:
        title == "Show Role Owner"
    }

    def "editing a test role owner"() {
        given:
        to(ApsLoginPage)
        go("login/logout")
        assert at(ApsLoginPage)

        when:
        loginModule.loginAs "admin", "admin"
        goTo(RoleOwnerListPage)
        assert at(RoleOwnerListPage)
        showRoleOwnerLink.click()
        def remote = new RemoteControl()
        def objectId = remote {
            RoleOwner.list().first().id
        }
        goTo('roleOwner/show/' + objectId, false)
        assert at(RoleOwnerShowPage)
        editRoleOwnerButton.click()
        remote = new RemoteControl()
        objectId = remote {
            RoleOwner.list().first().id
        }
        goTo('roleOwner/edit/' + objectId, false)
        assert at(RoleOwnerEditPage)
        String newName = UUID.randomUUID().toString()
        editRoleOwner(newName)

        remote = new RemoteControl()
        objectId = remote {
            RoleOwner.list().first().id
        }
        goTo('roleOwner/show/' + objectId, false)

        then:
        title == "Show Role Owner"

        and:
        nameEntry.text() == newName
    }
}