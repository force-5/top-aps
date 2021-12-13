package com.force5solutions.care.aps.pages.origin

import geb.Page

class OriginCreatePage extends Page {
	static url = "origin/create"
	static at = { title == "Create Origin" }
	static content = {
		name {$("input", name: "name")}
		createButton(to: OriginShowPage) {$("input", name: "create")}
	}

	void createOrigin(String nameValue) {
		name.value(nameValue)
		createButton.click()
	}
}