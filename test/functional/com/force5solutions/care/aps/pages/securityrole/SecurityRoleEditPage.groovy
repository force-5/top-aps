package com.force5solutions.care.aps.pages.securityrole

import geb.Page

class SecurityRoleEditPage extends Page {
    static at = { title == "Edit Security Role" }
    static content = {
        name {$ ("input", name: "name")}
        description {$ ("textarea", name: "roleDescription")}
        updateButton(to: SecurityRoleShowPage) {$ ("input", value: "Update")}
    }

    void editSecurityRole(String nameValue, String descriptionValue) {
        name.value(nameValue)
        description.value(descriptionValue)
        updateButton.click()
    }
}