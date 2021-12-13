<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show FeedRun</title>
</head>
<body>
<br/>
<div id="wrapper">
    <div id="right-panel">

        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">FeedRuns</g:link></span>
        </div>
        <div class="body">
            <h1>Show FeedRun</h1>
            <g:if test="${flash.message}">
                <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>

                    <tr class="prop">
                        <td valign="top" class="name"><g:message code="feedRun.id.label" default="Id"/></td>

                        <td valign="top" class="value">${fieldValue(bean: feedRunInstance, field: "id")}</td>

                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name"><g:message code="feedRun.reportMessages.label" default="Report Messages"/></td>

                        <td valign="top" style="text-align: left;" class="value">
                            <ul>
                                <g:each in="${feedRunInstance.reportMessages}" var="r">
                                    <li><g:link controller="feedRunReportMessage" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></li>
                                </g:each>
                            </ul>
                        </td>

                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name"><g:message code="feedRun.startTime.label" default="Start Time"/></td>

                        <td valign="top" class="value">${feedRunInstance?.startTime?.myDateTimeFormat()}</td>

                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name"><g:message code="feedRun.endTime.label" default="End Time"/></td>

                        <td valign="top" class="value">${feedRunInstance?.endTime?.myDateTimeFormat()}</td>

                    </tr>

                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
