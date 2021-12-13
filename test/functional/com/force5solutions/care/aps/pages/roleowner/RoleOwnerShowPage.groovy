package com.force5solutions.care.aps.pages.roleowner

import geb.Page

class RoleOwnerShowPage extends Page {
    static at = { title == "Show Role Owner" }
    static content = {
        editRoleOwnerButton(to: RoleOwnerEditPage) { $ ("input", value: "Edit") }
        nameEntry { $ ("div.dialog table tbody tr", 0).find ("td.value") }
    }
}