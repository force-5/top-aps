<%@ page import="com.force5solutions.care.ldap.Permission; org.codehaus.groovy.grails.commons.ConfigurationHolder; com.force5solutions.care.aps.Provisioner" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'provisioner.label', default: 'Provisioner')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></span>
    <g:if test="${care.hasPermission(permission: Permission.CREATE_PROVISIONER)}">
        <span class="menuButton"><g:link class="create" action="create">New Provisioner</g:link></span>
    </g:if>
    <g:else>
        <span class="menuButton" style="color:gray">New Provisioner</span>
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
                <td valign="top" class="name"><g:message code="provisioner.firstName.label" default="First Name"/></td>

                <td valign="top" class="value">${fieldValue(bean: provisioner, field: "firstName")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="provisioner.lastName.label" default="Last Name"/></td>

                <td valign="top" class="value">${fieldValue(bean: provisioner, field: "lastName")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="provisioner.slid.label" default="SLID"/></td>

                <td valign="top" class="value">${fieldValue(bean: provisioner, field: "slid")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="provisioner.email.label" default="Email"/></td>

                <td valign="top" class="value">${fieldValue(bean: provisioner, field: "email")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="provisioner.name.label" default="Name"/></td>

                <td valign="top" class="value">${fieldValue(bean: provisioner, field: "name")}</td>

            </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:if test="${!ConfigurationHolder.config.isEmployeeEditable.toString().equals('false')}">
            <g:form>
                <g:hiddenField name="id" value="${provisioner?.id}"/>
                <g:if test="${care.hasPermission(permission: Permission.UPDATE_PROVISIONER)}">
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}"/></span>
                </g:if>
                <g:else>
                    <span class="button"><input type="button" class="edit" style="color:gray;" value="Edit"/></span>
                </g:else>
                <g:if test="${care.hasPermission(permission: Permission.DELETE_PROVISIONER)}">
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
