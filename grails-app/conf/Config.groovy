import grails.util.Environment

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

grails.config.locations = [
        "file:${appName}-config.groovy",
        "classpath:${appName}-config.groovy"
]
farAheadCronExpression = '0 0 0 1 1 ? 2050'

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
        xml: ['text/xml', 'application/xml'],
        text: 'text/plain',
        js: 'text/javascript',
        rss: 'application/rss+xml',
        atom: 'application/atom+xml',
        css: 'text/css',
        csv: 'text/csv',
        all: '*/*',
        json: ['application/json', 'text/json'],
        form: 'application/x-www-form-urlencoded',
        multipartForm: 'multipart/form-data'
]
// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = ''

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable fo AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

grails.gorm.default.constraints = {
    '*'(blank: false)
}
homepageController = 'apsWorkflowTask'
homepageAction = 'list'
feed {
    ppFeed {
        url = 'jdbc:mysql://localhost:3306/caresap?user=root&password=igdefault'
        driver = 'com.mysql.jdbc.Driver'
        entitlementAccessQuery = 'select PERSONNEL_NUMBER, CATEGORY from SECURITY_INFO'
        entitlementAccessForWorkerQuery = 'select distinct CATEGORY from SECURITY_INFO where PERSONNEL_NUMBER=:PERSONNEL_NUMBER or BADGE=:BADGE_NUMBER;'
        entitlementQuery = 'select CATEGORY from PS_READAREACATS'
    }
    categoryAreaReaderFileFeed {
        fileName = 'CATEGORY_AREA_READER.csv'
    }
    categoryWorkerFileFeed {
        fileName = 'CATEGORY_WORKER.csv'
    }
    timEntitlementWorkerFileFeed {
        fileName = 'TIM_ENTITLEMENT_WORKER.csv'
    }
}
// set per-environment serverURL stem for creating absolute links
environments {
    production {
        grails.serverURL = "http://www.changeme.com"
    }
    development {
        top.db.url = "jdbc:mysql://localhost:3306/care"
        top.db.username = "root"
        top.db.password = "igdefault"
        top.db.driverClassName = 'com.mysql.jdbc.Driver'
        top.db.dialect = org.hibernate.dialect.MySQL5InnoDBDialect
        grails.serverURL = "http://localhost:8080/${appName}"
        cc.configFilePath = "/config/configProperties-development.groovy"
        careCentral {
            database {
                url = "jdbc:mysql://localhost:3306/care"
            }
            webService {
                url = "http://localhost:8080/care/services/careCentral"
                username = "admin"
                password = "admin"
            }
        }
        remoteControl.enabled = true      // Comment if you do not want to enable the remote-control plugin in dev too. By default, it is enabled only in test env.
    }
    demo {
        top.db.url = "jdbc:mysql://localhost:3306/care"
        top.db.username = "root"
        top.db.password = "igdefault"
        top.db.driverClassName = 'com.mysql.jdbc.Driver'
        top.db.dialect = org.hibernate.dialect.MySQL5InnoDBDialect
        grails.serverURL = "http://localhost:8080/aps"
        careCentral {
            database {
                url = "jdbc:mysql://localhost:3306/care"
            }
            webService {
                url = "http://localhost:8080/care/services/careCentral"
                username = "admin"
                password = "admin"
            }
        }
    }
    test {
        top.db.url = "jdbc:mysql://localhost:3306/care"
        top.db.username = "root"
        top.db.password = "igdefault"
        top.db.driverClassName = 'com.mysql.jdbc.Driver'
        top.db.dialect = org.hibernate.dialect.MySQL5InnoDBDialect
        grails.serverURL = "http://test.aps.force5solutions.com"
        cc.configFilePath = "/config/configProperties-test.groovy"
        careCentral {
            database {
                url = "jdbc:mysql://localhost:3306/care"
            }
            webService {
                url = "http://localhost:8080/care/services/careCentral"
                username = "admin"
                password = "admin"
            }
        }
    }
    qa {
        grails.serverURL = "http://qa.aps.force5solutions.com"
        cc.configFilePath = "/config/configProperties-qa.groovy"
        careCentral {
            database {
                url = "jdbc:mysql://qa.care.force5solutions.com:3306/careqa"
            }
        }
        remoteControl.enabled = true
    }
}
// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    appenders {
        environments {
            production {
                rollingFile name: "myAppender", maxFileSize: 26214400, maxBackupIndex: 10, file: "/var/log/tomcat6/aps.log"
                rollingFile name: "stacktrace", maxFileSize: 26214400, file: "/var/log/tomcat6/stacktrace_aps.log"
            }
            demo {
                rollingFile name: "myAppender", maxFileSize: 26214400, maxBackupIndex: 10, file: "/var/log/tomcat6/aps.log"
                rollingFile name: "stacktrace", maxFileSize: 26214400, file: "/var/log/tomcat6/stacktrace_aps.log"
            }
        }
    }
    error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
            'org.codehaus.groovy.grails.web.pages', //  GSP
            'org.codehaus.groovy.grails.web.sitemesh', //  layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping', // URL mapping
            'org.codehaus.groovy.grails.commons', // core / classloading
            'org.codehaus.groovy.grails.plugins', // plugins
            'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
            'org.springframework',
            'org.hibernate',
            'net.sf.ehcache.hibernate'

    warn 'org.mortbay.log'

    debug 'grails.app.controller',
            'grails.app.service.com.force5solutions',
            'grails.app.task.com.force5solutions',
            'org.codehaus.groovy.grails.plugins.com.force5solutions.care',
            'grails.app.filters'

}
