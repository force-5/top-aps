package com.force5solutions.care.web

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import com.force5solutions.care.aps.EntitlementRole
import com.force5solutions.care.workflow.CareCentralResponse
import com.force5solutions.care.aps.Entitlement
import com.force5solutions.care.cc.EntitlementPolicy
import java.text.SimpleDateFormat
import com.force5solutions.care.cc.Worker
import com.force5solutions.care.workflow.ProvisionerDeprovisionerTaskOnRoleUpdate

class CareWebService {

    static CareCentral_PortType proxy = new CareCentral_ServiceLocator()?.getcareCentralHttpPort();

    String getUsername() {
        return CH.config.careCentral.webService.username
    }

    String getPassword() {
        return CH.config.careCentral.webService.password
    }

    boolean transactional = false;

    public boolean markCcEntitlementRoleAsDeleted(String ccEntitlementRoleId) {
        log.info "Propagating the deletion of Entitlement Role with ID: ${ccEntitlementRoleId} to the Central"
        boolean status = proxy.markCcEntitlementRoleAsDeleted(username, password, ccEntitlementRoleId)
        return status
    }

    public WorkerEntitlementRoleDTO getWorkerEntitlementRoleDTO(long workerEntitlementRoleId) {
        return proxy?.getWorkerEntitlementRole(username, password, workerEntitlementRoleId)
    }

    public String getRequiredCertificationIdsForEntitlementPolicyOnAGiveDate(EntitlementPolicy entitlementPolicy, Date date, Worker worker) {
        String dateString = new SimpleDateFormat('MM/dd/yyyy hh:mm a').format(date)
        String certificationIds = proxy?.getRequiredCertificationIdsForEntitlementPolicyOnAGiveDate(username, password, entitlementPolicy.id, dateString, worker.id)
        return certificationIds
    }

    void changeCentralWorkflowTaskStatusToPending(long taskId) {
        proxy?.changeWorkflowTaskStatusToPending(username, password, taskId)
    }

    public void sendWorkflowResponseToCareCentral(CareCentralResponse careCentralResponse) {
        boolean isProcessed = false
        careCentralResponse.with {
            isProcessed = proxy.processEntitlementManagerResponse(username, password, careCentralTaskId, response)
        }
        if (isProcessed) {
            careCentralResponse.delete()
        }
    }

    void propagateEntitlementRole(EntitlementRole role) {
        log.debug "Propagating Entitlement Role: ${role}"
        EntitlementRoleDTO entitlementRoleDTO = new EntitlementRoleDTO(id: role.id, name: role.name, notes: role.notes, status: role.status.name(), standards: role.inheritedStandards?.join(', '), types: role.inheritedEntitlementPolicies?.join(', '), gatekeepers: role?.inheritedGatekeepers?.join(': '), tags: role?.tags)
        boolean isPropagated = proxy.createEntitlementRole(username, password, entitlementRoleDTO)
        role.isPropagated = isPropagated
        role.s()
    }

    void propagateEntitlement(Entitlement entitlement) {
        EntitlementDTO entitlementDTO = new EntitlementDTO(id: entitlement.id, alias: entitlement.alias, notes: entitlement.notes, status: entitlement.status.name(), type: entitlement.type, origin: entitlement.origin)
        boolean isPropagated = proxy.createEntitlement(username, password, entitlementDTO)
        entitlement.isPropagated = isPropagated
        entitlement.s()
    }

    void sendWorkflowResponsesToCareCentral() {
        CareCentralResponse.list().each {CareCentralResponse careCentralResponse ->
            sendWorkflowResponseToCareCentral(careCentralResponse)
        }
    }

