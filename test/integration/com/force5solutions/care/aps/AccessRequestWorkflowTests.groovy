package com.force5solutions.care.aps

import grails.test.GrailsUnitTestCase
import com.force5solutions.care.common.EntitlementStatus
import com.force5solutions.care.cc.*
import com.force5solutions.care.workflow.*
import com.force5solutions.care.common.MetaClassHelper
import org.apache.commons.logging.LogFactory

/**
 * Test cases:
 *
 * CASE 1; ER-1  ->  ACTIVE
 *          -- E-1 [GK1]
 *
 *         ER-2 [GK1] -> IS CURRENTLY BEING REQUESTED
 *           -- E-1  [GK1]
 *           -- E-2  [GK1]
 *
 *
 *
 * CASE 2; ER-1  ->  ACTIVE
 *          -- E-1 [GK1]
 *
 *         ER-2  -> IS CURRENTLY BEING REQUESTED
 *           -- E-1  [GK1]
 *           -- E-2  [GK1]
 *
 *
 *
 * CASE 3; ER-1  [GK1] ->  IS CURRENTLY BEING REQUESTED
 *          -- E-1 [GK1]
 *
 *         ER-2 [GK1] -> IS CURRENTLY BEING REQUESTED
 *           -- E-1  [GK1]
 *           -- E-2  [GK1]
 *
 *
 *
 * CASE 4; ER-1  ->  IS CURRENTLY BEING REQUESTED
 *          -- E-1 [GK1]
 *
 *         ER-2  -> IS CURRENTLY BEING REQUESTED
 *           -- E-1  [GK1]
 *           -- E-2  [GK1]
 *
 *
 *
 * CASE 5; ER-1 [GK1]  ->  IS CURRENTLY ACTIVE
 *
 *         ER-2  -> IS CURRENTLY BEING REQUESTED
 *           -- ER-1  [GK1]
 *           -- ER-3  [GK1]
 *
 *
 *
 * CASE 6; ER-1 ->  IS CURRENTLY ACTIVE
 *           -- E-1  [GK1]
 *
 *         ER-2  -> IS CURRENTLY BEING REQUESTED
 *           -- E-1  [GK1]
 *           -- E-2  [GK1]
 *
 *
 *
 */

class AccessRequestWorkflowTests extends GrailsUnitTestCase {

    Employee employee
    Entitlement entitlement
    ApsPerson person
    RoleOwner owner
    Origin origin
    Gatekeeper gatekeeper
    Location businessUnit
    private static final log = LogFactory.getLog(this)

