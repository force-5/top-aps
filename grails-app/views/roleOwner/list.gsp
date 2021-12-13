<%@ page import="com.force5solutions.care.aps.RoleOwner" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'roleOwner.label', default: 'RoleOwner')}"/>
    <title>Role Owner List</title>
</head>

<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New Role Owner</g:link></span>
</div>

<div class="body">
    <h1>Role Owner List
        <span style="float: right;color: #666666;">Show: <g:select from="[10, 25, 50, 100, 'Unlimited']"
                                                                   name="rowCount"
                                                                   id="rowCount" value="${max}"/></span>
    </h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="list" id='list' style="padding-top: 10px;">
        <g:render template="roleOwnersTable"
                  model="[roleOwnerList: roleOwnerList, roleOwnerTotal: roleOwnerTotal, offset: offset, max: max, order: order, sort: sort]"/>
    </div>
</div>
</body>
</html>
