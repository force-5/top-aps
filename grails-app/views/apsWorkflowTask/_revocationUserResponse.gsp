<%@ page import="com.force5solutions.care.cc.Employee; com.force5solutions.care.common.CareConstants; com.force5solutions.care.workflow.ApsWorkflowTask; com.force5solutions.care.workflow.WorkflowTask; com.force5solutions.care.cc.PeriodUnit" %>
<div id="wrapper">
    <h1>Revocation Action Required</h1>
    <br/>
    <%
        int days = (task.periodUnit == PeriodUnit.DAYS) ? 7 : 1
        WorkflowTask initialTask = ApsWorkflowTask.getInitialTask(task.workflowGuid)
    %>
    <g:form action="sendUserResponse" method="post" enctype="multipart/form-data">
        <g:if test="${task.provisionerOrDeprovisionerTask}">
            <care:showSharedAccountsAndProvisionedWorkers tasks="${[task]}" isRevocation="${true}"/>
        </g:if>
        <div style="margin:0 50px;font-size:14px;">

            <span style="font:14px; text-align: justify;">This alert notifies you that a revocation request has been initiated for ${task.worker?.firstMiddleLastName}'s access to ${task.entitlementRole} role(s).
            Your immediate action is required to remove access to all Critical Cyber Assets (CCAs). In order to meet compliance, access removal must
            be completed before ${(task.effectiveStartDate + days)?.format("h:mm a EEEE MMMM dd yyyy")}.</span>
            <br/>
            <br/>

            <div style="font-size:14px;">
                <span style="float:left;">Please confirm completion of revocation action for <b>${task.worker?.firstMiddleLastName}'s</b> access on <b>${task.entitlement}</b> entitlement(s).
                </span>
            </div>
            <br/>
            <g:if test="${task.actions.size() > 1 && !(task?.hasAnyProvisionerConfirmed())}">
                <br/><br/> Select Action <span
                    class="asterisk">*</span>
                <span style="padding-left: 50px;"><g:select class="listbox" style="padding:0;width:150px;"
                                                            name="userAction"
                                                            from="${task.actions}"/></span>
            </g:if>

            <g:if test="${task?.isDeprovisionerTask()}">
                <br/><br/>
                <span style="float:left;">Approver for Role ${task?.workerEntitlementRole}:</span>
                <span style="padding-left: 10px;">
                    <b>
                        ${task?.getAccessOrRevokeRequestApprover()?.firstMiddleLastName ?: com.force5solutions.care.common.CareConstants.CENTRAL_SYSTEM} ${task?.getAccessOrRevokeRequestApprover()?.slid ? "(" + task?.getAccessOrRevokeRequestApprover()?.slid + ")" : ''}
                    </b>
                </span>
                <br/>
                <br/>
                <g:if test="${task?.worker instanceof Employee}">
                    <g:set var="slidOrId" value="${task?.worker?.slid}"/>
                </g:if>
                <g:else>
                    <g:set var="slidOrId" value="${task?.worker?.workerNumber}"/>
                </g:else>
                <span style="float:left;">Workflow details:<b><g:link controller="util"
                                                                      action="workflowReportBySlidOrId"
                                                                      id="${slidOrId}" target="_blank"><span
                            style="font-size: 14px; padding-left: 10px;">Workflow Report  for ${task?.worker}</span></g:link>
                </b>
                </span>
                <br/>
                <br/>

                <div style="float:left;">
                    Enter the date/time of Action: &nbsp;&nbsp;&nbsp; <input type="text" value="" id="actionDate"
                                                                             name="actionDate"/>
                </div>
            </g:if>
            <br/><br/>

            <div>
                <span style="padding-right: 40px;"><b>Business Justification</b> :
                </span> ${initialTask.message} <br/> <br/>
                <span>Please provide an explanation: <span class="asterisk">*</span></span>
                <g:if test="${!task.nodeName.toString().equalsIgnoreCase('Entitlement Revoke Request')}">
                    <care:cannedResponse
                            taskDescription="${CareConstants.CANNED_RESPONSE_APS_REVOKE_REQUEST_GATEKEEPER_JUSTIFICATION}"
                            targetId="accessJustification"/><br>
                </g:if>
                <g:else>
                    <care:cannedResponse
                            taskDescription="${CareConstants.CANNED_RESPONSE_APS_REVOKE_REQUEST_PROVISIONER_JUSTIFICATION}"
                            targetId="accessJustification"/><br>
                </g:else>

                <span style="width:205px; display:inline-block;"></span>
                <g:render template="accessJustification"/>
                <div id="explanation-error" class="error-status" style="text-align:center; display:none;">
                    <span>Access Justification can not be left blank.</span></div>
                <input type="hidden" name="id" value="${task.id}"/>
                <g:render template="/apsWorkflowTask/attachment"/>
            </div>

            <div style="text-align:center;">
                <input class="button" type="submit" value="Submit"/>
            </div>
        </div>
    </g:form>

    <br/><span style="color: red;margin-left:16px; font-size:20px;">Request Details</span>
    <table cellspacing="1" cellpadding="0" border="0" bgcolor="#000000" width="100%">
        <thead>
        <th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff" width="25%">Name</th>
        <th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff"
            width="25%">Supervisor&nbsp;</th>
        <th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff"
            width="20%">Employee Number</th>
        <th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff" width="20%">SLID</th>
        <th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff">&nbsp;Badge &nbsp;</th>
        </thead>

        <tbody>
        <td bgcolor="#ffffff" style="padding:2px 5px;">
            ${task.worker.getFirstName() + ''' ''' + (task.worker.getMiddleName() ?: "") + ''' ''' + task.worker.getLastName()}
        </td>
        <td bgcolor="#ffffff" style="padding:2px 5px;">${task.worker.supervisorName}
        </td>
        <td bgcolor="#ffffff" style="padding:2px 5px;">
            ${(task.worker.workerNumber ?: "&nbsp;")}
        </td>
        <td bgcolor="#ffffff" style="padding:2px 5px;">
            ${task?.worker?.slid}</td>
        <td bgcolor="#ffffff" style="padding:2px 5px;">
            ${(task.worker.badgeNumber ?: "&nbsp;")}
        </td>
        </tbody>
    </table>
    <br/>
    <table cellspacing="1" cellpadding="0" border="0" bgcolor="#000000" width="100%">
        <thead>
        <th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff"
            width="30%">Initiated By</th>
        <th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff"
            width="40%">Request Date/Time &nbsp;</th>
        <th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff">Effective Date/Time</th>
        </thead>
        <tbody>
        <td bgcolor="#ffffff" style="padding:2px 5px;">
            ${initialTask?.actorSlid}
        </td>
        <td bgcolor="#ffffff" style="padding:2px 5px;">
            ${initialTask?.dateCreated?.myDateTimeFormat()}
        </td>
        <td bgcolor="#ffffff" style="padding:2px 5px;">
            ${task.effectiveStartDate.myDateTimeFormat()}
        </td>
        </tbody>
    </table>
    <br/>
    <table cellspacing="1" cellpadding="0" border="0" bgcolor="#000000" width="100%">
        <thead>
        <th align="left" style="color:#000; font-size:12px; padding:2px;" bgcolor="#ffffff">Revoke Justification</th>
        </thead>

        <tbody>
        <td bgcolor="#ffffff" style="padding:2px 5px;">
            ${(initialTask?.message ?: "&nbsp;")}</td>
        </tbody>
    </table>
</div>
<script type="text/javascript">
    jQuery(document).ready(function () {
        jQuery('#actionDate').datetimepicker({
            ampm: true
        });
        jQuery('.cannedSelectBox').focus();
        jQuery('form').submit(function () {
            return isStatusChangeCommentEmpty('accessJustification', 'explanation-error')
        });
    });
</script>