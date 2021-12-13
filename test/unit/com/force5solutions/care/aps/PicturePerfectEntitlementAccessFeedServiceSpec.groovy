package com.force5solutions.care.aps

import grails.plugin.spock.UnitSpec
import com.force5solutions.care.feed.PicturePerfectEntitlementAccessFeedService
import com.force5solutions.care.common.MetaClassHelper
import groovy.sql.Sql
import com.force5solutions.care.feed.HrInfo
import com.force5solutions.care.feed.FeedRun
import com.force5solutions.care.feed.FeedRunReportMessage
import com.force5solutions.care.feed.FeedRunReportMessageDetail
import com.force5solutions.care.cc.Worker
import com.force5solutions.care.cc.Employee
import com.force5solutions.care.cc.EntitlementPolicy
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.force5solutions.care.cc.Person


class PicturePerfectEntitlementAccessFeedServiceSpec extends UnitSpec {

    PicturePerfectEntitlementAccessFeedService service

    def setupSpec() {
        MetaClassHelper.enrichClasses();
        String sqlFilePath = "data-files/ppEntitlementAccessFeedServiceSpec.sql"
        Sql sql = Sql.newInstance("jdbc:hsqldb:mem:devDB", "org.hsqldb.jdbcDriver")
        String[] commands = new File(sqlFilePath).text.split(";");
        for (String command: commands) {
            sql.execute command.replace("\n", " ");
        }
    }

    def setup() {
        mockConfig('''
            ppPolicy = "Physical"
            emailDomain = "@gmail.com"
            ppOwner = "ppOwner"
            feed {
                ppFeed {
                            url = 'jdbc:mysql://localhost:3306/caresap?user=root&password=igdefault'
                            driver = 'com.mysql.jdbc.Driver'
                            entitlementAccessQuery = 'select PERSONNEL_NUMBER, CATEGORY from SECURITY_INFO'
                            entitlementQuery = 'select CATEGORY from PS_READAREACATS'
                        }
            }'''
        )
        mockDomain(Entitlement)
        mockDomain(EntitlementPolicy)
        mockDomain(Employee)
        mockDomain(Person)
        mockDomain(ApsPerson)
        mockDomain(RoleOwner)
        mockDomain(Origin)
        mockDomain(EntitlementPolicy)
        mockDomain(Worker)
        mockDomain(HrInfo)
        mockDomain(FeedRun)
        mockDomain(FeedRunReportMessage)
        mockDomain(FeedRunReportMessageDetail)
        mockLogging(PicturePerfectEntitlementAccessFeedService)

        def entitlementService = mockFor(EntitlementService)
        entitlementService.demand.getActiveEntitlementsForWorker(0..100) { Worker worker ->
            return [Entitlement.list().first()]
        }

        service = new PicturePerfectEntitlementAccessFeedService()
        service.entitlementService = entitlementService.createMock()

        RoleOwner roleOwner = createRoleOwner(createApsPerson("gatekeeper-1"))
        Origin origin = new Origin(name: Origin.PICTURE_PERFECT_FEED).save()
        EntitlementPolicy entitlementPolicy = new EntitlementPolicy(name: (ConfigurationHolder.config.ppPolicy).toString()).save()

        createEmployee(createPerson("gatekeeper-1"))
        addPersonInHrInfo("gatekeeper-1", 5001)
        addPersonInHrInfo("gatekeeper-1", 5002)
        addPersonInHrInfo("gatekeeper-1", 5003)
        addPersonInHrInfo("gatekeeper-1", 5004)
        addPersonInHrInfo("gatekeeper-1", 5006)
        addPersonInHrInfo("gatekeeper-1", 5008)
        addPersonInHrInfo("gatekeeper-1", 5011)
        addPersonInHrInfo("gatekeeper-1", 5010)
        addPersonInHrInfo("gatekeeper-1", 5014)

        Employee.metaClass.static.findBySlid = {String s ->
            Employee employee = null
            List<Employee> employeeList = Employee.list()
            employeeList.each {
                if (it.person.slid == s) {
                    employee = it
                }
            }
            return employee
        }

        Employee.metaClass.static.countBySlid = {String s ->
            int employeeSlidCount = 0
            List<Employee> employeeList = Employee.list()
            employeeList.each {
                if (it.person.slid == s) {
                    employeeSlidCount++
                }
            }
            return employeeSlidCount
        }

        (1..15).each {
            createEntitlement("Feed Entitlement-${it}", origin, roleOwner, entitlementPolicy)
        }


    }

    def "Feed runs without problems"() {
        when:
        FeedRun feedRun = service.execute();

        then:
        FeedRun.count() == 1
        feedRun.getErrorMessages().size() == 0
        feedRun.getExceptionMessages().size() == 5
    }

    private Entitlement createEntitlement(String entitlementName, Origin origin, RoleOwner owner, EntitlementPolicy entitlementPolicy) {
        Entitlement entitlement = new Entitlement(name: entitlementName, alias: entitlementName, origin: origin, owner: owner, type: entitlementPolicy.id, isApproved: true).s()
        return entitlement
    }

    private ApsPerson createApsPerson(String slid) {
        ApsPerson person = new ApsPerson()
        person.firstName = "RO-firstName"
        person.middleName = "RO-middleName"
        person.lastName = "RO-lastName"
        person.phone = "6234567899"
        person.email = "care.force5@gmail.com"
        person.notes = "Role Owner Notes"
        person.slid = slid
        person.save()
        return person
    }

    private Person createPerson(String slid) {
        Person person = new Person()
        person.firstName = "Person-firstName"
        person.middleName = "Person-middleName"
        person.lastName = "Person-lastName"
        person.phone = "6234567899"
        person.email = "care.force5+person@gmail.com"
        person.notes = "Person Notes"
        person.slid = slid
        person.save()
        return person
    }

    private RoleOwner createRoleOwner(ApsPerson person) {
        RoleOwner roleOwner = new RoleOwner()
        roleOwner.person = person
        roleOwner.save()
        return roleOwner
    }

    private void createEmployee(Person person) {
        new Employee(person: person).save()
    }

    private void addPersonInHrInfo(String slid, Long pernr) {
        HrInfo hrInfo = new HrInfo()
        hrInfo.pernr = pernr
        hrInfo.slid = slid
        hrInfo.save()
    }
}
