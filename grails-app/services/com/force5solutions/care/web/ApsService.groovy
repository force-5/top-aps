package com.force5solutions.care.web

import com.force5solutions.care.aps.EntitlementRole

class ApsService {

    def versioningService

    boolean transactional = true

    List<String> getEntitlementsInRoleByDate(String roleId, Date date){
        EntitlementRole role = EntitlementRole.findById(roleId)
        role.entitlements*.entitlement.id
        role = versioningService.getObjectOnDate(role, date)
        List<String> entitlementIds = role.entitlements*.entitlement.id
        return entitlementIds
    }
}
