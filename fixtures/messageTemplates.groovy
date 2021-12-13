import com.force5solutions.care.cc.TransportType
import com.force5solutions.care.aps.ApsMessageTemplate
import com.force5solutions.care.common.CareConstants
import com.force5solutions.care.common.SessionUtils

pre {

    ApsMessageTemplate messageTemplate;
    boolean overrideOrCreate;

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ACCESS_REQUEST)))
    if (overrideOrCreate) {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ACCESS_REQUEST) ?: new ApsMessageTemplate();
        messageTemplate.with {
            name = CareConstants.APS_MESSAGE_TEMPLATE_ACCESS_REQUEST
            subjectTemplate = 'Personnel ' + "###PersonnelNameByGroup###" + ''' requested access for entitlementRole ''' + "###PersonnelEntitlementRoleName###"
            bodyTemplate = '''
This alert notifies you that ''' + "###PersonnelNameByGroup###" + ''' is requesting access to a Critical Asset.  Please ensure that the person listed above has met all requirements prior to approving the request for access to:
<br/>###PersonnelEntitlementRoleName###
<br/><br/>
The following certifications must be current before access will be provisioned.<br />

<strong>Required Certifications</strong>

###RequiredCertificationsStatus### <br /><br />

<br/><br/>To view further details on this alert, please click the link below:<br/>
<a href="###MoreInformationLink###">###MoreInformationLink###</a><br/>

'''
            transportType = TransportType.EMAIL
        }
        messageTemplate.save();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ACCESS_REQUEST_APPROVED)))
    if (overrideOrCreate) {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ACCESS_REQUEST_APPROVED) ?: new ApsMessageTemplate();
        messageTemplate.with {
            name = CareConstants.APS_MESSAGE_TEMPLATE_ACCESS_REQUEST_APPROVED
            subjectTemplate = 'ACTION REQUIRED - Access Provisioning for ###PersonnelNameByGroup###'
            bodyTemplate = '''
     A provisioning task for ###PersonnelNameByGroup### has been added to your inbox. Please click on the link below to review the task details.<br/><br/>
<a href="###MoreInformationLink###">###MoreInformationLink###</a><br/><br/>

Please note that this is a system generated message and replies will not be accepted.<br/><br/>

"This document contains non-public transmission information and must be treated in accordance with the FERC Standards of Conduct."
        '''
            transportType = TransportType.EMAIL
        }
        messageTemplate.save();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_24H_REVOKE_REQUEST_NOTIFICATION)))
    if (overrideOrCreate) {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_24H_REVOKE_REQUEST_NOTIFICATION) ?: new ApsMessageTemplate();
        messageTemplate.with {
            name = CareConstants.APS_MESSAGE_TEMPLATE_24H_REVOKE_REQUEST_NOTIFICATION
            subjectTemplate = 'ACTION REQUIRED - CCA access revocation request for ###PersonnelFirstMiddleLastNameByGroup### ***ESCALATION***'
            bodyTemplate = '''
        <span style="color: #c41108;"> ***This email has been forwarded to you because revocation tasks have not been completed for ###PersonnelFirstMiddleLastNameByGroup###.  Please contact callout coordinator using the following link to identify who needs to complete this request as failure to complete this revocation request in a timely manner will result in a CIP violation:  https://lfosa01/cgi-bin/callout/callout****</span><br/><br/>
This alert notifies you that a revocation request has been initiated for ''' + "###PersonnelFirstMiddleLastNameByGroup###'s" + ''' access to ###PersonnelEntitlementRoleName### role(s). Your immediate action is required to remove access to all Critical Cyber Assets (CCAs).
In order to meet compliance, access removal must be completed before ###EndDateAndTime24Hours###.

        <br/><br/>Please ensure that access to the following has been removed:
        ###PersonnelEntitlementRoleName###
        <br/><br/>
        Thank you for your prompt attention to this matter.<br/><br/>
        ###RevocationTable###
        <br/>
        SCC Security Administrators<br/><br/>
        This document contains non-public transmission information and must be treated in accordance with the FERC Standards of Conduct.<br/><br/>
        This message may contain confidential and/or privileged information of Florida Power & Light Company.  If you are not the intended recipient please 1) do not disclose, copy, distribute or use this information, 2) advise the sender by return email, and 3) delete all copies from your computer.  Your cooperation is greatly appreciated.
        '''
            transportType = TransportType.EMAIL
        }
        messageTemplate.save();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_24H_REVOKE_REQUEST_ESCALATION)))
    if (overrideOrCreate) {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_24H_REVOKE_REQUEST_ESCALATION) ?: new ApsMessageTemplate();
        messageTemplate.with {
            name = CareConstants.APS_MESSAGE_TEMPLATE_24H_REVOKE_REQUEST_ESCALATION
            subjectTemplate = 'ACTION REQUIRED - CCA access revocation request for ###PersonnelFirstMiddleLastNameByGroup### ***ESCALATION***'
            bodyTemplate = '''
        <span style="color: #c41108;"> ***This email has been forwarded to you because revocation tasks have not been completed for ###PersonnelFirstMiddleLastNameByGroup###****</span><br/><br/>
This alert notifies you that a revocation request has been initiated for ''' + "###PersonnelFirstMiddleLastNameByGroup###'s" + ''' access to ###PersonnelEntitlementRoleName### role(s). Your immediate action is required to remove access to all Critical Cyber Assets (CCAs).
In order to meet compliance, access removal must be completed before ###EndDateAndTime24Hours###.

        <br/><br/>Please ensure that access to the following has been removed:
        ###PersonnelEntitlementRoleName### : ###EntitlementName###
        <br/><br/>To view further details on this alert, please click the link below:<br/>
        <a href="###MoreInformationLink###">###MoreInformationLink###</a><br/><br/>
        Thank you for your prompt attention to this matter.<br/><br/>
        ###RevocationTable###
        <br/>
        SCC Security Administrators<br/><br/>
        This document contains non-public transmission information and must be treated in accordance with the FERC Standards of Conduct.<br/><br/>
        This message may contain confidential and/or privileged information of Florida Power & Light Company.  If you are not the intended recipient please 1) do not disclose, copy, distribute or use this information, 2) advise the sender by return email, and 3) delete all copies from your computer.  Your cooperation is greatly appreciated.
        '''
            transportType = TransportType.EMAIL
        }
        messageTemplate.save();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_24H_REVOKE_REQUEST)))
    if (overrideOrCreate) {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_24H_REVOKE_REQUEST) ?: new ApsMessageTemplate();
        messageTemplate.with {
            name = CareConstants.APS_MESSAGE_TEMPLATE_24H_REVOKE_REQUEST
            subjectTemplate = 'ACTION REQUIRED - CCA access revocation request for ###PersonnelFirstMiddleLastNameByGroup###'
            bodyTemplate = '''This alert notifies you that a revocation request has been initiated for ''' + "###PersonnelFirstMiddleLastNameByGroup###'s" + ''' access to ###PersonnelEntitlementRoleName### role(s).
        Your immediate action is required to remove access to all Critical Cyber Assets (CCAs).
        In order to meet compliance, access removal must be completed before ###EndDateAndTime24Hours###.

        <br/><br/>Please ensure that access to the following has been removed:
        ###PersonnelEntitlementRoleName###
        <br/><br/>To view further details on this alert, please click the link below:<br/>
        <a href="###MoreInformationLink###">###MoreInformationLink###</a><br/><br/>
        Thank you for your prompt attention to this matter.<br/><br/>
        ###RevocationTable###
        <br/>
        SCC Security Administrators<br/><br/>
        This document contains non-public transmission information and must be treated in accordance with the FERC Standards of Conduct.<br/><br/>
        This message may contain confidential and/or privileged information of Florida Power & Light Company.  If you are not the intended recipient please 1) do not disclose, copy, distribute or use this information, 2) advise the sender by return email, and 3) delete all copies from your computer.  Your cooperation is greatly appreciated.
        '''
            transportType = TransportType.EMAIL
        }
        messageTemplate.save();
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_REVOKE_REQUEST_APPROVAL_GATEKEEPER)))
    if (overrideOrCreate) {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_REVOKE_REQUEST_APPROVAL_GATEKEEPER) ?: new ApsMessageTemplate();
        messageTemplate.with {
            name = CareConstants.APS_MESSAGE_TEMPLATE_REVOKE_REQUEST_APPROVAL_GATEKEEPER
            subjectTemplate = 'ACTION REQUIRED - CCA access revocation request for ###PersonnelFirstMiddleLastNameByGroup###'
            bodyTemplate = '''This alert notifies you that a revocation request has been initiated for ''' + "###PersonnelFirstMiddleLastNameByGroup###'s" + ''' access to ###PersonnelEntitlementRoleName### role(s).
        Your immediate action is required to remove access to all Critical Cyber Assets (CCAs).

        <br/><br/>To view further details on this alert, please click the link below:<br/>
        <a href="###MoreInformationLink###">###MoreInformationLink###</a><br/><br/>
        Thank you for your prompt attention to this matter.<br/><br/>
        ###RevocationTable###
        <br/>
        SCC Security Administrators<br/><br/>
        This document contains non-public transmission information and must be treated in accordance with the FERC Standards of Conduct.<br/><br/>
        This message may contain confidential and/or privileged information of Florida Power & Light Company.  If you are not the intended recipient please 1) do not disclose, copy, distribute or use this information, 2) advise the sender by return email, and 3) delete all copies from your computer.  Your cooperation is greatly appreciated.
        '''
            transportType = TransportType.EMAIL
        }
        messageTemplate.save();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_7D_REVOKE_REQUEST_NOTIFICATION)))
    if (overrideOrCreate) {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_7D_REVOKE_REQUEST_NOTIFICATION) ?: new ApsMessageTemplate();
        messageTemplate.with {
            name = CareConstants.APS_MESSAGE_TEMPLATE_7D_REVOKE_REQUEST_NOTIFICATION
            subjectTemplate = 'ACTION REQUIRED - CCA access revocation request for ###PersonnelFirstMiddleLastNameByGroup### ***ESCALATION***'
            bodyTemplate = '''
        <span style="color: #c41108;"> ***This email has been forwarded to you because revocation tasks have not been completed for ###PersonnelFirstMiddleLastNameByGroup###.  Please contact callout coordinator using the following link to identify who needs to complete this request as failure to complete this revocation request in a timely manner will result in a CIP violation: <a href="https://lfosa01/cgi-bin/callout/callout">https://lfosa01/cgi-bin/callout/callout</a>****</span><br/><br/>
This alert notifies you that a revocation request has been initiated for ''' + "###PersonnelFirstMiddleLastNameByGroup###'s" + ''' access to ###PersonnelEntitlementRoleName### role(s). Your immediate action is required to remove access to all Critical Cyber Assets (CCAs).
In order to meet compliance, access removal must be completed before ###EndDateAndTime7Days###.

        <br/><br/>Please ensure that access to the following has been removed:
        ###PersonnelEntitlementRoleName###
        <br/><br/>
        Thank you for your prompt attention to this matter.<br/><br/>
        ###RevocationTable###
        <br/>
        SCC Security Administrators<br/><br/>
        This document contains non-public transmission information and must be treated in accordance with the FERC Standards of Conduct.<br/><br/>
        This message may contain confidential and/or privileged information of Florida Power & Light Company.  If you are not the intended recipient please 1) do not disclose, copy, distribute or use this information, 2) advise the sender by return email, and 3) delete all copies from your computer.  Your cooperation is greatly appreciated.
        '''
            transportType = TransportType.EMAIL
        }
        messageTemplate.save();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_7D_REVOKE_REQUEST_ESCALATION)))
    if (overrideOrCreate) {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_7D_REVOKE_REQUEST_ESCALATION) ?: new ApsMessageTemplate();
        messageTemplate.with {
            name = CareConstants.APS_MESSAGE_TEMPLATE_7D_REVOKE_REQUEST_ESCALATION
            subjectTemplate = 'ACTION REQUIRED - CCA access revocation request for ###PersonnelFirstMiddleLastNameByGroup### ***ESCALATION***'
            bodyTemplate = '''
        <span style="color: #c41108;"> ***This email has been forwarded to you because revocation tasks have not been completed for ###PersonnelFirstMiddleLastNameByGroup###****</span><br/><br/>
This alert notifies you that a revocation request has been initiated for ''' + "###PersonnelFirstMiddleLastNameByGroup###'s" + ''' access to ###PersonnelEntitlementRoleName### role(s). Your immediate action is required to remove access to all Critical Cyber Assets (CCAs).
In order to meet compliance, access removal must be completed before ###EndDateAndTime7Days###.

        <br/><br/>Please ensure that access to the following has been removed:
        ###PersonnelEntitlementRoleName### : ###EntitlementName###
        <br/><br/>To view further details on this alert, please click the link below:<br/>
        <a href="###MoreInformationLink###">###MoreInformationLink###</a><br/><br/>
        Thank you for your prompt attention to this matter.<br/><br/>
        ###RevocationTable###
        <br/>
        SCC Security Administrators<br/><br/>
        This document contains non-public transmission information and must be treated in accordance with the FERC Standards of Conduct.<br/><br/>
        This message may contain confidential and/or privileged information of Florida Power & Light Company.  If you are not the intended recipient please 1) do not disclose, copy, distribute or use this information, 2) advise the sender by return email, and 3) delete all copies from your computer.  Your cooperation is greatly appreciated.
        '''
            transportType = TransportType.EMAIL
        }
        messageTemplate.save()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_7D_REVOKE_REQUEST)))
    if (overrideOrCreate) {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_7D_REVOKE_REQUEST) ?: new ApsMessageTemplate();
        messageTemplate.with {
            name = CareConstants.APS_MESSAGE_TEMPLATE_7D_REVOKE_REQUEST
            subjectTemplate = 'ACTION REQUIRED - CCA access revocation request for ###PersonnelFirstMiddleLastNameByGroup###'
            bodyTemplate = '''This alert notifies you that a revocation request has been initiated for ''' + "###PersonnelFirstMiddleLastNameByGroup###'s" + ''' access to ###PersonnelEntitlementRoleName### role(s).
        Your immediate action is required to remove access to all Critical Cyber Assets (CCAs).
        In order to meet compliance, access removal must be completed before ###EndDateAndTime7Days###.

        <br/><br/>Please ensure that access to the following has been removed:
        ###PersonnelEntitlementRoleName###
        <br/><br/>To view further details on this alert, please click the link below:<br/>
        <a href="###MoreInformationLink###">###MoreInformationLink###</a><br/><br/>
        Thank you for your prompt attention to this matter.<br/><br/>
        ###RevocationTable###
        <br/>
        SCC Security Administrators<br/><br/>
        This document contains non-public transmission information and must be treated in accordance with the FERC Standards of Conduct.<br/><br/>
        This message may contain confidential and/or privileged information of Florida Power & Light Company.  If you are not the intended recipient please 1) do not disclose, copy, distribute or use this information, 2) advise the sender by return email, and 3) delete all copies from your computer.  Your cooperation is greatly appreciated.
        '''
            transportType = TransportType.EMAIL
        }
        messageTemplate.save()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_TERMINATE_REQUEST)))
    if (overrideOrCreate) {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_TERMINATE_REQUEST) ?: new ApsMessageTemplate();
        messageTemplate.with {
            subjectTemplate = 'Personnel' + " ###PersonnelNameByGroup###'s access termination request from " + "Entitlment Role - ###PersonnelEntitlementRoleName### "
            name = CareConstants.APS_MESSAGE_TEMPLATE_TERMINATE_REQUEST
            bodyTemplate = '''
        This alert notifies you that ''' + "###PersonnelNameByGroup###'s" + ''' access on  Entitlment Role - ###PersonnelEntitlementRoleName### has been requested to be terminated. Your immediate action is required in order to remove access to all Critical Assets.
        In order to meet compliance, access removal must be completed within 24 hours.

        <br/><br/>Please ensure that access to the following has been removed:
        ###PersonnelEntitlementRoleName###
        <br/><br/>To view further details on this alert, please click the link below:<br/>
        <a href="###MoreInformationLink###">###MoreInformationLink###</a><br/>
        '''
            transportType = TransportType.EMAIL
        }
        messageTemplate.save()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ENTITLEMENT_ROLE_CREATION)))
    if (overrideOrCreate) {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ENTITLEMENT_ROLE_CREATION) ?: new ApsMessageTemplate();
        messageTemplate.with {
            name = CareConstants.APS_MESSAGE_TEMPLATE_ENTITLEMENT_ROLE_CREATION
            subjectTemplate = 'Request to approve new Entitlment Role - ' + "###EntitlementRoleName###"
            bodyTemplate = '''
        This alert notifies you that a new Entitlement Role - <strong>''' + "###EntitlementRoleName###" + ''' </strong> has been created.
        <br/><br/>Your approval is required for its usage.
        <br/><br/>Please login to APS to take appropriate action:
        <br/><a href="###MoreInformationLink###">###MoreInformationLink###</a><br/>
        '''
            transportType = TransportType.EMAIL
        }
        messageTemplate.save()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ENTITLEMENT_ROLE_UPDATION)))
    if (overrideOrCreate) {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ENTITLEMENT_ROLE_UPDATION) ?: new ApsMessageTemplate();
        messageTemplate.with {
            name = CareConstants.APS_MESSAGE_TEMPLATE_ENTITLEMENT_ROLE_UPDATION
            subjectTemplate = 'Request to approve changes in Entitlment Role - ' + "###EntitlementRoleName###"
            bodyTemplate = '''
        This alert notifies you that changes have been made in Entitlement Role - <strong>''' + "###EntitlementRoleName###" + ''' </strong>.
        <br/><br/>Your approval is required for their usage.
        <br/><br/>Please login to APS to take appropriate action:
        <br/><a href="###MoreInformationLink###">###MoreInformationLink###</a><br/>
        '''
            transportType = TransportType.EMAIL
        }
        messageTemplate.save()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ENTITLEMENT_CREATION)))
    if (overrideOrCreate) {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ENTITLEMENT_CREATION) ?: new ApsMessageTemplate();
        messageTemplate.with {
            name = CareConstants.APS_MESSAGE_TEMPLATE_ENTITLEMENT_CREATION
            subjectTemplate = 'Request to approve new Entitlement - ' + "###EntitlementName###"
            bodyTemplate = '''
        This alert notifies you that a new Entitlement - <strong>''' + "###EntitlementName###" + ''' </strong> has been created.
        <br/><br/>Your approval is required for its usage.
        <br/><br/>Please login to APS to take appropriate action:
        <br/><a href="###MoreInformationLink###">###MoreInformationLink###</a><br/>
        '''
            transportType = TransportType.EMAIL
        }
        messageTemplate.save()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ACCOUNT_PASSWORD_CHANGE)))
    if (overrideOrCreate) {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ACCOUNT_PASSWORD_CHANGE) ?: new ApsMessageTemplate();
        messageTemplate.with {
            name = CareConstants.APS_MESSAGE_TEMPLATE_ACCOUNT_PASSWORD_CHANGE
            subjectTemplate = 'Request to update the password for Entitlement - ' + "###EntitlementName###"
            bodyTemplate = '''
        This alert notifies you that the password for Entitlement - <strong>''' + "###EntitlementName###" + ''' </strong> needs to be changed.
        <br/><br/>Please approve the task to update the 'Last Password Change' attribute of the entitlement.
        <br/><br/>Please login to APS to take appropriate action:
        <br/><a href="###MoreInformationLink###">###MoreInformationLink###</a><br/>
        '''
            transportType = TransportType.EMAIL
        }
        messageTemplate.save()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ENTITLEMENT_UPDATION)))
    if (overrideOrCreate) {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ENTITLEMENT_UPDATION) ?: new ApsMessageTemplate();
        messageTemplate.with {
            name = CareConstants.APS_MESSAGE_TEMPLATE_ENTITLEMENT_UPDATION
            subjectTemplate = 'Request to approve new Entitlment - ' + "###EntitlementName###"
            bodyTemplate = '''
        This alert notifies you that changes have been made in Entitlement - <strong>''' + "###EntitlementName###" + ''' </strong>.
        <br/><br/>Your approval is required for its usage.
        <br/><br/>Please login to APS to take appropriate action:
        <br/><a href="###MoreInformationLink###">###MoreInformationLink###</a><br/>
        '''
            transportType = TransportType.EMAIL
        }
        messageTemplate.save()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_GATEKEEPER_REJECTION_NOTIFICATION)))
    if (overrideOrCreate) {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_GATEKEEPER_REJECTION_NOTIFICATION) ?: new ApsMessageTemplate();
        messageTemplate.with {
            name = CareConstants.APS_MESSAGE_TEMPLATE_GATEKEEPER_REJECTION_NOTIFICATION
            subjectTemplate = "Access Request Rejected by ###LastHumanActorName###"
            bodyTemplate = '''
###PersonnelNameByGroup###'s recent  access request has been rejected by ###LastHumanActorName###.<br/><br/>

Access Request<br/><br/>

Role: ###PersonnelEntitlementRoleName### <br/>
Description: ###PersonnelEntitlementRoleDescription###<br/><br/>

Please note that this is a system generated message and replies will not be accepted.<br/><br/>

"This document contains non-public transmission information and must be treated in accordance with the FERC Standards of Conduct."
            '''
            transportType = TransportType.EMAIL
        }
        messageTemplate.save()
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ACCESS_VERIFICATION)))
    if (overrideOrCreate) {
        messageTemplate = ApsMessageTemplate.findByName(CareConstants.APS_MESSAGE_TEMPLATE_ACCESS_VERIFICATION) ?: new ApsMessageTemplate();
        messageTemplate.with {
            name = CareConstants.APS_MESSAGE_TEMPLATE_ACCESS_VERIFICATION
            subjectTemplate = 'ACTION REQUIRED - Critical Cyber Asset continual access verification'
            bodyTemplate = '''
        Below is a list of personnel reporting to ###SupervisorName### that have authorized cyber and/or authorized unescorted physical access to the System Control Center's Critical Cyber Assets (CCAs).
        Please verify these individuals' continued need for access by clicking the <b>Confirm</b> link below. Should any of these employees listed no longer require access, please click the <b>Make Changes</b> link to revoke employees no longer needing access.
        Failure to verify continued need for access will result in escalation to your supervisor, and could result in the revocation of access to these CCAs for the individuals listed below.<br /><br />
        If you have any questions please contact Mina Soto at (305) 442-5376 or Maria Elena Martinez at (305) 442-5767.<br /><br />
        Thank you for your prompt attention to this matter.<br /><br />
        SCC Security Administrators<br /><br />
        This document contains non-public transmission information and must be treated in accordance with the FERC Standards of Conduct.<br /><br />
        This message may contain confidential and/or privileged information of Florida Power & Light Company.  If you are not the intended recipient please 1) do not disclose, copy, distribute or use this information, 2) advise the sender by return email, and 3) delete all copies from your computer.  Your cooperation is greatly appreciated.<br /><br />''' + "###activeWorkersGroupByEntitlementRole###" + '''
        <br /><br />
           '''
            transportType = TransportType.EMAIL
        }
        messageTemplate.save();
    }
}

fixture {
}
