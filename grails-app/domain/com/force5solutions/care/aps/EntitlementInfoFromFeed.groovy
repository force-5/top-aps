package com.force5solutions.care.aps


class EntitlementInfoFromFeed {
    String entitlementName
    String entitlementId
    Date dateCreated
    Date lastUpdated
    Boolean isProcessed = false
    String workflowType
    List<String> areaAttributes
    List<String> readerAttributes

    static hasMany = [areaAttributes: String, readerAttributes: String]

    static constraints = {
        entitlementName(nullable: false)
        entitlementId(nullable: true)
    }

}
