package com.force5solutions.care.aps.pages.roleowner

import geb.Page
import com.force5solutions.care.aps.modules.PersonModule

class RoleOwnerCreatePage extends Page {
    static url = "roleOwner/create"
    static at = { title == "Create Role Owner" }
    static content = {
          personModule {module PersonModule}
    }
}