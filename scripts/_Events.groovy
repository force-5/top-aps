import java.lang.Process

eventCreateWarStart = {warName, File stagingDir ->
	Process process = "git log -1".execute()
	File file = new File("${stagingDir}/versionInfo.txt")
	file.text = process.getText()
	println "Adding current revision number information in web-app/versionInfo.txt"
}
