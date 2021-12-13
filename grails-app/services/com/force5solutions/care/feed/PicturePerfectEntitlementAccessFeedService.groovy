package com.force5solutions.care.feed


import groovy.sql.GroovyRowResult
import com.force5solutions.care.cc.Employee
import com.force5solutions.care.aps.Entitlement
import com.force5solutions.care.cc.Worker
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
 * Responsible for validating employees' access to Picture Perfect entitlements.
 * Logs the following kinds of exceptions:
 *   -
 *   -
 */
class PicturePerfectEntitlementAccessFeedService extends DatabaseFeedService {

    boolean transactional = true
    def entitlementService = ApplicationHolder?.application?.mainContext?.getBean("entitlementService")

    List<String> personnelNumber = []
    List<String> entitlements = []

    List<FeedRunReportMessage> reportMessages = []




    FeedRunReportMessage exceptionPersonFoundInFeedButNotInApsMessage = new FeedRunReportMessage(type: FeedReportMessageType.EXCEPTION)
    FeedRunReportMessage exceptionPersonFoundInFeedButNotInHrInfo = new FeedRunReportMessage(type: FeedReportMessageType.EXCEPTION)
    FeedRunReportMessage exceptionEntitlementsGivenInFeedButNotInApsMessage = new FeedRunReportMessage(type: FeedReportMessageType.EXCEPTION)
    FeedRunReportMessage exceptionEntitlementsGivenInApsButNotInFeedMessage = new FeedRunReportMessage(type: FeedReportMessageType.EXCEPTION)
    FeedRunReportMessage exceptionEntitlementsExistingInFeedButNotCreatedInApsMessage = new FeedRunReportMessage(type: FeedReportMessageType.EXCEPTION)

    List<FeedRunReportMessageDetail> exceptionEmployeeDetails = []
    List<FeedRunReportMessageDetail> exceptionHrInfoDetails = []
    List<FeedRunReportMessageDetail> entitlementToBeGivenDetails = []
    List<FeedRunReportMessageDetail> entitlementsToBeRevokedDetails = []
    List<FeedRunReportMessageDetail> entitlementsNotCreatedInAPSDetails = []

    Integer entitlementsNotCreatedInAPSCount = 0
    Integer exceptionsEmployeeCount = 0
    Integer exceptionsHrInfoCount = 0
    Integer entitlementsToBeGivenCount = 0
    Integer entitlementsToBeRevokedCount = 0




    PicturePerfectEntitlementAccessFeedService() {
        feedName = "Picture Perfect Entitlement Access"
        driver = ConfigurationHolder.config.feed.ppFeed.driver
        url = ConfigurationHolder.config.feed.ppFeed.url
        query = ConfigurationHolder.config.feed.ppFeed.entitlementAccessQuery
    }

    public Map getQueryParameters() {
        return [:]
    }


    void preValidate() {}

    void preProcess() {}

    void postProcess() {}

    void startWorkflows() {}

