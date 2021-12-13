<%@ page import="com.force5solutions.care.ldap.Permission" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'entitlementRole.label', default: 'Entitlement Role')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    <g:render template="/permission/createButton"
              model="[permission: Permission.CREATE_ENTITLEMENT_ROLE, label: 'New Entitlement Role']"/>
    <span class="menuButton"><a class="create"
                                href="${createLink(action: 'unapprovedEntitlementRoles')}">Unapproved Entitlement Roles</a>
    </span>
</div>

<div class="body">
    <h1><g:message code="default.list.label" args="[entityName]"/>
        <g:if test="${session.filterEntitlementRoleCommand}">
            <g:link class="filterbutton" action="showAllEntitlementRole">
                <span>Show All</span></g:link>
        </g:if>
        <g:else>
            <a href="#" class="filterbutton" id="filterButton"><span>Filter</span></a>
        </g:else>
        <span style="float: right;color: #666666;">Show: <g:select from="[10, 25, 50, 100, 'Unlimited']" name="rowCount"
                                                                   id="rowCount" value="${max}"/></span>
    </h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="list" id="list" style="padding-top: 10px;">
        <g:render template="entitlementRolesTable"
                  model="[entitlementRoleList: entitlementRoleList, entitlementRoleTotal: entitlementRoleTotal, offset: offset, max: max, order: order, sort: sort]"/>
    </div>

    <div id="filterDialog" class="popupWindowContractorFilter">
    </div>

</div>
</body>
</html>
