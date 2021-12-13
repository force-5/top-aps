<%@ page import="com.force5solutions.care.ldap.Permission; com.force5solutions.care.aps.Entitlement" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'entitlement.label', default: 'Entitlement')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>
<body>
<div class="body">
    <h1><g:message code="default.create.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:form action="saveEntitlement" controller="util" method="post">
        <div class="dialog">
            <table>
                <tbody>
                <tr>
                    <td valign="top" width="35%">
                        <label><g:message code="entitlement.name.label" default="Name"/></label>
                    </td>
                    <td valign="top">
                        <g:textField size="48" name="name" value=""/>
                    </td>
                </tr>
                <tr>
                    <td valign="top">
                        <label for="notes"><g:message code="entitlement.notes.label" default="Notes"/></label>
                    </td>
                    <td valign="top">
                        <textarea id="notes" name="notes" cols="" class="area" style="width:300px; height:50px; " rows="3">${fieldValue(bean: entitlement, field: 'notes')}</textarea>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><g:submitButton name="create" class="save"
                    value="${message(code: 'default.button.create.label', default: 'Create')}"/></span>
        </div>
    </g:form>
    <div class="requiredIndicator">
        &nbsp;<span style="color:red;">*</span><g:message code="required.field.text"/></div>
</div>
</body>
</html>
