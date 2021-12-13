<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Inbox</title>
</head>
<body>
<br/>
<div id="wrapper">
    <div id="right-panel">
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
        </div>
        <div class="body">
            <h1>Inbox</h1>
            <g:if test="${flash.message}">
                <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                    <tr>
                        <th>Workflow GUID</th>
                        <th>Workflow Type</th>
                        <th>Date Created</th>
                        <th>Worker</th>
                        <th>Entitlement Role</th>
                        <th>Entitlement</th>
                        <th>Current Node</th>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${tasks}" status="i" var="task">
                        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                            <td>${task.workflowGuid}</td>
                            <td>${task.workflowType}</td>
                            <td>${task.dateCreated.myDateTimeFormat()}</td>
                            <td>${task.worker}</td>
                            <td>${task.entitlementRole}</td>
                            <td>${task.entitlement}</td>
                            <td>${task.nodeName}</td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
