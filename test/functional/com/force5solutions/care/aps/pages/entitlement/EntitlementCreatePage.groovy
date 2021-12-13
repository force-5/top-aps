package com.force5solutions.care.aps.pages.entitlement

import geb.Page
import com.force5solutions.care.aps.modules.CreateEntityModule

class EntitlementCreatePage extends Page {
    static at = { title == "Create Entitlement" }
    static content = {
        createModule {module CreateEntityModule}
        entitlementPolicy {$ ("select", name: "type")}
    }
}