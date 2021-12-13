<%@ page import="com.force5solutions.care.*; com.force5solutions.care.ldap.Permission" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Message Templates</title>
</head>

<body>
<br/>

<div id="wrapper">
    <div id="right-panel">
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
            <g:if test="${care.hasPermission(permission: Permission.CREATE_MESSAGE_TEMPLATE)}">
                <span class="menuButton"><g:link class="create" action="create">New Message Template</g:link></span>
            </g:if>
            <g:else>
                <span class="menuButton"><a class="create-disabled">New Message Template</a></span>
            </g:else>
        </div>

        <div class="body">
            <h1>Message Templates
                <span style="float: right;color: #666666;">Show: <g:select
                        from="[10, 25, 50, 100, 'Unlimited']"
                        name="rowCount"
                        id="rowCount" value="${max}"/></span>
            </h1>
            <g:if test="${flash.message}">
                <div class="message">${flash.message}</div>
            </g:if>
            <div class="list"  id="list" style="padding-top: 10px;">
                <g:render template="apsMessageTemplatesTable"
                          model="[apsMessageTemplateList: apsMessageTemplateList, apsMessageTemplateTotal: apsMessageTemplateTotal, offset: offset, max: max, order: order, sort: sort]"/>
            </div>
        </div>
    </div>
</div>
</body>
</html>
