<%@ page import="com.force5solutions.care.ldap.Permission; com.force5solutions.care.aps.EntitlementRole" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'entitlementRole.label', default: 'Entitlement Role')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    <g:render template="/permission/listButton" model="[permission: Permission.READ_ENTITLEMENT_ROLE, label: 'Entitlement Role List']"/>
    <g:render template="/permission/createButton" model="[permission: Permission.CREATE_ENTITLEMENT_ROLE, label: 'New Entitlement Role']"/>
    <span class="menuButton"><a class="create" href="${createLink(action:'unapprovedEntitlementRoles')}">Unapproved Entitlement Roles</a></span>
</div>
<div class="body">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="dialog">
        <table>
            <tbody>
            <tr class="prop">
                <td valign="top" class="name">Name</td>
                <td valign="top" class="value">${fieldValue(bean: entitlementRole, field: "name")}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Exposed in CARE Central</td>
                <td valign="top" class="value">${(entitlementRole?.isExposed) ? 'Yes' : 'No'}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Status</td>
                <td valign="top" class="value">${entitlementRole?.status?.encodeAsHTML()}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Owner</td>
                <td valign="top" class="value"><g:link controller="roleOwner" action="show" id="${entitlementRole?.owner?.id}">${entitlementRole?.owner?.encodeAsHTML()}</g:link></td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Gatekeepers</td>
                <td valign="top" style="text-align: left;" class="value">
                    <ul>
                        <g:each in="${entitlementRole.gatekeepers}" var="g">
                            <li><g:link controller="securityRole" action="show" id="${g.id}">${g?.encodeAsHTML()}</g:link></li>
                        </g:each>
                    </ul>
                </td>

            </tr>
            <tr class="prop">
                <td valign="top" class="name">Entitlements</td>
                <td valign="top" style="text-align: left;" class="value">
                    <ul>
                        <g:each in="${entitlementRole.entitlements}" var="p">
                            <li><a style="font-weight: bold;">${p?.encodeAsHTML()}</a></li>
                        </g:each>
                    </ul>
                </td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Roles</td>
                <td valign="top" style="text-align: left;" class="value">
                    <ul>
                        <g:each in="${entitlementRole.roles}" var="g">
                            <li><g:link controller="entitlementRole" action="show" id="${g.id}">${g?.encodeAsHTML()}</g:link></li>
                        </g:each>
                    </ul>
                </td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Notes</td>
                <td valign="top" class="value">${fieldValue(bean: entitlementRole, field: "notes")}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name"></td>
                <td valign="top" style="text-align: left;" class="value"></td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name"><span style="font-weight:bold;font-size:16px;">Inherited</span></td>
                <td valign="top" style="text-align: left;" class="value"></td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name"><span style="padding-left: 20px;">Gatekeepers</span></td>
                <td valign="top" style="text-align: left;" class="value">${gatekeepersString}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name"><span style="padding-left: 20px;">Standards</span></td>
                <td valign="top" style="text-align: left;" class="value">${standardsString}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name"><span style="padding-left: 20px;">Entitlement Policies</span></td>
                <td valign="top" style="text-align: left;" class="value">${entitlementPoliciesString}</td>
            </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form method="post">
            <g:hiddenField name="id" value="${entitlementRole?.id}"/>
            <g:render template="/permission/editButton" model="[permission: Permission.UPDATE_ENTITLEMENT_ROLE]"/>
            <span class="button">
                <g:actionSubmit action="resubmitApprovalRequest" value="Re-submit"/></span>
            %{--<g:render template="/permission/deleteButton" model="[permission: Permission.DELETE_ENTITLEMENT_ROLE]"/>--}%
        </g:form>
    </div>
    <versionable:showUnapprovedChanges object="${entitlementRole}"/>
</div>
</body>
</html>
