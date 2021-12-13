package com.force5solutions.care.aps.specs

import com.force5solutions.care.aps.pages.provisioner.*
import com.force5solutions.care.aps.pages.common.ApsLoginPage
import grails.plugin.remotecontrol.RemoteControl
import com.force5solutions.care.aps.Provisioner

class ProvisionerCRUDSpec extends BaseGebSpec {

    def "creating a test provisioner"() {
        given:
        goTo(ApsLoginPage)
        assert at(ApsLoginPage)

        when:
        loginModule.loginAs "admin", "admin"
        goTo(ProvisionerListPage)
        assert at(ProvisionerListPage)
        createProvisionerLink.click()
        goTo(ProvisionerCreatePage)
        assert at(ProvisionerCreatePage)
        personModule.fillDetailsAndCreate("firstName", "lastName", UUID.randomUUID().toString())

        def remote = new RemoteControl()
        def objectId = remote {
            Provisioner.list().last().id
        }
        goTo('provisioner/show/' + objectId, false)

        then:
        title == "Show Provisioner"
    }

    def "editing a test provisioner"() {
        given:
        goTo (ApsLoginPage)
        assert at (ApsLoginPage)

        when:
        loginModule.loginAs "admin", "admin"
        to (ProvisionerListPage)
        go ('provisioner/list')
        assert at (ProvisionerListPage)
        firstProvisionerLink.click ()
        def remote = new RemoteControl ()
        def objectId = remote {
        Provisioner.list ().first ().id
    }
    go ('provisioner/show/' + objectId)
    assert at (ProvisionerShowPage)
    editProvisionerButton.click ()
    remote = new RemoteControl ()
    objectId = remote {
        Provisioner.list().first().id
    }
    go ('provisioner/edit/' + objectId)
    assert at (ProvisionerEditPage)
    String newName = UUID.randomUUID().toString()
    editProvisioner (newName);

    remote = new RemoteControl ()
    objectId = remote {
        Provisioner.list().first().id
    }
    go ('provisioner/show/' + objectId)

    then:
    title == "Show Provisioner"

    and:
    nameEntry.text () == newName
}
}
