<%@ page import="com.force5solutions.care.cc.PeriodUnit; com.force5solutions.care.aps.ApsMessageTemplate; com.force5solutions.care.ldap.SecurityRole; com.force5solutions.care.aps.ApsApplicationRole; com.force5solutions.care.workflow.ApsWorkflowTaskType; com.force5solutions.care.workflow.ApsWorkflowTaskTemplate" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName"
           value="${message(code: 'apsWorkflowTaskTemplate.label', default: 'ApsWorkflowTaskTemplate')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
    <script type="text/javascript">
        function updateMultiSelectBox(selectBoxName) {
            jQuery(function() {
                jQuery("#" + selectBoxName + "-select option").each(function(i) {
                    this.value = this.text;
                });
                jQuery('input[name=' + selectBoxName + ']').each(function() {
                    var valueToUse = jQuery(this).parent().text().trim()
                    jQuery(this).val(valueToUse)
                })
            });
        }
    </script>
</head>

<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label"
                                                                           args="[entityName]"/></g:link></span>
</div>

<div class="body">
<h1><g:message code="default.create.label" args="[entityName]"/></h1>
<g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
</g:if>
<g:hasErrors bean="${apsWorkflowTaskTemplate}">
    <div class="errors">
        <g:renderErrors bean="${apsWorkflowTaskTemplate}" as="list"/>
    </div>
</g:hasErrors>
<g:form action="save" method="post">

<div class="dialog">
<table>
<tbody>
<tr class="prop">
    <td valign="top" class="name">
        <label for="templateName">ID</label>
    </td>
    <td valign="top"
        class="value ${hasErrors(bean: apsWorkflowTaskTemplate, field: 'messageTemplate', 'errors')}">
        <g:textField name="templateName"
                     value="${apsWorkflowTaskTemplate.id}" style="width:600px;"/>
    </td>
</tr>
%{----}%
<tr class="prop">
    <td valign="top" class="name">
        <label for="messageTemplate.id"><g:message
                code="apsWorkflowTaskTemplate.messageTemplate.label"
                default="Message Template"/></label>
    </td>
    <td valign="top"
        class="value ${hasErrors(bean: apsWorkflowTaskTemplate, field: 'messageTemplate', 'errors')}">
        <g:select name="messageTemplate.id"
                  from="${ApsMessageTemplate.list()}" optionKey="id"
                  value="${apsWorkflowTaskTemplate?.messageTemplate?.id}"
                  noSelection="['':'(Select One)']" style="width:300px;" class="listbox"/>
    </td>
</tr>
%{----}%
<tr class="prop">
    <td valign="top" class="name">
        <label for="responseForm"><g:message code="apsWorkflowTaskTemplate.responseForm.label"
                                             default="Response Form"/></label>
    </td>
    <td valign="top"
        class="value ${hasErrors(bean: apsWorkflowTaskTemplate, field: 'responseForm', 'errors')}">
        <g:textField name="responseForm" value="${apsWorkflowTaskTemplate?.responseForm}"
                     style="width:290px;"/><br/>
        <span>Ex: revocationUserResponse, gatekeeperResponse, roleAccessRequest, userResponse
        <br/>approveNewRole, approveNewEntitlement, adminAccessVerification</span>
    </td>
</tr>

<tr class="prop">
    <td valign="top" class="name">
        <label for="period"><g:message code="apsWorkflowTaskTemplate.period.label"
                                       default="Period"/></label>
    </td>
    <td valign="top"
        class="value ${hasErrors(bean: apsWorkflowTaskTemplate, field: 'period', 'errors')}">
        <g:textField name="period"
                     value="${fieldValue(bean: apsWorkflowTaskTemplate, field: 'period')}"
                     style="width:290px;"/>
    </td>
</tr>

