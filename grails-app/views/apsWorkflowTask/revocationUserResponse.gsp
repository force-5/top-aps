<%@ page import="com.force5solutions.care.common.CareConstants; com.force5solutions.care.workflow.WorkflowTask; com.force5solutions.care.workflow.CentralWorkflowTask; com.force5solutions.care.cc.PeriodUnit; com.force5solutions.care.workflow.ApsWorkflowTask" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Revocation Action Required</title>
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

    .scrollTable {
        width: 700px;
    }
    </style>
</head>

<body>
<br/>
<g:if test="${tasks}">
    <g:render template="revocationGroupResponse" model="[tasks: tasks, task: task]"/>
</g:if>
<g:else>
    <g:render template="revocationUserResponse" model="[task: task]"/>
</g:else>
</body>
</html>
