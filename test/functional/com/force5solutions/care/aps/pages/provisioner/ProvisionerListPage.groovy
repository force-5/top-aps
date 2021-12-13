package com.force5solutions.care.aps.pages.provisioner

import geb.Page

class ProvisionerListPage extends Page {
	static url = "provisioner/list"
	static at = { title == "Provisioner List" }
	static content = {
		createProvisionerLink(to: ProvisionerCreatePage) { $("a", text: "New Provisioner") }
		firstProvisionerLink(to: ProvisionerShowPage) { $("div.list table tbody tr", 0).find("a", 0) }
	}
}