<%@ page import="com.force5solutions.care.ldap.Permission; org.codehaus.groovy.grails.commons.ConfigurationHolder; com.force5solutions.care.aps.Gatekeeper" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'gatekeeper.label', default: 'Gatekeeper')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></span>
    <g:if test="${care.hasPermission(permission: Permission.CREATE_GATEKEEPER)}">
        <span class="menuButton"><g:link class="create" action="create">New Gatekeeper</g:link></span>
    </g:if>
    <g:else>
        <span class="menuButton" style="color:gray">New Gatekeeper</span>
    </g:else>
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
                <td valign="top" class="name"><g:message code="gatekeeper.firstName.label" default="First Name"/></td>

                <td valign="top" class="value">${fieldValue(bean: gatekeeper, field: "firstName")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="gatekeeper.lastName.label" default="Last Name"/></td>

                <td valign="top" class="value">${fieldValue(bean: gatekeeper, field: "lastName")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="gatekeeper.slid.label" default="SLID"/></td>

                <td valign="top" class="value">${fieldValue(bean: gatekeeper, field: "slid")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="gatekeeper.email.label" default="Email"/></td>

                <td valign="top" class="value">${fieldValue(bean: gatekeeper, field: "email")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="gatekeeper.name.label" default="Name"/></td>

                <td valign="top" class="value">${fieldValue(bean: gatekeeper, field: "name")}</td>

            </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:if test="${!ConfigurationHolder.config.isEmployeeEditable.toString().equals('false')}">
            <g:form>
                <g:hiddenField name="id" value="${gatekeeper?.id}"/>

                <g:if test="${care.hasPermission(permission: Permission.UPDATE_GATEKEEPER)}">
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}"/></span>
                </g:if>
                <g:else>
                    <span class="button"><input type="button" class="edit" style="color:gray;" value="Edit"/></span>
                </g:else>
                <g:if test="${care.hasPermission(permission: Permission.DELETE_GATEKEEPER)}">
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
                </g:if>
                <g:else>
                    <span class="button"><input type="button" class="delete" style="color:gray;" value="Delete"/></span>
                </g:else>
            </g:form>
        </g:if>
    </div>
</div>
</body>
</html>
