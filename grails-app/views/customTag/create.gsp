
<%@ page import="com.force5solutions.care.common.CustomTag" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'customTag.label', default: 'CustomTag')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${customTag}">
            <div class="errors">
                <g:renderErrors bean="${customTag}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name"><g:message code="customTag.name.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: customTag, field: 'name', 'errors')}">
                                    <g:textField name="name" value="${customTag?.name}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="displayValue"><g:message code="customTag.displayValue.label" default="Display Value" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: customTag, field: 'displayValue', 'errors')}">
                                    <g:textField name="displayValue" value="${customTag?.displayValue}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="value"><g:message code="customTag.value.label" default="Value" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: customTag, field: 'value', 'errors')}">
                                    <g:textArea name="value" cols="40" rows="5" value="${customTag?.value}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="dummyData"><g:message code="customTag.dummyData.label" default="Dummy Data" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: customTag, field: 'dummyData', 'errors')}">
                                    <g:textArea name="dummyData" cols="40" rows="5" value="${customTag?.dummyData}" />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
