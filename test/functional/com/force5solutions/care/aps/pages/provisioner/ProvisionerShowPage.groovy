package com.force5solutions.care.aps.pages.provisioner

import geb.Page

class ProvisionerShowPage extends Page {
    static at = { title == "Show Provisioner" }
    static content = {
        editProvisionerButton(to: ProvisionerEditPage) { $ ("input", value: "Edit") }
        nameEntry { $ ("div.dialog table tbody tr", 0).find ("td.value") }
    }
}