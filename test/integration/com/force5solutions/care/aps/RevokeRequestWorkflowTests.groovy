package com.force5solutions.care.aps

import grails.test.*
import com.force5solutions.care.common.EntitlementStatus
import com.force5solutions.care.cc.Employee
import com.force5solutions.care.cc.Location
import com.force5solutions.care.cc.LocationType
import com.force5solutions.care.cc.CcEntitlementRole
import com.force5solutions.care.cc.Person
import com.force5solutions.care.cc.EntitlementRoleAccessStatus
import com.force5solutions.care.cc.WorkerEntitlementRole
import com.force5solutions.care.workflow.ApsWorkflowUtilService
import com.force5solutions.care.workflow.ApsWorkflowTask
import com.force5solutions.care.workflow.CareCentralResponse
import com.force5solutions.care.cc.EntitlementPolicy
import com.force5solutions.care.workflow.CentralWorkflowTask
import com.force5solutions.care.workflow.CentralWorkflowTaskType
import com.force5solutions.care.workflow.WorkflowTaskStatus
import com.force5solutions.care.workflow.ApsWorkflowType
import com.force5solutions.care.workflow.CentralWorkflowType

/**
 *
 * CASE 1: ER-1  -> Active
 *          - E-1
 *         ER-2  -> Pending Revocation
 *          - E-1
 *          - E-2
 *
 * CASE 2: ER-1 -> Pending Revocation 
 *          - E-1
 *         ER-2 -> Active
 *          - E-1
 *          - E-2
 *
 * CASE 3: ER-1 -> Pending Revocation
 *          - E-1
 *         ER-2 -> Pending Revocation
 *          - E-1
 *          - E-2
 *
 * CASE 4: ER-1 -> Pending Revocation
 *          - E-1
 *          - E-2
 *         ER-2 -> Pending Revocation
 *          - E-2
 *          - E-3
 *
 * CASE 5: ER-1 -> Revoked
 *          - E-1
 *          - E-2
 *         ER-2 -> Pending Revocation
 *          - E-2
 *          - E-3
 *
 *CASE 6:  ER-2 -> Pending Revocation (No Gatekeeper)
 *           - E-3 (Has a Gatekeeper)
 *           - ER-1 (Has a Gatekeeper)
 *              - E-1
 *              - E-2
 *  So in this case, on the start of revocation workflow two requests for gatekeeper approval are created. One for the entitlement E-3 gatekeeper and the other one for
 *  entitlement role ER-1 gatekeeper.
 *
 *  */


class RevokeRequestWorkflowTests extends GrailsUnitTestCase {
    Employee employee
    Entitlement entitlement
    ApsPerson person
    RoleOwner owner
    Origin origin
    Gatekeeper gatekeeper
    Location businessUnit


    protected void setUp() {
        super.setUp()
        origin = new Origin(name: 'O-1').save(failOnError: true)
        person = new ApsPerson(firstName: 'Role-Owner', lastName: System.currentTimeMillis().toString(), slid: System.currentTimeMillis().toString()).save(failOnError: true)
        owner = new RoleOwner(person: person).save(failOnError: true)
        gatekeeper = new Gatekeeper(person: person).save(failOnError: true)
        employee = createAnEmployee()
        entitlement = createAnEntitlement(System.currentTimeMillis().toString())
        businessUnit = createABusinessUnit()
    }


    protected void tearDown() {
        super.tearDown()
    }

