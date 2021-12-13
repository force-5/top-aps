package com.force5solutions.care.feed

import com.force5solutions.care.aps.Entitlement
import com.force5solutions.care.cc.Contractor
import com.force5solutions.care.cc.Employee
import com.force5solutions.care.cc.EntitlementPolicy
import com.force5solutions.care.cc.Worker
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class CategoryWorkerFileFeedService extends FileFeedService {

    def entitlementService = ApplicationHolder?.application?.mainContext?.getBean("entitlementService")

    boolean transactional = true

    List<FeedRunReportMessage> reportMessages = []
    List<CategoryWorkerVO> categoryWorkerVOs = []
    Map<Worker, List<String>> feedEntitlementsByWorker = [:]
    List<EntitlementPolicy> entitlementPolicyList = EntitlementPolicy.findAllByNameInList(ConfigurationHolder.config.ppPolicy.tokenize(',')*.trim())

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

    CategoryWorkerFileFeedService() {
        feedName = "Category Worker File Feed"
        fileName = ConfigurationHolder.config.feed.categoryWorkerFileFeed.fileName
    }

    def process(def categoryWorkerVO) {
        categoryWorkerVO = categoryWorkerVO as CategoryWorkerVO
        Worker worker
        String personnelNumber = (categoryWorkerVO?.personnelNumber?.matches("[0-9]+") && categoryWorkerVO?.personnelNumber?.length()?.equals(8)) ? categoryWorkerVO.personnelNumber.toLong().toString() : null
        //TODO: Not a good way to find out the worker; Many contractors can have the same last name and first name;
        worker = personnelNumber ? Employee.findByWorkerNumber(personnelNumber) : (Contractor.findByFirstNameAndLastName(categoryWorkerVO.firstName.trim(), categoryWorkerVO.lastName.trim()) ?: null)

        if (worker) {
            if (feedEntitlementsByWorker.containsKey(worker)) {
                List<String> categoryNames = feedEntitlementsByWorker.get(worker)
                categoryNames.add(categoryWorkerVO.categoryName)
                feedEntitlementsByWorker.put(worker, categoryNames)
            } else {
                feedEntitlementsByWorker.put(worker, [categoryWorkerVO.categoryName])
            }
        } else {
            String entityId = categoryWorkerVO.personnelNumber ?: categoryWorkerVO.badge ?: categoryWorkerVO.firstName
            FeedRunReportMessageDetail workerInFeedButNotFoundInApsDetail = new FeedRunReportMessageDetail(entityType: "Worker", param1: categoryWorkerVO.firstName, param2: categoryWorkerVO.lastName, param3: categoryWorkerVO.personnelNumber, entityId: entityId)
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
        feedEntitlementsByWorker.each { Worker worker, List<String> categoryNames ->
            categoryNames = categoryNames*.trim()
            activeEntitlementNames = ((entitlementService.getActiveEntitlementsForWorker(worker)) as List).findAll { it.type in entitlementPolicyList*.id }*.name
            (categoryNames - activeEntitlementNames).each { String categoryName ->
                FeedRunReportMessageDetail entitlementInFeedButNotInApsDetail = new FeedRunReportMessageDetail(entityType: "Entitlement", param1: categoryName, param2: worker.firstMiddleLastName, entityId: worker.id)
                entitlementsActiveInFeedButNotInApsDetails.add(entitlementInFeedButNotInApsDetail)
                entitlementsActiveInFeedButNotInApsCount++
            }
            (activeEntitlementNames - categoryNames).each { String entitlementName ->
                FeedRunReportMessageDetail entitlementInApsButNotInFeedDetail = new FeedRunReportMessageDetail(entityType: "Entitlement", param1: entitlementName, param2: worker.firstMiddleLastName, entityId: worker.id)
                entitlementsActiveInApsButNotInFeedDetails.add(entitlementInApsButNotInFeedDetail)
                entitlementsActiveInApsButNotInFeedCount++
            }
        }
    }

    List getVOs(def rows) {
        rows?.each { String line ->
            if (line) { // Empty lines should not increase the recordsProcessedCount
                categoryWorkerVOs.add(new CategoryWorkerVO(line))
            }
        }
        categoryWorkerVOs = filterCategoriesBasedOnPpPolicy(categoryWorkerVOs)
        recordsProcessedCount = categoryWorkerVOs.size()
        return categoryWorkerVOs
    }

    List<CategoryWorkerVO> filterCategoriesBasedOnPpPolicy(List<CategoryWorkerVO> categoryWorkerVOs) {
        List<CategoryWorkerVO> categoryWorkerVoList = []
        categoryWorkerVOs.each { CategoryWorkerVO categoryWorkerVO ->
            if (Entitlement?.findByName(categoryWorkerVO.categoryName)?.type in entitlementPolicyList*.id) {
                categoryWorkerVoList.add(categoryWorkerVO)
            }
        }
        return categoryWorkerVoList
    }
}

class CategoryWorkerVO {
    String categoryName
    String lastName
    String firstName
    String slid
    String personnelNumber
    String jobTitle
    String badge

    CategoryWorkerVO(String line) {
        List<String> tokens = line.split('\\|').toList()*.trim()
        if (tokens) {
            lastName = tokens.get(0) ?: null
            firstName = tokens.get(1) ?: null
            personnelNumber = tokens.get(2) ?: null
            categoryName = tokens.get(3) ?: null
            badge = tokens.get(4) ?: null
            jobTitle = tokens.get(5) ?: null
        }
    }
}

