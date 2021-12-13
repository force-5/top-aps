<g:form action="sendUserResponse" method="post" enctype="multipart/form-data">
    <div style="font-size:14px;">
        <span style="float:left;">Action for approval of Entitlement : <b>${task.entitlement}</b>&nbsp;&nbsp;&nbsp;&nbsp;
        </span>
        <g:if test="${task.actions}">
            <span><g:select class="listbox" style="padding:0;width:150px;" name="userAction"
                            from="${task.actions}"/></span>
        </g:if>
    </div>
    <br/><br/>

    <div id="explanation-error" class="error-status" style="text-align:center; display:none;">
        <span>Comment can not be left blank.</span></div>

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