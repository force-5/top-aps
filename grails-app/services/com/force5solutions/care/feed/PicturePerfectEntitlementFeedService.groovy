package com.force5solutions.care.feed

import com.force5solutions.care.aps.ApsPerson
import com.force5solutions.care.aps.Origin
import com.force5solutions.care.aps.RoleOwner
import com.force5solutions.care.cc.EntitlementPolicy
import groovy.sql.GroovyRowResult
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.force5solutions.care.aps.Entitlement

class PicturePerfectEntitlementFeedService extends DatabaseFeedService {

    boolean transactional = true

    List<FeedRunReportMessage> reportMessages = []
    List<String> entitlementNames = []

    String ppOwnerString = ConfigurationHolder.config.ppOwner
    String ppPolicyString = ConfigurationHolder.config.ppPolicy
    String ppOriginString = ConfigurationHolder.config.ppOrigin

    RoleOwner ppOwner = RoleOwner.findByPerson(ApsPerson.findBySlid(ppOwnerString.toUpperCase()))
    EntitlementPolicy entitlementPolicy = EntitlementPolicy.findByName(ppPolicyString)
    Origin origin = Origin.findByName(ppOriginString)

    FeedRunReportMessage exceptionsMessage = new FeedRunReportMessage(type: FeedReportMessageType.EXCEPTION)
    FeedRunReportMessage entitlementsCreatedMessage = new FeedRunReportMessage(type: FeedReportMessageType.INFO)
    FeedRunReportMessage recordsProcessedMessage = new FeedRunReportMessage(type: FeedReportMessageType.INFO)

    FeedRunReportMessage exceptionWhileImportingData = new FeedRunReportMessage(type: FeedReportMessageType.ERROR)

    Integer entitlementsCreatedCount = 0
    Integer exceptionsCount = 0
    Integer exceptionWhileImportingDataCount = 0
    Integer recordsProcessedCount = 0

    List<FeedRunReportMessageDetail> entitlementCreatedDetails = []
    List<FeedRunReportMessageDetail> exceptionDetails = []

    PicturePerfectEntitlementFeedService() {
        feedName = "Picture Perfect Entitlement Feed"
        driver = ConfigurationHolder.config.feed.ppFeed.driver
        url = ConfigurationHolder.config.feed.ppFeed.url
        query = ConfigurationHolder.config.feed.ppFeed.entitlementQuery
    }

    def process(def entitlementName) {
        if (!Entitlement.countByName(entitlementName)) {
            Entitlement entitlement = createEntitlement(entitlementName, origin, ppOwner, entitlementPolicy)
            FeedRunReportMessageDetail entitlementCreatedDetail = new FeedRunReportMessageDetail(entityType: "Entitlement", param1: entitlement.toString(), entityId: entitlement.id)
            entitlementCreatedDetails.add(entitlementCreatedDetail)
            entitlementsCreatedCount++
        }

    }

    public Entitlement createEntitlement(String entitlementName, Origin origin, RoleOwner owner, EntitlementPolicy entitlementPolicy) {
        Entitlement entitlement = new Entitlement(name: entitlementName, alias: entitlementName, origin: origin, owner: owner, type: entitlementPolicy.id, isApproved: true).s()
        log.info "Created entitlement : " + entitlement
        return entitlement
    }


    List<FeedRunReportMessage> getFeedRunReportMessages() {

        if (recordsProcessedCount) {
            recordsProcessedMessage.operation = FeedOperation.PROCESS
            recordsProcessedMessage.numberOfRecords = recordsProcessedCount
            reportMessages.add(recordsProcessedMessage)
        }

        if (exceptionWhileImportingDataCount) {
            exceptionWhileImportingData.with {
                message = "Error occured during ${feedName} data import"
                numberOfRecords = exceptionWhileImportingDataCount
            }
            reportMessages.add(exceptionWhileImportingData)
        }

        if (entitlementsCreatedCount) {
            entitlementsCreatedMessage.operation = FeedOperation.CREATE
            entitlementsCreatedMessage.message = "Created Entitlement objects"
            entitlementsCreatedMessage.numberOfRecords = entitlementsCreatedCount
            entitlementCreatedDetails.each {
                it.feedRunReportMessage = entitlementsCreatedMessage
                entitlementsCreatedMessage.details << it
            }
            reportMessages.add(entitlementsCreatedMessage)
        }

        if (exceptionsCount) {
            exceptionsMessage.message = "Record in CARE but not in Entitlement Feed"
            exceptionsMessage.numberOfRecords = exceptionsCount
            exceptionDetails.each {
                it.feedRunReportMessage = exceptionsMessage
                exceptionsMessage.details << it
            }
            reportMessages.add(exceptionsMessage)
        }
        return reportMessages
    }


    List<GroovyRowResult> getRows() {
        List<GroovyRowResult> rows = []
        try {
            Map queryParams = getQueryParameters()
            rows = getDataAndErrorReport(queryParams)
        } catch (Throwable t) {
            t.printStackTrace();
            exceptionWhileImportingDataCount++
        }

        return rows
    }


    public Map getQueryParameters() {
        return [:]
    }

    void startWorkflows() {}

    void preProcess() {}

    void preValidate() {}

    void postProcess() {

        List<Entitlement> entitlementsInCareButNotInFeed = []
        Entitlement.list().each { Entitlement entitlement ->
            if (entitlement.origin == origin && !(entitlement.name in entitlementNames)) {
                entitlementsInCareButNotInFeed.add(entitlement)
            }
        }

        if (entitlementsInCareButNotInFeed) {
            entitlementsInCareButNotInFeed.each { Entitlement entitlementInCareButNotInFeed ->
                FeedRunReportMessageDetail exceptionDetail = new FeedRunReportMessageDetail(entityType: "Entitlement", param1: entitlementInCareButNotInFeed.toString(), entityId: entitlementInCareButNotInFeed.id)
                exceptionDetails.add(exceptionDetail)
                exceptionsCount++
            }
        }
    }

    List getVOs(def rows) {
        rows?.each { GroovyRowResult rowResult ->
            entitlementNames.add(rowResult.getProperty('CATEGORY') as String)
        }
        recordsProcessedCount = entitlementNames.size()
        return entitlementNames
    }
}