    protected void setUp() {
        super.setUp()
        MetaClassHelper.enrichClasses()
        origin = new Origin(name: 'O-1').save(failOnError: true)
        person = new ApsPerson(firstName: 'Role-Owner', lastName: System.currentTimeMillis().toString(), slid: System.currentTimeMillis().toString()).s()
        owner = new RoleOwner(person: person).save(failOnError: true)
        gatekeeper = new Gatekeeper(person: person).save(failOnError: true)
        employee = createAnEmployee()
        entitlement = createAnEntitlement(System.currentTimeMillis().toString())
        businessUnit = createABusinessUnit()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testAccessRequest_APPROVAL_FOR_TEST_CASE_6() {
        Entitlement entitlement2 = createAnEntitlement(System.currentTimeMillis().toString())
        assertNotNull(entitlement2.id)

        EntitlementRole role1 = new EntitlementRole(name: System.currentTimeMillis().toString(), entitlements: [entitlement], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role1.id)
        EntitlementRole role2 = new EntitlementRole(name: System.currentTimeMillis().toString(), entitlements: [entitlement, entitlement2], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role2.id)

        log.debug "Entitlement Role-1 : ${role1.id}"
        log.debug "Entitlement Role-2 : ${role2.id}"

        CcEntitlementRole ccEntitlementRole1 = createCcEntitlementRole(role1)
        WorkerEntitlementRole workerEntitlementRole = new WorkerEntitlementRole()
        workerEntitlementRole.entitlementRole = ccEntitlementRole1
        workerEntitlementRole.worker = employee
        workerEntitlementRole.lastStatusChange = new Date()
        def activeStatus = EntitlementRoleAccessStatus.active
        workerEntitlementRole.status = activeStatus
        workerEntitlementRole.save(failOnError: true)
        assertNotNull(workerEntitlementRole.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 1, employee.entitlementRoles.size())

        CcEntitlementRole ccEntitlementRole2 = createCcEntitlementRole(role2)
        workerEntitlementRole = new WorkerEntitlementRole()
        workerEntitlementRole.entitlementRole = ccEntitlementRole2
        workerEntitlementRole.worker = employee
        workerEntitlementRole.lastStatusChange = new Date()
        workerEntitlementRole.status = EntitlementRoleAccessStatus.pendingApproval
        workerEntitlementRole.save(failOnError: true)
        assertNotNull(workerEntitlementRole.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 2, employee.entitlementRoles.size())

        CentralWorkflowTask task = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole.id, workItemId: 1, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REQUEST, droolsSessionId: 1, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task.id)
        Integer initialTaskCount = ApsWorkflowTask.count()
        ApsWorkflowUtilService.startRoleAccessRequestWorkflow(role2, task)
        task.status = WorkflowTaskStatus.PENDING
        task.s()
        Integer finalTaskCount = ApsWorkflowTask.count()
        assertEquals('No new task created for Entitlement', initialTaskCount + 2, finalTaskCount)
        ApsWorkflowTask roleApprovalTask = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole.id, WorkflowTaskStatus.NEW)
        assertNotNull(roleApprovalTask)
        assertEquals('Current workflow node is incorrect', 'Pending Approval by Entitlement Gatekeeper', roleApprovalTask.nodeName)
        assertEquals(2, ApsWorkflowTask.findAllByWorkerEntitlementRoleId(workerEntitlementRole.id).size())
    }

    void testAccessRequest_APPROVAL_FOR_TEST_CASE_5() {
        EntitlementRole role1 = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role1.id)
        EntitlementRole role2 = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement], owner: owner, isApproved: true).save(failOnError: true)
        assertNotNull(role2.id)
        EntitlementRole role3 = new EntitlementRole(name: System.currentTimeMillis().toString(), roles: [role1, role2], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role3.id)

        CcEntitlementRole ccEntitlementRole1 = createCcEntitlementRole(role1)
        WorkerEntitlementRole workerEntitlementRole = new WorkerEntitlementRole()
        workerEntitlementRole.entitlementRole = ccEntitlementRole1
        workerEntitlementRole.worker = employee
        workerEntitlementRole.lastStatusChange = new Date()
        workerEntitlementRole.status = EntitlementRoleAccessStatus.active
        workerEntitlementRole.save(failOnError: true)
        assertNotNull(workerEntitlementRole.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 1, employee.entitlementRoles.size())

        CcEntitlementRole ccEntitlementRole3 = createCcEntitlementRole(role3)
        workerEntitlementRole = new WorkerEntitlementRole()
        workerEntitlementRole.entitlementRole = ccEntitlementRole3
        workerEntitlementRole.worker = employee
        workerEntitlementRole.lastStatusChange = new Date()
        workerEntitlementRole.status = EntitlementRoleAccessStatus.pendingApproval
        workerEntitlementRole.save(failOnError: true)
        assertNotNull(workerEntitlementRole.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 2, employee.entitlementRoles.size())

        CentralWorkflowTask task = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole.id, workItemId: 1, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REQUEST, droolsSessionId: 1, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task.id)
        Integer initialTaskCount = ApsWorkflowTask.count()
        ApsWorkflowUtilService.startRoleAccessRequestWorkflow(role3, task)
        task.status = WorkflowTaskStatus.PENDING
        task.s()
        Integer finalTaskCount = ApsWorkflowTask.count()
        assertEquals('No new task created either for Entitlement Role', initialTaskCount + 2, finalTaskCount)
        ApsWorkflowTask roleApprovalTask = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole.id, WorkflowTaskStatus.NEW)
        assertNotNull(roleApprovalTask)
        assertTrue(roleApprovalTask.nodeName == 'Pending Approval by Entitlement Role Gatekeeper')
        assertEquals(2, ApsWorkflowTask.findAllByWorkerEntitlementRoleId(workerEntitlementRole.id).size())
    }

    void testAccessRequest_APPROVAL_FOR_TEST_CASE_1() {
        Entitlement entitlement2 = createAnEntitlement(System.currentTimeMillis().toString())
        EntitlementRole role1 = new EntitlementRole(name: System.currentTimeMillis().toString(), entitlements: [entitlement], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role1.id)
        EntitlementRole role2 = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement, entitlement2], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role2.id)

        CcEntitlementRole ccEntitlementRole = createCcEntitlementRole(role1)
        WorkerEntitlementRole workerEntitlementRole = new WorkerEntitlementRole()
        workerEntitlementRole.entitlementRole = ccEntitlementRole
        workerEntitlementRole.worker = employee
        workerEntitlementRole.lastStatusChange = new Date()
        workerEntitlementRole.status = EntitlementRoleAccessStatus.active
        workerEntitlementRole.save(failOnError: true)
        assertNotNull(workerEntitlementRole.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 1, employee.entitlementRoles.size())

        ccEntitlementRole = createCcEntitlementRole(role2)
        workerEntitlementRole = new WorkerEntitlementRole()
        workerEntitlementRole.entitlementRole = ccEntitlementRole
        workerEntitlementRole.worker = employee
        workerEntitlementRole.lastStatusChange = new Date()
        workerEntitlementRole.status = EntitlementRoleAccessStatus.pendingApproval
        workerEntitlementRole.save(failOnError: true)
        assertNotNull(workerEntitlementRole.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 2, employee.entitlementRoles.size())

        CentralWorkflowTask task = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole.id, workItemId: 1, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REQUEST, droolsSessionId: 1, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task.id)
        Integer initialTaskCount = ApsWorkflowTask.count()
        ApsWorkflowUtilService.startRoleAccessRequestWorkflow(role2, task)
        task.status = WorkflowTaskStatus.PENDING
        task.s()
        Integer finalTaskCount = ApsWorkflowTask.count()
        assertEquals('No new task created either for Entitlement Role or Entitlement (Contained within an Entitlement Role)', initialTaskCount + 2, finalTaskCount)
        ApsWorkflowTask roleApprovalTask = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole.id, WorkflowTaskStatus.NEW)
        assertNotNull(roleApprovalTask)
        assertTrue(roleApprovalTask.nodeName == 'Pending Approval by Entitlement Role Gatekeeper')
        assertEquals(2, ApsWorkflowTask.findAllByWorkerEntitlementRoleId(workerEntitlementRole.id).size())

        Map responseElements = ['userAction': 'APPROVE', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(roleApprovalTask, responseElements)
        assertNotNull(ApsWorkflowTask.findByIdAndStatus(roleApprovalTask.id, WorkflowTaskStatus.COMPLETE))
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals('No new task created for asking Provisioner to provide access on Entitlement', initialTaskCount + 3, finalTaskCount)
        assertEquals(3, ApsWorkflowTask.findAllByWorkerEntitlementRoleId(workerEntitlementRole.id).size())

        ApsWorkflowTask provisionerTask = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole.id, WorkflowTaskStatus.NEW)
        assertEquals('Current node name is incorrect', 'Pending Approval from Entitlement Provisioner', provisionerTask.nodeName)
        assertTrue(provisionerTask.nodeName == 'Pending Approval from Entitlement Provisioner')
        assertEquals(workerEntitlementRole.id, provisionerTask.workerEntitlementRoleId)
        Integer initialResponseCount = CareCentralResponse.count()

        responseElements = ['userAction': 'CONFIRM', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(provisionerTask, responseElements)
        assertNotNull(ApsWorkflowTask.findByIdAndStatus(provisionerTask.id, WorkflowTaskStatus.COMPLETE))

        Integer finalResponseCount = CareCentralResponse.count()
        assertEquals('No response created for Care Central after workflow completion', initialResponseCount + 1, finalResponseCount)
    }


    void testAccessRequest_APPROVAL_FOR_TEST_CASE_2() {
        Entitlement entitlement2 = createAnEntitlement(System.currentTimeMillis().toString())
        EntitlementRole role1 = new EntitlementRole(name: System.currentTimeMillis().toString(), entitlements: [entitlement], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role1.id)
        EntitlementRole role2 = new EntitlementRole(name: System.currentTimeMillis().toString(), entitlements: [entitlement, entitlement2], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role2.id)
        CcEntitlementRole ccEntitlementRole = createCcEntitlementRole(role1)
        WorkerEntitlementRole workerEntitlementRole = new WorkerEntitlementRole()
        workerEntitlementRole.entitlementRole = ccEntitlementRole
        workerEntitlementRole.worker = employee
        workerEntitlementRole.lastStatusChange = new Date()
        workerEntitlementRole.status = EntitlementRoleAccessStatus.active
        workerEntitlementRole.save(failOnError: true)
        assertNotNull(workerEntitlementRole.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 1, employee.entitlementRoles.size())

        ccEntitlementRole = createCcEntitlementRole(role2)
        workerEntitlementRole = new WorkerEntitlementRole()
        workerEntitlementRole.entitlementRole = ccEntitlementRole
        workerEntitlementRole.worker = employee
        workerEntitlementRole.lastStatusChange = new Date()
        workerEntitlementRole.status = EntitlementRoleAccessStatus.pendingApproval
        workerEntitlementRole.save(failOnError: true)
        assertNotNull(workerEntitlementRole.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 2, employee.entitlementRoles.size())

        CentralWorkflowTask task = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole.id, workItemId: 1, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REQUEST, droolsSessionId: 1, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task.id)
        Integer initialTaskCount = ApsWorkflowTask.count()
        ApsWorkflowUtilService.startRoleAccessRequestWorkflow(role2, task)
        task.status = WorkflowTaskStatus.PENDING
        task.s()
        Integer finalTaskCount = ApsWorkflowTask.count()
        assertEquals('No new task created either for Entitlement Role or Entitlement (Contained within an Entitlement Role)', initialTaskCount + 2, finalTaskCount)
        ApsWorkflowTask entitlementApprovalTask = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole.id, WorkflowTaskStatus.NEW)
        assertEquals(entitlementApprovalTask.entitlementId, entitlement2.id)
        assertEquals(2, ApsWorkflowTask.findAllByWorkerEntitlementRoleId(workerEntitlementRole.id).size())

        Map responseElements = ['userAction': 'APPROVE', 'accessJustification': 'Approved by CARE System Tests']
        assertNotNull(entitlementApprovalTask)
        ApsWorkflowUtilService.sendResponseElements(entitlementApprovalTask, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, entitlementApprovalTask.status)
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals('No new task created for asking Provisioner to provide access on Entitlement', initialTaskCount + 3, finalTaskCount)

        assertEquals(3, ApsWorkflowTask.findAllByWorkerEntitlementRoleId(workerEntitlementRole.id).size())

        ApsWorkflowTask provisionerTask = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole.id, WorkflowTaskStatus.NEW)
        assertEquals('Current node name is incorrect', 'Pending Approval from Entitlement Provisioner', provisionerTask.nodeName)
        assertTrue(provisionerTask.workerEntitlementRoleId == workerEntitlementRole.id)
        Integer initialResponseCount = CareCentralResponse.count()

        responseElements = ['userAction': 'CONFIRM', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(provisionerTask, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, provisionerTask.status)

        Integer finalResponseCount = CareCentralResponse.count()
        assertEquals('No response created for Care Central after workflow completion', initialResponseCount + 1, finalResponseCount)
    }
