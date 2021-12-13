package com.force5solutions.care.aps.specs

import com.force5solutions.care.aps.Gatekeeper
import com.force5solutions.care.aps.pages.common.ApsLoginPage
import com.force5solutions.care.aps.pages.gatekeeper.GatekeeperCreatePage
import com.force5solutions.care.aps.pages.gatekeeper.GatekeeperEditPage
import com.force5solutions.care.aps.pages.gatekeeper.GatekeeperListPage
import com.force5solutions.care.aps.pages.gatekeeper.GatekeeperShowPage
import grails.plugin.remotecontrol.RemoteControl
import spock.lang.Stepwise

@Stepwise
class GatekeeperCRUDSpec extends BaseGebSpec {

    def "creating a test gatekeeper"() {
        given:
        goTo(ApsLoginPage)
//        go("login/logout")
        assert at(ApsLoginPage)

        when:
        loginModule.loginAs "admin", "admin"
        to(GatekeeperListPage)
        go('gatekeeper/list')
        assert at(GatekeeperListPage)
        createGatekeeperLink.click()
        go('gatekeeper/create')
        to(GatekeeperCreatePage)
        assert at(GatekeeperCreatePage)
        personModule.fillDetailsAndCreate("firstName", "lastName", UUID.randomUUID().toString())

        def remote = new RemoteControl()
        def objectId = remote {
            Gatekeeper.list().last().id
        }
        go('gatekeeper/show/' + objectId)

        then:
        title == "Show Gatekeeper"
    }

    def "editing a test gatekeeper"() {
        given:
        to(ApsLoginPage)
        go("login/logout")
        assert at(ApsLoginPage)

        when:
        loginModule.loginAs "admin", "admin"
        to(GatekeeperListPage)
        go('gatekeeper/list')
        assert at(GatekeeperListPage)
        showGatekeeperLink.click()
        def remote = new RemoteControl()
        def objectId = remote {
            Gatekeeper.list().first().id
        }
        go('gatekeeper/show/' + objectId)
        assert at(GatekeeperShowPage)
        editGatekeeperButton.click()
        remote = new RemoteControl()
        objectId = remote {
            Gatekeeper.list().first().id
        }
        go('gatekeeper/edit/' + objectId)
        assert at(GatekeeperEditPage)
        def newName = UUID.randomUUID().toString()
        editGatekeeper(newName)

        remote = new RemoteControl()
        objectId = remote {
            Gatekeeper.list().first().id
        }
        go('gatekeeper/show/' + objectId)

        then:
        title == "Show Gatekeeper"

        and:
        nameEntry.text() == newName
    }
}
