package com.force5solutions.care.aps

import com.force5solutions.care.common.DataFile
import com.force5solutions.care.cc.UploadedFile

class ApsDataFile extends DataFile{
       static constraints = {
        bytes(maxSize: 5000000)
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
        size(nullable: true)
    }

    def beforeInsert = {
        size = bytes?.size()
    }

    def beforeUpdate= {
        size = bytes?.size()
    }

    ApsDataFile(){}

    ApsDataFile(UploadedFile uploadedFile) {
        fileName = uploadedFile.fileName
        bytes = uploadedFile.bytes
    }

    String toString() {
        return fileName
    }
}
