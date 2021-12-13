package com.force5solutions.care.aps.pages.common

import geb.*
import com.force5solutions.care.aps.modules.LoginModule

class ApsLoginPage extends Page {
	static url = "login/logout"
	static at = {
		title == "Login Screen"
	}

	static content = {
		loginModule { module LoginModule }
	}
}