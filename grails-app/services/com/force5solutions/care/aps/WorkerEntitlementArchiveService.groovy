package com.force5solutions.care.aps

import com.force5solutions.care.common.CareConstants
import com.force5solutions.care.workflow.ApsWorkflowTask
import com.force5solutions.care.cc.WorkerEntitlementArchive
import com.force5solutions.care.cc.EntitlementPolicy
import com.force5solutions.care.workflow.CentralWorkflowTask

class WorkerEntitlementArchiveService {

    boolean transactional = true

    void createWorkerEntitlementEntry(ApsWorkflowTask apsWorkflowTask, Map responseElements) {
        try {
            List<String> nodeNameList = ['Pending Approval from Entitlement Provisioner', 'Entitlement Revoke Request', 'TIM Request', 'Provisioner Task', 'Deprovisioner Task']
            if (apsWorkflowTask.workerEntitlementRole && apsWorkflowTask.entitlement && (apsWorkflowTask.nodeName in nodeNameList)) {
                createWorkerEntitlementArchiveEntry(apsWorkflowTask, responseElements)
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    public WorkerEntitlementArchive createWorkerEntitlementArchiveEntry(ApsWorkflowTask apsWorkflowTask, Map responseElements) {
        WorkerEntitlementArchive workerEntitlementArchive = new WorkerEntitlementArchive()
        workerEntitlementArchive.workerId = apsWorkflowTask.workerEntitlementRole.worker.id
        workerEntitlementArchive.apsWorkflowTaskId = apsWorkflowTask.id
        workerEntitlementArchive.entitlementId = apsWorkflowTask?.entitlement?.id
        workerEntitlementArchive.workerFirstName = apsWorkflowTask.workerEntitlementRole.worker.firstName
        workerEntitlementArchive.workerMiddleName = apsWorkflowTask.workerEntitlementRole.worker.middleName
        workerEntitlementArchive.workerLastName = apsWorkflowTask.workerEntitlementRole.worker.lastName
        workerEntitlementArchive.workerSlid = apsWorkflowTask.workerEntitlementRole.worker.slid
        workerEntitlementArchive.entitlementName = apsWorkflowTask.entitlement.name
        workerEntitlementArchive.entitlementAlias = apsWorkflowTask.entitlement.alias
        workerEntitlementArchive.userResponse = (responseElements && responseElements.containsKey('userAction')) ? responseElements.get('userAction') : apsWorkflowTask.response
        workerEntitlementArchive.entitlementOrigin = apsWorkflowTask?.entitlement?.origin?.name
        workerEntitlementArchive.entitlementPolicyType = EntitlementPolicy.get(apsWorkflowTask?.entitlement?.type)?.name
        workerEntitlementArchive.notes = apsWorkflowTask.message
        if (apsWorkflowTask.isProvisionerTask()) {
            workerEntitlementArchive.entitlementProvisionerSlid = apsWorkflowTask.actorSlid
            workerEntitlementArchive.actionType = CareConstants.ACCESS_REQUEST
        } else if (apsWorkflowTask.isDeprovisionerTask()) {
            workerEntitlementArchive.entitlementDeProvisionerSlid = apsWorkflowTask.actorSlid
            workerEntitlementArchive.actionType = CareConstants.REVOKE_REQUEST
        }
        workerEntitlementArchive.actionDate = apsWorkflowTask.actionDate ?: apsWorkflowTask.lastUpdated
        workerEntitlementArchive.entitlementAttributes = apsWorkflowTask.entitlement.jsonifyEntitlementAttributes()
        workerEntitlementArchive.evidenceIds = apsWorkflowTask.documents*.id.join(',')
        workerEntitlementArchive.s()
        return workerEntitlementArchive
    }

    public WorkerEntitlementArchive createWorkerEntitlementArchiveEntry(CentralWorkflowTask centralWorkflowTask, Map responseElements, Entitlement entitlement) {
        WorkerEntitlementArchive workerEntitlementArchive = new WorkerEntitlementArchive()
        workerEntitlementArchive.workerId = centralWorkflowTask.workerEntitlementRole.worker.id
        workerEntitlementArchive.centralWorkflowTaskId = centralWorkflowTask.id
        workerEntitlementArchive.entitlementId = entitlement?.id
        workerEntitlementArchive.workerFirstName = centralWorkflowTask.workerEntitlementRole.worker.firstName
        workerEntitlementArchive.workerMiddleName = centralWorkflowTask.workerEntitlementRole.worker.middleName
        workerEntitlementArchive.workerLastName = centralWorkflowTask.workerEntitlementRole.worker.lastName
        workerEntitlementArchive.workerSlid = centralWorkflowTask.workerEntitlementRole.worker.slid
        workerEntitlementArchive.entitlementName = entitlement.name
        workerEntitlementArchive.entitlementAlias = entitlement.alias
        workerEntitlementArchive.userResponse = (responseElements && responseElements.containsKey('userAction')) ? responseElements.get('userAction') : centralWorkflowTask.response
        workerEntitlementArchive.entitlementOrigin = entitlement?.origin?.name
        workerEntitlementArchive.entitlementPolicyType = EntitlementPolicy.get(entitlement?.type)?.name
        workerEntitlementArchive.notes = centralWorkflowTask.message
        if (centralWorkflowTask.isAccessRequestedTask()) {
            workerEntitlementArchive.entitlementProvisionerSlid = centralWorkflowTask.actorSlid
            workerEntitlementArchive.actionType = CareConstants.ACCESS_REQUEST
        } else if (centralWorkflowTask.isAccessRevokedTask()) {
            workerEntitlementArchive.entitlementDeProvisionerSlid = centralWorkflowTask.actorSlid
            workerEntitlementArchive.actionType = CareConstants.REVOKE_REQUEST
        }
        workerEntitlementArchive.actionDate = centralWorkflowTask.effectiveStartDate
        workerEntitlementArchive.entitlementAttributes = entitlement.jsonifyEntitlementAttributes()
        workerEntitlementArchive.evidenceIds = centralWorkflowTask.documents*.id.join(',')
        workerEntitlementArchive.s()
        return workerEntitlementArchive
    }
}
