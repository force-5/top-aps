package com.force5solutions.care.aps

class EntitlementInfoFromFeedController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [entitlementInfoFromFeedList: EntitlementInfoFromFeed.list(params), entitlementInfoFromFeedTotal: EntitlementInfoFromFeed.count()]
    }

    def show = {
        def entitlementInfoFromFeed = EntitlementInfoFromFeed.get(params.id)
        if (!entitlementInfoFromFeed) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'entitlementInfoFromFeed.label', default: 'EntitlementInfoFromFeed'), params.id])}"
            redirect(action: "list")
        } else {
            [entitlementInfoFromFeed: entitlementInfoFromFeed]
        }
    }
}
