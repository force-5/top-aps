<%@ page import="com.force5solutions.care.cc.Employee; com.force5solutions.care.common.CareConstants" %>

<g:if test="${task.provisionerOrDeprovisionerTask}">
    <care:showSharedAccountsAndProvisionedWorkers tasks="${tasks}"/>
</g:if>

<div style="margin:0 50px;font-size:14px; padding-top: 20px;">
    <h3>Comments</h3><br/>
    <g:form action="sendGroupResponse" method="post" enctype="multipart/form-data">
        <g:render template="groupResponseTable" model="[tasks: tasks]"/>
        <div style="font-size:14px;">
            <g:if test="${task.entitlementId}">
                <g:each in="${tasks.unique { it.workerEntitlementRole }}" var="workflowTask">
                    <br/><br/>
                    <span style="float:left;">Approver for Role ${workflowTask?.workerEntitlementRole}:</span>
                    <span style="padding-left: 10px;">
                        <b>
                            ${workflowTask?.getAccessOrRevokeRequestApprover()?.firstMiddleLastName ?: com.force5solutions.care.common.CareConstants.CENTRAL_SYSTEM} ${workflowTask?.getAccessOrRevokeRequestApprover()?.slid ? "(" + workflowTask?.getAccessOrRevokeRequestApprover()?.slid + ")" : ''}
                        </b>
                    </span>
                </g:each>
                <br/>
                <br/>
                <g:each in="${(tasks*.worker).unique()}" var="worker">
                    <g:if test="${worker instanceof Employee}">
                        <g:set var="slidOrId" value="${worker?.slid}"/>
                    </g:if>
                    <g:else>
                        <g:set var="slidOrId" value="${worker?.workerNumber}"/>
                    </g:else>
                    <span style="float:left;">Workflow details:
                        <b><g:link controller="util" action="workflowReportBySlidOrId" id="${slidOrId}" target="_blank">
                            <span style="font-size: 14px; padding-left: 10px;">Workflow Report for ${worker}</span>
                        </g:link></b>
                    </span>
                    <br/>
                    <br/>
                </g:each>
                <div style="float:left;">
                    Enter the date and time of Action: &nbsp; <input type="text" value="" id="actionDate"
                                                                     name="actionDate"/>

                </div>
            </g:if>
            <g:else>
                <span style="float:left;">Action for access on roles.
                </span>
            </g:else>
        </div>
        <br/>
        <br/>
        <br/>

        <div style="font-size:14px;">
            <g:if test="${task.actions?.size() > 1 && !(task?.hasAnyProvisionerConfirmedInTheTaskList(tasks))}">
                Select Action <span
                    class="asterisk">*</span>
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
            <input class="button" type="submit" value="Submit"/>
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
            if (jQuery(':checkbox:checked').length < 1) {
                alert('Please select any one check-box');
                return false;
            } else {
                return isStatusChangeCommentEmpty('accessJustification', 'explanation-error')
            }
        });
        if (jQuery("#tablesorter tr").size() > 1) {
            jQuery("#tablesorter").tablesorter({textExtraction: myTextExtraction, sortList: [
                [1, 1]
            ], headers: {
                6: {
                    sorter: false
                }}});
        }
    });
</script>