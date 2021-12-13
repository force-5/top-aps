<%@ page import="com.force5solutions.care.ldap.Permission" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Security Role List</title>
</head>

<body>
<br/>

<div id="wrapper">
    <div id="right-panel">
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
            <g:if test="${care.hasPermission(permission: Permission.CREATE_SECURITY_ROLE)}">
                <span class="menuButton"><g:link class="create createSecurityRoleLink"
                                                 action="create">New Security Role</g:link></span>
            </g:if>
            <g:else>
                <span class="menuButton"><a class="create-disabled createSecurityRoleLink">New Security Role</a></span>
            </g:else>
        </div>

        <div class="body">
            <h1>Security Role List
                <span style="float: right;color: #666666;">Show: <g:select from="[10, 25, 50, 100, 'Unlimited']"
                                                                           name="rowCount"
                                                                           id="rowCount" value="${max}"/></span>
            </h1>
            <g:if test="${flash.message}">
                <div class="message">${flash.message}</div>
            </g:if>
            <div class="list" id="list" style="padding-top: 10px;">
                <g:render template="securityRolesTable"
                          model="[roles: roles, securityRoleTotal: securityRoleTotal, offset: offset, max: params.max]"/>
            </div>
        </div>
    </div>
</div>

<g:render template="/shared/defaultListViewSizeScript" model="[controller: 'securityRole']"/>

</body>
</html>
