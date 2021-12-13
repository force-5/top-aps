package com.force5solutions.care.aps.pages.gatekeeper

import geb.Page
import com.force5solutions.care.aps.modules.PersonModule

class GatekeeperCreatePage extends Page {
    static url = "gatekeeper/create"
    static at = { title == "Create Gatekeeper" }
    static content = {
        personModule {module PersonModule}
    }
}