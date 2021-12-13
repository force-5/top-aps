<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder; com.force5solutions.care.aps.RoleOwner" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'roleOwner.label', default: 'RoleOwner')}"/>
    <title>Show Role Owner</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Role Owner List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New Role Owner</g:link></span>
</div>
<div class="body">
    <h1>Show Role Owner</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="dialog">
        <table>
            <tbody>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="roleOwner.firstName.label" default="First Name"/></td>

                <td valign="top" class="value">${fieldValue(bean: roleOwner, field: "firstName")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="roleOwner.lastName.label" default="Last Name"/></td>

                <td valign="top" class="value">${fieldValue(bean: roleOwner, field: "lastName")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="roleOwner.slid.label" default="SLID"/></td>

                <td valign="top" class="value">${fieldValue(bean: roleOwner, field: "slid")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="roleOwner.email.label" default="Email"/></td>

                <td valign="top" class="value">${fieldValue(bean: roleOwner, field: "email")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="roleOwner.name.label" default="Name"/></td>

                <td valign="top" class="value">${fieldValue(bean: roleOwner, field: "name")}</td>

            </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:if test="${!ConfigurationHolder.config.isEmployeeEditable.toString().equals('false')}">
            <g:form>
                <g:hiddenField name="id" value="${roleOwner?.id}"/>
                <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}"/></span>
                <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
            </g:form>
        </g:if>
    </div>
</div>
</body>
</html>
