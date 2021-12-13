package com.force5solutions.care.feed

import com.force5solutions.care.aps.Entitlement
import com.force5solutions.care.aps.EntitlementAttribute
import com.force5solutions.care.aps.Origin
import com.force5solutions.care.aps.RoleOwner
import com.force5solutions.care.cc.EntitlementPolicy
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.force5solutions.care.aps.ApsPerson
import org.codehaus.groovy.grails.commons.ApplicationHolder

import com.force5solutions.care.aps.EntitlementInfoFromFeed
import com.force5solutions.care.common.CareConstants

class CategoryAreaReaderFileFeedService extends FileFeedService {

    boolean transactional = true
    def apsWorkflowUtilService = ApplicationHolder?.application?.mainContext?.getBean("apsWorkflowUtilService")

    List<FeedRunReportMessage> reportMessages = []
    List<CategoryAreaReaderVO> categoryAreaReaderVOs = []
    List<String> processedCategoryNames = []

    String ppOwnerString = ConfigurationHolder.config.ppOwner
    String ppPolicyString = ConfigurationHolder.config.ppPolicy
    String ppOriginString = ConfigurationHolder.config.ppOrigin

    RoleOwner roleOwner = RoleOwner.findByPerson(ApsPerson.findBySlid(ppOwnerString.toUpperCase()))
    List<EntitlementPolicy> entitlementPolicyList = EntitlementPolicy.findAllByNameInList(ppPolicyString.tokenize(',')*.trim())
    Origin origin = Origin.findByName(ppOriginString)

    FeedRunReportMessage entitlementsCreatedMessage = new FeedRunReportMessage(type: FeedReportMessageType.INFO)
    FeedRunReportMessage entitlementsUpdatedMessage = new FeedRunReportMessage(type: FeedReportMessageType.INFO)
    FeedRunReportMessage recordsProcessedMessage = new FeedRunReportMessage(type: FeedReportMessageType.INFO)
    FeedRunReportMessage exceptionWhileImportingData = new FeedRunReportMessage(type: FeedReportMessageType.ERROR)
    FeedRunReportMessage exceptionWhileUpdatingEntitlementMessage = new FeedRunReportMessage(type: FeedReportMessageType.EXCEPTION, operation: FeedOperation.UPDATE)
    FeedRunReportMessage exceptionWhileCreatingEntitlementMessage = new FeedRunReportMessage(type: FeedReportMessageType.EXCEPTION, operation: FeedOperation.CREATE)

    Integer entitlementsCreatedCount = 0
    Integer entitlementsUpdatedCount = 0
    Integer exceptionWhileImportingDataCount = 0
    Integer exceptionWhileUpdatingEntitlementCount = 0
    Integer exceptionWhileCreatingEntitlementCount = 0
    Integer recordsProcessedCount = 0

    List<FeedRunReportMessageDetail> entitlementCreatedDetails = []
    List<FeedRunReportMessageDetail> entitlementUpdatedDetails = []
    List<FeedRunReportMessageDetail> exceptionWhileUpdatingEntitlementDetails = []
    List<FeedRunReportMessageDetail> uniqueExceptionWhileUpdatingEntitlementDetails = []
    List<FeedRunReportMessageDetail> exceptionWhileCreatingEntitlementDetails = []
    List<FeedRunReportMessageDetail> uniqueExceptionWhileCreatingEntitlementDetails = []

    public static Boolean isPreviewMode = false

    CategoryAreaReaderFileFeedService() {
        feedName = "Category Area Reader Feed"
        fileName = ConfigurationHolder.config.feed.categoryAreaReaderFileFeed.fileName
    }

    def process(def categoryAreaReaderVO) {}

