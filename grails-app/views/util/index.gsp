<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Utility Links</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
</div>
<div class="body">
    <h1>TOP APS</h1>
    <br/>
    <br/>
    <div class="list">
        <g:link action="workflowReport" id="latest" target="_blank"><span style="font-size:14px;">Workflow Report</span></g:link>
        <span style="font-size:14px;">
            <br/>(By default, shows the 'latest' triggered workflow's report. You can also put the workflow's GUID in the URL to see the report on any other workflow.)
        </span>
        <br/>
        <br/>
        <g:link action="chooseWorkerForWorkflowReport" target="_blank"><span style="font-size:14px;">Workflow Report By SLID</span></g:link>
        <span style="font-size:14px;">
            <br/>(Please put the SLID of the employee in the URL for whom you want to see the workflow reports)
        </span>
        <br/>
        <br/>
        <g:link action="feeds" target="_blank"><span style="font-size:14px;">Feeds</span></g:link>
        <br/>
        <br/>
        <g:link action="selectDateTimeForEscalation" target="_blank"><span style="font-size:14px;">Select Date/Time for Escalation</span></g:link>
        <br/>
        <br/>
        <g:link action="list" controller="apsWorkflowTaskTemplate" target="_blank"><span style="font-size:14px;">APS Workflow Task Templates</span></g:link>
        <br/>
        <br/>
        <g:link action="kickOffJobs" target="_blank"><span style="font-size:14px;">Jobs Details</span></g:link>
        <br/>
        <br/>
        <g:link action="uploadFixture" target="_blank"><span style="font-size:14px;">Upload/Execute Fixture</span></g:link>
    </div>
</div>
</body>
</html>
