package com.force5solutions.care.feed

import com.force5solutions.care.aps.Origin
import com.force5solutions.care.cc.EntitlementPolicy
import com.force5solutions.care.aps.ApsPerson
import com.force5solutions.care.aps.RoleOwner
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.force5solutions.care.aps.Entitlement

class TimEntitlementFeedService {

    String feedName;

    boolean transactional = true

    def timService = ApplicationHolder?.application?.mainContext?.getBean("timService")
    def entitlementService = ApplicationHolder?.application?.mainContext?.getBean("entitlementService")

    TimEntitlementFeedService() {
        feedName = "TIM Entitlement Feed"
    }

    public FeedRun executeFeed(Boolean isPreviewMode) {
        log.info "Processing TIM Entitlements Feed now.."
        FeedRun feedRun = new FeedRun(feedName: feedName)
        List<String> timRoles = []
        FeedRunReportMessage reportMessage

        try {
            //TODO: In prod, we should uncomment the line below and remove the line which assigns the hard-code value to timRoles
            timRoles = timService.getRoles()
//            timRoles = ['Entitlement - 1A', 'Category 7', 'Category 9', 'Dummyxzx Entitlement']
        } catch (Exception e) {
            e.printStackTrace();
            reportMessage = new FeedRunReportMessage()
            reportMessage.type = FeedReportMessageType.ERROR
            reportMessage.message = "Error occured during ${feedName} data import"
        }

        if (reportMessage) {
            reportMessage.feedRun = feedRun
            feedRun.addToReportMessages(reportMessage)
        }

        if (timRoles) {
            String timOwnerString = ConfigurationHolder.config.timOwner
            String timRoleTypeString = ConfigurationHolder.config.timRoleType
            RoleOwner timOwner = RoleOwner.findByPerson(ApsPerson.findBySlid(timOwnerString))
            EntitlementPolicy entitlementPolicy = EntitlementPolicy.findByName(timRoleTypeString)
            Origin origin = Origin.findByName(Origin.TIM_FEED)
            createEntitlements(timRoles, origin, timOwner, entitlementPolicy, isPreviewMode).each { FeedRunReportMessage feedRunReportMessage ->
                feedRunReportMessage.feedRun = feedRun
                feedRun.addToReportMessages(feedRunReportMessage)
            }
        }

        feedRun.s()
        log.info "Processed Entitlement Feed.."
        return feedRun
    }

    private List<FeedRunReportMessage> createEntitlements(List<String> entitlementNames, Origin origin, RoleOwner roleOwner, EntitlementPolicy entitlementPolicy, Boolean isPreviewMode) {
        List<FeedRunReportMessage> reportMessages = []

        Integer entitlementsCreatedCount = 0
        Integer entitlementInCareButNotInTimFeedExceptionsCount = 0
        Integer entitlementInTimFeedButNotInCareExceptionsCount = 0
        Integer recordsProcessedCount = entitlementNames.size()

        List<FeedRunReportMessageDetail> entitlementCreatedDetails = []
        List<FeedRunReportMessageDetail> entitlementInCareButNotInTimFeedExceptionsDetails = []
        List<FeedRunReportMessageDetail> entitlementInTimFeedButNotInCareExceptionsDetails = []
        Entitlement entitlement

        entitlementNames.each { String entitlementName ->
            if (!Entitlement.countByName(entitlementName)) {
                if (!isPreviewMode) {
                    entitlement = entitlementService.createEntitlement(entitlementName, origin, roleOwner, entitlementPolicy)
                    FeedRunReportMessageDetail entitlementCreatedDetail = new FeedRunReportMessageDetail(entityType: "Entitlement", param1: entitlement.toString(), entityId: entitlement.id)
                    entitlementCreatedDetails.add(entitlementCreatedDetail)
                    entitlementsCreatedCount++
                }
                FeedRunReportMessageDetail entitlementInTimFeedButNotInCareExceptionsDetail = new FeedRunReportMessageDetail(entityType: "Entitlement", param1: entitlementName, entityId: entitlementName)
                entitlementInTimFeedButNotInCareExceptionsDetails.add(entitlementInTimFeedButNotInCareExceptionsDetail)
                entitlementInTimFeedButNotInCareExceptionsCount++
            }
        }

        List<Entitlement> entitlementsInCareButNotInFeed = Entitlement.findAllByOriginAndNameNotInList(origin, entitlementNames)
        if (entitlementsInCareButNotInFeed) {
            entitlementsInCareButNotInFeed.each { Entitlement entitlementInCareButNotInFeed ->
                FeedRunReportMessageDetail exceptionDetail = new FeedRunReportMessageDetail(entityType: "Entitlement", param1: entitlementInCareButNotInFeed.toString(), entityId: entitlementInCareButNotInFeed.id)
                entitlementInCareButNotInTimFeedExceptionsDetails.add(exceptionDetail)
                entitlementInCareButNotInTimFeedExceptionsCount++
            }
        }

        FeedRunReportMessage reportMessage = new FeedRunReportMessage()
        reportMessage.type = FeedReportMessageType.INFO
        reportMessage.operation = FeedOperation.PROCESS
        reportMessage.numberOfRecords = recordsProcessedCount
        reportMessages.add(reportMessage)

        reportMessage = new FeedRunReportMessage()
        reportMessage.type = FeedReportMessageType.INFO
        reportMessage.operation = FeedOperation.CREATE
        reportMessage.numberOfRecords = entitlementsCreatedCount
        entitlementCreatedDetails.each { FeedRunReportMessageDetail messageDetail ->
            messageDetail.feedRunReportMessage = reportMessage
            reportMessage.addToDetails(messageDetail)
        }
        reportMessages.add(reportMessage)

        reportMessage = new FeedRunReportMessage()
        reportMessage.type = FeedReportMessageType.EXCEPTION
        reportMessage.message = "Record in CARE but not in TIM role Feed"
        reportMessage.numberOfRecords = entitlementInCareButNotInTimFeedExceptionsCount
        entitlementInCareButNotInTimFeedExceptionsDetails.each { FeedRunReportMessageDetail messageDetail ->
            messageDetail.feedRunReportMessage = reportMessage
            reportMessage.addToDetails(messageDetail)
        }
        reportMessages.add(reportMessage)

        reportMessage = new FeedRunReportMessage()
        reportMessage.type = FeedReportMessageType.EXCEPTION
        reportMessage.message = "Entitlement not found for TIM role in the feed."
        reportMessage.numberOfRecords = entitlementInTimFeedButNotInCareExceptionsCount
        entitlementInTimFeedButNotInCareExceptionsDetails.each { FeedRunReportMessageDetail messageDetail ->
            messageDetail.feedRunReportMessage = reportMessage
            reportMessage.addToDetails(messageDetail)
        }
        reportMessages.add(reportMessage)

        return reportMessages
    }
}
