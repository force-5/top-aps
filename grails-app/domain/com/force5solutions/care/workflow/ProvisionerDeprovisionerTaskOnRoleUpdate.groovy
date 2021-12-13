package com.force5solutions.care.workflow

class ProvisionerDeprovisionerTaskOnRoleUpdate {
    String guid
    Long workerEntitlementRoleId
    String entitlementId
    String type
    boolean isTriggered = false

    static constraints = {
    }

    ProvisionerDeprovisionerTaskOnRoleUpdate() {}

    ProvisionerDeprovisionerTaskOnRoleUpdate(String guid, Long workerEntitlementRoleId, String entitlementId, String type) {
        this.guid = guid
        this.workerEntitlementRoleId = workerEntitlementRoleId
        this.entitlementId = entitlementId
        this.type = type
    }

    static boolean isProvisionerTask(Object provisionerDeprovisionerTaskOnRoleUpdateId) {
        ProvisionerDeprovisionerTaskOnRoleUpdate provisionerDeprovisionerTaskOnRoleUpdate = ProvisionerDeprovisionerTaskOnRoleUpdate.get(provisionerDeprovisionerTaskOnRoleUpdateId.toString().toLong())
        return provisionerDeprovisionerTaskOnRoleUpdate.type.startsWith('Provision')
    }

    static String getEntitlementId(Object provisionerDeprovisionerTaskOnRoleUpdateId) {
        ProvisionerDeprovisionerTaskOnRoleUpdate provisionerDeprovisionerTaskOnRoleUpdate = ProvisionerDeprovisionerTaskOnRoleUpdate.get(provisionerDeprovisionerTaskOnRoleUpdateId.toString().toLong())
        return provisionerDeprovisionerTaskOnRoleUpdate.entitlementId
    }
}
