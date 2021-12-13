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
    <span class="menuButton"><a class="create" href="${createLink(action: 'list')}">Approved Entitlements</a></span>
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
    </h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="list" id="list">
        <table>
            <thead>
            <tr>
                <g:sortableColumn property="name" title="Name"/>
                <g:sortableColumn property="alias" title="Alias"/>
                <g:sortableColumn property="isExposed" title="Exposed in CARE Central"/>
                <g:sortableColumn property="status" title="Status"/>
                <g:sortableColumn property="origin" title="Origin"/>
                <g:sortableColumn property="entitlementPolicy" title="Entitlement Policy"/>
                <g:sortableColumn property="owner" title="Owner"/>
            </tr>
            </thead>
            <tbody>
            <g:each in="${entitlementList}" status="i" var="entitlement">
                <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                    <td class="breakWord"><g:link action="showUnapprovedChanges"
                                id="${entitlement.id}">${fieldValue(bean: entitlement, field: "name")}</g:link></td>
                    <td class="breakWord"><g:link action="showUnapprovedChanges"
                                id="${entitlement.id}">${fieldValue(bean: entitlement, field: "alias")}</g:link></td>
                    <td><g:link action="showUnapprovedChanges"
                                id="${entitlement.id}">${fieldValue(bean: entitlement, field: "isExposed") ? "Yes" : "No"}</g:link></td>
                    <td><g:link action="showUnapprovedChanges"
                                id="${entitlement.id}">${fieldValue(bean: entitlement, field: "status")}</g:link></td>
                    <td><g:link action="showUnapprovedChanges"
                                id="${entitlement.id}">${fieldValue(bean: entitlement, field: "origin")}</g:link></td>
                    <td><g:link action="showUnapprovedChanges"
                                id="${entitlement.id}">${fieldValue(bean: entitlement, field: "type")}</g:link></td>
                    <td><g:link action="showUnapprovedChanges"
                                id="${entitlement.id}">${fieldValue(bean: entitlement, field: "owner")}</g:link></td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>

    <div class="paginateButtons">
        <g:paginate total="${entitlementList.size()}"/>
    </div>

    <div id="filterDialog" class="popupWindowContractorFilter" style="width: 570px;">
    </div>
</div>
<script type="text/javascript">
    jQuery(document).ready(function () {
        jQuery('#filterButton').click(function () {
            jQuery.post("${createLink(controller:'entitlement', action:'filterDialog')}",
                    { ajax:'true'}, function (htmlText) {
                        jQuery('#filterDialog').html(htmlText);
                    });
            showModalDialog('filterDialog', true);
        });
    });
</script>
</body>
</html>
