<%@ page import="com.force5solutions.care.common.CareConstants" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Access Verification</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
</div>
<div class="body">

    <style type="text/css">
    h1 {
        color: #F15A29;
        font-weight: normal;
        font-size: 16px;
        margin: 8px 0px 3px 0px;
        padding: 0px;
    }

    .list {
        border: 1px solid #ddd;
        margin-bottom: 20px;
        padding: 5px 10px;

    }

    .heading-access {
        font-size: 16px;
        color: #7e7e7e
    }

    .table-access-verification {
        font-size: 14px;
        border: none;
    }

    .table-access-verification th {
        font-size: 14px;
        padding: 6px 6px;
        background: #fff;
        border: none;
        border-bottom: 1px solid #ddd;
    }

    .table-access-verification th:hover {
        background: #fff;
    }

    .table-access-verification td {
        font-size: 14px;
        padding: 10px 6px;
        border: none
    }

    table {
        border: 1px solid #ccc;
        width: 100%;
    }

    tr {
        border: 0;
    }

    td, th {
        font: 11px verdana, arial, helvetica, sans-serif;
        line-height: 12px;
        padding: 3px 6px;
        text-align: left;
        vertical-align: top;
    }

    th {
        background: #fff url(../images/skin/shadow.jpg);
        color: #666;
        font-size: 10px;
        line-height: 17px;
        padding: 2px 6px;
    }

    th a:link, th a:visited, th a:hover {
        color: #333;
        display: block;
        font-size: 10px;
        text-decoration: none;
        width: 100%;
    }

    .odd {
        background: #CFD0D2;
    }

    .even {
        background: #fff;
    }

    .buttonContainer {
        border: 0px;
    }
    </style>

    <h1>Access Verification</h1>
    <p style="font-size:14px; padding-bottom:20px; text-align:justify !important;">Below is a list of personnel reporting to ${supervisorName} that have authorized cyber and/or authorized unescorted physical access to the
    System Control Center's Critical Cyber Assets (CCAs). Please verify these individuals' continued need for access. Select <strong>CONFIRM</strong> or <strong>REVOKE ALL</strong> from the drop down list and click Submit to apply this action to the entire list. Should an individual employee listed no longer require access, please log into top central and make changes to that employee and return to this task to CONFIRM the modified list.</p>

    <g:form action="sendUserResponse" method="post" enctype="multipart/form-data">
        <input type="hidden" name="id" value="${task.id}"/>
        <g:each in="${rolesMap}" var="roleMap">
            <div class="list">
                <p class="heading-access">Entitlement Role : <span style="color:black;">${roleMap.key}</span></p>
                <table cellpadding="0" cellspacing="0" border="0" class="table-access-verification">
                    <thead>
                    <tr>
                        <th width="300">Name</th>
                        <th>SLID</th>
                        <th>Badge</th>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${roleMap.value}" status="i" var="emp">
                        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                            <td>${emp.name}</td>
                            <td>${emp.slid}</td>
                            <td>${emp.badge}</td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
        </g:each>
        <g:if test="${task.actions}">
            <span style="font-size:14px;">Select Action</span> <span class="asterisk" style="padding-right:38px;">*</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <span><g:select class="listbox" style="padding:0;width:150px;" name="userAction" from="${task.actions}"/></span>
        </g:if>
        <div>
            <div id="explanation-error" class="error-status" style="text-align:center; display:none;">
                <span>Business Justification can not be left blank.</span>
            </div>
            <span style="font-size:14px;">Select Business Justification</span><span class="asterisk" style="padding-right:5px;">*</span> &nbsp; &nbsp;<care:cannedResponse taskDescription="${CareConstants.CANNED_RESPONSE_APS_ACCESS_VERIFICATION_JUSTIFICATION}" targetId="accessJustification"/><br>
            <span style="font-size:14px;">Enter Business Justification</span><span class="asterisk">*</span>
            <g:render template="accessJustificationMarginLeft20"/>
            <g:render template="/apsWorkflowTask/attachment"/>
        </div>
        <div class="list buttonContainer" style="text-align:center;">
            <input class="button" type="submit" value="Submit" onclick="return isStatusChangeCommentEmpty('accessJustification', 'explanation-error')"/>
        </div>
    </g:form>
</div>
</body>
</html>
