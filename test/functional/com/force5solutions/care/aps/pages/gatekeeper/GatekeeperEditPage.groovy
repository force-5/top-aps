package com.force5solutions.care.aps.pages.gatekeeper

import geb.Page

class GatekeeperEditPage extends Page {
    static at = { title == "Edit Gatekeeper" }
    static content = {
        firstName { $ ("input", id: "firstName") }
        createGatekeeperLink(to: GatekeeperCreatePage) {  $ ("a", text: "New Gatekeeper")  }
        updateButton(to: GatekeeperShowPage) {$ ("input", value: "Update")}
    }

    void editGatekeeper(String nameValue) {
        firstName.value(nameValue)
        updateButton.click()
    }
}