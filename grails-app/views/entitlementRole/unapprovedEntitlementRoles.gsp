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
    <span class="menuButton"><a class="create" href="${createLink(action: 'list')}">Approved Entitlement Roles</a>
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
    </h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="list" id="list">
        <table>
            <thead>
            <tr>
                <g:sortableColumn property="name"
                                  title="${message(code: 'entitlementRole.name.label', default: 'Name')}"/>
                <g:sortableColumn property="isExposed" title="${message(code: 'isExposed.label', default: 'Exposed')}"/>
                <g:sortableColumn property="status"
                                  title="${message(code: 'entitlementRole.status.label', default: 'Status')}"/>
                <g:sortableColumn property="owner"
                                  title="${message(code: 'entitlementRole.owner.label', default: 'Owner')}"/>
            </tr>
            </thead>
            <tbody>
            <g:each in="${entitlementRoleList}" status="i" var="entitlementRole">
                <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                    <td class="breakWord"><g:link action="showUnapprovedChanges"
                                id="${entitlementRole.id}">${fieldValue(bean: entitlementRole, field: "name")}</g:link></td>
                    <td><g:link action="showUnapprovedChanges"
                                id="${entitlementRole.id}">${(entitlementRole?.isExposed) ? 'Yes' : 'No'}</g:link></td>
                    <td><g:link action="showUnapprovedChanges"
                                id="${entitlementRole.id}">${fieldValue(bean: entitlementRole, field: "status")}</g:link></td>
                    <td><g:link action="showUnapprovedChanges"
                                id="${entitlementRole.id}">${fieldValue(bean: entitlementRole, field: "owner")}</g:link></td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>

    <div class="paginateButtons">
        <g:paginate total="${entitlementRoleList.size()}"/>
    </div>

    <div id="filterDialog" class="popupWindowContractorFilter">
    </div>

</div>
<script type="text/javascript">
    jQuery(document).ready(function () {
        jQuery('#filterButton').click(function () {
            jQuery.post("${createLink(controller:'entitlementRole', action:'filterDialog')}",
                    { ajax:'true'}, function (htmlText) {
                        jQuery('#filterDialog').html(htmlText);
                    });
            showModalDialog('filterDialog', true);
        });
    });
</script>

</body>
</html>
