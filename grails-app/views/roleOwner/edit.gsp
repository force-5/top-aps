<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder; com.force5solutions.care.aps.RoleOwner" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'roleOwner.label', default: 'RoleOwner')}"/>
    <title>Edit Role Owner</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Role Owner List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New Role Owner</g:link></span>
</div>
<div class="body">
    <h1>Edit Role Owner</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${roleOwner}">
        <div class="errors">
            <g:renderErrors bean="${roleOwner}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <g:hiddenField name="id" value="${roleOwner?.id}"/>
        <g:hiddenField name="version" value="${roleOwner?.version}"/>
        <div class="dialog">
            <table>
                <tbody>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="person.firstName"><g:message code="roleOwner.firstName.label" default="First Name"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: roleOwner, field: 'firstName', 'errors')}">
                        <g:textField id="firstName" name="person.firstName" value="${roleOwner?.firstName}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="person.lastName"><g:message code="roleOwner.lastName.label" default="Last Name"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: roleOwner, field: 'lastName', 'errors')}">
                        <g:textField id="lastName" name="person.lastName" value="${roleOwner?.lastName}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="person.slid"><g:message code="roleOwner.slid.label" default="SLID"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: roleOwner, field: 'slid', 'errors')}">
                        <g:textField id="slid" name="person.slid" value="${roleOwner?.slid}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="email"><g:message code="roleOwner.email.label" default="Email"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: roleOwner, field: 'email', 'errors')}">

                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="name"><g:message code="roleOwner.name.label" default="Name"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: roleOwner, field: 'name', 'errors')}">

                    </td>
                </tr>

                </tbody>
            </table>
        </div>
        <div class="buttons">
            <g:if test="${!ConfigurationHolder.config.isEmployeeEditable.toString().equals('false')}">
                <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}"/></span>
                <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
            </g:if>
        </div>
    </g:form>
</div>
</body>
</html>