//
//
    void testAccessRequest_APPROVAL_FOR_TEST_CASE_3() {
        Entitlement entitlement2 = createAnEntitlement(System.currentTimeMillis().toString())
        EntitlementRole role1 = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role1.id)
        EntitlementRole role2 = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement, entitlement2], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role2.id)

        CcEntitlementRole ccEntitlementRole1 = createCcEntitlementRole(role1)
        WorkerEntitlementRole workerEntitlementRole_ER1 = new WorkerEntitlementRole()
        workerEntitlementRole_ER1.entitlementRole = ccEntitlementRole1
        workerEntitlementRole_ER1.worker = employee
        workerEntitlementRole_ER1.lastStatusChange = new Date()
        workerEntitlementRole_ER1.status = EntitlementRoleAccessStatus.pendingApproval
        workerEntitlementRole_ER1.save(failOnError: true)
        assertNotNull(workerEntitlementRole_ER1.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 1, employee.entitlementRoles.size())

        CentralWorkflowTask task1 = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole_ER1.id, workItemId: 1, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REQUEST, droolsSessionId: 1, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task1.id)
        ApsWorkflowUtilService.startRoleAccessRequestWorkflow(role1, task1)
        task1.status = WorkflowTaskStatus.PENDING
        task1.s()

        CcEntitlementRole ccEntitlementRole2 = createCcEntitlementRole(role2)
        WorkerEntitlementRole workerEntitlementRole_ER2 = new WorkerEntitlementRole()
        workerEntitlementRole_ER2.entitlementRole = ccEntitlementRole2
        workerEntitlementRole_ER2.worker = employee
        workerEntitlementRole_ER2.lastStatusChange = new Date()
        workerEntitlementRole_ER2.status = EntitlementRoleAccessStatus.pendingApproval
        workerEntitlementRole_ER2.save(failOnError: true)
        assertNotNull(workerEntitlementRole_ER2.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 2, employee.entitlementRoles.size())

        CentralWorkflowTask task2 = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole_ER2.id, workItemId: 2, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REQUEST, droolsSessionId: 2, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task2.id)
        Integer initialTaskCount = ApsWorkflowTask.count()
        ApsWorkflowUtilService.startRoleAccessRequestWorkflow(role2, task2)
        task2.status = WorkflowTaskStatus.PENDING
        task2.s()
        Integer finalTaskCount = ApsWorkflowTask.count()

        assertEquals('No new task created either for Entitlement Role or Entitlement (Contained within an Entitlement Role)', initialTaskCount + 2, finalTaskCount)

        ApsWorkflowTask roleApprovalTask_ER1 = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole_ER1.id, WorkflowTaskStatus.NEW)
        assertNotNull(roleApprovalTask_ER1)
        assertTrue(roleApprovalTask_ER1.nodeName == 'Pending Approval by Entitlement Role Gatekeeper')
        assertEquals(2, ApsWorkflowTask.findAllByWorkerEntitlementRoleId(workerEntitlementRole_ER1.id).size())

        Map responseElements = ['userAction': 'APPROVE', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(roleApprovalTask_ER1, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, roleApprovalTask_ER1.status)
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals('No new task created for asking Provisioner to provide access on Entitlement', initialTaskCount + 3, finalTaskCount)

        ApsWorkflowTask roleApprovalTask_ER2 = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole_ER2.id, WorkflowTaskStatus.NEW)
        assertNotNull(roleApprovalTask_ER2)
        assertTrue(roleApprovalTask_ER2.nodeName == 'Pending Approval by Entitlement Role Gatekeeper')
        assertEquals(2, ApsWorkflowTask.findAllByWorkerEntitlementRoleId(workerEntitlementRole_ER2.id).size())

        ApsWorkflowUtilService.sendResponseElements(roleApprovalTask_ER2, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, roleApprovalTask_ER2.status)

        finalTaskCount = ApsWorkflowTask.count()
        assertEquals('No new task created for asking Provisioner to provide access on Entitlement', initialTaskCount + 5, finalTaskCount)

        ApsWorkflowTask provisionerTask_ER1 = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole_ER1.id, WorkflowTaskStatus.NEW)

        assertEquals('Current node name is incorrect', 'Pending Approval from Entitlement Provisioner', provisionerTask_ER1.nodeName)
        assertTrue(provisionerTask_ER1.nodeName == 'Pending Approval from Entitlement Provisioner')
        assertTrue(provisionerTask_ER1.workerEntitlementRoleId == workerEntitlementRole_ER1.id)
        assertTrue(provisionerTask_ER1.entitlementId == entitlement.id)
        Integer initialResponseCount = CareCentralResponse.count()

        responseElements = ['userAction': 'CONFIRM', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(provisionerTask_ER1, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, provisionerTask_ER1.status)

        Integer finalResponseCount = CareCentralResponse.count()
        assertEquals('No response created for Care Central after workflow completion', initialResponseCount + 1, finalResponseCount)

        finalTaskCount = ApsWorkflowTask.count()
        assertEquals('Only one pending task should be remaining i.e. for Entitlement-2 approval ', initialTaskCount + 5, finalTaskCount)

        ApsWorkflowTask provisionerTask_ER2 = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole_ER2.id, WorkflowTaskStatus.NEW)

        assertTrue(provisionerTask_ER2.nodeName == 'Pending Approval from Entitlement Provisioner')
        assertTrue(provisionerTask_ER2.workerEntitlementRoleId == workerEntitlementRole_ER2.id)
        assertTrue(provisionerTask_ER2.entitlementId == entitlement.id)

        ApsWorkflowUtilService.sendResponseElements(provisionerTask_ER2, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, provisionerTask_ER2.status)

        finalResponseCount = CareCentralResponse.count()
        assertEquals('No response created for Care Central after workflow completion', initialResponseCount + 1, finalResponseCount)
        assertEquals('No pending task should be remaining', 1, ApsWorkflowTask.countByStatus(WorkflowTaskStatus.NEW))
    }

    void testAccessRequest_APPROVAL_FOR_TEST_CASE_4() {
        Entitlement entitlement2 = createAnEntitlement(System.currentTimeMillis().toString())
        EntitlementRole role1 = new EntitlementRole(name: System.currentTimeMillis().toString(), entitlements: [entitlement], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role1.id)
        EntitlementRole role2 = new EntitlementRole(name: System.currentTimeMillis().toString(), entitlements: [entitlement, entitlement2], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role2.id)

        CcEntitlementRole ccEntitlementRole1 = createCcEntitlementRole(role1)
        WorkerEntitlementRole workerEntitlementRole_ER1 = new WorkerEntitlementRole()
        workerEntitlementRole_ER1.entitlementRole = ccEntitlementRole1
        workerEntitlementRole_ER1.worker = employee
        workerEntitlementRole_ER1.lastStatusChange = new Date()
        workerEntitlementRole_ER1.status = EntitlementRoleAccessStatus.pendingApproval
        workerEntitlementRole_ER1.save(failOnError: true)
        assertNotNull(workerEntitlementRole_ER1.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 1, employee.entitlementRoles.size())

        CentralWorkflowTask task1 = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole_ER1.id, workItemId: 1, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REQUEST, droolsSessionId: 1, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task1.id)
        ApsWorkflowUtilService.startRoleAccessRequestWorkflow(role1, task1)
        task1.status = WorkflowTaskStatus.PENDING
        task1.s()

        CcEntitlementRole ccEntitlementRole2 = createCcEntitlementRole(role2)
        WorkerEntitlementRole workerEntitlementRole_ER2 = new WorkerEntitlementRole()
        workerEntitlementRole_ER2.entitlementRole = ccEntitlementRole2
        workerEntitlementRole_ER2.worker = employee
        workerEntitlementRole_ER2.lastStatusChange = new Date()
        workerEntitlementRole_ER2.status = EntitlementRoleAccessStatus.pendingApproval
        workerEntitlementRole_ER2.save(failOnError: true)
        assertNotNull(workerEntitlementRole_ER2.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 2, employee.entitlementRoles.size())

        Integer initialTaskCount = ApsWorkflowTask.count()
        CentralWorkflowTask task2 = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole_ER2.id, workItemId: 2, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REQUEST, droolsSessionId: 2, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task2.id)
        ApsWorkflowUtilService.startRoleAccessRequestWorkflow(role2, task2)
        task2.status = WorkflowTaskStatus.PENDING
        task2.s()
        Integer finalTaskCount = ApsWorkflowTask.count()
        assertEquals('No new task created either for Entitlement Role or Entitlement (Contained within an Entitlement Role)', initialTaskCount + 2, finalTaskCount)
        ApsWorkflowTask entitlementApprovalTask_ER1 = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole_ER1.id, WorkflowTaskStatus.NEW)
        assertNotNull(entitlementApprovalTask_ER1)
        assertEquals('Current node name is incorrect', 'Pending Approval by Entitlement Gatekeeper', entitlementApprovalTask_ER1.nodeName)
        assertEquals(2, ApsWorkflowTask.findAllByWorkerEntitlementRoleId(workerEntitlementRole_ER1.id).size())
        Map responseElements = ['userAction': 'APPROVE', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(entitlementApprovalTask_ER1, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, entitlementApprovalTask_ER1.status)
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals('No new task created for asking Provisioner to provide access on Entitlement', initialTaskCount + 3, finalTaskCount)
        ApsWorkflowTask entitlementApprovalTask_ER2 = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole_ER2.id, WorkflowTaskStatus.NEW)
        assertNotNull(entitlementApprovalTask_ER2)
        assertEquals('Current node name is incorrect', 'Pending Approval by Entitlement Gatekeeper', entitlementApprovalTask_ER2.nodeName)
        assertEquals(2, ApsWorkflowTask.findAllByWorkerEntitlementRoleId(workerEntitlementRole_ER2.id).size())
        assertEquals(entitlementApprovalTask_ER2.entitlementId, entitlement2.id)

        ApsWorkflowUtilService.sendResponseElements(entitlementApprovalTask_ER2, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, entitlementApprovalTask_ER2.status)

        finalTaskCount = ApsWorkflowTask.count()
        assertEquals('No new task created for asking Provisioner to provide access on Entitlement', initialTaskCount + 5, finalTaskCount)


        ApsWorkflowTask provisionerTask_ER1 = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole_ER1.id, WorkflowTaskStatus.NEW)

        assertTrue(provisionerTask_ER1.nodeName == 'Pending Approval from Entitlement Provisioner')
        assertTrue(provisionerTask_ER1.workerEntitlementRoleId == workerEntitlementRole_ER1.id)
        assertTrue(provisionerTask_ER1.entitlementId == entitlement.id)
        Integer initialResponseCount = CareCentralResponse.count()

        responseElements = ['userAction': 'CONFIRM', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(provisionerTask_ER1, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, provisionerTask_ER1.status)

        Integer finalResponseCount = CareCentralResponse.count()
        assertEquals('No response created for Care Central after workflow completion', initialResponseCount + 1, finalResponseCount)

        finalTaskCount = ApsWorkflowTask.count()
        assertEquals('Only one pending task should be remaining i.e. for Entitlement-2 approval ', initialTaskCount + 5, finalTaskCount)

        ApsWorkflowTask provisionerTask_ER2 = ApsWorkflowTask.findByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole_ER2.id, WorkflowTaskStatus.NEW)

        assertTrue(provisionerTask_ER2.nodeName == 'Pending Approval from Entitlement Provisioner')
        assertTrue(provisionerTask_ER2.workerEntitlementRoleId == workerEntitlementRole_ER2.id)

        ApsWorkflowUtilService.sendResponseElements(provisionerTask_ER2, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, provisionerTask_ER2.status)

        finalResponseCount = CareCentralResponse.count()
        assertEquals('No response created for Care Central after workflow completion', initialResponseCount + 1, finalResponseCount)
        assertEquals('No pending task should be remaining', 1, ApsWorkflowTask.countByStatus(WorkflowTaskStatus.NEW))

    }
