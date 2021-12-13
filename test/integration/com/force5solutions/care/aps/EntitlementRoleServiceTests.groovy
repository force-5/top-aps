package com.force5solutions.care.aps

import grails.test.GrailsUnitTestCase

import com.force5solutions.care.workflow.ApsWorkflowTask
import com.force5solutions.care.workflow.ApsWorkflowUtilService
import org.grails.plugins.versionable.VersionHistory
import com.force5solutions.care.workflow.ApsWorkflowType
import com.force5solutions.care.workflow.WorkflowTaskStatus

class EntitlementRoleServiceTests extends GrailsUnitTestCase {

    def entitlementRoleService

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    EntitlementRole createEntitlementRole() {
        EntitlementRole role = new EntitlementRole()
        role.name = "Entitlement Role-${System.currentTimeMillis()}"
        role.owner = RoleOwner.count() ? RoleOwner.get(new Random().nextInt(RoleOwner.count()) + 1) : null
        role.gatekeepers = Gatekeeper.getAll([1l]) //as Set
        List<Entitlement> approvedEntitlements = Entitlement.listApproved()
        role.roles = [EntitlementRole.listApproved().first()]
        entitlementRoleService.save(role)
        role = role.refresh()
        return role
    }

    void approveEntitlementRole(EntitlementRole role) {
        ApsWorkflowTask task = ApsWorkflowTask.findByEntitlementRoleIdAndStatus(role.id, WorkflowTaskStatus.NEW)
        Map responseElements = ['accessJustification': 'Approved by CARE System during bootstrap', 'userAction': 'APPROVE']
        ApsWorkflowUtilService.sendResponseElements(task, responseElements)
        role = role.refresh()
    }

    void testSomething() {
        EntitlementRole role =  createEntitlementRole()
        assertNotNull(role.id)
        assertFalse(role.isApproved)
        assertEquals(2, ApsWorkflowTask.countByEntitlementRoleIdAndWorkflowType(role.id, ApsWorkflowType.ADD_ROLE))
        approveEntitlementRole(role)
        assertTrue(role.isApproved)
        VersionHistory history = VersionHistory.findByClassNameAndObjectId(EntitlementRole.class.name, role.id, [sort: 'id', order: 'desc'])
        assertNotNull(history)
        assertEquals('isApproved', history.propertyName)
        assertNotNull(history.effectiveDate)
        role.entitlements = [Entitlement.listApproved().first()]
        entitlementRoleService.save(role)
        history = VersionHistory.findByClassNameAndObjectId(EntitlementRole.class.name, role.id, [sort: 'id', order: 'desc'])
        assertEquals('entitlements', history.propertyName)
        assertNull(history.effectiveDate)
    }

}
