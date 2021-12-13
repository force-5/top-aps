package com.force5solutions.care.aps

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.force5solutions.care.cc.AppUtil


public class ApsPerson implements Serializable {

    def sessionFactory
    private static final long serialVersionUID = 1L

    static emailDomain = ConfigurationHolder.config.emailDomain
    Date dateCreated
    Date lastUpdated

    Boolean isApproved = true
    String firstName
    String middleName
    String lastName
    String email
    String phone
    String notes
    String slid

    static transients = ['firstMiddleLastName']

    String getFirstMiddleLastName(){
        return ((firstName ? (firstName + ' ') : '') + (middleName ? (middleName + ' ') : '') + lastName ?: '')
    }

    void setSlid(String slid) {
        this.slid = slid?.toUpperCase()
    }

    void setEmail(String email) {
        this.email = email?.toLowerCase()
    }

    static constraints = {
        email(nullable: true, email: true)
        middleName(nullable: true)
        phone(nullable: true)
        notes(nullable: true)
        slid(nullable: true, blank: false, validator: {val, obj ->
            if (val) {
//                int objCount = ApsPerson.countBySlid(val)
                if ((!obj.id && ApsPerson.countBySlid(val)) || (obj.id && !ApsPerson.countBySlidAndId(val, obj.id))) {
                    return "default.not.unique.message"
                }
//                if (!obj.id) {
//                    if (objCount > 0) {
//                        return "default.not.unique.message"
//                    }
//                } else {
//                    if (objCount > 0) {
//                        ApsPerson person = ApsPerson.findBySlid(val)
//                        if (!(person.id == obj.id)) {
//                            return "default.not.unique.message"
//                        }
//                    }
//                }
            }
        })
    }

    def getName() {
        return "${lastName ?: ''}, ${firstName ?: ''} ${middleName ?: ''}"

    }

    def beforeInsert = {
        email = AppUtil.getEmailFromSlid(slid)
    }

    def beforeUpdate = {
        email = AppUtil.getEmailFromSlid(slid)
    }

    String toString() {
        return name
    }

    String toDetailString() {
        return (firstName + middleName + lastName + email + slid + phone + notes)
    }

}