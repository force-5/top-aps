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
            <a class="filterbutton" href="${createLink(action: 'filterCompletedTasks')}"><span>Completed</span></a>
            <a href="#" class="filterbutton" id="filterButton"><span>Filter</span></a>
            <g:if test="${session?.filterVO}">
                <g:link class="filterbutton" controller="apsWorkflowTask" action="showAllTasks">
                    <span>Show All</span></g:link>
            </g:if>
        </div>

        <div class="body">
            <h1>Inbox</h1>
            <g:if test="${flash.message}">
                <div class="message">${flash.message}</div>
            </g:if>
            <div class="list" id="list">
                <g:form action="getGroupResponse" name="groupResponse" id="groupResponse">
                    <g:render template="apsWorkflowTasksTable"
                              model="[tasks: tasks]"/>
                    <g:if test="${tasks}">
                        <div style="text-align:center;">
                            <input class="button" type="submit" value="Submit"/>
                            <input class="button" type="reset" value="Reset"/>
                        </div>
                    </g:if>
                </g:form>
            </div>
        </div>

        <div id="filterDialog" class="popupWindowContractorFilter">
        </div>
    </div>
</div>
</body>
</html>
