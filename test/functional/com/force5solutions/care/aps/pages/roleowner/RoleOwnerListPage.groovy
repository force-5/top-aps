package com.force5solutions.care.aps.pages.roleowner

import geb.Page

class RoleOwnerListPage extends Page {
    static url = "roleOwner/list"
    static at = { title == "Role Owner List" }
    static content = {
        createRoleOwnerLink(to: RoleOwnerCreatePage) { $ ("a", text: "New Role Owner") }
        showRoleOwnerLink(to: RoleOwnerShowPage) { $ ("div.list table tbody tr", 0).find("a", 0) }
    }
}