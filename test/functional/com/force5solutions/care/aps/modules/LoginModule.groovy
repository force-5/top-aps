package com.force5solutions.care.aps.modules

import geb.Module
import com.force5solutions.care.aps.pages.common.ApsLandingPage

class LoginModule extends Module {
    static at = { title == "Login Screen" }
    static content = {
        loginBox {  $ ("input", name: "slid")  }
        passwordBox {  $ ("input", name: "password")  }
        submitButton(to: ApsLandingPage) {  $ ("input", name: "loginbtn")  }
    }

    void loginAs(String userName, String password) {
        loginBox.value(userName)
        passwordBox.value(password)
        submitButton.click()
    }
}