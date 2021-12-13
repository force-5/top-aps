package com.force5solutions.care.aps.pages.origin

import geb.*

class OriginListPage extends Page {
    static url =  "origin/list"

    static at = { title == "Origin List" }

    static content = {
        createOriginLink(to: OriginCreatePage) { $ ("a", text: "New Origin") }
        showOriginLink(to: OriginShowPage) { $ ("div.list table tbody tr", 0).find("a", 0) }
    }
}