    void test_REVOKE_PROCESS_FOR_TEST_CASE_1() {
        EntitlementRole role1 = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role1.id)
        Entitlement entitlement2 = createAnEntitlement(System.currentTimeMillis().toString())
        EntitlementRole role2 = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement, entitlement2], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role2.id)

        CcEntitlementRole ccEntitlementRole = createCcEntitlementRole(role1)
        WorkerEntitlementRole workerEntitlementRole_ER1 = new WorkerEntitlementRole()
        workerEntitlementRole_ER1.entitlementRole = ccEntitlementRole
        workerEntitlementRole_ER1.worker = employee
        workerEntitlementRole_ER1.lastStatusChange = new Date()
        workerEntitlementRole_ER1.status = EntitlementRoleAccessStatus.active
        workerEntitlementRole_ER1.save(failOnError: true)
        assertNotNull(workerEntitlementRole_ER1.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 1, employee.entitlementRoles.size())


        ccEntitlementRole = createCcEntitlementRole(role2)
        WorkerEntitlementRole workerEntitlementRole_ER2 = new WorkerEntitlementRole()
        workerEntitlementRole_ER2.entitlementRole = ccEntitlementRole
        workerEntitlementRole_ER2.worker = employee
        workerEntitlementRole_ER2.lastStatusChange = new Date()
        workerEntitlementRole_ER2.status = EntitlementRoleAccessStatus.pendingRevocation
        workerEntitlementRole_ER2.save(failOnError: true)
        assertNotNull(workerEntitlementRole_ER2.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 2, employee.entitlementRoles.size())

        Integer initialTaskCount = ApsWorkflowTask.count()
        Integer initialResponseCount = CareCentralResponse.count()
        CentralWorkflowTask task2 = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole_ER2.id, workItemId: 2, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REVOKE, droolsSessionId: 2, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task2.id)
        ApsWorkflowUtilService.startRoleRevokeRequestWorkflow(role2, task2)
        task2.status = WorkflowTaskStatus.PENDING
        task2.s()
        Integer intermediateTaskCount = ApsWorkflowTask.count()
        assertEquals(1, ApsWorkflowTask.findAllByWorkerEntitlementRoleIdAndNodeName(workerEntitlementRole_ER2.id, 'Pending Revocation by Entitlement Role Gatekeeper').size())
        assertEquals("Gatekeeper Approval request for Entitlement Role revoke not created", initialTaskCount + 2, intermediateTaskCount)
        ApsWorkflowTask apsWorkflowTask = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndNodeName(workerEntitlementRole_ER2.id, 'Pending Revocation by Entitlement Role Gatekeeper')
        Map responseElements = ['userAction': 'APPROVE', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(apsWorkflowTask, responseElements)

        List<ApsWorkflowTask> entitlementRevokeTasks = ApsWorkflowTask.findAllByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole_ER2.id, WorkflowTaskStatus.NEW)
        assertEquals("Provisioner request for Entitlement revoke not created", 1, entitlementRevokeTasks.size())
        assertEquals(entitlementRevokeTasks.last().workflowType, ApsWorkflowType.ROLE_REVOKE_REQUEST)
        assertEquals(entitlementRevokeTasks.last().nodeName, "Entitlement Revoke Request")

        Integer finalTaskCount = ApsWorkflowTask.count()
        assertEquals("Provisioner request for Entitlement revoke not created", intermediateTaskCount + 1, finalTaskCount)

        responseElements = ["userAction": "CONFIRM", "accessJustification": "Access Revoked by APS Application"]
        entitlementRevokeTasks.each {
            ApsWorkflowUtilService.sendResponseElements(it, responseElements)
        }

        assertEquals(WorkflowTaskStatus.COMPLETE, entitlementRevokeTasks.last().status)
        Integer finalResponseCount = CareCentralResponse.count()
        assertEquals('No response created for Care Central after workflow completion', initialResponseCount + 1, finalResponseCount)
    }

    void test_REVOKE_PROCESS_FOR_TEST_CASE_2() {
        EntitlementRole role1 = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role1.id)
        Entitlement entitlement2 = createAnEntitlement(System.currentTimeMillis().toString())
        EntitlementRole role2 = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement, entitlement2], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role2.id)

        CcEntitlementRole ccEntitlementRole = createCcEntitlementRole(role1)
        WorkerEntitlementRole workerEntitlementRole_ER1 = new WorkerEntitlementRole()
        workerEntitlementRole_ER1.entitlementRole = ccEntitlementRole
        workerEntitlementRole_ER1.worker = employee
        workerEntitlementRole_ER1.lastStatusChange = new Date()
        workerEntitlementRole_ER1.status = EntitlementRoleAccessStatus.pendingRevocation
        workerEntitlementRole_ER1.save(failOnError: true)
        assertNotNull(workerEntitlementRole_ER1.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 1, employee.entitlementRoles.size())


        ccEntitlementRole = createCcEntitlementRole(role2)
        WorkerEntitlementRole workerEntitlementRole_ER2 = new WorkerEntitlementRole()
        workerEntitlementRole_ER2.entitlementRole = ccEntitlementRole
        workerEntitlementRole_ER2.worker = employee
        workerEntitlementRole_ER2.lastStatusChange = new Date()
        workerEntitlementRole_ER2.status = EntitlementRoleAccessStatus.active
        workerEntitlementRole_ER2.save(failOnError: true)
        assertNotNull(workerEntitlementRole_ER2.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 2, employee.entitlementRoles.size())

        Integer initialTaskCount = ApsWorkflowTask.count()
        Integer intermediateTaskCount
        Integer finalTaskCount
        Integer initialResponseCount = CareCentralResponse.count()
        CentralWorkflowTask task1 = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole_ER1.id, workItemId: 1, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REVOKE, droolsSessionId: 1, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task1.id)
        ApsWorkflowUtilService.startRoleRevokeRequestWorkflow(role1, task1)
        task1.status = WorkflowTaskStatus.PENDING
        task1.s()
        intermediateTaskCount = ApsWorkflowTask.count()
        assertEquals(1, ApsWorkflowTask.findAllByWorkerEntitlementRoleIdAndNodeName(workerEntitlementRole_ER1.id, 'Pending Revocation by Entitlement Role Gatekeeper').size())
        ApsWorkflowTask apsWorkflowTask = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndNodeName(workerEntitlementRole_ER1.id, 'Pending Revocation by Entitlement Role Gatekeeper')
        Map responseElements = ['userAction': 'APPROVE', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(apsWorkflowTask, responseElements)
        assertEquals("Gatekeeper Approval request for Entitlement Role revoke not created", initialTaskCount + 2, intermediateTaskCount)
        List<ApsWorkflowTask> entitlementRevokeTasks = ApsWorkflowTask.findAllByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole_ER1.id, WorkflowTaskStatus.NEW)
        assertEquals("Provisioner request for Entitlement revoke created", 0, entitlementRevokeTasks.size())
        Integer finalResponseCount
        finalResponseCount = CareCentralResponse.count()
        assertEquals('Response created for Care Central after workflow completion', initialResponseCount + 1, finalResponseCount)
    }


    void test_REVOKE_PROCESS_FOR_TEST_CASE_3() {
        EntitlementRole role1 = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role1.id)
        Entitlement entitlement2 = createAnEntitlement(System.currentTimeMillis().toString())
        EntitlementRole role2 = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement, entitlement2], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role2.id)

        CcEntitlementRole ccEntitlementRole = createCcEntitlementRole(role1)
        WorkerEntitlementRole workerEntitlementRole_ER1 = new WorkerEntitlementRole()
        workerEntitlementRole_ER1.entitlementRole = ccEntitlementRole
        workerEntitlementRole_ER1.worker = employee
        workerEntitlementRole_ER1.lastStatusChange = new Date()
        workerEntitlementRole_ER1.status = EntitlementRoleAccessStatus.pendingRevocation
        workerEntitlementRole_ER1.save(failOnError: true)
        assertNotNull(workerEntitlementRole_ER1.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 1, employee.entitlementRoles.size())


        ccEntitlementRole = createCcEntitlementRole(role2)
        WorkerEntitlementRole workerEntitlementRole_ER2 = new WorkerEntitlementRole()
        workerEntitlementRole_ER2.entitlementRole = ccEntitlementRole
        workerEntitlementRole_ER2.worker = employee
        workerEntitlementRole_ER2.lastStatusChange = new Date()
        workerEntitlementRole_ER2.status = EntitlementRoleAccessStatus.pendingRevocation
        workerEntitlementRole_ER2.save(failOnError: true)
        assertNotNull(workerEntitlementRole_ER2.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 2, employee.entitlementRoles.size())

        Integer initialTaskCount = ApsWorkflowTask.count()
        Integer intermediateTaskCount
        Integer finalTaskCount
        CentralWorkflowTask task1 = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole_ER1.id, workItemId: 1, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REVOKE, droolsSessionId: 1, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task1.id)
        ApsWorkflowUtilService.startRoleRevokeRequestWorkflow(role1, task1)
        task1.status = WorkflowTaskStatus.PENDING
        task1.s()
        intermediateTaskCount = ApsWorkflowTask.count()
        ApsWorkflowTask gatekeeperTask = ApsWorkflowTask.findByNodeName('Pending Revocation by Entitlement Role Gatekeeper')
        assertNotNull(gatekeeperTask.id)
        assertEquals("Gatekeeper request for Entitlement revoke not created", initialTaskCount + 2, intermediateTaskCount)
        Map responseElements = ['userAction': 'APPROVE', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(gatekeeperTask, responseElements)
        intermediateTaskCount = ApsWorkflowTask.count()
        assertEquals("Provisioner request for Entitlement revoke not created", initialTaskCount + 3, intermediateTaskCount)

        CentralWorkflowTask task2 = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole_ER2.id, workItemId: 2, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REVOKE, droolsSessionId: 2, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task2.id)
        ApsWorkflowUtilService.startRoleRevokeRequestWorkflow(role2, task2)
        task2.status = WorkflowTaskStatus.PENDING
        task2.s()
        finalTaskCount = ApsWorkflowTask.count()
        ApsWorkflowTask newGatekeeperTask = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndNodeName(workerEntitlementRole_ER2.id, 'Pending Revocation by Entitlement Role Gatekeeper')
        assertNotNull(newGatekeeperTask)
        assertEquals("Gatekeeper request for Entitlement revoke not created", intermediateTaskCount + 2, finalTaskCount)
        ApsWorkflowUtilService.sendResponseElements(newGatekeeperTask, responseElements)

        ApsWorkflowTask entitlementRevokeTask_ER1 = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndEntitlementId(workerEntitlementRole_ER1.id, entitlement.id)
        assertNotNull(entitlementRevokeTask_ER1)
        assertEquals("Entitlement Ids do not match", entitlement.id, entitlementRevokeTask_ER1.entitlementId)
        assertEquals("Workflow Type do not match", ApsWorkflowType.ROLE_REVOKE_REQUEST, entitlementRevokeTask_ER1.workflowType)
        assertEquals("Node name do not match", "Entitlement Revoke Request", entitlementRevokeTask_ER1.nodeName)


        ApsWorkflowTask entitlementRevokeTask_ER2 = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndEntitlementId(workerEntitlementRole_ER2.id, entitlement2.id)
        assertNotNull(entitlementRevokeTask_ER2)
        assertEquals("Entitlement Ids do not match", entitlement2.id, entitlementRevokeTask_ER2.entitlementId)
        assertEquals("Workflow Type do not match", ApsWorkflowType.ROLE_REVOKE_REQUEST, entitlementRevokeTask_ER2.workflowType)
        assertEquals("Node name do not match", "Entitlement Revoke Request", entitlementRevokeTask_ER2.nodeName)


        finalTaskCount = ApsWorkflowTask.count()
        assertEquals("Provisioner request for Entitlement revoke not created", intermediateTaskCount + 4, finalTaskCount)

        responseElements = ["userAction": "CONFIRM", "accessJustification": "Access Revoked by APS Application"]
        Integer initialResponseCount = CareCentralResponse.count()
        ApsWorkflowUtilService.sendResponseElements(entitlementRevokeTask_ER1, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, entitlementRevokeTask_ER1.status)
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals("Provisioner request for Entitlement revoke not created", intermediateTaskCount + 4, finalTaskCount)

        Integer finalResponseCount
        finalResponseCount = CareCentralResponse.count()
        assertEquals('Response created for Care Central after workflow completion', initialResponseCount + 1, finalResponseCount)

        ApsWorkflowUtilService.sendResponseElements(entitlementRevokeTask_ER2, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, entitlementRevokeTask_ER2.status)
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals("Provisioner request for Entitlement revoke not created", intermediateTaskCount + 4, finalTaskCount)
        finalResponseCount = CareCentralResponse.count()
        assertEquals('No response created for Care Central after workflow completion', initialResponseCount + 1, finalResponseCount)
    }


    void test_REVOKE_PROCESS_FOR_TEST_CASE_4() {
        Entitlement entitlement2 = createAnEntitlement(System.currentTimeMillis().toString())
        sleep(2000L)
        Entitlement entitlement3 = createAnEntitlement(System.currentTimeMillis().toString())
        EntitlementRole role1 = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement, entitlement2], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role1.id)
        EntitlementRole role2 = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement2, entitlement3], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role2.id)

        CcEntitlementRole ccEntitlementRole = createCcEntitlementRole(role1)
        WorkerEntitlementRole workerEntitlementRole_ER1 = new WorkerEntitlementRole()
        workerEntitlementRole_ER1.entitlementRole = ccEntitlementRole
        workerEntitlementRole_ER1.worker = employee
        workerEntitlementRole_ER1.lastStatusChange = new Date()
        workerEntitlementRole_ER1.status = EntitlementRoleAccessStatus.pendingRevocation
        workerEntitlementRole_ER1.save(failOnError: true)
        assertNotNull(workerEntitlementRole_ER1.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 1, employee.entitlementRoles.size())


        ccEntitlementRole = createCcEntitlementRole(role2)
        WorkerEntitlementRole workerEntitlementRole_ER2 = new WorkerEntitlementRole()
        workerEntitlementRole_ER2.entitlementRole = ccEntitlementRole
        workerEntitlementRole_ER2.worker = employee
        workerEntitlementRole_ER2.lastStatusChange = new Date()
        workerEntitlementRole_ER2.status = EntitlementRoleAccessStatus.pendingRevocation
        workerEntitlementRole_ER2.save(failOnError: true)
        assertNotNull(workerEntitlementRole_ER2.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 2, employee.entitlementRoles.size())

        Integer initialTaskCount = ApsWorkflowTask.count()
        Integer intermediateTaskCount, finalTaskCount
        CentralWorkflowTask task1 = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole_ER1.id, workItemId: 1, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REVOKE, droolsSessionId: 1, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task1.id)
        ApsWorkflowUtilService.startRoleRevokeRequestWorkflow(role1, task1)
        task1.status = WorkflowTaskStatus.PENDING
        task1.s()
        intermediateTaskCount = ApsWorkflowTask.count()
        ApsWorkflowTask gatekeeperTask = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndNodeName(workerEntitlementRole_ER1.id, 'Pending Revocation by Entitlement Role Gatekeeper')
        assertNotNull(gatekeeperTask.id)
        assertEquals("Gatekeeper request for Entitlement revoke not created", initialTaskCount + 2, intermediateTaskCount)
        Map responseElements = ['userAction': 'APPROVE', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(gatekeeperTask, responseElements)
        intermediateTaskCount = ApsWorkflowTask.count()
        assertEquals("Provisioner request for Entitlement revoke created", initialTaskCount + 4, intermediateTaskCount)

        CentralWorkflowTask task2 = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole_ER2.id, workItemId: 2, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REVOKE, droolsSessionId: 2, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task2.id)
        ApsWorkflowUtilService.startRoleRevokeRequestWorkflow(role2, task2)
        task2.status = WorkflowTaskStatus.PENDING
        task2.s()
        finalTaskCount = ApsWorkflowTask.count()
        gatekeeperTask = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndNodeName(workerEntitlementRole_ER2.id, 'Pending Revocation by Entitlement Role Gatekeeper')
        assertEquals(1, ApsWorkflowTask.findAllByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole_ER2.id, WorkflowTaskStatus.NEW).size())
        assertNotNull(gatekeeperTask.id)
        assertEquals("Gatekeeper request for Entitlement revoke not created", intermediateTaskCount + 2, finalTaskCount)
        ApsWorkflowUtilService.sendResponseElements(gatekeeperTask, responseElements)
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals("Provisioner request for Entitlement revoke created", intermediateTaskCount + 4, finalTaskCount)

        ApsWorkflowTask entitlementRevokeTask1_ER1 = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndEntitlementId(workerEntitlementRole_ER1.id, entitlement.id)
        assertNotNull(entitlementRevokeTask1_ER1)
        assertEquals("Entitlement Ids do not match", entitlement.id, entitlementRevokeTask1_ER1.entitlementId)
        assertEquals("Workflow Type do not match", ApsWorkflowType.ROLE_REVOKE_REQUEST, entitlementRevokeTask1_ER1.workflowType)
        assertEquals("Node name do not match", "Entitlement Revoke Request", entitlementRevokeTask1_ER1.nodeName)


        ApsWorkflowTask entitlementRevokeTask2_ER1 = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndEntitlementId(workerEntitlementRole_ER1.id, entitlement2.id)
        assertNotNull(entitlementRevokeTask2_ER1)
        assertEquals("Entitlement Ids do not match", entitlement2.id, entitlementRevokeTask2_ER1.entitlementId)


        ApsWorkflowTask entitlementRevokeTask_ER2 = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndEntitlementId(workerEntitlementRole_ER2.id, entitlement3.id)
        assertNotNull(entitlementRevokeTask_ER2)
        assertEquals("Entitlement Ids do not match", entitlement3.id, entitlementRevokeTask_ER2.entitlementId)
        assertEquals("Workflow Type do not match", ApsWorkflowType.ROLE_REVOKE_REQUEST, entitlementRevokeTask_ER2.workflowType)
        assertEquals("Node name do not match", "Entitlement Revoke Request", entitlementRevokeTask_ER2.nodeName)


        responseElements = ["userAction": "CONFIRM", "accessJustification": "Access Revoked by APS Application"]
        Integer initialResponseCount = CareCentralResponse.count()
        ApsWorkflowUtilService.sendResponseElements(entitlementRevokeTask1_ER1, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, entitlementRevokeTask1_ER1.status)
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals("Provisioner request for Entitlement revoke not created", intermediateTaskCount + 4, finalTaskCount)

        Integer finalResponseCount
        finalResponseCount = CareCentralResponse.count()
        assertEquals('Response created for Care Central after workflow completion', initialResponseCount, finalResponseCount)

        ApsWorkflowUtilService.sendResponseElements(entitlementRevokeTask2_ER1, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, entitlementRevokeTask2_ER1.status)
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals("Provisioner request for Entitlement revoke not deleted", intermediateTaskCount + 4, finalTaskCount)
        finalResponseCount = CareCentralResponse.count()
        assertEquals('Response created for Care Central after workflow completion', initialResponseCount + 1, finalResponseCount)

        ApsWorkflowUtilService.sendResponseElements(entitlementRevokeTask_ER2, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, entitlementRevokeTask_ER2.status)
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals("Provisioner request for Entitlement revoke not deleted", intermediateTaskCount + 4, finalTaskCount)
        finalResponseCount = CareCentralResponse.count()
        assertEquals('No response created for Care Central after workflow completion', initialResponseCount + 1, finalResponseCount)
    }

    void test_REVOKE_PROCESS_FOR_TEST_CASE_5() {
        Entitlement entitlement2 = createAnEntitlement(System.currentTimeMillis().toString())
        sleep(2000L)
        Entitlement entitlement3 = createAnEntitlement(System.currentTimeMillis().toString())
        EntitlementRole role1 = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement, entitlement2], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role1.id)
        EntitlementRole role2 = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement2, entitlement3], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role2.id)

        CcEntitlementRole ccEntitlementRole = createCcEntitlementRole(role1)
        WorkerEntitlementRole workerEntitlementRole_ER1 = new WorkerEntitlementRole()
        workerEntitlementRole_ER1.entitlementRole = ccEntitlementRole
        workerEntitlementRole_ER1.worker = employee
        workerEntitlementRole_ER1.lastStatusChange = new Date()
        workerEntitlementRole_ER1.status = EntitlementRoleAccessStatus.revoked
        workerEntitlementRole_ER1.save(failOnError: true)
        assertNotNull(workerEntitlementRole_ER1.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 1, employee.entitlementRoles.size())


        ccEntitlementRole = createCcEntitlementRole(role2)
        WorkerEntitlementRole workerEntitlementRole_ER2 = new WorkerEntitlementRole()
        workerEntitlementRole_ER2.entitlementRole = ccEntitlementRole
        workerEntitlementRole_ER2.worker = employee
        workerEntitlementRole_ER2.lastStatusChange = new Date()
        workerEntitlementRole_ER2.status = EntitlementRoleAccessStatus.pendingRevocation
        workerEntitlementRole_ER2.save(failOnError: true)
        assertNotNull(workerEntitlementRole_ER2.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 2, employee.entitlementRoles.size())

        Integer initialTaskCount = ApsWorkflowTask.count()
        Integer intermediateTaskCount, finalTaskCount

        CentralWorkflowTask task = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole_ER2.id, workItemId: 2, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REVOKE, droolsSessionId: 2, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task.id)
        ApsWorkflowUtilService.startRoleRevokeRequestWorkflow(role2, task)
        task.status = WorkflowTaskStatus.PENDING
        task.s()
        intermediateTaskCount = ApsWorkflowTask.count()

        ApsWorkflowTask gatekeeperTask = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndNodeName(workerEntitlementRole_ER2.id, 'Pending Revocation by Entitlement Role Gatekeeper')
        assertEquals(1, ApsWorkflowTask.findAllByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole_ER2.id, WorkflowTaskStatus.NEW).size())
        assertNotNull(gatekeeperTask.id)
        assertEquals("Gatekeeper request for Entitlement revoke not created", initialTaskCount + 2, intermediateTaskCount)
        Map responseElements = ['userAction': 'APPROVE', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(gatekeeperTask, responseElements)
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals("Provisioner request for Entitlement revoke created", intermediateTaskCount + 2, finalTaskCount)
        assertEquals(4, ApsWorkflowTask.findAllByWorkerEntitlementRoleId(workerEntitlementRole_ER2.id).size())

        ApsWorkflowTask entitlementRevokeTask1_ER2 = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndEntitlementId(workerEntitlementRole_ER2.id, entitlement2.id)
        assertNotNull(entitlementRevokeTask1_ER2)
        assertEquals("Entitlement Ids do not match", entitlement2.id, entitlementRevokeTask1_ER2.entitlementId)
        assertEquals("Workflow Type do not match", ApsWorkflowType.ROLE_REVOKE_REQUEST, entitlementRevokeTask1_ER2.workflowType)
        assertEquals("Node name do not match", "Entitlement Revoke Request", entitlementRevokeTask1_ER2.nodeName)

        ApsWorkflowTask entitlementRevokeTask2_ER2 = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndEntitlementId(workerEntitlementRole_ER2.id, entitlement3.id)
        assertNotNull(entitlementRevokeTask2_ER2)
        assertEquals("Entitlement Ids do not match", entitlement3.id, entitlementRevokeTask2_ER2.entitlementId)
        assertEquals("Workflow Type do not match", ApsWorkflowType.ROLE_REVOKE_REQUEST, entitlementRevokeTask2_ER2.workflowType)
        assertEquals("Node name do not match", "Entitlement Revoke Request", entitlementRevokeTask2_ER2.nodeName)

        responseElements = ["userAction": "CONFIRM", "accessJustification": "Access Revoked by APS Application"]
        Integer initialResponseCount = CareCentralResponse.count()
        ApsWorkflowUtilService.sendResponseElements(entitlementRevokeTask1_ER2, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, entitlementRevokeTask1_ER2.status)
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals("Provisioner request for Entitlement revoke not created", intermediateTaskCount + 2, finalTaskCount)

        Integer finalResponseCount
        finalResponseCount = CareCentralResponse.count()
        assertEquals('Response created for Care Central after workflow completion', initialResponseCount, finalResponseCount)

        ApsWorkflowUtilService.sendResponseElements(entitlementRevokeTask2_ER2, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, entitlementRevokeTask2_ER2.status)
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals("Provisioner request for Entitlement revoke not deleted", intermediateTaskCount + 2, finalTaskCount)
        finalResponseCount = CareCentralResponse.count()
        assertEquals('Response created for Care Central after workflow completion', initialResponseCount + 1, finalResponseCount)
    }

    void test_REVOKE_PROCESS_FOR_TEST_CASE_6() {
        String entitlementTwoName = System.currentTimeMillis().toString()
        Entitlement entitlement2 = createAnEntitlement(entitlementTwoName)
        sleep(2000L)
        String entitlementThreeName = System.currentTimeMillis().toString()
        Entitlement entitlement3 = createAnEntitlement(entitlementThreeName)
        EntitlementRole role1 = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement, entitlement2], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role1.id)
        EntitlementRole role2 = new EntitlementRole(name: System.currentTimeMillis().toString(), entitlements: [entitlement3], roles: [role1], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role2.id)

        CcEntitlementRole ccEntitlementRole = createCcEntitlementRole(role2)
        WorkerEntitlementRole workerEntitlementRole_ER2 = new WorkerEntitlementRole()
        workerEntitlementRole_ER2.entitlementRole = ccEntitlementRole
        workerEntitlementRole_ER2.worker = employee
        workerEntitlementRole_ER2.lastStatusChange = new Date()
        workerEntitlementRole_ER2.status = EntitlementRoleAccessStatus.pendingRevocation
        workerEntitlementRole_ER2.save(failOnError: true)
        assertNotNull(workerEntitlementRole_ER2.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 1, employee.entitlementRoles.size())

        Integer initialTaskCount = ApsWorkflowTask.count()
        Integer intermediateTaskCount, finalTaskCount

        CentralWorkflowTask task = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole_ER2.id, workItemId: 2, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REVOKE, droolsSessionId: 2, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task.id)
        ApsWorkflowUtilService.startRoleRevokeRequestWorkflow(role2, task)
        task.status = WorkflowTaskStatus.PENDING
        task.s()
        intermediateTaskCount = ApsWorkflowTask.count()

        ApsWorkflowTask gatekeeperTask = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndNodeName(workerEntitlementRole_ER2.id, 'Pending Revocation by Entitlement Role Gatekeeper')
        ApsWorkflowTask entitlementGatekeeperTask = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndEntitlementId(workerEntitlementRole_ER2.id, Entitlement.findByName(entitlementThreeName).id)
        assertEquals(2, ApsWorkflowTask.findAllByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole_ER2.id, WorkflowTaskStatus.NEW).size())
        assertNotNull(gatekeeperTask.id)
        assertNotNull(entitlementGatekeeperTask.id)
        assertEquals("Gatekeeper request for Entitlement revoke not created", initialTaskCount + 3, intermediateTaskCount)
        Map responseElements = ['userAction': 'APPROVE', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(gatekeeperTask, responseElements)
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals("Provisioner request for Entitlement revoke created", intermediateTaskCount, finalTaskCount)
        ApsWorkflowUtilService.sendResponseElements(entitlementGatekeeperTask, responseElements)
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals("Provisioner request for Entitlement revoke created", intermediateTaskCount + 3, finalTaskCount)
        assertEquals(6, ApsWorkflowTask.findAllByWorkerEntitlementRoleId(workerEntitlementRole_ER2.id).size())

        ApsWorkflowTask entitlementRevokeTask1_ER2 = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndEntitlementId(workerEntitlementRole_ER2.id, entitlement2.id)
        ApsWorkflowTask entitlementRevokeTask3_ER2 = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndEntitlementId(workerEntitlementRole_ER2.id, entitlement.id)
        assertNotNull(entitlementRevokeTask1_ER2)
        assertNotNull(entitlementRevokeTask3_ER2)
        assertEquals("Entitlement Ids do not match", entitlement2.id, entitlementRevokeTask1_ER2.entitlementId)
        assertEquals("Workflow Type do not match", ApsWorkflowType.ROLE_REVOKE_REQUEST, entitlementRevokeTask1_ER2.workflowType)
        assertEquals("Node name do not match", "Entitlement Revoke Request", entitlementRevokeTask1_ER2.nodeName)

        List<ApsWorkflowTask> entitlementRevokeTask2List = ApsWorkflowTask.findAllByWorkerEntitlementRoleIdAndEntitlementId(workerEntitlementRole_ER2.id, entitlement3.id)
        ApsWorkflowTask entitlementRevokeTask2_ER2 = entitlementRevokeTask2List.sort {it.lastUpdated}.toList().last()
        assertNotNull(entitlementRevokeTask2_ER2)
        assertEquals("Entitlement Ids do not match", entitlement3.id, entitlementRevokeTask2_ER2.entitlementId)
        assertEquals("Workflow Type do not match", ApsWorkflowType.ROLE_REVOKE_REQUEST, entitlementRevokeTask2_ER2.workflowType)
        assertEquals("Node name do not match", "Entitlement Revoke Request", entitlementRevokeTask2_ER2.nodeName)

        responseElements = ["userAction": "CONFIRM", "accessJustification": "Access Revoked by APS Application"]
        Integer initialResponseCount = CareCentralResponse.count()
        ApsWorkflowUtilService.sendResponseElements(entitlementRevokeTask1_ER2, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, entitlementRevokeTask1_ER2.status)
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals("Provisioner request for Entitlement revoke not created", intermediateTaskCount + 3, finalTaskCount)

        Integer finalResponseCount
        finalResponseCount = CareCentralResponse.count()
        assertEquals('Response created for Care Central after workflow completion', initialResponseCount, finalResponseCount)

        ApsWorkflowUtilService.sendResponseElements(entitlementRevokeTask1_ER2, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, entitlementRevokeTask1_ER2.status)
        ApsWorkflowUtilService.sendResponseElements(entitlementRevokeTask3_ER2, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, entitlementRevokeTask3_ER2.status)
        finalResponseCount = CareCentralResponse.count()
        assertEquals('Response created for Care Central after workflow completion', initialResponseCount, finalResponseCount)
        ApsWorkflowUtilService.sendResponseElements(entitlementRevokeTask2_ER2, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, entitlementRevokeTask2_ER2.status)
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals("Provisioner request for Entitlement revoke not deleted", intermediateTaskCount + 3, finalTaskCount)
        finalResponseCount = CareCentralResponse.count()
        assertEquals('Response not created for Care Central after workflow completion', initialResponseCount + 1, finalResponseCount)
    }

    Entitlement createAnEntitlement(String name) {
        List<String> standards = ['S-1', 'S-2']
        EntitlementPolicy entitlementPolicy = new EntitlementPolicy(name: System.currentTimeMillis().toString(), standards: standards).save(failOnError: true)
        Entitlement entitlement = new Entitlement(name: name, alias: name, gatekeepers: [gatekeeper], origin: origin, owner: owner, status: EntitlementStatus.INACTIVE, type: entitlementPolicy.id, isApproved: true).save(failOnError: true)
        return entitlement
    }


    Employee createAnEmployee() {
        Employee employee = new Employee()
        employee.person = createAPerson()
        employee.save(failOnError: true)
        employee = employee.refresh()
        assertNotNull(employee.id)
        return employee
    }

    Person createAPerson() {
        Person person = new Person(firstName: 'John', lastName: 'Doe', phone: System.currentTimeMillis().toString(), slid: System.currentTimeMillis().toString()).save(failOnError: true)
        return person
    }

    CcEntitlementRole createCcEntitlementRole(EntitlementRole role) {
        CcEntitlementRole entitlementRole = new CcEntitlementRole()
        entitlementRole.id = role.id
        entitlementRole.notes = role.notes
        entitlementRole.name = role.name
        entitlementRole.status = EntitlementStatus.INACTIVE
        entitlementRole.gatekeepers = "${role.name}'s Gatekeeper"
        entitlementRole.location = Location.list().find {it.isBusinessUnit()}
        entitlementRole.save(failOnError: true)
        assertNotNull(entitlementRole.id)
        return entitlementRole
    }

    Location createABusinessUnit() {
        LocationType type = new LocationType(type: 'Business Unit', level: 3).save(failOnError: true)
        Location location = new Location(name: 'BU-1', locationType: type).save(failOnError: true)
        return location
    }
}
