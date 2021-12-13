<%@ page import="com.force5solutions.care.aps.Origin; com.force5solutions.care.common.CareConstants; com.force5solutions.care.cc.EntitlementPolicy; com.force5solutions.care.ldap.Permission; com.force5solutions.care.aps.Entitlement" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'entitlement.label', default: 'Entitlement')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    <g:render template="/permission/listButton"
              model="[permission: Permission.READ_ENTITLEMENT, label: 'Entitlement List']"/>
    <span class="menuButton"><a class="create" href="${createLink(action:'unapprovedEntitlements')}">Unapproved Entitlements</a></span>
</div>

<div class="body" id="bodyContainer">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="dialog">
        <table>
            <tbody>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="entitlement.name.label" default="Name"/></td>

                <td valign="top" class="value">${fieldValue(bean: entitlement, field: "name")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="entitlement.alias.label" default="Alias"/></td>

                <td valign="top" class="value">${fieldValue(bean: entitlement, field: "alias")}</td>

            </tr>
            <tr class="prop">
                <td valign="top" class="name"><g:message code="isExposed.label" default="Is Exposed"/></td>

                <td valign="top" class="value">${fieldValue(bean: entitlement, field: "isExposed") ? 'Yes' : 'No'}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name">Is Auto-provisioned</td>

                <td valign="top" class="value">${entitlement?.toBeAutoProvisioned ? 'Yes' : 'No'}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name">Is Auto-deprovisioned</td>

                <td valign="top" class="value">${entitlement?.toBeAutoDeprovisioned ? 'Yes' : 'No'}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="entitlement.status.label" default="Status"/></td>

                <td valign="top" class="value">${entitlement?.status?.encodeAsHTML()}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="entitlement.origin.label" default="Origin"/></td>

                <td valign="top" class="value"><g:link controller="origin" action="show"
                                                       id="${entitlement?.origin?.id}">${entitlement?.origin?.encodeAsHTML()}</g:link></td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="entitlement.owner.label" default="Owner"/></td>

                <td valign="top" class="value"><g:link controller="roleOwner" action="show"
                                                       id="${entitlement?.owner?.id}">${entitlement?.owner?.encodeAsHTML()}</g:link></td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="entitlement.gatekeepers.label"
                                                         default="Gatekeepers"/></td>

                <td valign="top" style="text-align: left;" class="value">
                    <ul>
                        <g:each in="${entitlement.gatekeepers}" var="g">
                            <li><g:link controller="securityRole" action="show"
                                        id="${g.id}">${g?.encodeAsHTML()}</g:link></li>
                        </g:each>
                    </ul>
                </td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="entitlement.provisioners.label"
                                                         default="Provisioners"/></td>

                <td valign="top" style="text-align: left;" class="value">
                    <ul>
                        <g:each in="${entitlement.provisioners}" var="g">
                            <li><g:link controller="provisioner" action="show"
                                        id="${g.id}">${g?.encodeAsHTML()}</g:link></li>
                        </g:each>
                    </ul>
                </td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="entitlement.deProvisioners.label"
                                                         default="Deprovisioners"/></td>

                <td valign="top" style="text-align: left;" class="value">
                    <ul>
                        <g:each in="${entitlement.deProvisioners}" var="g">
                            <li><g:link controller="deProvisioner" action="show"
                                        id="${g.id}">${g?.encodeAsHTML()}</g:link></li>
                        </g:each>
                    </ul>
                </td>

            </tr>


            <tr class="prop">
                <td valign="top" class="name"><g:message code="entitlement.type.label"
                                                         default="Entitlement Policy"/></td>

                <td valign="top" class="value"><g:link controller="entitlementPolicy" action="show"
                                                       id="${entitlement?.type}">${EntitlementPolicy.get(entitlement?.type)?.encodeAsHTML()}</g:link></td>

            </tr>
            <g:if test="${entitlement?.customPropertyValues}">
                <g:each in="${entitlement?.customPropertyValues}" var="customPropertyValue">
                    <tr>
                        <td valign="top" class="name">${customPropertyValue.customProperty.name}</td>

                        <td valign="top" class="value">${customPropertyValue.value}</td>
                    </tr>
                </g:each>
            </g:if>
            <tr class="prop">
                <td valign="top" class="name"><g:message code="entitlement.standards.label" default="Standards"/></td>

                <td valign="top" style="text-align: left;"
                    class="value">${EntitlementPolicy.get(entitlement?.type)?.standards?.join(', ')}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="entitlement.notes.label" default="Notes"/></td>

                <td valign="top" class="value">${fieldValue(bean: entitlement, field: "notes")}</td>

            </tr>

            </tbody>
        </table>
    </div>

    <div class="buttons">
        <g:form method="post">
            <g:hiddenField name="id" value="${entitlement?.id}"/>
            <g:if test="${entitlement.origin.name==Origin.PICTURE_PERFECT_FEED}">
                <span class="button">
                    <input type="button" class="edit" style="color:gray;" value="Edit"/>
                </span>
            </g:if>
            <g:else>
                <g:render template="/permission/editButton" model="[permission: Permission.UPDATE_ENTITLEMENT]"/>
                <span class="button">
                    <g:actionSubmit class="edit" action="resubmitApprovalRequest" value="Re-submit"/></span>
            </g:else>
        %{--<g:render template="/permission/deleteButton" model="[permission: Permission.DELETE_ENTITLEMENT]"/>--}%
        </g:form>
    </div>

    <versionable:showUnapprovedChanges object="${entitlement}"/>
</div>
</body>
</html>