<tr class="prop">
    <td valign="top" class="name">
        <label for="periodUnit"><g:message code="apsWorkflowTaskTemplate.periodUnit.label"
                                           default="Period Unit"/></label>
    </td>
    <td valign="top"
        class="value ${hasErrors(bean: apsWorkflowTaskTemplate, field: 'periodUnit', 'errors')}">
        <g:select name="periodUnit" from="${PeriodUnit?.values()}"
                  value="${apsWorkflowTaskTemplate?.periodUnit}" noSelection="['':'(Select One)']"
                  style="width:300px;" class="listbox"/>
    </td>
</tr>

<tr class="prop">
    <td valign="top" class="name">
        <label for="workflowTaskType"><g:message
                code="apsWorkflowTaskTemplate.workflowTaskType.label"
                default="Workflow Task Type"/></label>
    </td>
    <td valign="top"
        class="value ${hasErrors(bean: apsWorkflowTaskTemplate, field: 'workflowTaskType', 'errors')}">
        <g:select border="1px;" name="workflowTaskType" from="${ApsWorkflowTaskType?.values()}"
                  noSelection="['':'(Select One)']" value="${apsWorkflowTaskTemplate?.workflowTaskType}"
                  style="width:300px;" class="listbox"/>
    </td>
</tr>

<tr class="prop">
    <td valign="top" class="name">
        <label for="actions">Actions</label>
    </td>
    <td valign="top"
        class="value ${hasErrors(bean: apsWorkflowTaskTemplate, field: 'actions', 'errors')}">
        <g:select name="actions" from="${['APPROVE', 'REJECT', 'CONFIRM']}" multiple="yes" size="3"
                  value="${apsWorkflowTaskTemplate?.actions}" style="width:300px;border: 1px solid #000;"/>
    </td>
</tr>

<tr class="prop">
    <td valign="top" class="name">
        <label for="actorSlids">Actor Slids</label>
    </td>
    <td valign="top"
        class="value ${hasErrors(bean: apsWorkflowTaskTemplate, field: 'actorSlids', 'errors')}">
        <g:textField name="actorSlids"
                     value="${apsWorkflowTaskTemplate?.actorSlids}" style="width:290px;"/>
    </td>
</tr>


<tr class="prop">
    <td valign="top" class="name">
        <label for="toNotificationSlids">to Notification Slids</label>
    </td>
    <td valign="top"
        class="value ${hasErrors(bean: apsWorkflowTaskTemplate, field: 'toNotificationSlids', 'errors')}">
        <g:textField name="toNotificationSlids"
                     value="${apsWorkflowTaskTemplate?.toNotificationSlids}" style="width:290px;"/>
    </td>
</tr>


<tr class="prop">
    <td valign="top" class="name">
        <label for="toNotificationEmails">to Notification Emails</label>
    </td>
    <td valign="top"
        class="value ${hasErrors(bean: apsWorkflowTaskTemplate, field: 'toNotificationEmails', 'errors')}">
        <g:textField name="toNotificationEmails"
                     value="${apsWorkflowTaskTemplate?.toNotificationEmails}" style="width:290px;"/>
    </td>
</tr>

<tr class="prop">
    <td valign="top" class="name">
        <label for="ccNotificationSlids">cc Notification Slids</label>
    </td>
    <td valign="top"
        class="value ${hasErrors(bean: apsWorkflowTaskTemplate, field: 'ccNotificationSlids', 'errors')}">
        <g:textField name="ccNotificationSlids"
                     value="${apsWorkflowTaskTemplate?.ccNotificationSlids}" style="width:290px;"/>
    </td>
</tr>


<tr class="prop">
    <td valign="top" class="name">
        <label for="ccNotificationEmails">cc Notification Emails</label>
    </td>
    <td valign="top"
        class="value ${hasErrors(bean: apsWorkflowTaskTemplate, field: 'ccNotificationEmails', 'errors')}">
        <g:textField name="ccNotificationEmails"
                     value="${apsWorkflowTaskTemplate?.ccNotificationEmails}" style="width:290px;"/>
    </td>
</tr>