    void preValidate() {
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

        if (entitlementsUpdatedCount) {
            entitlementsUpdatedMessage.operation = FeedOperation.UPDATE
            entitlementsUpdatedMessage.message = "Updated Entitlement objects"
            entitlementsUpdatedMessage.numberOfRecords = entitlementsUpdatedCount
            entitlementUpdatedDetails.each {
                it.feedRunReportMessage = entitlementsUpdatedMessage
                entitlementsUpdatedMessage.details << it
            }
            reportMessages.add(entitlementsUpdatedMessage)
        }

        if (exceptionWhileUpdatingEntitlementCount) {
            exceptionWhileUpdatingEntitlementMessage.with {
                message = "Exception occurred while updating Entitlement"
                numberOfRecords = exceptionWhileUpdatingEntitlementCount
            }
            uniqueExceptionWhileUpdatingEntitlementDetails.each {
                it.feedRunReportMessage = exceptionWhileUpdatingEntitlementMessage
                exceptionWhileUpdatingEntitlementMessage.details << it
            }
            reportMessages.add(exceptionWhileUpdatingEntitlementMessage)
        }

        if (exceptionWhileCreatingEntitlementCount > 0) {
            exceptionWhileCreatingEntitlementMessage.with {
                message = "Entitlement not found for category in the feed"
                numberOfRecords = exceptionWhileCreatingEntitlementCount
            }
            uniqueExceptionWhileCreatingEntitlementDetails.each {
                it.feedRunReportMessage = exceptionWhileCreatingEntitlementMessage
                exceptionWhileCreatingEntitlementMessage.details << it
            }
            reportMessages.add(exceptionWhileCreatingEntitlementMessage)
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
        Map<String, List<CategoryAreaReaderVO>> categoryWiseAreaAndReaders = (Map<String, List<CategoryAreaReaderVO>>) categoryAreaReaderVOs.groupBy { it.categoryName }
        categoryWiseAreaAndReaders.each { String categoryName, List<CategoryAreaReaderVO> categoryAreaReaderVOs ->
            Entitlement entitlement = Entitlement.findByName(categoryName)
            if (!entitlement) {
                addEntitlementCreateExceptionAndUpdateExceptionCount(categoryName, categoryAreaReaderVOs)
            } else {
                categoryAreaReaderVOs.each { CategoryAreaReaderVO categoryAreaReaderVO ->
                    addUpdateExceptionOnAttributeChangeFromFeed(entitlement, categoryAreaReaderVO);
                }
            }
        }
    }

    void startWorkflows() {
        if (!isPreviewMode) {
            if (exceptionWhileCreatingEntitlementCount > 0) {
                startWorkFlowsForEntitlementNotFoundInCategoryAreaFeed()
            }
            if (exceptionWhileUpdatingEntitlementCount > 0) {
                startWorkFlowForUpdateEntitlementsException()
            }
        }
    }

    void startWorkFlowsForEntitlementNotFoundInCategoryAreaFeed() {
        Map<String, List<FeedRunReportMessageDetail>> feedDetailsGroupedByCategoryName = getFeedDetailsGroupedByCategoryName(exceptionWhileCreatingEntitlementDetails)
        Map feedDetailsForWhichNewEntitlementInfoObjectsAreToBeCreated = [:]
        Boolean isInitialLoadOfEntitlementsFromFileFeedService = ConfigurationHolder.config.isInitialLoadOfEntitlementsFromFileFeedService.toString().toBoolean()
        if (!isInitialLoadOfEntitlementsFromFileFeedService) {
            List<EntitlementInfoFromFeed> existingEntitlementInfoObjectsForFeed = getEntitlementInfoObjectsForWhichCreateEntitlementWorkflowHasStarted(feedDetailsGroupedByCategoryName.collect { it?.key })
            updateExistingEntitlementInfoObjectsForWhichWorkflowIsRunning(feedDetailsGroupedByCategoryName, existingEntitlementInfoObjectsForFeed)
            List<String> existingCategoryNames = existingEntitlementInfoObjectsForFeed*.entitlementName
            feedDetailsForWhichNewEntitlementInfoObjectsAreToBeCreated = feedDetailsGroupedByCategoryName.findAll { !(it.key in existingCategoryNames) } as Map
        } else {
            feedDetailsForWhichNewEntitlementInfoObjectsAreToBeCreated = feedDetailsGroupedByCategoryName
        }
        createNewEntitlementInfoObjectsForCreateEntitlementExceptionAndStartTheirWorkflow(feedDetailsForWhichNewEntitlementInfoObjectsAreToBeCreated, isInitialLoadOfEntitlementsFromFileFeedService)
    }

    void updateExistingEntitlementInfoObjectsForWhichWorkflowIsRunning(Map feedDetailsGroupedByCategoryName, List existingEntitlementsForFeed) {
        existingEntitlementsForFeed.each { EntitlementInfoFromFeed entitlementInfoFromFeed ->
            List feedDetailsForEntitlementInfoFromFeed = feedDetailsGroupedByCategoryName.find { entitlementInfoFromFeed.entitlementName == it?.key }.value as List
            updateAttributesOfEntitlementInfoFromFeed(entitlementInfoFromFeed, feedDetailsForEntitlementInfoFromFeed)
        }
    }

    void createNewEntitlementInfoObjectsForCreateEntitlementExceptionAndStartTheirWorkflow(Map feedDetailsForWhichNewEntitlementInfoObjectsAreToBeCreated, Boolean isInitialLoadOfEntitlementsFromFileFeedService) {
        EntitlementInfoFromFeed entitlementInfoFromFeed
        feedDetailsForWhichNewEntitlementInfoObjectsAreToBeCreated.each { String categoryName, List feedRunDetails ->
            entitlementInfoFromFeed = createEntitlementInfo(feedRunDetails, CareConstants.WORKFLOW_TYPE_FOR_CREATE_ENTITLEMENT_FROM_FILE_FEED)
            if (isInitialLoadOfEntitlementsFromFileFeedService) {
                apsWorkflowUtilService.createOrUpdateEntitlementFromFeedDetail(entitlementInfoFromFeed.id)
            } else {
                apsWorkflowUtilService.startCreateEntitlementExceptionFromFeedWorkFlow(entitlementInfoFromFeed)
            }
        }
    }

    void createNewEntitlementInfoObjectsForUpdateEntitlementExceptionAndStartTheirWorkflow(Map feedDetailsForWhichNewEntitlementInfoObjectsAreToBeCreated, Boolean isInitialLoadOfEntitlementsFromFileFeedService) {
        EntitlementInfoFromFeed entitlementInfoFromFeed
        feedDetailsForWhichNewEntitlementInfoObjectsAreToBeCreated.each { String categoryName, List feedRunDetails ->
            entitlementInfoFromFeed = createEntitlementInfo(feedRunDetails, CareConstants.WORKFLOW_TYPE_FOR_UPDATE_ENTITLEMENT_FROM_FILE_FEED)
            if (isInitialLoadOfEntitlementsFromFileFeedService) {
                apsWorkflowUtilService.createOrUpdateEntitlementFromFeedDetail(entitlementInfoFromFeed.id)
            } else {
                apsWorkflowUtilService.startUpdateEntitlementExceptionFromFeedWorkFlow(entitlementInfoFromFeed)
            }
        }
    }

    void updateAttributesOfEntitlementInfoFromFeed(EntitlementInfoFromFeed entitlementInfoFromFeed, List<FeedRunReportMessageDetail> feedRunReportMessageDetails) {
        List<String> areaAttributes = []
        List<String> readerAttributes = []
        feedRunReportMessageDetails.each {
            areaAttributes << it?.param2
            readerAttributes << it?.param3
        }
        entitlementInfoFromFeed.areaAttributes = areaAttributes.unique()
        entitlementInfoFromFeed.readerAttributes = readerAttributes.unique()
        entitlementInfoFromFeed.s()
    }

    EntitlementInfoFromFeed createEntitlementInfo(List<FeedRunReportMessageDetail> feedRunReportMessageDetails, String workFlowType) {
        EntitlementInfoFromFeed entitlementInfoFromFeed
        if (feedRunReportMessageDetails) {
            FeedRunReportMessageDetail feedRunReportMessageDetail = feedRunReportMessageDetails.first()
            entitlementInfoFromFeed = new EntitlementInfoFromFeed(entitlementId: feedRunReportMessageDetail?.entityId, entitlementName: feedRunReportMessageDetail?.param1,
                    workflowType: workFlowType)
            entitlementInfoFromFeed.s()
            updateAttributesOfEntitlementInfoFromFeed(entitlementInfoFromFeed, feedRunReportMessageDetails)
        }
        entitlementInfoFromFeed
    }

    Map<String, List<FeedRunReportMessageDetail>> getFeedDetailsGroupedByCategoryName(List<FeedRunReportMessageDetail> feedRunReportMessageDetails) {
        return (Map<String, List<FeedRunReportMessageDetail>>) feedRunReportMessageDetails?.flatten()?.groupBy { it?.param1 }
    }

    List<EntitlementInfoFromFeed> getEntitlementInfoObjectsForWhichCreateEntitlementWorkflowHasStarted(List<String> allCategoryNames) {
        getEntitlementInfoWithCriteria(allCategoryNames, CareConstants.WORKFLOW_TYPE_FOR_CREATE_ENTITLEMENT_FROM_FILE_FEED)
    }

    List<EntitlementInfoFromFeed> getEntitlementInfoObjectsForWhichUpdateEntitlementWorkflowHasStarted(List<String> allCategoryNames) {
        getEntitlementInfoWithCriteria(allCategoryNames, CareConstants.WORKFLOW_TYPE_FOR_UPDATE_ENTITLEMENT_FROM_FILE_FEED)
    }

    List<EntitlementInfoFromFeed> getEntitlementInfoWithCriteria(List<String> allCategoryNames, String workflowType) {
        EntitlementInfoFromFeed.createCriteria().list {
            'inList'('entitlementName', allCategoryNames)
            'eq'('isProcessed', false)
            'eq'('workflowType', workflowType)
        }
    }

    void startWorkFlowForUpdateEntitlementsException() {
        Map<String, List<FeedRunReportMessageDetail>> feedDetailsGroupedByCategoryName = getFeedDetailsGroupedByCategoryName(exceptionWhileUpdatingEntitlementDetails)
        Map feedDetailsForWhichNewEntitlementInfoObjectsAreToBeCreated = [:]
        Boolean isInitialLoadOfEntitlementsFromFileFeedService = ConfigurationHolder.config.isInitialLoadOfEntitlementsFromFileFeedService.toString().toBoolean()
        if (!isInitialLoadOfEntitlementsFromFileFeedService) {
            List<EntitlementInfoFromFeed> existingEntitlementInfoObjectsForFeed = getEntitlementInfoObjectsForWhichUpdateEntitlementWorkflowHasStarted(feedDetailsGroupedByCategoryName.collect { it?.key })
            updateExistingEntitlementInfoObjectsForWhichWorkflowIsRunning(feedDetailsGroupedByCategoryName, existingEntitlementInfoObjectsForFeed)
            List<String> existingCategoryNames = existingEntitlementInfoObjectsForFeed*.entitlementName
            feedDetailsForWhichNewEntitlementInfoObjectsAreToBeCreated = feedDetailsGroupedByCategoryName.findAll { !(it.key in existingCategoryNames) } as Map
        } else {
            feedDetailsForWhichNewEntitlementInfoObjectsAreToBeCreated = feedDetailsGroupedByCategoryName
        }
        createNewEntitlementInfoObjectsForUpdateEntitlementExceptionAndStartTheirWorkflow(feedDetailsForWhichNewEntitlementInfoObjectsAreToBeCreated, isInitialLoadOfEntitlementsFromFileFeedService)
    }

    void addUpdateExceptionOnAttributeChangeFromFeed(Entitlement entitlement, CategoryAreaReaderVO categoryAreaReaderVO) {
        if ((entitlement.type in entitlementPolicyList*.id) && entitlementHasExceptionInAttributes(entitlement, categoryAreaReaderVO)) {
            if (!processedCategoryNames.contains(categoryAreaReaderVO.categoryName)) {
                processedCategoryNames.add(categoryAreaReaderVO.categoryName)
                List<CategoryAreaReaderVO> categoryAreaReaderVOList = categoryAreaReaderVOs.findAll { it.categoryName.equals(categoryAreaReaderVO.categoryName) }
                categoryAreaReaderVOList = categoryAreaReaderVOList.unique { CategoryAreaReaderVO areaReaderVO -> [areaReaderVO.areaName, areaReaderVO.readerName] }
                categoryAreaReaderVOList.each {
                    addEntitlementUpdateExceptionAndUpdateExceptionCount(['entityType': "Entitlement", 'entityId': entitlement?.id, 'param1': entitlement.name,
                            'param2': it?.areaName,
                            'param3': it?.readerName])
                }
            }
        }
    }

    Boolean entitlementHasExceptionInAttributes(Entitlement entitlement, CategoryAreaReaderVO categoryAreaReaderVO) {
        List<String> uniqueEntitlementAttributeValues = entitlement.entitlementAttributes*.value.unique()
        List<CategoryAreaReaderVO> categoryAreaReaderVOList = categoryAreaReaderVOs.findAll { it.categoryName.equalsIgnoreCase(entitlement.name) }
        List<String> uniqueAttributeValuesForEntitlementFromFeed = (categoryAreaReaderVOList*.areaName + categoryAreaReaderVOList*.readerName).flatten().unique()
        ((uniqueEntitlementAttributeValues.size() != uniqueAttributeValuesForEntitlementFromFeed.size()) || !(entitlementHasAttributeWithKeyNameAndValue(entitlement, 'Area', categoryAreaReaderVO?.areaName) && entitlementHasAttributeWithKeyNameAndValue(entitlement, 'Reader', categoryAreaReaderVO.readerName)))
    }

    Boolean entitlementHasAttributeWithKeyNameAndValue(Entitlement entitlement, String keyName, String value) {
        List<EntitlementAttribute> entitlementAttributes = entitlement.entitlementAttributes.findAll { it.keyName == keyName }
        entitlementAttributes ? entitlementAttributes.findAll { it.value == value } as Boolean : false
    }


    void addEntitlementUpdateExceptionAndUpdateExceptionCount(Map parameterMap) {
        FeedRunReportMessageDetail exceptionDetail = new FeedRunReportMessageDetail(parameterMap)
        exceptionWhileUpdatingEntitlementDetails.add(exceptionDetail)
        if (!uniqueExceptionWhileUpdatingEntitlementDetails.any { it.entityId.equalsIgnoreCase(parameterMap['entityId']) && (it.entityType.equalsIgnoreCase(parameterMap['entityType'])) }) {
            uniqueExceptionWhileUpdatingEntitlementDetails.add(exceptionDetail)
            exceptionWhileUpdatingEntitlementCount++;
        }
    }

    void addEntitlementCreateExceptionAndUpdateExceptionCount(String categoryName, List<CategoryAreaReaderVO> categoryAreaReaderVOs) {
        categoryAreaReaderVOs.each { CategoryAreaReaderVO categoryAreaReaderVO ->
            FeedRunReportMessageDetail exceptionDetail = new FeedRunReportMessageDetail(entityType: 'Category Name', entityId: categoryName, param1: categoryName,
                    param2: categoryAreaReaderVO?.areaName, param3: categoryAreaReaderVO?.readerName)
            exceptionWhileCreatingEntitlementDetails.add(exceptionDetail)
            if (!uniqueExceptionWhileCreatingEntitlementDetails.any { it.entityId.equalsIgnoreCase(categoryName) && (it.entityType.equalsIgnoreCase('Category Name')) }) {
                uniqueExceptionWhileCreatingEntitlementDetails.add(exceptionDetail)
                exceptionWhileCreatingEntitlementCount++;
            }
        }
    }

    List getVOs(def rows) {
        rows?.each { String line ->
            if (line) {
                categoryAreaReaderVOs.add(new CategoryAreaReaderVO(line))
            }
        }
        recordsProcessedCount = categoryAreaReaderVOs.size()
        return categoryAreaReaderVOs
    }

    // This method is overloaded just to enable the preview mode for this service.
    public FeedRun execute(Boolean isPreview) {
        this.isPreviewMode = isPreview
        super.execute()
    }
}

class CategoryAreaReaderVO {
    String categoryName
    String areaName
    String readerName

    CategoryAreaReaderVO(String line) {
        List<String> tokens = line.split('\\|').toList()*.trim()
        if (tokens) {
            categoryName = tokens.get(1)
            areaName = tokens.get(0)
            readerName = tokens.get(2)
        }
    }
}
