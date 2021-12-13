package com.force5solutions.care.aps

class TimEntitlementsAccessLog {

    String workerSlid
    String entitlements
    Date dateCreated
    Date lastUpdated
    String comment

    TimEntitlementsAccessLog(){}

    TimEntitlementsAccessLog(String slid, String entitlements, String comment){
        this.workerSlid = slid
        this.entitlements = entitlements
        this.comment = comment
    }

    static constraints = {
        entitlements(nullable: true, blank: true)
        comment(nullable: true, blank: true)
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
    }
}
