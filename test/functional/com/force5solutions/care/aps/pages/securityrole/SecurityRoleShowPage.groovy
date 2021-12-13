package com.force5solutions.care.aps.pages.securityrole

import geb.Page

class SecurityRoleShowPage extends Page {
    static at = { title == "Show Security Role" }
    static content = {
        editSecurityRoleButton(to: SecurityRoleEditPage) { $ ("input", value: "Edit") }
        nameEntry { $ ("div.securityForm table tbody tr", 0).find ("td.value1") }
    }
}