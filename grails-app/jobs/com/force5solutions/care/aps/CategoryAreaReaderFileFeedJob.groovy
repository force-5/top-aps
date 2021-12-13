package com.force5solutions.care.aps

import com.force5solutions.care.cc.FeedJob
import com.force5solutions.care.feed.CategoryAreaReaderFileFeedService
import com.force5solutions.care.feed.PicturePerfectEntitlementFeedService
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class CategoryAreaReaderFileFeedJob extends FeedJob {

    def concurrent = false
    static triggers = {
        cron name: 'categoryAreReaderFileFeedServiceCronTrigger',
                group: 'topJobsGroup',
                cronExpression: ConfigurationHolder.config.farAheadCronExpression
    }

    def execute() {
        log.info "Executing Category Area Reader File Feed Job at ${new Date()}"
        configKeyName = "runDatabaseFeedForCategoryAreaReader"
        super.execute()
    }

    @Override
    void executeFeedFromDbSource() {
        new PicturePerfectEntitlementFeedService().execute()
    }

    @Override
    void executeFeedFromFileSource() {
        new CategoryAreaReaderFileFeedService().execute()
    }
}
