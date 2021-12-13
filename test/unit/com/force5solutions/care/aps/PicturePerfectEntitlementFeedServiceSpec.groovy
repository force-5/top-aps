package com.force5solutions.care.aps

import com.force5solutions.care.common.MetaClassHelper
import grails.plugin.spock.UnitSpec
import groovy.sql.Sql
import com.force5solutions.care.feed.*
import com.force5solutions.care.cc.EntitlementPolicy
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class PicturePerfectEntitlementFeedServiceSpec extends UnitSpec {

    PicturePerfectEntitlementFeedService service;

    def setupSpec() {
        MetaClassHelper.enrichClasses();
        String sqlFilePath = "data-files/ppEntitlementFeedServiceSpec.sql"
        Sql sql = Sql.newInstance("jdbc:hsqldb:mem:picturePerfectEntitlementFeedDB", "org.hsqldb.jdbcDriver")

        String[] commands = new File(sqlFilePath).text.split(";");
        for (String command: commands) {
            sql.execute command.replace("\n", " ");
        }
    }

    def setup() {
        mockConfig('''
            feed {
                 ppFeed {
                        url = 'jdbc:mysql://localhost:3306/caresap?user=root&password=igdefault'
                        driver = 'com.mysql.jdbc.Driver'
                        entitlementQuery = 'select CATEGORY from PS_READAREACATS'
                        }
            }
               emailDomain = "@gmail.com"
               ppOwner = "ppOwner"
               ppPolicy = "Physical"
'''
        )
        mockDomain(Entitlement)
        mockDomain(ApsPerson)
        mockDomain(RoleOwner)
        mockDomain(Origin)
        mockDomain(EntitlementPolicy)
        mockDomain(FeedRun)
        mockDomain(FeedRunReportMessage)
        mockDomain(FeedRunReportMessageDetail)
        mockLogging(PicturePerfectEntitlementFeedService)
        createOrigin()
        createEntitlementPolicy()
        createRoleOwner(createApsPerson((ConfigurationHolder.config.ppOwner).toString()))
        service = new PicturePerfectEntitlementFeedService();
    }

    def "Feed runs without problems"() {
        when:
        int initialCount = Entitlement.count()
        FeedRun feedRun = service.execute();

        then:
        FeedRun.count() == 1
        feedRun.getErrorMessages().size() == 0
        feedRun.getExceptionMessages().size() == 0
        feedRun.getCreateMessages().size() == 1
        feedRun.getProcessMessages().size() == 1
        feedRun.getCreateMessages().first().details.size() == 15
        Entitlement.count() == initialCount + 15
    }

    def "Report is created with an error message if not being able to connect to the remote database"() {
        when:
        service.url = "blahblahblah"
        FeedRun feedRun = service.execute()

        then:
        FeedRun.count() == 1
        feedRun.getErrorMessages().size() == 1
        feedRun.getErrorMessages().first().message == "Error occured during Picture Perfect Entitlement Feed data import"
        feedRun.getProcessMessages().size() == 0
        feedRun.getCreateMessages().size() == 0
    }

    def "Report is created with an error message if some invalid query"() {
        when:
        service.query = "select home from blah;"
        FeedRun feedRun = service.execute()

        then:
        FeedRun.count() == 1
        feedRun.getErrorMessages().size() == 1
        feedRun.getErrorMessages().first().message == "Error occured during Picture Perfect Entitlement Feed data import"
        feedRun.getProcessMessages().size() == 0
        feedRun.getCreateMessages().size() == 0
    }

    def "Report is created with an exception message if an extra feed is found in CARE"() {
        when:
        FeedRun feedRun = service.execute();
        createAnExtraEntitlement()
        feedRun = service.execute();

        then:
        FeedRun.count() == 1
        feedRun.getExceptionMessages().size() == 1
        feedRun.getExceptionMessages().first().message == "Record in CARE but not in Entitlement Feed"
    }


    def "Report is created with an error message if ALL required fields are not present"() {
        when:
        service.query = 'select AREA from PS_READAREACATS'
        FeedRun feedRun = service.execute()

        then:
        FeedRun.count() == 1
        feedRun.getErrorMessages().size() == 1
        feedRun.getExceptionMessages().size() == 0
        feedRun.getProcessMessages().size() == 0
        feedRun.getCreateMessages().size() == 0
    }

    private void createAnExtraEntitlement() {
        RoleOwner ppOwner = RoleOwner.findByPerson(ApsPerson.findBySlid((ConfigurationHolder.config.ppOwner).toString().toUpperCase()))
        EntitlementPolicy entitlementPolicy = EntitlementPolicy.findByName((ConfigurationHolder.config.ppPolicy).toString())
        Origin origin = Origin.findByName(Origin.PICTURE_PERFECT_FEED)
        new Entitlement(name: 'Feed Entitlement-16', alias: 'Feed Entitlement-16', origin: origin, owner: ppOwner, type: entitlementPolicy.id, isApproved: true).save()
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

    private void createRoleOwner(ApsPerson person) {
        new RoleOwner(person: person).save()
    }

    private EntitlementPolicy createEntitlementPolicy() {
        new EntitlementPolicy(name: (ConfigurationHolder.config.ppPolicy).toString()).save()
    }

    private void createOrigin() {
        new Origin(name: Origin.PICTURE_PERFECT_FEED).save()
    }
}