<tr class="prop">
    <td valign="top" class="name">
        <label for="actorSecurityRoles">Actor Security Roles</label>
    </td>
    <td valign="top"
        class="value ${hasErrors(bean: apsWorkflowTaskTemplate, field: 'actorSecurityRoles', 'errors')}">
        <div style="width:300px;">
            <ui:multiSelect name="actorSecurityRoles" from="${SecurityRole.list()}"
                            noSelection="['':'(Select One)']"
                            class="listbox" style="width:300px;"
                            multiple="yes" optionKey="id" size="1"
                            value="${apsWorkflowTaskTemplate?.actorSecurityRoles}"/>
        </div>
    </td>
</tr>
<tr class="prop">
    <td valign="top" class="name">
        <label for="actorApplicationRoles">Actor Application Roles</label>
    </td>
    <td valign="top"
        class="value ${hasErrors(bean: apsWorkflowTaskTemplate, field: 'actorApplicationRoles', 'errors')}">
        <div style="width:300px;">
            <ui:multiSelect name="actorApplicationRoles" from="${ApsApplicationRole.list()}"
                            noSelection="['':'(Select One)']"
                            class="listbox" style="width:300px;"
                            multiple="yes" size="1"
                            value="${apsWorkflowTaskTemplate?.actorApplicationRoles}"/>
        </div>
        <script type="text/javascript">
            updateMultiSelectBox('actorApplicationRoles')
        </script>
    </td>
</tr>
<tr class="prop">
    <td valign="top" class="name">
        <label for="toNotificationApplicationRoles">to Notification Application Roles</label>
    </td>
    <td valign="top"
        class="value ${hasErrors(bean: apsWorkflowTaskTemplate, field: 'toNotificationApplicationRoles', 'errors')}">
        <div style="width:300px;">
            <ui:multiSelect name="toNotificationApplicationRoles" from="${ApsApplicationRole.list()}"
                            noSelection="['':'(Select One)']"
                            class="listbox" style="width:300px;"
                            multiple="yes" size="1"
                            value="${apsWorkflowTaskTemplate?.toNotificationApplicationRoles}"/>
        </div>
        <script type="text/javascript">
            updateMultiSelectBox('toNotificationApplicationRoles')
        </script>
    </td>
</tr>

<tr class="prop">
    <td valign="top" class="name">
        <label for="ccNotificationApplicationRoles">cc Notification Application Roles</label>
    </td>
    <td valign="top"
        class="value ${hasErrors(bean: apsWorkflowTaskTemplate, field: 'ccNotificationApplicationRoles', 'errors')}">
        <div style="width:300px;">
            <ui:multiSelect name="ccNotificationApplicationRoles" from="${ApsApplicationRole.list()}"
                            noSelection="['':'(Select One)']"
                            class="listbox" style="width:300px;"
                            multiple="yes" size="1"
                            value="${apsWorkflowTaskTemplate?.ccNotificationApplicationRoles}"/>
        </div>
        <script type="text/javascript">
            updateMultiSelectBox('ccNotificationApplicationRoles')
        </script>
    </td>
</tr>

<tr class="prop">
    <td valign="top" class="name">
        <label for="escalationTemplate.id"><g:message
                code="apsWorkflowTaskTemplate.escalationTemplate.label"
                default="Escalation Template"/></label>
    </td>
    <td valign="top"
        class="value ${hasErrors(bean: apsWorkflowTaskTemplate, field: 'escalationTemplate', 'errors')}">
        <g:select name="escalationTemplate.id"
                  from="${ApsWorkflowTaskTemplate.list()}" style="width: 610px;"
                  optionKey="id" optionValue="id" noSelection="['':'(Select One)']"
                  value="${apsWorkflowTaskTemplate?.escalationTemplate?.id}" class="listbox"/>
    </td>
</tr>

</tbody>
</table>
</div>


<div class="buttons">
    <span class="button"><g:submitButton name="create" class="save"
                                         value="${message(code: 'default.button.create.label', default: 'Create')}"/></span>
       <span class="button"> <g:actionSubmit class="save" value="Cancel" action="list"/></span>
</div>
</g:form>
</div>
</body>
</html>
