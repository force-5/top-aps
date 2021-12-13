includeTargets << grailsScript("_GrailsClasspath");
ant.path(id: "grails.classpath", runtimeClasspath)
def sourceDir = (config.'jasper.dir.reports' ?: 'reports')
ant.echo "sourceDir = ${sourceDir}"
ant.taskdef(classpathref: "grails.classpath", name: "jrc", classname: "net.sf.jasperreports.ant.JRAntCompileTask")
ant.mkdir(dir: "~/.grails/.jasper")
ant.jrc(srcdir: "${basedir}/web-app/${sourceDir}", tempdir: "~/.grails/.jasper") {
    include(name: "**/*SubReport.jrxml")
}
target(default: 'Dummy Block') {

}
