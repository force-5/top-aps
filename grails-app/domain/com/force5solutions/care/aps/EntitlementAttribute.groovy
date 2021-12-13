package com.force5solutions.care.aps

class EntitlementAttribute {

    Date dateCreated
    Date lastUpdated
    String keyName
    String value
    static belongsTo = [entitlement: Entitlement]

    static constraints = {
    }

    static mapping = {
        keyName type: 'text'
        value type: 'text'
    }

    String toString() {
        return "${keyName} : ${value}, ${entitlement}"
    }
}
