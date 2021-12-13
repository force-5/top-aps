package com.force5solutions.care.aps.pages.entitlementrole

import com.force5solutions.care.aps.modules.CreateEntityModule
import geb.Page

class EntitlementRoleCreatePage extends Page {
    static url = 'entitlementRole/create'
    static at = { title == "Create Entitlement Role" }
    static content = {
        createModule {module CreateEntityModule}

        gatekeepersHiddenInput {$("input", name: "gatekeepers")}
        entitlementsHiddenInput {$("input", name: "entitlementIds")}
        rolesHiddenInput {$("input", name: "roles")}
    }
}