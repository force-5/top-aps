package com.force5solutions.care.aps

import com.force5solutions.care.cc.WorkerEntitlementRole;

public class ProvisionerDeprovisionerTaskVO {
    String workerName
    String entitlementRoleName
    String entitlementName
    String type
    Long workerEntitlementRoleId
    String entitlementId

    ProvisionerDeprovisionerTaskVO(WorkerEntitlementRole workerEntitlementRole, Entitlement entitlement, String type) {
        this.workerName = workerEntitlementRole.worker.firstMiddleLastName as String
        this.entitlementRoleName = workerEntitlementRole.entitlementRole.name
        this.entitlementName = entitlement.name
        this.entitlementId = entitlement.id
        this.type = type
        this.workerEntitlementRoleId = workerEntitlementRole.id
    }
}
