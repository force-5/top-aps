package com.force5solutions.care

import com.force5solutions.care.common.CustomTag

class PatchService {

    boolean transactional = true

    def applyPatch() {
        CustomTag revocationTable = CustomTag.findByName('Revocation Table')
        revocationTable.with {
            displayValue = '###RevocationTable###'
            value = """
            <%
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
%>
            """
            dummyData = "Preview Revocation Table"
        }
        revocationTable.s()
    }
}
