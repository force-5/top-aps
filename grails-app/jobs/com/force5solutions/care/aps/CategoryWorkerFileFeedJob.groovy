package com.force5solutions.care.aps

import com.force5solutions.care.cc.FeedJob
import com.force5solutions.care.feed.CategoryWorkerFileFeedService
import com.force5solutions.care.feed.PicturePerfectEntitlementAccessFeedService
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class CategoryWorkerFileFeedJob extends FeedJob {

    def concurrent = false
    static triggers = {
        cron name: 'categoryWorkerFileFeedServiceCronTrigger',
                group: 'topJobsGroup',
                cronExpression: ConfigurationHolder.config.farAheadCronExpression
    }

    def execute() {
        log.info "Executing Category Worker File Feed at ${new Date()}"
        configKeyName = "runDatabaseFeedForCategoryWorker"
        super.execute()
    }

    @Override
    void executeFeedFromDbSource() {
        new PicturePerfectEntitlementAccessFeedService().execute()
    }

    @Override
    void executeFeedFromFileSource() {
        new CategoryWorkerFileFeedService().execute()
    }
}
