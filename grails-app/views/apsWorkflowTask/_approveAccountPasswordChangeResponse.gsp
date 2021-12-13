<g:form action="sendUserResponse" method="post" enctype="multipart/form-data">
    <div style="font-size:14px;">
        <span style="float:left;">Confirm Password Change for: <b>${task.entitlement}</b>&nbsp;&nbsp;&nbsp;&nbsp;
        </span>
        <g:if test="${task.actions}">
            <span><g:select class="listbox" style="padding:0;width:150px;" name="userAction"
                            from="${task.actions}"/></span>
        </g:if>
    </div>
    <br/><br/>

    <div id="explanation-error" class="error-status" style="text-align:center; display:none;">
        <span>Comment can not be left blank.</span></div>

    <div style="float:left;">
        Enter the date and time of Action: &nbsp; <input type="text" value="" id="actionDate"
                                                         name="actionDate"/>
    </div>
    <br/><br/>
    <div>
        <span>Comment:</span>
        <g:render template="accessJustificationMarginLeft130"/>
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