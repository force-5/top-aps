package com.force5solutions.care.aps

import com.force5solutions.care.feed.HrInfo
import com.force5solutions.care.cc.AppUtil

class EmployeeUtilService {

    public ApsPerson findPerson(String employeeSlid) {
        log.debug "*******Looking into ApsPerson table*******"
        ApsPerson person = ApsPerson.findBySlid(employeeSlid)
        if (!person) {
            log.debug "*******Looking into HrInfo table*******"
            HrInfo hrInfo = HrInfo.findBySlid(employeeSlid ?: "")
            if (hrInfo) {
                log.debug "Person found in HrInfo table********"
                person = populatePersonFromHrInfo(hrInfo)
            } else {
                log.debug "Person not found in HrInfo table********"
            }
        }
        return person
    }

    ApsPerson populatePersonFromHrInfo(HrInfo hrInfo, ApsPerson person = new ApsPerson()) {
        if (hrInfo.FIRST_NAME && hrInfo.LAST_NAME) {
            person.firstName = hrInfo.FIRST_NAME
            person.lastName = hrInfo.LAST_NAME
        } else {
            def splitName = hrInfo.FULL_NAME?.split(" ") as List
            if (splitName == null) {
                person.firstName = "Dummy firstName"
                person.lastName = "Dummy lastName"
            } else if (splitName?.size() == 1) {
                person.firstName = splitName.get(0)
                person.lastName = "Dummy lastName"
            } else if (splitName?.size() == 2) {
                person.firstName = splitName.get(0)
                person.lastName = splitName.get(1)
            } else {
                person.firstName = splitName.get(0)
                person.middleName = splitName.get(1)
                person.lastName = splitName.get(2)
            }
        }
        person.phone = hrInfo.CELL_PHONE_NUM
        if(!person.phone){
            person.phone = hrInfo.OFFICE_PHONE_NUM
        }
        if(!person.phone){
            person.phone = 'NA'
        }
        person.slid = hrInfo.slid
        person.email = AppUtil.getEmailFromSlid(person.slid)
        return person
    }
}
