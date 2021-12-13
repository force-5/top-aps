package com.force5solutions.care.aps

import grails.test.*
import com.force5solutions.care.workflow.ApsWorkflowTask
import com.force5solutions.care.workflow.ApsWorkflowUtilService
import com.force5solutions.care.common.EntitlementStatus
import com.force5solutions.care.cc.EntitlementPolicy
import com.force5solutions.care.workflow.ApsWorkflowType

class WorkflowTests extends GrailsUnitTestCase {

    def entitlementService


    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    EntitlementPolicy createEntitlementPolicy() {
        List<String> standards = ['S-1', 'S-2']
        EntitlementPolicy entitlementPolicy = new EntitlementPolicy(name: "ET-1", standards: standards).save()
        return entitlementPolicy
    }

    RoleOwner createRoleOwner() {
        ApsPerson person = new ApsPerson(firstName: 'Role-Owner', lastName: System.currentTimeMillis().toString(), slid: System.currentTimeMillis().toString()).save()
        RoleOwner roleOwner = new RoleOwner(person: person).save()
        roleOwner
    }

    Origin createOrigin() {
        Origin origin = new Origin(name: 'Origin-1').save()
        origin
    }

    public void test_CREATE_ENTITLEMENT_AND_APPROVAL_FROM_ROLE_OWNER() {
        def entitlementPolicy = createEntitlementPolicy().save()
        Origin origin = createOrigin()
        RoleOwner roleOwner = createRoleOwner()
        def initialEntitlementsCount = Entitlement.count()
        Entitlement entitlement = new Entitlement(name: "E-1", alias: "E-1", origin: origin, owner: roleOwner, status: EntitlementStatus.INACTIVE, type: entitlementPolicy.id)
        entitlementService.saveEntitlement(entitlement)
        assertFalse(entitlement.isApproved)
        def task = ApsWorkflowTask.list().last()
        assertEquals(entitlement.id, task.entitlementId)
        assertEquals(ApsWorkflowType.ADD_ENTITLEMENT, task.workflowType)
        assertEquals("Get approval from Entitlement Owner", task.nodeName)
        Map responseElements = ['userAction': 'APPROVE', 'accessJustification': 'Approved By APS System Tests']
        ApsWorkflowUtilService.sendResponseElements(task, responseElements)
        def currentEntitlementsCount = Entitlement.count()
        assertTrue(currentEntitlementsCount > initialEntitlementsCount)
        assertEquals(currentEntitlementsCount, initialEntitlementsCount + 1)
        assertEquals(entitlement.id, task.entitlementId)
        assertTrue(entitlement.isApproved)
    }

    public void test_CREATE_ENTITLEMENT_AND_REJECTION_FROM_ROLE_OWNER() {
        def entitlementPolicy = createEntitlementPolicy()
        Origin origin = createOrigin()
        RoleOwner roleOwner = createRoleOwner()
        def initialEntitlementsCount = Entitlement.count()
        Entitlement entitlement = new Entitlement(name: "E-1", alias: "E-1", origin: origin, owner: roleOwner, status: EntitlementStatus.INACTIVE, type: entitlementPolicy.id)
        entitlementService.saveEntitlement(entitlement)
        def task = ApsWorkflowTask.list().last()
        assertEquals(entitlement.id, task.entitlementId)
        assertEquals(ApsWorkflowType.ADD_ENTITLEMENT, task.workflowType)
        assertEquals("Get approval from Entitlement Owner", task.nodeName)
        Map responseElements = ['userAction': 'REJECT', 'accessJustification': 'Approved By APS System Tests']
        ApsWorkflowUtilService.sendResponseElements(task, responseElements)
        def currentEntitlementsCount = Entitlement.count()
        assertEquals(currentEntitlementsCount, initialEntitlementsCount + 1)
        assertEquals(entitlement.id, task.entitlementId)
        assertFalse(entitlement.isApproved)
    }

