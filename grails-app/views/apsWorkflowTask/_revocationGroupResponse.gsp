<%@ page import="com.force5solutions.care.cc.Employee; com.force5solutions.care.common.CareConstants; com.force5solutions.care.workflow.ApsWorkflowTask; com.force5solutions.care.workflow.WorkflowTask; com.force5solutions.care.cc.PeriodUnit" %>
<div id="wrapper">
    <h1>Revocation Action Required</h1>
    <br/>
    <g:form action="sendGroupResponse" method="post" enctype="multipart/form-data">

        <g:if test="${task.provisionerOrDeprovisionerTask}">
            <care:showSharedAccountsAndProvisionedWorkers tasks="${tasks}" isRevocation="${true}"/>
        </g:if>
        <div style="margin:0 50px;font-size:14px;">
            <br/>
            <g:render template="groupResponseTable" model="[tasks: tasks]"/>
            <div style="font-size:14px;">
                <span style="float:left;">Please confirm completion of revocation actions.
                </span>
            </div>
            <g:if test="${task.actions.size() > 1 && !(task?.hasAnyProvisionerConfirmedInTheTaskList(tasks))}">
                <br/><br/> Select Action <span
                    class="asterisk">*</span>
                <span style="padding-left: 50px;"><g:select class="listbox" style="padding:0;width:150px;"
                                                            name="userAction"
                                                            from="${task.actions}"/></span>
            </g:if>

            <g:if test="${task?.isDeprovisionerTask()}">
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
                    Enter the date/time of Action: &nbsp;&nbsp;&nbsp; <input type="text" value="" id="actionDate"
                                                                             name="actionDate"/>
                </div>
            </g:if>
            <br/><br/>

            <div>
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