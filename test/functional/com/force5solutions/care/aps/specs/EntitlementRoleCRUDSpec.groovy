package com.force5solutions.care.aps.specs

import com.force5solutions.care.aps.EntitlementRole
import com.force5solutions.care.aps.pages.common.ApsLandingPage
import com.force5solutions.care.aps.pages.common.ApsLoginPage
import com.force5solutions.care.aps.pages.entitlementrole.EntitlementRoleCreatePage
import com.force5solutions.care.aps.pages.entitlementrole.EntitlementRoleListPage
import grails.plugin.remotecontrol.RemoteControl
import geb.Browser
import org.openqa.selenium.htmlunit.HtmlUnitDriver

class EntitlementRoleCRUDSpec extends BaseGebSpec {


    def setupSpec() {
        Browser.drive {
            HtmlUnitDriver htmlUnitDriver = new HtmlUnitDriver(true)
            htmlUnitDriver.setJavascriptEnabled(true)
            if (browser.driver instanceof HtmlUnitDriver) {
                browser.driver.setJavascriptEnabled(true)
            }
        }
    }

	def "creating a test entitlement role with one entitlement and a gatekeeper"() {
		given:

		goTo(ApsLoginPage)
		assert at(ApsLoginPage)

		when:
		loginModule.loginAs "admin", "admin"
		to(ApsLandingPage)
		go("entitlement/list")
		assert at(ApsLandingPage)
		entitlementRolePageLink.click()
		goTo(EntitlementRoleListPage)
		assert at(EntitlementRoleListPage)
		createEntitlementRoleLink.click()
		goTo(EntitlementRoleCreatePage)
		assert at(EntitlementRoleCreatePage)
		String ownerId = $("select", name: "owner.id").find("option")[1].value()
		createModule.fillBasicDetails(UUID.randomUUID().toString(), ownerId)

		js.exec("jQuery('#gatekeepers-select>option:last').attr('selected', 'selected')");
		js.exec("jQuery('#gatekeepers-select').change()");

		js.exec("jQuery('#entitlementIds-select>option:last').attr('selected', 'selected')");
		js.exec("jQuery('#entitlementIds-select').change()");


		createModule.createButton.click()

		def remote = new RemoteControl()
		def objectId = remote {
			EntitlementRole.list().last().id
		}
		go('entitlementRole/show/' + objectId)

		then:
		title == "Show Entitlement Role"
	}

	def "creating a test entitlement role with one entitlement, a gatekeeper and an associated role"() {
		given:
		goTo(ApsLoginPage)
		assert at(ApsLoginPage)

		when:
		loginModule.loginAs "admin", "admin"
		goTo(ApsLandingPage)
		assert at(ApsLandingPage)
		entitlementRolePageLink.click()
		goTo(EntitlementRoleListPage)
		assert at(EntitlementRoleListPage)
		createEntitlementRoleLink.click()
		goTo(EntitlementRoleCreatePage)
		assert at(EntitlementRoleCreatePage)
		String ownerId = $("select", name: "owner.id").find("option")[1].value()
		createModule.fillBasicDetails(UUID.randomUUID().toString(), ownerId)

		js.exec("jQuery('#gatekeepers-select>option:last').attr('selected', 'selected')");
		js.exec("jQuery('#gatekeepers-select').change()");

		js.exec("jQuery('#entitlementIds-select>option:last').attr('selected', 'selected')");
		js.exec("jQuery('#entitlementIds-select').change()");

		js.exec("jQuery('#roles-select>option:last').attr('selected', 'selected')");
		js.exec("jQuery('#roles-select').change()");
		createModule.createButton.click()

		def remote = new RemoteControl()
		def objectId = remote {
			EntitlementRole.list().last().id
		}
		goTo('entitlementRole/show/' + objectId, false)

		then:
		title == "Show Entitlement Role"
	}
}
