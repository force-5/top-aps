<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>FeedRun List</title>
</head>
<body>
<br/>
<div id="wrapper">
    <div id="right-panel">
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
        </div>
        <div class="body">
            <h1>FeedRun List</h1>
            <g:if test="${flash.message}">
                <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                    <tr>
                        <g:sortableColumn property="id" title="${message(code: 'feedRun.id.label', default: 'Id')}"/>
                        <g:sortableColumn property="feed" title="${message(code: 'feedRun.feed.label', default: 'Feed')}"/>
                        <g:sortableColumn property="startTime" title="${message(code: 'feedRun.startTime.label', default: 'Start Time')}"/>
                        <g:sortableColumn property="endTime" title="${message(code: 'feedRun.endTime.label', default: 'End Time')}"/>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${feedRunInstanceList}" status="i" var="feedRunInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                            <td><g:link action="show" id="${feedRunInstance.id}">${feedRunInstance.id}</g:link></td>
                            <td><g:link action="show" id="${feedRunInstance.id}">${feedRunInstance.feedName}</g:link></td>
                            <td><g:link action="show" id="${feedRunInstance.id}">${feedRunInstance.startTime?.myDateTimeFormat()}</g:link></td>
                            <td><g:link action="show" id="${feedRunInstance.id}">${feedRunInstance.endTime?.myDateTimeFormat()}</g:link></td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${feedRunInstanceTotal}"/>
            </div>
        </div>
    </div>
</div>
</body>
</html>