//
    void testAccessRequest_APPROVAL_FOR_ENTITLEMENT_ROLE_AND_ENTITLEMENT() {
        Entitlement entitlement2 = createAnEntitlement(System.currentTimeMillis().toString())
        EntitlementRole role1 = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement], owner: owner, isApproved: true).save(failOnError: true)
        assertNotNull(role1.id)
        EntitlementRole role2 = new EntitlementRole(name: System.currentTimeMillis().toString(), roles: [role1], entitlements: [entitlement2], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role2.id)
        CcEntitlementRole ccEntitlementRole = createCcEntitlementRole(role2)
        WorkerEntitlementRole workerEntitlementRole = new WorkerEntitlementRole()
        workerEntitlementRole.entitlementRole = ccEntitlementRole
        workerEntitlementRole.worker = employee
        workerEntitlementRole.lastStatusChange = new Date()
        workerEntitlementRole.status = EntitlementRoleAccessStatus.pendingApproval
        workerEntitlementRole.save(failOnError: true)
        assertNotNull(workerEntitlementRole.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 1, employee.entitlementRoles.size())
        CentralWorkflowTask task = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole.id, workItemId: 1, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REQUEST, droolsSessionId: 1, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task.id)
        Integer initialTaskCount = ApsWorkflowTask.count()
        ApsWorkflowUtilService.startRoleAccessRequestWorkflow(role2, task)
        task.status = WorkflowTaskStatus.PENDING
        task.s()
        Integer finalTaskCount = ApsWorkflowTask.count()
        assertEquals('No new task created either for Entitlement Role or Entitlement (Contained within an Entitlement Role)', initialTaskCount + 3, finalTaskCount)
        List<ApsWorkflowTask> tasks = ApsWorkflowTask.findAllByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole.id, WorkflowTaskStatus.NEW)
        assertEquals(2, tasks.size())

        ApsWorkflowTask roleApprovalTask = tasks[0]
        ApsWorkflowTask entitlementApprovalTask = tasks[1]
        assertNotNull(roleApprovalTask)
        assertNotNull(entitlementApprovalTask)
        Map responseElements = ['userAction': 'APPROVE', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(roleApprovalTask, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, roleApprovalTask.status)

        assertNotNull(entitlementApprovalTask)
        ApsWorkflowUtilService.sendResponseElements(entitlementApprovalTask, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, entitlementApprovalTask.status)

        tasks = ApsWorkflowTask.findAllByWorkerEntitlementRoleIdAndStatus(workerEntitlementRole.id, WorkflowTaskStatus.NEW)
        assertNotNull('No new task created for asking Provisioner to provide access on Entitlement', tasks)

        assertEquals(2, tasks.size())
        def (task1, task2) = tasks
        assertEquals('Current node name is incorrect', 'Pending Approval from Entitlement Provisioner', task1.nodeName)
        assertTrue(task1.workerEntitlementRoleId == workerEntitlementRole.id)
        assertEquals('Current node name is incorrect', 'Pending Approval from Entitlement Provisioner', task2.nodeName)
        assertTrue(task1.workerEntitlementRoleId == workerEntitlementRole.id)
        Integer initialResponseCount = CareCentralResponse.count()

        responseElements = ['userAction': 'CONFIRM', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(task1, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, task1.status)

        responseElements = ['userAction': 'CONFIRM', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(task2, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, task2.status)

        Integer finalResponseCount = CareCentralResponse.count()
        assertEquals('No response created for Care Central after workflow completion', initialResponseCount + 1, finalResponseCount)
    }

    void testAccessRequest_APPROVAL_FOR_ENTITLEMENT_ROLE() {
        EntitlementRole role = new EntitlementRole(name: System.currentTimeMillis().toString(), gatekeepers: [gatekeeper], entitlements: [entitlement], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role.id)
        CcEntitlementRole ccEntitlementRole = createCcEntitlementRole(role)
        WorkerEntitlementRole workerEntitlementRole = new WorkerEntitlementRole()
        workerEntitlementRole.entitlementRole = ccEntitlementRole
        workerEntitlementRole.worker = employee
        workerEntitlementRole.lastStatusChange = new Date()
        workerEntitlementRole.status = EntitlementRoleAccessStatus.pendingApproval
        workerEntitlementRole.save(failOnError: true)
        assertNotNull(workerEntitlementRole.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 1, employee.entitlementRoles.size())
        CentralWorkflowTask task = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole.id, workItemId: 1, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REQUEST, droolsSessionId: 1, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task.id)
        Integer initialTaskCount = ApsWorkflowTask.count()
        ApsWorkflowUtilService.startRoleAccessRequestWorkflow(role, task)
        task.status = WorkflowTaskStatus.PENDING
        task.s()
        Integer finalTaskCount = ApsWorkflowTask.count()
        assertEquals('No new task created for getting Gatekeeper approval on Entitlement Role', initialTaskCount + 2, finalTaskCount)
        ApsWorkflowTask approvalTask = ApsWorkflowTask.list().last()
        assertEquals('Pending Approval by Entitlement Role Gatekeeper', approvalTask.nodeName)
        assertEquals(workerEntitlementRole.id, approvalTask.workerEntitlementRoleId)
        Map responseElements = ['userAction': 'APPROVE', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(approvalTask, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, approvalTask.status)
        finalTaskCount = ApsWorkflowTask.count()
        assertEquals('No new task created for asking Provisioner to provide access on Entitlement', initialTaskCount + 3, finalTaskCount)
        approvalTask = ApsWorkflowTask.list().last()
        assertEquals('Pending Approval from Entitlement Provisioner', approvalTask.nodeName)
        assertEquals(workerEntitlementRole.id, approvalTask.workerEntitlementRoleId)
        responseElements = ['userAction': 'CONFIRM', 'accessJustification': 'Approved by CARE System Tests']
        Integer initialResponseCount = CareCentralResponse.count()
        ApsWorkflowUtilService.sendResponseElements(approvalTask, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, approvalTask.status)
        Integer finalResponseCount = CareCentralResponse.count()
        assertEquals('No response created for Care Central after workflow completion', initialResponseCount + 1, finalResponseCount)
    }

    void testAccessRequest_APPROVAL_FOR_ENTITLEMENT() {
        EntitlementRole role = new EntitlementRole(name: System.currentTimeMillis().toString(), entitlements: [entitlement], owner: owner, isApproved: true, isExposed: true, isPropagated: true).save(failOnError: true)
        assertNotNull(role.id)
        CcEntitlementRole ccEntitlementRole = createCcEntitlementRole(role)
        WorkerEntitlementRole workerEntitlementRole = new WorkerEntitlementRole()
        workerEntitlementRole.entitlementRole = ccEntitlementRole
        workerEntitlementRole.worker = employee
        workerEntitlementRole.lastStatusChange = new Date()
        workerEntitlementRole.status = EntitlementRoleAccessStatus.pendingApproval
        workerEntitlementRole.save(failOnError: true)
        assertNotNull(workerEntitlementRole.id)
        employee = employee.refresh()
        assertEquals("Entitlement Role could not be added to employee's entitlement roles", 1, employee.entitlementRoles.size())
        CentralWorkflowTask task = new CentralWorkflowTask(workflowGuid: UUID.randomUUID().toString(), type: CentralWorkflowTaskType.SYSTEM_APS, workerEntitlementRoleId: workerEntitlementRole.id, workItemId: 1, workflowType: CentralWorkflowType.EMPLOYEE_ACCESS_REQUEST, droolsSessionId: 1, actions: ['APPROVE', 'REJECT'], nodeName: 'Some node').save(failOnError: true)
        assertNotNull(task.id)
        Integer initialTaskCount = ApsWorkflowTask.count()
        ApsWorkflowUtilService.startRoleAccessRequestWorkflow(role, task)
        task.status = WorkflowTaskStatus.PENDING
        task.s()
        Integer finalTaskCount = ApsWorkflowTask.count()
        assertEquals('No new task created for getting Gatekeeper approval on Entitlement', initialTaskCount + 2, finalTaskCount)
        ApsWorkflowTask approvalTask = ApsWorkflowTask.list().last()
        assertEquals('Pending Approval by Entitlement Gatekeeper', approvalTask.nodeName)
        assertEquals(entitlement.id, approvalTask.entitlementId)
        assertEquals(workerEntitlementRole.id, approvalTask.workerEntitlementRoleId)
        Map responseElements = ['userAction': 'APPROVE', 'accessJustification': 'Approved by CARE System Tests']
        ApsWorkflowUtilService.sendResponseElements(approvalTask, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, approvalTask.status)
        assertEquals('No new task created for asking Provisioner to provide access on Entitlement', initialTaskCount + 2, finalTaskCount)
        approvalTask = ApsWorkflowTask.list().last()
        assertEquals('Pending Approval from Entitlement Provisioner', approvalTask.nodeName)
        assertEquals(workerEntitlementRole.id, approvalTask.workerEntitlementRoleId)
        responseElements = ['userAction': 'CONFIRM', 'accessJustification': 'Approved by CARE System Tests']
        Integer initialResponseCount = CareCentralResponse.count()
        ApsWorkflowUtilService.sendResponseElements(approvalTask, responseElements)
        assertEquals(WorkflowTaskStatus.COMPLETE, approvalTask.status)
        Integer finalResponseCount = CareCentralResponse.count()
        assertEquals('No response created for Care Central after workflow completion', initialResponseCount + 1, finalResponseCount)
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
        entitlementRole.location = Location.list().find {it.isBusinessUnit()}
        entitlementRole.gatekeepers = "${role.name}'s Gatekeeper"
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