    void triggerProvisionerDeprovisionerTasksOnRoleUpdate() {
        Map<Object, List<ProvisionerDeprovisionerTaskOnRoleUpdate>> finalProvisionerDeprovisionerTaskMap = [:]
        Map<Object, List<ProvisionerDeprovisionerTaskOnRoleUpdate>> provisionerDeprovisonerTaskOnRoleUpdateMap = ProvisionerDeprovisionerTaskOnRoleUpdate.findAllByIsTriggered(false).groupBy {it.guid.toString()}
        provisionerDeprovisonerTaskOnRoleUpdateMap.each {key, value ->
            finalProvisionerDeprovisionerTaskMap[key] = value.unique {it.workerEntitlementRoleId}
        }
        finalProvisionerDeprovisionerTaskMap.each { key, value ->
            value.each { ProvisionerDeprovisionerTaskOnRoleUpdate provisionerDeprovisonerTaskOnRoleUpdate ->
                triggerProvisionerDeprovisionerTaskOnRoleUpdateWorkflow(provisionerDeprovisonerTaskOnRoleUpdate)
            }
        }
    }

    void triggerProvisionerDeprovisionerTaskOnRoleUpdateWorkflow(ProvisionerDeprovisionerTaskOnRoleUpdate provisionerDeprovisionerTaskOnRoleUpdate) {
        log.debug "Provisioner Deprovisoner Task On Role Update for Worker Entitlement Role ID: ${provisionerDeprovisionerTaskOnRoleUpdate.workerEntitlementRoleId} and Guid: ${provisionerDeprovisionerTaskOnRoleUpdate.guid}"
        TriggerProvisionerDeprovisionerTaskOnRoleUpdateVO triggerProvisionerDeprovisionerTaskOnRoleUpdateVO = new TriggerProvisionerDeprovisionerTaskOnRoleUpdateVO(provisionerDeprovisionerTaskOnRoleUpdate.guid, provisionerDeprovisionerTaskOnRoleUpdate.workerEntitlementRoleId)
        provisionerDeprovisionerTaskOnRoleUpdate.isTriggered = proxy.triggerProvisionerDeprovisionerTaskOnRoleUpdateWorkflowInCentral(username, password, triggerProvisionerDeprovisionerTaskOnRoleUpdateVO)
        provisionerDeprovisionerTaskOnRoleUpdate.s()
        markSameGuidAndWorkerEntitlementRoleRecordsAsTriggered(provisionerDeprovisionerTaskOnRoleUpdate)
    }

    void markSameGuidAndWorkerEntitlementRoleRecordsAsTriggered(ProvisionerDeprovisionerTaskOnRoleUpdate provisionerDeprovisionerTaskOnRoleUpdate) {
        List<ProvisionerDeprovisionerTaskOnRoleUpdate> provisionerDeprovisionerTaskOnRoleUpdateList = ProvisionerDeprovisionerTaskOnRoleUpdate.findAllByGuidAndWorkerEntitlementRoleId(provisionerDeprovisionerTaskOnRoleUpdate.guid, provisionerDeprovisionerTaskOnRoleUpdate.workerEntitlementRoleId)
        provisionerDeprovisionerTaskOnRoleUpdateList*.isTriggered = true
        provisionerDeprovisionerTaskOnRoleUpdateList*.s()
    }

    void propagateEntitlementRoles() {
        List<EntitlementRole> entitlementRoles = EntitlementRole.createCriteria().list {
            and {
                eq("isApproved", true)
                eq("isExposed", true)
                eq("isPropagated", false)
            }
        }
        entitlementRoles.each {EntitlementRole entitlementRole ->
            propagateEntitlementRole(entitlementRole)
        }
    }

    void propagateEntitlements() {
        List<Entitlement> entitlements = Entitlement.createCriteria().list {
            and {
                eq("isApproved", true)
                eq("isExposed", true)
                eq("isPropagated", false)
            }
        }
        entitlements.each {Entitlement entitlement ->
            propagateEntitlement(entitlement)
        }
    }

    public void markWorkflowAsAborted(String workflowGuid) {
        proxy.markWorkflowAsAborted(username, password, workflowGuid)
    }
}

