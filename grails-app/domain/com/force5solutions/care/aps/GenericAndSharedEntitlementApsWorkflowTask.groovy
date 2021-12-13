package com.force5solutions.care.aps

class GenericAndSharedEntitlementApsWorkflowTask {
    String workflowGuid
    String entitlementId
    Boolean isProcessed = false
    String taskIdsOnWhichResponseIsDependentOn = null

    static constraints = {
        taskIdsOnWhichResponseIsDependentOn(nullable: true, blank: true)
    }

    String toString() {
        return "${workflowGuid} : ${entitlementId}"
    }
}
