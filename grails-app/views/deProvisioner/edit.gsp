<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder;" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'deProvisioner.label', default: 'Deprovisioner')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></span>
    <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></span>
</div>
<div class="body">
    <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${deProvisioner}">
        <div class="errors">
            <g:renderErrors bean="${deProvisioner}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <g:hiddenField name="id" value="${deProvisioner?.id}"/>
        <g:hiddenField name="version" value="${deProvisioner?.version}"/>
        <div class="dialog">
            <table>
                <tbody>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="person.firstName"><g:message code="deProvisioner.firstName.label" default="First Name"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: deProvisioner, field: 'person.firstName', 'errors')}">
                        <g:textField id="firstName" name="person.firstName" value="${deProvisioner?.firstName}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="person.lastName"><g:message code="deProvisioner.lastName.label" default="Last Name"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: deProvisioner, field: 'person.lastName', 'errors')}">
                        <g:textField id="lastName" name="person.lastName" value="${deProvisioner?.lastName}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="person.slid"><g:message code="deProvisioner.slid.label" default="SLID"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: deProvisioner, field: 'person.slid', 'errors')}">
                        <g:textField id="slid" name="person.slid" value="${deProvisioner?.slid}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="email"><g:message code="deProvisioner.email.label" default="Email"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: deProvisioner, field: 'email', 'errors')}">

                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="name"><g:message code="deProvisioner.name.label" default="Name"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: deProvisioner, field: 'name', 'errors')}">

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
