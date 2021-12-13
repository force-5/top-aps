<%@ page import="com.force5solutions.care.common.CareConstants" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>TOP Human Task</title>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery-1.4.4.min.js')}"></script>
    <script type="text/javascript">jQuery.noConflict();</script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery-ui-1.8.13.custom.min-new.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery-ui-timepicker-addon.js')}"></script>
    <script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'jQuery.tablesorter.js')}"></script>

    <style type="text/css">
    .scrollTable {
        width: 700px;
    }

    .ui-timepicker-div .ui-widget-header {
        margin-bottom: 8px;
    }

    .ui-timepicker-div dl {
        text-align: left;
    }

    .ui-timepicker-div dl dt {
        height: 25px;
    }

    .ui-timepicker-div dl dd {
        margin: -25px 0 10px 65px;
    }

    .ui-timepicker-div td {
        font-size: 90%;
    }
    </style>
</head>

<body>
<br/>

<div id="wrapper">
    <h1>TOP Human Task</h1>
    <br/>
<div style="margin:0 50px;font-size:14px;">
    <g:form action="sendGroupResponse" method="post" enctype="multipart/form-data">
        <table class="tablesorter" id="tablesorter">
            <thead>
            <tr>
                <th>Workflow Type</th>
                <th>Date Created</th>
                <th>Worker</th>
                <th>Entitlement Role</th>
                <th>Entitlement</th>
                <th>Current Node</th>
                <th>Select</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${apsWorkflowTasks}" status="i" var="task">
                <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                    <td><g:link action="getUserResponse" id="${task.id}">${task.workflowType}</g:link></td>
                    <td><g:link action="getUserResponse"
                                id="${task.id}">${task.dateCreated.myDateTimeFormat()}</g:link></td>
                    <td><g:link action="getUserResponse" id="${task?.id}">${task?.worker}</g:link></td>
                    <td><g:link action="getUserResponse"
                                id="${task.id}">${task.entitlementRole}</g:link></td>
                    <td><g:link action="getUserResponse" id="${task.id}">${task.entitlement}</g:link></td>
                    <td><g:link action="getUserResponse" id="${task.id}">${task.nodeName}</g:link></td>
                    <td><g:checkBox name="taskIds" value="${task?.id}"/></td>
                </tr>
            </g:each>
            </tbody>
        </table>

        <h3>Comments</h3><br/>

        <div style="font-size:14px;">
            <div style="float:left;">
                Enter the date and time of Action: &nbsp; <input type="text" value="" id="actionDate"
                                                                 name="actionDate"/>
            </div>
            <br/>
        </div>
        <br/>

        <div>
            <span style="float:left;">Action: &nbsp;&nbsp;&nbsp;&nbsp;</span>
            <span style="padding-left: 162px;"><g:select class="listbox" style="padding:0;width:150px;"
                                                         name="userAction"
                                                         from="['APPROVE', 'REJECT']"/></span>
        </div>

        <br/><br/>

        <div id="explanation-error" class="error-status" style="text-align:center; display:none;">
            <span>Access Justification can not be left blank.</span></div>

        <div>
            <span>Select Business Justification</span><span class="asterisk">*</span> &nbsp; &nbsp;
            <care:cannedResponse
                    taskDescription="${CareConstants.CANNED_RESPONSE_APS_ACCESS_REQUEST_PROVISIONER_JUSTIFICATION}"
                    targetId="accessJustification"/><br>
            <span>Please provide an explanation:</span>
            <g:render template="accessJustification"/>
            <g:render template="/apsWorkflowTask/attachment"/>
        </div>

        <div style="text-align:center;">
            <input class="button" type="submit" value="Submit"/>
            <g:link action="list" name="back" style="text-decoration: none;">
                <input class="button" type="button" value="Back"/>
            </g:link>
        </div>
        </div>
    </g:form>
</div>
<script type="text/javascript">
    jQuery(document).ready(function () {
        jQuery('#actionDate').datetimepicker({
            ampm:true
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
            jQuery("#tablesorter").tablesorter({textExtraction:myTextExtraction, sortList:[
                [1, 1]
            ], headers:{
                6:{
                    sorter:false
                }}});
        }
    });
</script>
</body>
</html>
