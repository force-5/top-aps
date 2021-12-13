package com.force5solutions.care.aps.pages.securityrole

import geb.Page

class SecurityRoleListPage extends Page {
    static url = "securityRole/list"
    static at = { title == "Security Role List" }
    static content = {
        createSecurityRoleLink(to: SecurityRoleCreatePage) { $ ("a", text: "New Security Role") }
        showSecurityRoleLink(to: SecurityRoleShowPage) { $ ("div.list table tbody tr", 2).find("a", 0) } // 2 is given here as we want to edit a security role which is already not assigned to anyone
    }
}