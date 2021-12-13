<%@ page import="com.force5solutions.care.common.CareConstants" %>
<g:form action="sendUserResponse" method="post" enctype="multipart/form-data">
    <div style="font-size:14px;">
        <g:if test="${task.entitlementId}">
            <span style="float:left;">Action for <b>${task.workerName}'s</b> access on entitlement <b>${task.entitlement}</b>&nbsp;&nbsp;&nbsp;&nbsp;
            </span>
        </g:if>
        <g:else>
            <span style="float:left;">Action for <b>${task.workerName}'s</b> access on role <b>${task.entitlementRole}</b>&nbsp;&nbsp;&nbsp;&nbsp;
            </span>
        </g:else>
        <g:if test="${task.actions?.size() > 2}">
            <span><g:select class="listbox" style="padding:0;width:150px;" name="userAction"
                            from="${task.actions}"/></span>
        </g:if>
    </div>

    <br/><br/>

    <div id="explanation-error" class="error-status" style="text-align:center; display:none;">
        <span>Access Justification can not be left blank.</span></div>

    <div>
        <span>Select Business Justification</span><span
            class="asterisk">*</span> &nbsp; &nbsp;<care:cannedResponse
            taskDescription="${CareConstants.CANNED_RESPONSE_APS_USER_RESPONSE}"
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
<script type="text/javascript">
    jQuery(document).ready(function () {
        jQuery('#actionDate').datetimepicker({
            ampm:true
        });
        jQuery('.cannedSelectBox').focus();
        jQuery('form').submit(function () {
            return isStatusChangeCommentEmpty('accessJustification', 'explanation-error')
        });
    });
</script>