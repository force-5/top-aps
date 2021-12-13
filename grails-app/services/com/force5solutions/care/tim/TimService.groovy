package com.force5solutions.care.tim

import com.force5solutions.care.aps.TimEntitlementsAccessLog
import com.force5solutions.care.cc.Worker
import com.force5solutions.care.common.CareConstants
import com.force5solutions.care.workflow.ApsWorkflowUtilService
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONElement
import static groovyx.net.http.ContentType.*
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.force5solutions.care.workflow.ApsWorkflowTask
import com.force5solutions.care.workflow.WorkflowTaskStatus
import com.force5solutions.care.workflow.ApsWorkflowTaskType
import com.force5solutions.care.workflow.ApsWorkflowType
import groovyx.net.http.Method

class TimService {

    boolean transactional = true
    boolean connected = false;

    static scope = "prototype"

    static config = ConfigurationHolder.config

    private void processAccessRequest(ApsWorkflowTask task) {
        long requestId
        def people = getPeople("uid", task.workerEntitlementRole.worker.slid)
        if (people.size() > 0) {
            //If Person exists in TIM, add roles to that Person
            log.debug "****************Person with slid ${task.workerEntitlementRole.worker.slid} exists. Adding roles "
            if (isProvisioningRequired(task.workerEntitlementRole.worker.slid, task.entitlement.name)) {
                requestId = addRolesToPerson(task.workerEntitlementRole.worker.slid, [task.entitlement.name])
                task.timRequestId = requestId
                task.status = WorkflowTaskStatus.PENDING
                task.s()
            } else {
                log.info "Provisioning is not required"
                ApsWorkflowUtilService.sendResponseElements(task, [userAction: "SUCCESS", accessJustification: "Entitlement access granted by Tim System Task"])
            }
        } else {
            //If Person doesn't exist, create the Person along with Roles
            log.debug "****************Person with slid ${task.workerEntitlementRole.worker.slid} does not exist. Creating person. "
            requestId = createPerson(task.workerEntitlementRole.worker, [task.entitlement.name])
            task.timRequestId = requestId
            task.status = WorkflowTaskStatus.PENDING
            task.s()
        }
    }

    private void processRevokeRequest(ApsWorkflowTask task) {
        long requestId
        def people = getPeople("uid", task.workerEntitlementRole.worker.slid)
        if (people.size() > 0) {
            if (isDeProvisioningRequired(task.workerEntitlementRole.worker.slid, task.entitlement.name)) {
                requestId = removeRolesFromPerson(task.workerEntitlementRole.worker.slid, [task.entitlement.name])
                task.timRequestId = requestId
                task.status = WorkflowTaskStatus.PENDING
                task.s()
            } else {
                ApsWorkflowUtilService.sendResponseElements(task, [userAction: "SUCCESS", accessJustification: "Entitlement access revoked by Tim System Task"])
            }
        } else {
            log.info "De-Provisioning is not required"
            ApsWorkflowUtilService.sendResponseElements(task, [userAction: "SUCCESS", accessJustification: "Entitlement access revoked by Tim System Task"])
        }
    }

    private void processTerminateRequest(ApsWorkflowTask task) {
        long requestId
        log.info "********Sending Suspend Request to TIM"
        requestId = suspendPerson(task.workerEntitlementRole.worker.slid)
        task.timRequestId = requestId
        task.status = WorkflowTaskStatus.PENDING
        task.s()
        log.debug "Request Id: " + requestId
    }

    public void processNewTasks() {
        List<ApsWorkflowTask> tasks = ApsWorkflowTask.findAllByStatusAndType(WorkflowTaskStatus.NEW, ApsWorkflowTaskType.SYSTEM_TIM)
        tasks.each { ApsWorkflowTask task ->
            switch (task.workflowType) {
                case ApsWorkflowType.ROLE_ACCESS_REQUEST:
                case ApsWorkflowType.ROLE_ACCESS_REQUEST_FOR_CONTRACTOR:
                    processAccessRequest(task)
                    break;
                case ApsWorkflowType.ROLE_REVOKE_REQUEST:
                    processRevokeRequest(task)
                    break;
                case ApsWorkflowType.CANCEL_ACCESS_REVOCATION:
                    processAccessRequest(task)
                    break;
                case ApsWorkflowType.CANCEL_ACCESS_APPROVAL:
                    processRevokeRequest(task)
                    break;
                case ApsWorkflowType.TERMINATE_REQUEST:
                    processTerminateRequest(task)
                    break;
            }
        }
    }

    public void logWorkerEntitlements(String slid, String comment, boolean createNewSession = true) {
        try {
            if (createNewSession) {
                if (getPeople("uid", slid)) {
                    new TimEntitlementsAccessLog(slid, getRoles(slid).join(', '), comment).s()
                }
            }
        } catch (Throwable t) {
            t.printStackTrace()
        }
    }

    public boolean isProvisioningRequired(String slid, String role) {
        Collection<String> slidTimRoles = getRoles(slid)
        boolean hasRole = (role in slidTimRoles)
        log.debug "Does User with slid already ${slid} have ${role} role : ${hasRole}. Provisioning required : ${!hasRole}"
        return !hasRole
    }

