<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show FeedRunReportMessage</title>
</head>

<body>
<br/>

<div id="wrapper">
    <div id="right-panel">
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
        </div>

        <div class="body">
            <h1>Show FeedRunReportMessage</h1>
            <g:if test="${flash.message}">
                <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>

                    <tr class="prop">
                        <td valign="top" width="80"><g:message code="feedRunReportMessage.id.label" default="Id"/></td>

                        <td valign="top">${feedRunReportMessageInstance.id}</td>

                    </tr>

                    <tr class="prop">
                        <td valign="top"><g:message code="feedRunReportMessage.dateCreated.label"
                                                    default="Date Created"/></td>

                        <td valign="top">${feedRunReportMessageInstance?.dateCreated?.myDateTimeFormat()}</td>

                    </tr>

                    <tr class="prop">
                        <td valign="top"><g:message code="feedRunReportMessage.type.label" default="Type"/></td>

                        <td valign="top">${feedRunReportMessageInstance?.type?.encodeAsHTML()}</td>

                    </tr>

                    <tr class="prop">
                        <td valign="top"><g:message code="feedRunReportMessage.message.label" default="Message"/></td>

                        <td valign="top">${fieldValue(bean: feedRunReportMessageInstance, field: "message")}</td>

                    </tr>

                    <tr class="prop">
                        <td valign="top"><g:message code="feedRunReportMessage.numberOfRecords.label"
                                                    default="Number Of Records"/></td>

                        <td valign="top">${feedRunReportMessageInstance?.numberOfRecords?.encodeAsHTML()}</td>

                    </tr>

                    <tr class="prop">
                        <td valign="top"><g:message code="feedRunReportMessage.feedRun.label" default="Feed Run"/></td>

                        <td valign="top"><g:link controller="feedRun" action="show"
                                                 id="${feedRunReportMessageInstance?.feedRun?.id}">${feedRunReportMessageInstance?.feedRun?.encodeAsHTML()}</g:link></td>

                    </tr>

                    <tr class="prop">
                        <td valign="top"><g:message code="feedRunReportMessage.details.label" default="Details"/></td>

                        <td valign="top" style="text-align: left;">
                            <ul>
                                <g:each in="${feedRunReportMessageInstance.details}" var="detail">
                                    <li style="padding-bottom: 5px;">${detail?.encodeAsHTML()}</li>
                                </g:each>
                            </ul>
                        </td>

                    </tr>

                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
