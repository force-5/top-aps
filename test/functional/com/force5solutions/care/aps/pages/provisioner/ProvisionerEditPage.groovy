package com.force5solutions.care.aps.pages.provisioner

import geb.Page

class ProvisionerEditPage extends Page {
    static at = { title == "Edit Provisioner" }
    static content = {
        firstName { $ ("input", id: "firstName") }
        createGatekeeperLink(to: ProvisionerCreatePage) {  $ ("a", text: "New Provisioner")  }
        updateButton(to: ProvisionerShowPage) {$ ("input", value: "Update")}
    }

    void editProvisioner(String nameValue) {
        firstName.value(nameValue)
        updateButton.click()
    }
}