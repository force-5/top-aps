package com.force5solutions.care

import com.force5solutions.care.cp.ConfigProperty
import com.force5solutions.care.aps.News

class NewsController {

    def index = {

    }

    def save = {
        News news = new News()
        news.properties = params.properties
        if (!news.hasErrors() && news.s()) {
            flash.message = "News Added Successfully"

            render(template: '/news/newsAndNotesLink', model: [news: news])
        }
        else {
            flash.message = "News not saved"
            render 'failure'
        }
    }

    def show = {
        News news = News.get(params.id)
        if (!news) {
            flash.message = "News not found"
            redirect(controller: dashboard, action: list)
        }
        else
            render(template: '/news/showNewsAndNotes', model: [news: news])
    }

    def delete = {
        News news = News.get(params.id)
        if (news) {
            try {
                news.delete(flush: true)
                render(params.id)
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
             redirect(controller:'dashboard',action:'index')
            }
        }
        else{
            redirect(controller:'dashboard',action:'index')
        }

    }
    def editRssFeed = {
        String feedUrl = params.feedUrl
        ConfigProperty rssFeedUrl = ConfigProperty.findByName('rssFeedUrl')
        if (rssFeedUrl) {
            rssFeedUrl.value = feedUrl
            rssFeedUrl.s()
            render(care.rssFeed())
        } else {
            rssFeedUrl = new ConfigProperty(name: 'rssFeedUrl', value: params.feedUrl)
            rssFeedUrl.value = feedUrl
            rssFeedUrl.s()
            render(care.rssFeed())
        }
    }
}



class RssFeedsVO {
    String title
    Date publishDate
    String description
    String uri

    RssFeedsVO() {
    }

    RssFeedsVO(String title, String uri, Date publishedDate, String description) {

        this.title = title
        this.uri = uri
        this.publishDate = publishedDate
        this.description = description
    }
}
