coverage {
    exclusions = ['**/plugins/**', '**/functionaltestplugin/**', '**/org/grails/**', '**/org/*/grails/**', '**/*Filters*/**', '**/*TagLib*/**', '**/*Config*/**', '**/*Searchable*/**']
}
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.plugin.location.'care-commons' = "../custom-plugins/care-commons"
grails.plugin.location.'drools-gorm' = "../custom-plugins/drools-gorm"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        //mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }

    dependencies {
        compile('org.codehaus.groovy.modules.http-builder:http-builder:0.5.0-RC2') {
            excludes 'groovy', 'xml-apis', 'xercesImpl', 'xmlParserAPIs'
        }

        test 'org.seleniumhq.selenium:selenium-firefox-driver:2.0a7'
        test 'org.seleniumhq.selenium:selenium-chrome-driver:2.0a7'
        test 'org.seleniumhq.selenium:selenium-ie-driver:2.0a7'
        test('org.seleniumhq.selenium:selenium-htmlunit-driver:2.0a7') {
            exclude 'xml-apis'
        }
        test "org.codehaus.geb:geb-spock:0.6.0"
    }

    plugins {
        test ":spock:0.5-groovy-1.7"
        test ":geb:0.6.0"
    }

}
