
<%@ page import="com.force5solutions.care.ldap.Permission; com.force5solutions.care.aps.Origin" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'origin.label', default: 'Origin')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
            <g:render template="/permission/listButton" model="[permission: Permission.READ_ORIGIN, label: 'Origin List']" />
            <g:render template="/permission/createButton" model="[permission: Permission.CREATE_ORIGIN, label: 'New Origin']" />
        </div>
        <div class="body">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="origin.name.label" default="Name" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: origin, field: "name")}</td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${origin?.id}" />
                    <g:render template="/permission/editButton" model="[permission: Permission.UPDATE_ORIGIN]"/>
                    <g:render template="/permission/deleteButton" model="[permission: Permission.DELETE_ENTITLEMENT]"/>
                </g:form>
            </div>
        </div>
    </body>
</html>
