package com.force5solutions.care.aps.modules

import geb.Module

class CreateEntityModule extends Module {
    static content = {
        name {$("input", name: "name")}
        alias {$("input", name: "alias")}
        isExposed {$("input", name: "isExposed")}
        status {$("input", name: "status")}
        owner {$("select", name: "owner.id")}
        createButton {$("input", name: "create")}
    }

    void fillBasicDetails(String nameValue, String ownerValue, String isExposedValue = "true", String statusValue = "ACTIVE") {
        name.value(nameValue)
        status.value(statusValue)
        isExposed.value(isExposedValue)
        selectRoleOwner(ownerValue)
    }

    void selectRoleOwner(String ownerValue) {
        owner.value(ownerValue)
    }
}