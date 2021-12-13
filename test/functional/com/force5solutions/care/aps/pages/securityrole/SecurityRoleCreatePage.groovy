package com.force5solutions.care.aps.pages.securityrole

import geb.Page

class SecurityRoleCreatePage extends Page {
    static url = "securityRole/create"
    static at = { title == "Create Security Role" }
    static content = {
        name {$ ("input", name: "name")}
        description {$ ("textarea", id: "roleDescription")}
        saveButton(to: SecurityRoleShowPage) {$ ("input", value: "Save")}
    }

    void createSecurityRole(String nameValue, String descriptionValue) {
        name.value(nameValue)
        description.value(descriptionValue)
        saveButton.click()
    }
}