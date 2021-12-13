package com.force5solutions.care.aps.pages.common

import geb.Page
import com.force5solutions.care.aps.pages.entitlement.EntitlementCreatePage
import com.force5solutions.care.aps.pages.entitlementrole.EntitlementRoleListPage

class ApsLandingPage extends Page {
    static url =  "entitlement/list"
    static at = { title == "Entitlement List" }
    static content = {
        createEntitlementLink(to: EntitlementCreatePage) { $("a", text: "New Entitlement") }
        entitlementRolePageLink(to: EntitlementRoleListPage) { $("a", text: "Entitlement Roles") }
    }
}