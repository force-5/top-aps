package com.force5solutions.care.aps.pages.entitlementrole

import geb.Page

class EntitlementRoleListPage extends Page {
	static url = 'entitlementRole/list'
	static at = { title == "Entitlement Role List" }
	static content = {
		createEntitlementRoleLink(to: EntitlementRoleCreatePage) { $("a", text: "New Entitlement Role") }
	}
}