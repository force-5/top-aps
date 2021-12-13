<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>REVOCATION EVIDENCE PACKAGE</title>
</head>
<body>
<div class="body">
    <h1>CREATE REVOCATION EVIDENCE PACKAGE</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:form action="createRevocationEvidencePackage" controller="util" method="post">
        <div>
            <input type="hidden" name="SUBREPORT_DIR" value="${application.getRealPath('/reports') + File.separator}"/>
            <input type="hidden" name="_format" value="PDF"/>
        </div>
        <div class="dialog" style="padding-bottom:20px;">
            <strong>Choose Employee to create a Revocation Package:</strong>
            <select name="taskId">
                <g:each in="${tasks}" var="task">
                    <option value="${task.id}">${task?.worker}[${task?.workerEntitlementRole}, ${task.effectiveStartDate.myDateTimeFormat()}]</option>
                </g:each>
            </select>
        </div>
        <div class="buttons">
            <span class="button">
                <input type="submit" name="createEvidence" value="Create Evidence"/>
            </span>
        </div>
    </g:form>
</div>
</body>
</html>
