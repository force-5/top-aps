package com.force5solutions.care.aps.pages.gatekeeper

import geb.Page

class GatekeeperListPage extends Page {
    static url = "gatekeeper/list"
    static at = { title == "Gatekeeper List" }
    static content = {
        createGatekeeperLink(to: GatekeeperCreatePage) { $ ("a", text: "New Gatekeeper") }
        showGatekeeperLink(to: GatekeeperShowPage) { $ ("div.list table tbody tr", 0).find("a", 0) }
    }
}