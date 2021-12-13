<%@ page import="com.force5solutions.care.cc.Employee" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Workflow Report</title>
</head>

<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
</div>

<div class="body">
    <g:form action="workflowReportBySlidOrId" method='post'>
        <div style="padding-top: 20px;"><span style="color: #F15A29; font-size: 16px; padding-right: 10px;">Choose Worker for Workflow Report: </span>
            <select name="id">
                <g:each in="${workers}" var="worker">
                    <g:if test="${worker?.slid}">
                        <option value="${worker.slid}">${worker.person.toString()}</option>
                    </g:if>
                    <g:else>
                        <option value="${worker.workerNumber}">${worker.person.toString()}</option>
                    </g:else>
                </g:each>
            </select>
            <br/>
            <br/>

            <div align="center"><g:submitButton name="submit" value="OK"/></div>
        </div>
    </g:form>
</div>
</body>
</html>
