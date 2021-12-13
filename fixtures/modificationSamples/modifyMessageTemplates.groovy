import com.force5solutions.care.aps.ApsMessageTemplate
import com.force5solutions.care.cc.TransportType
import com.force5solutions.care.common.MessageTemplate

pre {
    MessageTemplate templateToBeModified = MessageTemplate.findByName("Terminate for cause request template")
    templateToBeModified.with {
        name = "Terminate for cause request template"
        subjectTemplate = 'Personnel' + "###PersonnelNameByGroup###" + ''' needs to be terminated for Cause'''
        bodyTemplate = '''
This alert notifies you that ''' + "###PersonnelNameByGroup###" + ''' needs to be terminated for a cause. Your <u>immediate</u> action is required in order to remove access to all Critical Assets. In order to meet compliance, access removal must be completed within 24 hours.
<br/><br/>Please ensure that access to the following has been removed:<br/>
###PersonnelEntitlementRoleName###
To view further details on this alert, please click the link below:<br/>
<a href="###MoreInformationLink###">###MoreInformationLink###</a><br/>
'''
        transportType = TransportType.EMAIL
    }
}
