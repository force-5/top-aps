package com.force5solutions.care.aps.pages.provisioner

import geb.Page
import com.force5solutions.care.aps.modules.PersonModule

class ProvisionerCreatePage extends Page {
    static url = "provisioner/create"
    static at = { title == "Create Provisioner" }
    static content = {
       personModule {module PersonModule}
    }
}