package com.force5solutions.care.aps

import grails.test.*
import com.force5solutions.care.cc.Employee
import com.force5solutions.care.cc.EntitlementPolicy

class EntitlementRoleTests extends GrailsUnitTestCase {


    Origin origin
    ApsPerson person
    RoleOwner roleOwner
    Gatekeeper gk1, gk2, gk3, gk4, gk5, gk6, gk7
    EntitlementRole er1, er2, er3, er4, er5, er6, er7
    Entitlement e1, e2, e3, e4, e5, e6, e7
    EntitlementPolicy et

    protected void setUp() {
        super.setUp()
        mockConfig('''
                    emailDomain='admin@gmail.com'
                ''')
        mockDomain(Origin)
        mockDomain(RoleOwner)
        mockDomain(Employee)
        mockDomain(Gatekeeper)
        mockDomain(EntitlementRole)
        mockDomain(Entitlement)
        mockDomain(EntitlementPolicy)
        mockDomain(ApsPerson)
        origin = new Origin(name: 'Origin-1').save()
        et = new EntitlementPolicy(name: "Type-1").save()
        person = new ApsPerson(firstName: 'Role-1', lastName: 'Owner-1', slid: 'role-owner-1').save()
        roleOwner = new RoleOwner(person: person).save()
        createEntitlements()
        createRoles()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void createEntitlements() {
        List<Entitlement> entitlements = []
        (1..7).each {
            entitlements << createEntitlement(it)
        }
        (e1, e2, e3, e4, e5, e6, e7) = entitlements
    }

    Entitlement createEntitlement(Integer index) {
        return new Entitlement(name: 'E-' + index, gatekeepers: [gk1], origin: origin, alias: 'E-' + index, owner: roleOwner, type: et.id).save()
    }

    EntitlementRole createRole(Integer index, Gatekeeper gatekeeper) {
        EntitlementRole entitlementRole = new EntitlementRole(owner: roleOwner, origin: origin, name: 'ER-' + index, gatekeepers: [gatekeeper])
        entitlementRole.save()
        return entitlementRole
    }

    void createRoles() {
        List<Gatekeeper> gatekeepers = createGatekeepers()
        (gk1, gk2, gk3, gk4, gk5, gk6, gk7) = gatekeepers
        List<EntitlementRole> roles = []
        (1..7).each {
            roles.add(createRole(it, gatekeepers[it - 1]))
        }
        (er1, er2, er3, er4, er5, er6, er7) = roles
    }

    List<Gatekeeper> createGatekeepers() {
        List<Gatekeeper> gatekeepers = []
        (1..7).each {
            gatekeepers.add(createGatekeeper(it))
        }
        return gatekeepers
    }

    Gatekeeper createGatekeeper(Integer index) {
        return (new Gatekeeper(firstName: 'GK-' + index, lastName: 'GK', slid: 'gk' + index).save())
    }

    void test_ROLES_THAT_REQUIRE_APPROVAL() {
        er1.gatekeepers = []
        er1.roles = [er2, er5]
        er1.save()
        assertTrue(er1.gatekeepers.size() == 0)
        assertTrue(er1.roles.size() == 2)
        er2.roles = [er3, er4]
        er2.save()
        assertTrue(er2.roles.size() == 2)
        er5.gatekeepers = []
        er5.roles = [er6, er7]
        er5.save()
        assertTrue(er5.gatekeepers.size() == 0)
        assertTrue(er5.roles.size() == 2)
        er1 = er1.refresh()
        Set<EntitlementRole> roles = er1.rolesThatRequireApproval
        assertEquals(3, roles.size())
        assertTrue([er2, er6, er7].every {it in roles})
    }

    void test_ROLES_THAT_REQUIRE_APPROVAL_1() {
        assertTrue(er1.gatekeepers.size() == 1)
        er1.gatekeepers = []
        er1.roles = [er3, er4]
        assertTrue er1.rolesThatRequireApproval.size() == 2
        assertTrue([er3, er4].every {it in er1.rolesThatRequireApproval})
    }

    void test_ENTITLEMENTS_THAT_REQUIRE_APPROVAL() {
        er1.gatekeepers = []
        er1.roles = [er3, er4]
        assertTrue(er1.rolesThatRequireApproval.size() == 2)
        assertTrue([er4].every {it in er1.rolesThatRequireApproval})
        e1 = e1.refresh()
        e1.gatekeepers = [gk3]
        er1.entitlements = [e1]
        assertTrue(er1.entitlementsThatRequireApproval.size() == 1)
        assertTrue([e1].every {it in er1.entitlementsThatRequireApproval})
    }
}
