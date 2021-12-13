eventCreateWarStart = { warName, stagingDir ->
    if(grailsSettings.grailsEnv == 'production') {
        println "################### Delete Selenium plugin and tests ##################### "
		ant.delete(dir:"${stagingDir}/selenium", failonerror:true)
		ant.delete(dir:"${stagingDir}/plugins/selenium-0.6", failonerror:true)
		ant.delete(dir:"${stagingDir}/WEB-INF/plugins/selenium-0.6", failonerror:true)
    }
}

