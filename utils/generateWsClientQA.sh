export GRAILS_HOME=/opt/grails-1.3.5
export PROJECT_HOME=/home/aps/CARE/aps
export WS_WSDL_URL=http://qa.care.force5solutions.com/services/careCentral?wsdl
export PACKAGE=com.force5solutions.care.web
rm -rf $PROJECT_HOME/src/java/com/force5solutions/care/web/*.java
java -cp $GRAILS_HOME/lib/*:$GRAILS_HOME/dist/*:$PROJECT_HOME/lib/* -Xmx128M org.apache.axis.wsdl.WSDL2Java -p$PACKAGE -T1.1 -o$PROJECT_HOME/src/java $WS_WSDL_URL
sed -i 's/"http:\/\/qa.care.force5solutions.com\/services\/careCentral"/com.force5solutions.care.UtilService.getCareCentralUrl()/g' $PROJECT_HOME/src/java/com/force5solutions/care/web/CareCentral_ServiceLocator.java

