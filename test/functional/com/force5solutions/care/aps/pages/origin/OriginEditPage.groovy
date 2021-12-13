package com.force5solutions.care.aps.pages.origin

import geb.Page

class OriginEditPage extends Page {
    static at = { title == "Edit Origin" }
    static content = {
        name {$ ("input", name: "name")}
        updateButton(to: OriginShowPage) {$ ("input", value: "Update")}
    }

    void editOrigin(String nameValue) {
        name.value(nameValue)
        updateButton.click()
    }
}