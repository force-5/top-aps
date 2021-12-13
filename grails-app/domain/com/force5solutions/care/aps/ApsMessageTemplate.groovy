package com.force5solutions.care.aps

import com.force5solutions.care.common.MessageTemplate

class ApsMessageTemplate extends MessageTemplate implements Serializable {
    Set<ApsDataFile> attachments = []

    static hasMany = [attachments: ApsDataFile]
    static transients = ['body', 'subject']

    static constraints = {
        name(unique: true, blank: false)
        subjectTemplate(maxSize: 5000)
        bodyTemplate(maxSize: 8000)
        attachments(nullable: true)
    }

    String toString() {
        return name
    }

}