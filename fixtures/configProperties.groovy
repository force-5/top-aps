import com.force5solutions.care.cp.ConfigProperty
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder

pre {
    if (!ConfigProperty.count()) {
        def configMap = new ConfigSlurper().parse(new File(ApplicationHolder.application.parentContext.servletContext.getRealPath(ConfigurationHolder.config.cc.configFilePath)).toURL())
        configMap.each { key, value ->
            if (value instanceof String) {
                new ConfigProperty(name: key, value: value).s()
            } else {
                createConfigProperty(key, value)
            }
        }
    }
}

fixture {
}

post {
    ConfigProperty.list()*.updateConfigPropertyMap()
}

public void createConfigProperty(String prefix, Map configMap) {
    configMap.each { key, value ->
        key = prefix + "." + key
        if (value instanceof String) {
            new ConfigProperty(name: key, value: value).s()
        } else {
            createConfigProperty(key, value)
        }
    }
}


