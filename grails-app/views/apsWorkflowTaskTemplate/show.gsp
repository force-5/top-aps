<%@ page import="com.force5solutions.care.workflow.ApsWorkflowTaskTemplate" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName"
           value="${message(code: 'apsWorkflowTaskTemplate.label', default: 'ApsWorkflowTaskTemplate')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label"
                                                                           args="[entityName]"/></g:link></span>
    <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label"
                                                                               args="[entityName]"/></g:link></span>
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
                <td valign="top" class="name"><g:message code="apsWorkflowTaskTemplate.messageTemplate.label"
                                                         default="ID"/></td>

                <td valign="top" class="value">${apsWorkflowTaskTemplate?.id}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="apsWorkflowTaskTemplate.messageTemplate.label"
                                                         default="Message Template"/></td>

                <td valign="top" class="value"><g:link controller="apsMessageTemplate" action="show"
                                                       id="${apsWorkflowTaskTemplate?.messageTemplate?.id}">${apsWorkflowTaskTemplate?.messageTemplate?.encodeAsHTML()}</g:link></td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="apsWorkflowTaskTemplate.responseForm.label"
                                                         default="Response Form"/></td>

                <td valign="top"
                    class="value">${fieldValue(bean: apsWorkflowTaskTemplate, field: "responseForm")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="apsWorkflowTaskTemplate.period.label"
                                                         default="Period"/></td>

                <td valign="top"
                    class="value">${fieldValue(bean: apsWorkflowTaskTemplate, field: "period")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="apsWorkflowTaskTemplate.periodUnit.label"
                                                         default="Period Unit"/></td>

                <td valign="top" class="value">${apsWorkflowTaskTemplate?.periodUnit?.encodeAsHTML()}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="apsWorkflowTaskTemplate.workflowTaskType.label"
                                                         default="Workflow Task Type"/></td>

                <td valign="top"
                    class="value">${apsWorkflowTaskTemplate?.workflowTaskType?.name?.encodeAsHTML()}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="apsWorkflowTaskTemplate.actions.label"
                                                         default="Actions"/></td>

                <td valign="top" class="value">${apsWorkflowTaskTemplate?.actions?.join(", ")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="apsWorkflowTaskTemplate.slids.label"
                                                         default="Actor Slids"/></td>

                <td valign="top"
                    class="value">${fieldValue(bean: apsWorkflowTaskTemplate, field: "actorSlids")}</td>

            </tr>



            <tr class="prop">
                <td valign="top" class="name"><g:message code="apsWorkflowTaskTemplate.slids.label"
                                                         default="to Notification Slids"/></td>

                <td valign="top"
                    class="value">${fieldValue(bean: apsWorkflowTaskTemplate, field: "toNotificationSlids")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="apsWorkflowTaskTemplate.slids.label"
                                                         default="cc Notification Slids"/></td>

                <td valign="top"
                    class="value">${fieldValue(bean: apsWorkflowTaskTemplate, field: "ccNotificationSlids")}</td>

            </tr>
            <tr class="prop">
                <td valign="top" class="name"><g:message code="apsWorkflowTaskTemplate.slids.label"
                                                         default="to Notification Emails"/></td>

                <td valign="top"
                    class="value">${fieldValue(bean: apsWorkflowTaskTemplate, field: "toNotificationEmails")}</td>

            </tr>
            <tr class="prop">
                <td valign="top" class="name"><g:message code="apsWorkflowTaskTemplate.slids.label"
                                                         default="cc Notification Emails"/></td>

                <td valign="top"
                    class="value">${fieldValue(bean: apsWorkflowTaskTemplate, field: "ccNotificationEmails")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="apsWorkflowTaskTemplate.escalationTemplate.label"
                                                         default="Escalation Template"/></td>

                <td valign="top" class="value"><g:link controller="apsWorkflowTaskTemplate" action="show"
                                                       id="${apsWorkflowTaskTemplate?.escalationTemplate?.id}">${apsWorkflowTaskTemplate?.escalationTemplate?.id?.encodeAsHTML()}</g:link></td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="apsWorkflowTaskTemplate.applicationRoles.label"
                                                         default="Application Roles"/></td>

                <td valign="top"
                    class="value">${apsWorkflowTaskTemplate?.actorApplicationRoles?.join(", ")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="apsWorkflowTaskTemplate.securityRoles.label"
                                                         default="Security Roles"/></td>

                <td valign="top" style="text-align: left;" class="value">
                    <ul>
                        <g:each in="${apsWorkflowTaskTemplate.actorSecurityRoles}" var="s">
                            <li><g:link controller="securityRole" action="show"
                                        id="${s.id}">${s?.encodeAsHTML()}</g:link></li>
                        </g:each>
                    </ul>
                </td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name">Actor Application Roles</td>

                <td valign="top" style="text-align: left;" class="value">
                    <ul>
                        <g:each in="${apsWorkflowTaskTemplate?.actorApplicationRoles}" var="s">
                            <li>${s?.encodeAsHTML()}</li>
                        </g:each>
                    </ul>
                </td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name">to Notification Application Roles</td>

                <td valign="top" style="text-align: left;" class="value">
                    <ul>
                        <g:each in="${apsWorkflowTaskTemplate?.toNotificationApplicationRoles}" var="s">
                            <li>${s?.encodeAsHTML()}</li>
                        </g:each>
                    </ul>
                </td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name">cc Notification Application Roles</td>

                <td valign="top" style="text-align: left;" class="value">
                    <ul>
                        <g:each in="${apsWorkflowTaskTemplate?.ccNotificationApplicationRoles}" var="s">
                            <li>${s?.encodeAsHTML()}</li>
                        </g:each>
                    </ul>
                </td>

            </tr>

            </tbody>
        </table>
    </div>

    <div class="buttons">
        <g:form>
            <g:hiddenField name="id" value="${apsWorkflowTaskTemplate?.id}"/>
            <span class="button"><g:actionSubmit class="edit" action="edit"
                                                 value="${message(code: 'default.button.edit.label', default: 'Edit')}"/></span>
            <span class="button"><g:actionSubmit class="delete" action="delete"
                                                 value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                                                 onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
              <span class="button"><g:actionSubmit class="edit" action="cloneTemplate"
                                                 value="Clone Template"/></span>
        </g:form>
    </div>
</div>

</body>
</html>
