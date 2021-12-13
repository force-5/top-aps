package com.force5solutions.care

import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class RssFeedService {

    boolean transactional = true
    static config = ConfigurationHolder.config

    List<RssFeedsVO> getRssFeeds() {
        List<RssFeedsVO> rssFeedsVOs = []
        if (config.rssFeedUrl) {
            try {
                String url = config.rssFeedUrl
                URL feedUrl = new URL(url)
                List feedEntries = []
                SyndFeedInput input = new SyndFeedInput()
                SyndFeed feed = input.build(new XmlReader(feedUrl))
                feedEntries = feed.getEntries();
                feedEntries.each {
                    rssFeedsVOs << new RssFeedsVO(it.title, it.uri.toString(), it.publishedDate, it.description?.value)
                }
            } catch (Exception e) {
                log.error 'Exception Occurred '
            } finally{
                return rssFeedsVOs
            }
        }
    }
}