    List<FeedRunReportMessage> getFeedRunReportMessages() {

        exceptionPersonFoundInFeedButNotInApsMessage.message = "Persons in Entitlement Feed but not in CARE"
        exceptionPersonFoundInFeedButNotInApsMessage.numberOfRecords = exceptionsEmployeeCount
        exceptionEmployeeDetails.each { FeedRunReportMessageDetail messageDetail ->
            messageDetail.feedRunReportMessage = exceptionPersonFoundInFeedButNotInApsMessage
            exceptionPersonFoundInFeedButNotInApsMessage.addToDetails(messageDetail)
        }
        reportMessages.add(exceptionPersonFoundInFeedButNotInApsMessage)

        exceptionPersonFoundInFeedButNotInHrInfo.message = "Persons in Entitlement Feed but not in HRINFO"
        exceptionPersonFoundInFeedButNotInHrInfo.numberOfRecords = exceptionsHrInfoCount
        exceptionHrInfoDetails.each { FeedRunReportMessageDetail messageDetail ->
            messageDetail.feedRunReportMessage = exceptionPersonFoundInFeedButNotInHrInfo
            exceptionPersonFoundInFeedButNotInHrInfo.addToDetails(messageDetail)
        }
        reportMessages.add(exceptionPersonFoundInFeedButNotInHrInfo)

        exceptionEntitlementsGivenInFeedButNotInApsMessage.message = "Entitlements given in Entitlement Feed but not in CARE"
        exceptionEntitlementsGivenInFeedButNotInApsMessage.numberOfRecords = entitlementsToBeGivenCount
        entitlementToBeGivenDetails.each { FeedRunReportMessageDetail messageDetail ->
            messageDetail.feedRunReportMessage = exceptionEntitlementsGivenInFeedButNotInApsMessage
            exceptionEntitlementsGivenInFeedButNotInApsMessage.addToDetails(messageDetail)
        }
        reportMessages.add(exceptionEntitlementsGivenInFeedButNotInApsMessage)


        exceptionEntitlementsGivenInApsButNotInFeedMessage.message = "Entitlements given in CARE but not in Entitlement Feed"
        exceptionEntitlementsGivenInApsButNotInFeedMessage.numberOfRecords = entitlementsToBeRevokedCount
        entitlementsToBeRevokedDetails.each { FeedRunReportMessageDetail messageDetail ->
            messageDetail.feedRunReportMessage = exceptionEntitlementsGivenInApsButNotInFeedMessage
            exceptionEntitlementsGivenInApsButNotInFeedMessage.addToDetails(messageDetail)
        }
        reportMessages.add(exceptionEntitlementsGivenInApsButNotInFeedMessage)

        exceptionEntitlementsExistingInFeedButNotCreatedInApsMessage.message = "Entitlements existing in Feed but not created in APS"
        exceptionEntitlementsExistingInFeedButNotCreatedInApsMessage.numberOfRecords = entitlementsNotCreatedInAPSCount
        entitlementsNotCreatedInAPSDetails.each { FeedRunReportMessageDetail messageDetail ->
            messageDetail.feedRunReportMessage = exceptionEntitlementsExistingInFeedButNotCreatedInApsMessage
            exceptionEntitlementsExistingInFeedButNotCreatedInApsMessage.addToDetails(messageDetail)
        }
        reportMessages.add(exceptionEntitlementsExistingInFeedButNotCreatedInApsMessage)

        return reportMessages
    }


    List getVOs(def rows) {
        List<PPAccessFeedVO> feedVOs = []
        rows?.each { GroovyRowResult rowResult ->
            personnelNumber.add(rowResult.getProperty('PERSONNEL_NUMBER') as String)
            entitlements.add(rowResult.getProperty('CATEGORY') as String)
        }
        feedVOs.add(new PPAccessFeedVO(personnelNumber, entitlements))
        return feedVOs
    }


    def process(def feedVO) {
        log.info "Processing Picture Perfect Entitlements Access Feed now.."
        if (feedVO.personnelNumber) {
            Set<String> pernr = feedVO.personnelNumber as Set
            matchEmployees(pernr)
        }

        if (feedVO.entitlements) {
            matchEntitlementsExistInFeedButNotCreatedInAPS(feedVO.entitlements)
            matchEntitlementsGivenInFeedButNotInCare(feedVO.personnelNumber, feedVO.entitlements)
            matchEntitlementsGivenInCareButNotInFeed(feedVO.personnelNumber, feedVO.entitlements)
        }
    }

    private void matchEmployees(Set<String> personnelNumber) {
        personnelNumber.each { String pernr ->
            HrInfo hrInfo = HrInfo.findByPernr(pernr as Long)
            if (hrInfo) {
                String slid = hrInfo.slid
                if (!Employee.countBySlid(slid)) {
                    FeedRunReportMessageDetail exceptionDetail = new FeedRunReportMessageDetail(entityType: "Employee with pernr", param1: pernr, entityId: pernr)
                    exceptionEmployeeDetails.add(exceptionDetail)
                    exceptionsEmployeeCount++
                }
            } else {
                FeedRunReportMessageDetail exceptionDetail = new FeedRunReportMessageDetail(entityType: "Employee with pernr", param1: pernr, entityId: pernr)
                exceptionHrInfoDetails.add(exceptionDetail)
                exceptionsHrInfoCount++
            }
        }
    }



