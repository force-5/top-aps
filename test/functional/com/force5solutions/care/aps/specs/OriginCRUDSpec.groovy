package com.force5solutions.care.aps.specs

import com.force5solutions.care.aps.pages.origin.*
import com.force5solutions.care.aps.pages.common.ApsLoginPage
import com.force5solutions.care.aps.Origin
import grails.plugin.remotecontrol.RemoteControl
import com.force5solutions.care.aps.pages.common.ApsLandingPage

class OriginCRUDSpec extends BaseGebSpec {

    def "creating a test origin"() {
        given:
        to(ApsLoginPage)
        go('login/logout')
        assert at(ApsLoginPage)

        when:
        loginModule.loginAs "admin", "admin"
        to(ApsLandingPage)
        go("entitlement/list")
        assert at(ApsLandingPage)
        to(OriginListPage)
        go('origin/list')
        assert at(OriginListPage)
        createOriginLink.click()
        go('origin/create')
        assert at(OriginCreatePage)
        createOrigin(UUID.randomUUID().toString())

        def remote = new RemoteControl()
        def objectId = remote {
            Origin.list().last().id
        }
        go('origin/show/' + objectId)

        then:
        title == "Show Origin"
    }

    def "editing a test origin"() {
        given:
        to(ApsLoginPage)
        go("login/logout")
        assert at(ApsLoginPage)

        when:
        loginModule.loginAs "admin", "admin"
        to(ApsLandingPage)
        go("entitlement/list")
        assert at(ApsLandingPage)
        to(OriginListPage)
        go('origin/list')
        assert at(OriginListPage)
        showOriginLink.click()
        def remote = new RemoteControl()
        def objectId = remote {
            Origin.list().first().id
        }
        go('origin/show/' + objectId)
        assert at(OriginShowPage)
        editOriginButton.click()
        remote = new RemoteControl()
        objectId = remote {
            Origin.list().first().id
        }
        go('origin/edit/' + objectId)
        assert at(OriginEditPage)
        def newName = UUID.randomUUID().toString()
        editOrigin(newName)

        remote = new RemoteControl()
        objectId = remote {
            Origin.list().first().id
        }
        go('origin/show/' + objectId)

        then:
        title == "Show Origin"

        and:
        nameEntry.text() == newName
    }
}
