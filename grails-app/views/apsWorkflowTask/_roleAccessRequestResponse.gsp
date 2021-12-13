<%@ page import="com.force5solutions.care.cc.Employee; com.force5solutions.care.common.CareConstants" %>
<g:if test="${task.provisionerOrDeprovisionerTask}">
    <care:showSharedAccountsAndProvisionedWorkers tasks="${[task]}"/>
</g:if>
<div style="margin:0 50px;font-size:14px;padding-top: 20px;">
    <h3>Comments</h3><br/>
    <g:form action="sendUserResponse" method="post" enctype="multipart/form-data">
        <div style="font-size:14px;">
            <g:if test="${task.entitlementId}">
                <span style="float:left;">Action for <b>${task.workerName}'s ${task?.worker?.slid ? "(" + task?.worker?.slid + ")" : ""}</b> access on entitlement <b>${task.entitlement}</b>
                </span>
                <br/>
                <br/>
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
                            style="font-size: 14px; padding-left: 10px;">Workflow Report  for ${task?.worker} ${task?.worker?.slid ? "(" + task?.worker?.slid + ")" : ""}</span></g:link>
                </b>
                </span>
                <br/>
                <br/>

                <div style="float:left;">
                    Enter the date and time of Action: &nbsp; <input type="text" value="" id="actionDate"
                                                                     name="actionDate"/>
                </div>
            </g:if>
            <g:else>
                <span style="float:left;">Action for <b>${task.workerName}'s</b> access on role <b>${task.entitlementRole}</b>
                </span>
            </g:else>
        </div>
        <br/>
        <br/>
        <br/>

        <div style="font-size:14px;">
            <g:if test="${task.actions?.size() > 1 && !(task?.hasAnyProvisionerConfirmed())}">
                Select Action <span class="asterisk">*</span>
                <span style="padding-left: 105px;"><g:select class="listbox" style="padding:0;width:150px;"
                                                             name="userAction"
                                                             from="${task.actions}"/></span>
            </g:if>
        </div>
        <br/>

        <div id="explanation-error" class="error-status" style="text-align:center; display:none;">
            <span>Access Justification can not be left blank.</span></div>

        <div>
            <span>Select Business Justification</span><span
                class="asterisk">*</span> &nbsp; &nbsp;<care:cannedResponse
                taskDescription="${CareConstants.CANNED_RESPONSE_APS_ACCESS_REQUEST_PROVISIONER_JUSTIFICATION}"
                targetId="accessJustification"/><br>
            <span>Please provide an explanation:</span>
            <g:render template="accessJustification"/>
            <input type="hidden" name="id" value="${task.id}"/>
            <g:render template="/apsWorkflowTask/attachment"/>
        </div>

        <div style="text-align:center;">
            <input class="button" type="submit" value="Submit">
        </div>
    </g:form>
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