    private void matchEntitlementsGivenInFeedButNotInCare(List<String> personnelNumber, List<String> entitlements) {
        personnelNumber.eachWithIndex { String pernr, int i ->
            HrInfo hrInfo = HrInfo.findByPernr(pernr.toLong())
            Worker worker = Employee.findBySlid(hrInfo.slid as String)

            if (worker) {
                String correspondingRowEntitlement = entitlements.getAt(i) as String
                Entitlement entitlement = Entitlement.findByName(correspondingRowEntitlement)
                List<Entitlement> activeEntitlements = (entitlementService.getActiveEntitlementsForWorker(worker) as List)
                if (entitlement && !(entitlement in activeEntitlements)) {
                    FeedRunReportMessageDetail exceptionDetail = new FeedRunReportMessageDetail(entityType: "Entitlement ", param1: entitlement.toString(), param2: worker.toString(), entityId: entitlement.id)
                    entitlementToBeGivenDetails.add(exceptionDetail)
                    entitlementsToBeGivenCount++
                }
            }
        }
    }

    private void matchEntitlementsGivenInCareButNotInFeed(List<String> personnelNumber, List<String> entitlements) {

        /** *****************************************************************************************************/
        /*
        The logic below is applied to create a map from two lists (i.e. personnelNumber and entitlements),
        where personnelNumber list is : [pN1, pN1, pN1, pN2, pN2]
        & entitlements list is       :  [e1,  e2,  e3,  e1,  e2]

        The resulting finalMap would be: [pN1:[e1, e2, e3], pN2:[e1, e2]]
        */
        List holderList = []
        Map holderMap = [:]
        Map finalMap = [:]

        personnelNumber.eachWithIndex { it, i ->
            holderList.add([workerId: it, entitlementId: entitlements.getAt(i)])
        }
        holderMap = holderList.groupBy { it.workerId }
        holderMap.each {
            finalMap.put(it.key, it.value*.entitlementId)
        }
        /** *****************************************************************************************************/

        finalMap.each { key, value ->
            HrInfo hrInfo = HrInfo.findByPernr(key.toString().toLong())
            Worker worker = Employee.findBySlid(hrInfo.slid as String)
            if (worker) {
                List<Entitlement> activeEntitlements = (entitlementService.getActiveEntitlementsForWorker(worker) as List)
                List<Entitlement> activePpEntitlements = activeEntitlements.findAll { Entitlement e -> e.owner.slid.equals("ppOwner") }
                List<Entitlement> entitlementsInFeedForAWorker = []

                value.each { String feedEntitlement ->
                    entitlementsInFeedForAWorker << Entitlement.findByName(feedEntitlement)
                }

                activePpEntitlements.each { Entitlement ppEntitlement ->
                    if (!(ppEntitlement in entitlementsInFeedForAWorker)) {
                        FeedRunReportMessageDetail exceptionDetail = new FeedRunReportMessageDetail(entityType: "Entitlement ", param1: ppEntitlement.toString(), param2: worker.toString(), entityId: ppEntitlement.id)
                        entitlementsToBeRevokedDetails.add(exceptionDetail)
                        entitlementsToBeRevokedCount++
                    }
                }
            }
        }
    }

    private void matchEntitlementsExistInFeedButNotCreatedInAPS(List<String> entitlements) {
        entitlements = entitlements.unique()
        entitlements.each { String entitlement ->
            if (!Entitlement.countByName(entitlement)) {
                FeedRunReportMessageDetail exceptionDetail = new FeedRunReportMessageDetail(entityType: "Entitlement ", param1: entitlement, entityId: entitlement)
                entitlementsNotCreatedInAPSDetails.add(exceptionDetail)
                entitlementsNotCreatedInAPSCount++
            }
        }
    }
}

class PPAccessFeedVO {
    List personnelNumber = []
    List entitlements = []

    PPAccessFeedVO(List pernr, List entitlements) {
        this.personnelNumber = pernr
        this.entitlements = entitlements
    }

    PPAccessFeedVO() {
    }
}