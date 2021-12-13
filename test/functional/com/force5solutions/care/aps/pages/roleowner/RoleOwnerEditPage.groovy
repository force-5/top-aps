package com.force5solutions.care.aps.pages.roleowner

import geb.Page

class RoleOwnerEditPage extends Page {
    static at = { title == "Edit Role Owner" }
    static content = {
        firstName { $ ("input", id: "firstName") }
        createGatekeeperLink(to: RoleOwnerCreatePage) {  $ ("a", text: "New Provisioner")  }
        updateButton(to: RoleOwnerShowPage) {$ ("input", value: "Update")}
    }

    void editRoleOwner(String nameValue) {
        firstName.value(nameValue)
        updateButton.click()
    }
}