    public void test_UPDATE_ENTITLEMENT_AND_APPROVAL_FROM_ROLE_OWNER() {
        def entitlementPolicy = createEntitlementPolicy()
        Origin origin = createOrigin()
        RoleOwner roleOwner = createRoleOwner()
        def initialEntitlementsCount = Entitlement.count()
        Entitlement entitlement = new Entitlement(name: "E-1 Old", alias: "E-1 Old", origin: origin, owner: roleOwner, status: EntitlementStatus.INACTIVE, type: entitlementPolicy.id)
        entitlementService.saveEntitlement(entitlement)
        def task = ApsWorkflowTask.list().last()
        assertEquals(entitlement.id, task.entitlementId)
        assertEquals(ApsWorkflowType.ADD_ENTITLEMENT, task.workflowType)
        assertEquals("Get approval from Entitlement Owner", task.nodeName)
        assertFalse(entitlement.isApproved)
        Map responseElements = ['userAction': 'APPROVE', 'accessJustification': 'Approved By APS System Tests']
        ApsWorkflowUtilService.sendResponseElements(task, responseElements)
        def currentEntitlementsCount = Entitlement.count()
        assertEquals(currentEntitlementsCount, initialEntitlementsCount + 1)
        assertEquals(entitlement.id, task.entitlementId)
        assertTrue(entitlement.isApproved)

        entitlement.name = "E-1 New"
        entitlementService.saveEntitlement(entitlement)
        task = ApsWorkflowTask.list().last()
        assertEquals(entitlement.id, task.entitlementId)
        assertEquals(ApsWorkflowType.UPDATE_ENTITLEMENT, task.workflowType)
        assertEquals("Get approval from Entitlement Owner", task.nodeName)
        responseElements = ['userAction': 'APPROVE', 'accessJustification': 'Approved By APS System Tests']
        ApsWorkflowUtilService.sendResponseElements(task, responseElements)
        currentEntitlementsCount = Entitlement.count()
        assertEquals(currentEntitlementsCount, initialEntitlementsCount + 1)
        entitlement = entitlement.refresh()
        assertEquals("E-1 New", entitlement.name)
    }

    public void test_UPDATE_ENTITLEMENT_AND_REJECTION_FROM_ROLE_OWNER() {
        def entitlementPolicy = createEntitlementPolicy()
        Origin origin = createOrigin()
        RoleOwner roleOwner = createRoleOwner()
        def initialEntitlementsCount = Entitlement.count()
        Entitlement entitlement = new Entitlement(name: "E-1 Old", alias: "E-1 Old", origin: origin, owner: roleOwner, status: EntitlementStatus.INACTIVE, type: entitlementPolicy.id)
        entitlementService.saveEntitlement(entitlement)
        def task = ApsWorkflowTask.list().last()
        assertEquals(entitlement.id, task.entitlementId)
        assertEquals(ApsWorkflowType.ADD_ENTITLEMENT, task.workflowType)
        assertEquals("Get approval from Entitlement Owner", task.nodeName)
        assertFalse(entitlement.isApproved)
        Map responseElements = ['userAction': 'APPROVE', 'accessJustification': 'Approved By APS System Tests']
        ApsWorkflowUtilService.sendResponseElements(task, responseElements)
        def currentEntitlementsCount = Entitlement.count()
        assertEquals(currentEntitlementsCount, initialEntitlementsCount + 1)
        assertEquals(entitlement.id, task.entitlementId)
        assertTrue(entitlement.isApproved)

        entitlement.name = "E-1 New"
        entitlementService.saveEntitlement(entitlement)
        task = ApsWorkflowTask.list().last()
        assertEquals(entitlement.id, task.entitlementId)
        assertEquals(ApsWorkflowType.UPDATE_ENTITLEMENT, task.workflowType)
        assertEquals("Get approval from Entitlement Owner", task.nodeName)
        responseElements = ['userAction': 'REJECT', 'accessJustification': 'Approved By APS System Tests']
        ApsWorkflowUtilService.sendResponseElements(task, responseElements)
        currentEntitlementsCount = Entitlement.count()
        assertEquals(currentEntitlementsCount, initialEntitlementsCount + 1)
        entitlement = entitlement.refresh()
        assertEquals("E-1 Old", entitlement.name)
    }
}
