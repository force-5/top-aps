import com.force5solutions.care.common.CustomTag
import com.force5solutions.care.common.SessionUtils

pre {
    CustomTag customTag
    boolean overrideOrCreate;

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    overrideOrCreate = (SessionUtils.request || (!CustomTag.findByName("Access Request")))
    if (overrideOrCreate) {
        customTag = CustomTag.findByName("Access Request") ?: new CustomTag();
        customTag.with {
            name = "Access Request"
            displayValue = '###AccessRequest###'
            value = '''
<h3>Access Request</h3>
<table border="1" cellpadding="5">
    <tr><th>Role</th><th>Description</th></tr>
    <tr><td>${workerEntitlementRole}</td><td>${workerEntitlementRole?.entitlementRole?.notes}</td></tr>
</table>

<br />'''
            dummyData = '<br />Preview Access Request Table<br />'
        }
        customTag.save()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!CustomTag.findByName("Contractor Name")))
    if (overrideOrCreate) {

        customTag = CustomTag.findByName("Contractor Name") ?: new CustomTag();
        customTag.with {
            name = "Contractor Name"
            displayValue = '###PersonnelName###'
            value = '${worker.name}'
            dummyData = 'Preview Person Name'
        }
        customTag.save()
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!CustomTag.findByName("Contractor Name by Group")))
    if (overrideOrCreate) {
        customTag = CustomTag.findByName("Contractor Name by Group") ?: new CustomTag();
        customTag.with {
            name = "Contractor Name by Group"
            displayValue = '###PersonnelNameByGroup###'
            value = '${workerEntitlementRole.worker.name}'
            dummyData = 'Preview Person Name'
        }
        customTag.save()
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!CustomTag.findByName("Personnel Name by Group in format First Name Middle Name Last Name")))
    if (overrideOrCreate) {
        customTag = CustomTag.findByName("Personnel Name by Group in format First Name Middle Name Last Name") ?: new CustomTag();
        customTag.with {
            name = "Personnel Name by Group in format First Name Middle Name Last Name"
            displayValue = '###PersonnelFirstMiddleLastNameByGroup###'
            value = '''<%
            out << workerEntitlementRole.worker?.firstMiddleLastName
        %>'''
            dummyData = 'Preview Person Name'
        }
        customTag.save()
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    overrideOrCreate = (SessionUtils.request || (!CustomTag.findByName("Entitlement Role")))
    if (overrideOrCreate) {
        customTag = CustomTag.findByName("Entitlement Role") ?: new CustomTag();
        customTag.with {
            name = "Entitlement Role"
            displayValue = '###EntitlementRoleName###'
            value = '${entitlementRole.name}'
            dummyData = 'Preview Entitlement Role Name'
        }
        customTag.save()
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!CustomTag.findByName("Entitlement")))
    if (overrideOrCreate) {

        customTag = CustomTag.findByName("Entitlement") ?: new CustomTag();
        customTag.with {
            name = "Entitlement"
            displayValue = '###EntitlementName###'
            value = '${entitlement.name}'
            dummyData = 'Preview Entitlement Name'
        }
        customTag.save()
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!CustomTag.findByName("Worker Entitlement Role Name")))
    if (overrideOrCreate) {
        customTag = CustomTag.findByName("Worker Entitlement Role Name") ?: new CustomTag();
        customTag.with {
            name = "Worker Entitlement Role Name"
            displayValue = '###PersonnelEntitlementRoleName###'
            value = '${workerEntitlementRole}'
            dummyData = 'Preview Entitlement Role Name'
        }
        customTag.save()
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!CustomTag.findByName("Worker Entitlement Role Description")))
    if (overrideOrCreate) {

        customTag = CustomTag.findByName("Worker Entitlement Role Description") ?: new CustomTag();
        customTag.with {
            name = "Worker Entitlement Role Description"
            displayValue = '###PersonnelEntitlementRoleDescription###'
            value = '${workerEntitlementRole?.entitlementRole?.notes}'
            dummyData = 'Preview Entitlement Role Description'
        }
        customTag.save()
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!CustomTag.findByName("Last Human Actor Name For a Task")))
    if (overrideOrCreate) {

        customTag = CustomTag.findByName("Last Human Actor Name For a Task") ?: new CustomTag();
        customTag.with {
            name = "Last Human Actor Name For a Task"
            displayValue = '###LastHumanActorName###'
            value = '${lastHumanActorName}'
            dummyData = 'Preview Dummy Name'
        }
        customTag.save()
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!CustomTag.findByName("More Information Link")))
    if (overrideOrCreate) {
        customTag = CustomTag.findByName("More Information Link") ?: new CustomTag();
        customTag.with {
            name = "More Information Link"
            displayValue = '###MoreInformationLink###'
            value = '${link}'
            dummyData = 'http://previewdomain/previewurl'
        }
        customTag.save()
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!CustomTag.findByName("End Date And Time for 24 Hours revocation")))
    if (overrideOrCreate) {
        customTag = CustomTag.findByName("End Date And Time for 24 Hours revocation") ?: new CustomTag();
        customTag.with {
            name = "End Date And Time for 24 Hours revocation"
            displayValue = '###EndDateAndTime24Hours###'
            value = '''<%
java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("HH:mm EEEE MMMM dd yyyy");
String dateString = formatter.format((effectiveStartDate + 1));
        out << dateString
%>'''
            dummyData = "${new Date()?.myDateTimeFormat()}"
        }
        customTag.save()
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!CustomTag.findByName("End Date And Time for 7 Days revocation")))
    if (overrideOrCreate) {
        customTag = CustomTag.findByName("End Date And Time for 7 Days revocation") ?: new CustomTag();
        customTag.with {
            name = "End Date And Time for 7 Days revocation"
            displayValue = '###EndDateAndTime7Days###'
            value = '''<%
java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("HH:mm EEEE MMMM dd yyyy");
String dateString = formatter.format((effectiveStartDate + 7));
        out << dateString
%>'''
            dummyData = "${new Date()?.myDateTimeFormat()}"
        }
        customTag.save()
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!CustomTag.findByName("Revocation Table")))
    if (overrideOrCreate) {
        customTag = CustomTag.findByName("Revocation Table") ?: new CustomTag();
        customTag.with {
            name = "Revocation Table"
            displayValue = '###RevocationTable###'
            value = """<%

    out << '''<br/><span style="color: red; font-size:24px;"> Request Details </span>
           <table cellspacing="1" cellpadding="0" border="0" bgcolor="#000000" width="100%">
<thead>
<tr>
<th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff" width="25%">Name</th>
<th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff" width="25%">Supervisor</th>
<th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff" width="20%">Employee Number</th>
<th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff" width="20%">SLID</th>
<th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff">&nbsp;Badge &nbsp;</th>
</tr>
</thead>

<tbody>
<tr>
<td bgcolor="#ffffff" style="padding:2px 5px;"> '''

    out << task.worker.getFirstName() + ''' ''' + (task.worker.getMiddleName() ?: "") + ''' ''' + task.worker.getLastName()

    out << '''</td><td bgcolor="#ffffff" style="padding:2px 5px;"> ''' + task.worker.supervisorName + '''</td><td bgcolor="#ffffff" style="padding:2px 5px;"> '''

    out << (task.worker.workerNumber ?: "&nbsp;") + '''</td><td bgcolor="#ffffff" style="padding:2px 5px;"> '''

    out << task.worker.slid + '''</td><td bgcolor="#ffffff" style="padding:2px 5px;"> '''

    out << (task.worker.badgeNumber ?: "&nbsp;") + '''</td>
</tr>
</tbody>
</table><br/>'''


    out << '''<table cellspacing="1" cellpadding="0" border="0" bgcolor="#000000" width="100%">
<thead>
<tr>
<th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff" width="30%">Initiated By</th>
<th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff" width="40%">Request Date/Time &nbsp;</th>
<th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff">Effective Date/Time</th>
</tr>
</thead>

<tbody>
<tr>
<td bgcolor="#ffffff" style="padding:2px 5px;"> '''
    out << initialTask.actorSlid

    out << '''</td><td bgcolor="#ffffff" style="padding:2px 5px;"> ''' + initialTask.dateCreated.myDateTimeFormat() + '''</td><td bgcolor="#ffffff" style="padding:2px 5px;"> '''

    out << task.effectiveStartDate.myDateTimeFormat() + ''' </td>
</tr>
</tbody>
</table><br/>'''

    out << '''<table cellspacing="1" cellpadding="0" border="0" bgcolor="#000000" width="100%">
<thead>
<tr>
<th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff">Revoke Justification</th>
</tr>
</thead>

<tbody>
<tr>
<td bgcolor="#ffffff" style="padding:2px 5px;"> '''

    out << (initialTask.message ?: "&nbsp;") + '''</td>
</tr>
</tbody>
</table>

'''
%>"""
            dummyData = "Preview Revocation Table"
        }
        customTag.save()
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!CustomTag.findByName("Active Workers By Entitlement Role")))
    if (overrideOrCreate) {
        customTag = CustomTag.findByName("Active Workers By Entitlement Role") ?: new CustomTag();
        customTag.with {
            name = "Active Workers By Entitlement Role"
            displayValue = '###activeWorkersGroupByEntitlementRole###'
            value = """<%
        out << '''
<h1 style="color: #F15A29;font-weight: normal;font-size: 16px;margin: 8px 0px 3px 0px;padding: 0px;">Access Verification</h1>'''

        int index
        workerAsSupervisor.activeWorkersGroupByEntitlementRole.each { roleMap ->
            out << '''<div class="list" style="border: 1px solid #ddd;margin-bottom: 20px;padding: 5px 10px;"><p class="heading-access" style="font-size: 16px;color: #7e7e7e">Entitlement Role : <span style="color:black;">'''
            out << roleMap.key.name
            out << '''</span></p>
                    <table cellpadding="0" cellspacing="0" border="0" class="table-access-verification" style="width: 100%; font-size: 14px;border: none;">
                    <thead>
                        <tr style="border: 0;">
                            <th style="color: #666;line-height: 17px;font: 11px verdana, arial, helvetica, sans-serif;line-height: 12px;padding: 3px 6px;text-align: left;vertical-align: top;font-size: 14px;padding: 6px 6px;background: #fff;border: none;border-bottom: 1px solid #ddd;" width="300">Name</th>
                            <th style="color: #666;line-height: 17px;font: 11px verdana, arial, helvetica, sans-serif;line-height: 12px;padding: 3px 6px;text-align: left;vertical-align: top;font-size: 14px;padding: 6px 6px;background: #fff;border: none;border-bottom: 1px solid #ddd;">SLID</th>
                            <th style="color: #666;line-height: 17px;font: 11px verdana, arial, helvetica, sans-serif;line-height: 12px;padding: 3px 6px;text-align: left;vertical-align: top;font-size: 14px;padding: 6px 6px;background: #fff;border: none;border-bottom: 1px solid #ddd;">Badge</th>
                        </tr>
                    </thead>
                    <tbody>'''
            index = 0
            roleMap.value.each { emp ->
                if ((index % 2) == 0) {
                    out << '''<tr style="background: #fff;border: 0;"><td style="font: 11px verdana, arial, helvetica, sans-serif;line-height: 12px;text-align: left;vertical-align: top; font-size: 14px;padding: 10px 6px;border: none">'''
                } else {
                    out << '''<tr style="background: #CFD0D2;border: 0;"><td style="font: 11px verdana, arial, helvetica, sans-serif;line-height: 12px;text-align: left;vertical-align: top; font-size: 14px;padding: 10px 6px;border: none">'''
                }
                out << emp?.name
                out << '''</td><td>'''
                out << emp?.person?.slid?: ""
                out << '''</td><td>'''
                out << ((emp.badgeNumber)? emp.badgeNumber : "")
                out << '''</td></tr>'''
                index++
            }
            out << '''</tbody></table></div>'''
        }
    %>

     """
            dummyData = 'Preview Data for Active Workers By Entitlement Role'
        }
        customTag.save();
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!CustomTag.findByName("Supervisor Name")))
    if (overrideOrCreate) {
        customTag = CustomTag.findByName("Supervisor Name") ?: new CustomTag();
        customTag.with {
            name = "Supervisor Name"
            displayValue = '###SupervisorName###'
            value = '${workerAsSupervisor.firstMiddleLastName}'
            dummyData = 'Preview Dummy Supervisor Name'
        }
        customTag.save()
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    overrideOrCreate = (SessionUtils.request || (!CustomTag.findByName("Required Certfication Status")))
    if (overrideOrCreate) {
        customTag = CustomTag.findByName("Required Certfication Status") ?: new CustomTag();
        customTag.with {
            name = "Required Certfication Status"
            displayValue = '###RequiredCertificationsStatus###'
            value = """<%
             Set requiredCertfications = (workerEntitlementRole?.entitlementRole?.requiredCertifications + workerEntitlementRole?.entitlementRole?.inheritedCertifications)?.unique()
        out << '''<table bgcolor="#cccccc" cellspacing="1" cellpadding="5" style="width:100%;font-size:12px;border:none">
                    <thead>
                        <tr>
                            <th bgcolor="#ffffff" align="left">Certification</th>
                            <th bgcolor="#ffffff" align="left">Employee Status</th>
                        </tr>
                    </thead>
                    <tbody>'''
        requiredCertfications?.each { certification ->
            out << '''<tr><td bgcolor="#ffffff">'''
            out << certification
            out << '''</td>'''
            out << '''<td bgcolor="#ffffff">'''
            if (certification in workerEntitlementRole.worker.effectiveCertifications*.certification) {
                def workerCertification = workerEntitlementRole.worker.effectiveCertifications.find { it.certification == certification}
                out << workerCertification.currentStatus
            } else {
                out << "PENDING"
            }
            out << '''</td></tr>'''
        }
        out << '''</tbody></table>'''
    %>
"""
            dummyData = 'Preview Data for Rquired Cerification Status'
        }
        customTag.save();
    }
}

fixture {
}
