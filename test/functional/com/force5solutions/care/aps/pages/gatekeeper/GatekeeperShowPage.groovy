package com.force5solutions.care.aps.pages.gatekeeper

import geb.Page

class GatekeeperShowPage extends Page {
    static at = { title == "Show Gatekeeper" }
    static content = {
        editGatekeeperButton(to: GatekeeperEditPage) { $("input", value: "Edit") }
        nameEntry { $("div.dialog table tbody tr",0).find("td.value") }
    }
}