package com.force5solutions.care.aps.modules

import geb.Module

class PersonModule extends Module {
    static content = {
        firstName {$("input", id: "firstName")}
        lastName {$("input", id: "lastName")}
        slid {$("input", id: "slid")}
        createButton {$("input", name: "create")}
    }

    void fillDetailsAndCreate(String fname, String lname, String slidValue) {
        firstName.value(fname)
        lastName.value(lname)
        slid.value(slidValue)
        createButton.click()
    }
}