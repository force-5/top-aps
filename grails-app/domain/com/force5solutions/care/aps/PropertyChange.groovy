package com.force5solutions.care.aps

public class PropertyChange {
    String propertyName
    String oldValue
    String newValue
    Date dateCreated
    Date lastUpdated

    static constraints = {
        oldValue(nullable: true)
        newValue(nullable: true)
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
    }

    PropertyChange(){
    }

    PropertyChange(String propertyName, String oldValue, String newValue){
        this.propertyName = propertyName
        this.oldValue = oldValue
        this.newValue = newValue
    }

    String toString(){
        return "Property Name: ${propertyName}, Old Value: ${oldValue}, New Value: ${newValue}"
    }
}