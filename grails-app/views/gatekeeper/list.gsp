<%@ page import="com.force5solutions.care.ldap.Permission; com.force5solutions.care.aps.Gatekeeper" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'gatekeeper.label', default: 'Gatekeeper')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    <g:if test="${care.hasPermission(permission: Permission.CREATE_GATEKEEPER)}">
        <span class="menuButton"><g:link class="create" action="create">New Gatekeeper</g:link></span>
    </g:if>
    <g:else>
        <span class="menuButton" style="color:gray">New Gatekeeper</span>
    </g:else>
</div>

<div class="body">
    <h1><g:message code="default.list.label" args="[entityName]"/>
        <span style="float: right;color: #666666;">Show: <g:select from="[10, 25, 50, 100, 'Unlimited']"
                                                                   name="rowCount"
                                                                   id="rowCount" value="${max}"/></span>
    </h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="list" id="list" style="padding-top: 10px;">
        <g:render template="gatekeepersTable"
                  model="[gatekeeperList: gatekeeperList, gatekeeperTotal: gatekeeperTotal, offset: offset, max: max, order: order, sort: sort]"/>
    </div>

</div>
</body>
</html>
