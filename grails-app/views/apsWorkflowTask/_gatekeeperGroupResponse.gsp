<%@ page import="com.force5solutions.care.common.CareConstants; com.force5solutions.care.workflow.ApsWorkflowType" %>
<div id="wrapper">
    <h1>TOP Workflow Gatekeeper Request</h1>
    <br/>

    <div style="margin:0 50px;font-size:14px;">
        <div>
            <span>
                Hello <care:fullName slid="${session.loggedUser}"/>,
                <br/>
                <br/>
                <g:if test="${task.workflowType in [ApsWorkflowType.ROLE_ACCESS_REQUEST, ApsWorkflowType.ROLE_ACCESS_REQUEST_FOR_CONTRACTOR, ApsWorkflowType.CANCEL_ACCESS_REVOCATION]}">
                    <g:set var="taskDescription"
                           value="${CareConstants.CANNED_RESPONSE_APS_ACCESS_REQUEST_GATEKEEPER_JUSTIFICATION}"/>
                </g:if>
                <g:elseif
                        test="${task.workflowType in [ApsWorkflowType.ROLE_REVOKE_REQUEST, ApsWorkflowType.CANCEL_ACCESS_APPROVAL]}">
                    <g:set var="taskDescription"
                           value="${CareConstants.CANNED_RESPONSE_APS_REVOKE_REQUEST_GATEKEEPER_JUSTIFICATION}"/>
                </g:elseif>
                <br/>
                <br/>
            </span>
        </div>
        <g:form action="sendGroupResponse" method="post" enctype="multipart/form-data">
            <g:render template="groupResponseTable" model="[tasks: tasks]"/>
            <div style="font-size:14px;">
                <g:if test="${task.actions}">
                    Select Action <span
                        class="asterisk">*</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <span><g:select class="listbox" style="padding:0;width:150px;" name="userAction"
                                    from="${task.actions}"/></span>
                </g:if>
            </div>
            <br/><br/>

            <div id="explanation-error" class="error-status" style="text-align:center; display:none;">
                <span>Business Justification can not be left blank.</span></div>

            <div>
                <span>Select Business Justification</span><span
                    class="asterisk">*</span> &nbsp; &nbsp;<care:cannedResponse taskDescription="${taskDescription}"
                                                                                targetId="accessJustification"/><br>
                <span>Enter Business Justification</span><span class="asterisk">*</span>
                <g:render template="accessJustificationMarginLeft20"/>
                <input type="hidden" name="id" value="${task.id}"/>
                <g:render template="/apsWorkflowTask/attachment"/>
            </div>

            <div style="text-align:center;">
                <input class="button" type="submit" value="Submit"/>
            </div>
        </g:form>
    </div>
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