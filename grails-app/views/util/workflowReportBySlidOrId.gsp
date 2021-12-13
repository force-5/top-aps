<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>

    <title>Workflow Report By SLID</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    %{--<span class="menuButton"><a class="home" href="${createLink(action: 'chooseWorkerForWorkflowReport')}">Workflow Report By SLID/ID</a></span>--}%
</div>
<div class="body">
    <h1>Workflows for ${workerName} - (SLID/ID: ${slid})</h1>
    <div class="list">
        <table>
            <thead>
            <tr>
                <th>Entitlement Role</th>
                <th>Workflow Type</th>
                <th>Date Created</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${selectedCentralWorkflowTasks}" status="i" var="task">
                <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                    <td><g:link action="workflowReport" id="${task?.workflowGuid}">${task?.entitlementRole}</g:link></td>
                    <td><g:link action="workflowReport" id="${task?.workflowGuid}">${task?.workflowType}</g:link></td>
                    <td><g:link action="workflowReport" id="${task?.workflowGuid}">${task?.dateCreated.myDateTimeFormat()} (<prettytime:display date="${task?.dateCreated}"/>)</g:link></td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
