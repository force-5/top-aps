package com.force5solutions.care.feed

import com.force5solutions.care.aps.Entitlement
import com.force5solutions.care.cc.Contractor
import com.force5solutions.care.cc.Employee
import com.force5solutions.care.cc.EntitlementPolicy
import com.force5solutions.care.cc.Worker
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class TimEntitlementWorkerFileFeedService extends FileFeedService {

    def entitlementService = ApplicationHolder?.application?.mainContext?.getBean("entitlementService")

    boolean transactional = true

    List<FeedRunReportMessage> reportMessages = []
    List<TimEntitlementWorkerVO> timEntitlementWorkerVOs = []
    Map<Worker, List<String>> feedEntitlementsByWorker = [:]
    List<EntitlementPolicy> entitlementPolicyList = EntitlementPolicy.findAllByNameInList(ConfigurationHolder.config.timRoleType.tokenize(',')*.trim())

    FeedRunReportMessage exceptionEntitlementsActiveInApsButNotInFeedMessage = new FeedRunReportMessage(type: FeedReportMessageType.EXCEPTION)
    FeedRunReportMessage exceptionEntitlementsActiveInFeedButNotInApsMessage = new FeedRunReportMessage(type: FeedReportMessageType.EXCEPTION)
    FeedRunReportMessage exceptionWorkerInFeedButNotFoundInApsMessage = new FeedRunReportMessage(type: FeedReportMessageType.EXCEPTION)
    FeedRunReportMessage recordsProcessedMessage = new FeedRunReportMessage(type: FeedReportMessageType.INFO)
    FeedRunReportMessage exceptionWhileImportingData = new FeedRunReportMessage(type: FeedReportMessageType.ERROR)

    Integer entitlementsActiveInApsButNotInFeedCount = 0
    Integer entitlementsActiveInFeedButNotInApsCount = 0
    Integer exceptionWorkerInFeedButNotFoundInApsCount = 0
    Integer exceptionWhileImportingDataCount = 0
    Integer recordsProcessedCount = 0

    List<FeedRunReportMessageDetail> entitlementsActiveInApsButNotInFeedDetails = []
    List<FeedRunReportMessageDetail> entitlementsActiveInFeedButNotInApsDetails = []
    List<FeedRunReportMessageDetail> exceptionWorkerInFeedButNotFoundInApsDetails = []

    TimEntitlementWorkerFileFeedService() {
        feedName = "Tim Entitlement Worker File Feed"
        fileName = ConfigurationHolder.config.feed.timEntitlementWorkerFileFeed.fileName
    }

    def process(def timEntitlementWorkerVO) {
        timEntitlementWorkerVO = timEntitlementWorkerVO as TimEntitlementWorkerVO
        Worker worker
        worker = Employee.findBySlid(timEntitlementWorkerVO?.slid) ?: (Contractor.findBySlid(timEntitlementWorkerVO?.slid) ?: null)
        if (worker) {
            if (feedEntitlementsByWorker.containsKey(worker)) {
                List<String> timEntitlementNames = feedEntitlementsByWorker.get(worker)
                timEntitlementNames.addAll(timEntitlementWorkerVO.timRoles)
                feedEntitlementsByWorker.put(worker, timEntitlementNames.flatten())
            } else {
                feedEntitlementsByWorker.put(worker, timEntitlementWorkerVO.timRoles)
            }
        } else {
            String entityId = timEntitlementWorkerVO.slid ?: "$timEntitlementWorkerVO.firstName, $timEntitlementWorkerVO.lastName"
            FeedRunReportMessageDetail workerInFeedButNotFoundInApsDetail = new FeedRunReportMessageDetail(entityType: "Worker", param1: timEntitlementWorkerVO.firstName, param2: timEntitlementWorkerVO.lastName, param3: timEntitlementWorkerVO.slid, entityId: entityId)
            exceptionWorkerInFeedButNotFoundInApsDetails.add(workerInFeedButNotFoundInApsDetail)
            exceptionWorkerInFeedButNotFoundInApsCount++
        }
    }

    void preValidate() {}

    void startWorkflows() {}

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

        if (entitlementsActiveInApsButNotInFeedCount) {
            exceptionEntitlementsActiveInApsButNotInFeedMessage.message = "Entitlements Active In APS But Not In Feed"
            exceptionEntitlementsActiveInApsButNotInFeedMessage.numberOfRecords = entitlementsActiveInApsButNotInFeedCount
            entitlementsActiveInApsButNotInFeedDetails.each {
                it.feedRunReportMessage = exceptionEntitlementsActiveInApsButNotInFeedMessage
                exceptionEntitlementsActiveInApsButNotInFeedMessage.details << it
            }
            reportMessages.add(exceptionEntitlementsActiveInApsButNotInFeedMessage)
        }

        if (entitlementsActiveInFeedButNotInApsCount) {
            exceptionEntitlementsActiveInFeedButNotInApsMessage.message = "Entitlements Active In Feed But Not In APS"
            exceptionEntitlementsActiveInFeedButNotInApsMessage.numberOfRecords = entitlementsActiveInFeedButNotInApsCount
            entitlementsActiveInFeedButNotInApsDetails.each {
                it.feedRunReportMessage = exceptionEntitlementsActiveInFeedButNotInApsMessage
                exceptionEntitlementsActiveInFeedButNotInApsMessage.details << it
            }
            reportMessages.add(exceptionEntitlementsActiveInFeedButNotInApsMessage)
        }

        if (exceptionWorkerInFeedButNotFoundInApsCount) {
            exceptionWorkerInFeedButNotFoundInApsMessage.message = "Worker In Feed But Not Found In APS"
            exceptionWorkerInFeedButNotFoundInApsMessage.numberOfRecords = exceptionWorkerInFeedButNotFoundInApsCount
            exceptionWorkerInFeedButNotFoundInApsDetails.each {
                it.feedRunReportMessage = exceptionWorkerInFeedButNotFoundInApsMessage
                exceptionWorkerInFeedButNotFoundInApsMessage.details << it
            }
            reportMessages.add(exceptionWorkerInFeedButNotFoundInApsMessage)
        }
        return reportMessages
    }

    List<String> getRows() {
        def rows = []
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


    void preProcess() {}


    void postProcess() {
        List<String> activeEntitlementNames = []
        feedEntitlementsByWorker.each { Worker worker, List<String> timEntitlements ->
            timEntitlements = timEntitlements*.trim()
            activeEntitlementNames = ((entitlementService.getActiveEntitlementsForWorker(worker)) as List).findAll { it.type in entitlementPolicyList*.id }*.name
            (timEntitlements - activeEntitlementNames).each { String categoryName ->
                FeedRunReportMessageDetail entitlementInFeedButNotInApsDetail = new FeedRunReportMessageDetail(entityType: "Entitlement", param1: categoryName, param2: worker.firstMiddleLastName, entityId: worker.id)
                entitlementsActiveInFeedButNotInApsDetails.add(entitlementInFeedButNotInApsDetail)
                entitlementsActiveInFeedButNotInApsCount++
            }
            (activeEntitlementNames - timEntitlements).each { String entitlementName ->
                FeedRunReportMessageDetail entitlementInApsButNotInFeedDetail = new FeedRunReportMessageDetail(entityType: "Entitlement", param1: entitlementName, param2: worker.firstMiddleLastName, entityId: worker.id)
                entitlementsActiveInApsButNotInFeedDetails.add(entitlementInApsButNotInFeedDetail)
                entitlementsActiveInApsButNotInFeedCount++
            }
        }
    }

    List getVOs(def rows) {
        rows?.each { String line ->
            if (line) { // Empty lines should not increase the recordsProcessedCount
                timEntitlementWorkerVOs.add(new TimEntitlementWorkerVO(line))
            }
        }
        timEntitlementWorkerVOs = filterCategoriesBasedOnTimRolePolicy(timEntitlementWorkerVOs)
        recordsProcessedCount = timEntitlementWorkerVOs.size()
        return timEntitlementWorkerVOs
    }

    List<TimEntitlementWorkerVO> filterCategoriesBasedOnTimRolePolicy(List<TimEntitlementWorkerVO> timEntitlementWorkerVOs) {
        List<TimEntitlementWorkerVO> timEntitlementWorkerVOList = []
        timEntitlementWorkerVOs.each { TimEntitlementWorkerVO timEntitlementWorkerVO ->
            timEntitlementWorkerVO.timRoles = timEntitlementWorkerVO.timRoles.findAll { Entitlement?.findByName(it)?.type in entitlementPolicyList*.id }
            if (timEntitlementWorkerVO.timRoles) {
                timEntitlementWorkerVOList.add(timEntitlementWorkerVO)
            }
        }
        return timEntitlementWorkerVOList
    }
}

class TimEntitlementWorkerVO {
    List<String> timRoles
    String lastName
    String firstName
    String slid

    TimEntitlementWorkerVO(String line) {
        List<String> tokens = line.split('\\|').toList()*.trim()
        String nameTokens
        if (tokens) {
            slid = tokens.get(0) ?: null
            nameTokens = tokens.get(1) ?: null
            lastName = nameTokens.split(',').toList().get(0) ?: null
            firstName = nameTokens.split(',').toList().get(1) ?: null
            timRoles = tokens.getAt(2..(tokens.size() - 1))
        }
    }
}


