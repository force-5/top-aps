
<%@ page import="com.force5solutions.care.ldap.Permission; com.force5solutions.care.aps.Origin" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'origin.label', default: 'Origin')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
            <g:render template="/permission/listButton" model="[permission: Permission.READ_ORIGIN, label: 'Origin List']" />
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:render template="/shared/errors" model="[instance: origin]"/>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name"><g:message code="origin.name.label" default="Name" />&nbsp;<span class="asterisk">*</span></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: origin, field: 'name', 'errors')}">
                                    <g:textField name="name" value="${origin?.name}" />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:form>
             <div class="requiredIndicator">
                    &nbsp;<span style="color:red;">*</span><g:message code="required.field.text"/></div>
        </div>
    </body>
</html>