    public boolean isDeProvisioningRequired(String slid, String role) {
        boolean isDeProvisiongRequired = !(isProvisioningRequired(slid, role))
        log.debug "Is de-provisioning required for user with the slid ${slid} for the role ${role} : ${isDeProvisiongRequired}"
        return isDeProvisiongRequired
    }

    public Collection getPeople(String attributeName, String attributeValue) {
        Map parameters = [attributeName: attributeName, attributeValue: attributeValue]
        makeTimRestCall(CareConstants.TIM_RAW_SERVLET_GET_PEOPLE_PATH, GET, parameters)
    }

    public int getRequestStatus(long id) {
        Map parameters = [id: id]
        makeTimRestCall(CareConstants.TIM_SERVLET_GET_REQUEST_STATUS_PATH, GET, parameters)?.requestStatus
    }

    public List<String> getRoles() {
        makeTimRestCall(CareConstants.TIM_RAW_SERVLET_GET_ROLES_PATH, GET)
    }

    public List<String> getRoles(String slid) {
        Map parameters = [slid: slid]
        makeTimRestCall(CareConstants.TIM_RAW_SERVLET_GET_ROLES_WITH_SLID_PATH, GET, parameters)
    }

    public long suspendPerson(String slid) {
        logWorkerEntitlements(slid, 'Before Sending Suspend Worker Request')
        Map parameters = [slid: slid]
        makeTimRestCall(CareConstants.TIM_RAW_SERVLET_SUSPEND_PERSON_PATH, POST, parameters)?.requestId
    }

    public long createPerson(Worker worker, Collection<String> roleNames) {
        Map parameters = [workerAttributes: [worker?.slid, worker?.firstName, worker?.lastName, worker?.name], roleNames: roleNames]
        makeTimRestCall(CareConstants.TIM_RAW_SERVLET_CREATE_PERSON_PATH, POST, parameters)?.requestId
    }

    public long addRolesToPerson(String uid, Collection<String> roleNames) {
        logWorkerEntitlements(uid, 'Before Adding TIM Roles to Worker')
        Map parameters = [uid: uid, roleNames: roleNames]
        makeTimRestCall(CareConstants.TIM_RAW_SERVLET_ADD_ROLES_TO_PERSON_PATH, POST, parameters)?.requestId
    }

    public long removeRolesFromPerson(String uid, Collection<String> roleNames) {
        logWorkerEntitlements(uid, 'Before Adding TIM Roles to Worker')
        Map parameters = [uid: uid, roleNames: roleNames]
        makeTimRestCall(CareConstants.TIM_RAW_SERVLET_REMOVE_ROLES_FROM_PERSON_PATH, POST, parameters)?.requestId
    }

    public void checkRequestStatus() {
        List<ApsWorkflowTask> tasks = ApsWorkflowTask.findAllByStatusAndType(WorkflowTaskStatus.PENDING, ApsWorkflowTaskType.SYSTEM_TIM)
        if (tasks) {
            tasks.each { ApsWorkflowTask task ->
                int requestStatus = getRequestStatus(task.timRequestId)
                switch (requestStatus) {
                    case 2:
                        logWorkerEntitlements(task.worker.slid as String, 'After Request Status Changes to SUCCEEDED in TIM for workflow of type: ' + task.workflowType, false)
                        ApsWorkflowUtilService.sendResponseElements(task, [userAction: "SUCCESS", accessJustification: "Entitlement access granted by Tim System Task"])
                        log.debug "TIM request for task: ${task} having workItemId: ${task.workItemId} has SUCCEEDED."
                        break;
                    case 3:
                        logWorkerEntitlements(task.worker.slid as String, 'After Request Status Changes to FAILED in TIM for workflow of type: ' + task.workflowType, false)
                        ApsWorkflowUtilService.sendResponseElements(task, [userAction: "FAILURE", accessJustification: "Some error occured in TIM while granting access"])
                        log.debug "TIM request for task: ${task} having workItemId: ${task.workItemId} has FAILED."
                        break;
                    default:
                        log.debug "TIM request for task: ${task} having workItemId: ${task.workItemId} is still pending."
                }
            }
        }
    }

    private def makeTimRestCall(String path, Method requestType, Map parameters = [:]) {
        def result = null
        try {
            def http = new HTTPBuilder(config.timRawServlet.url)
            http.auth.basic(config?.timRawServlet?.username, config?.timRawServlet?.password)
            http.request(requestType, groovyx.net.http.ContentType.JSON) {
                uri.path = path
                if (requestType != GET) {
                    send URLENC, parameters
                } else {
                    uri.query = parameters
                }
                response.success = { resp, json ->
                    result = json
                }
                response.failure = { resp ->
                    log.debug "Request to TIM server for ${path} failed with status ${resp.status}"
                }
            }
        } catch (groovyx.net.http.HttpResponseException ex) {
            ex.printStackTrace()
        } catch (java.net.ConnectException ex) {
            ex.printStackTrace()
        }
        result
    }

}
