<%@ page import="com.force5solutions.care.cc.EntitlementPolicy; com.force5solutions.care.ldap.Permission" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'entitlement.label', default: 'Entitlement')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    <g:render template="/permission/createButton"
              model="[permission: Permission.CREATE_ENTITLEMENT, label: 'New Entitlement']"/>
    <span class="menuButton"><a class="create"
                                href="${createLink(action: 'unapprovedEntitlements')}">Unapproved Entitlements</a>
    </span>
</div>

<div class="body">
    <h1><g:message code="default.list.label" args="[entityName]"/>
        <g:if test="${session.filterEntitlementCommand}">
            <g:link class="filterbutton" action="showAllEntitlement">
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
        <g:render template="entitlementsTable"
                  model="[entitlementList: entitlementList, entitlementTotal: entitlementTotal, offset: offset, max: max, order: order, sort: sort]"/>
    </div>

    <div id="filterDialog" class="popupWindowContractorFilter" style="width: 570px;">
    </div>
</div>
</body>
</html>
