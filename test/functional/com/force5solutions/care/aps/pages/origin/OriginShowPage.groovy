package com.force5solutions.care.aps.pages.origin

import geb.Page

class OriginShowPage extends Page {
    static at = { title == "Show Origin" }
    static content = {
        editOriginButton(to: OriginEditPage) { $("input", value: "Edit") }
        nameEntry { $("div.dialog table tbody tr",0).find("td.value") }
    }

}