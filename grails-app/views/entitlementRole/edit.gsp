<%@ page import="com.force5solutions.care.ldap.Permission; com.force5solutions.care.aps.EntitlementRole" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'entitlementRole.label', default: 'Entitlement Role')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    <g:render template="/permission/listButton" model="[permission: Permission.READ_ENTITLEMENT_ROLE, label: 'Entitlement Role List']"/>
    <g:render template="/permission/createButton" model="[permission: Permission.CREATE_ENTITLEMENT_ROLE, label: 'New Entitlement Role']"/>
</div>
<div class="body">
    <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${entitlementRole}">
        <div class="errors">
            <g:if test="${entitlementRole?.errors?.allErrors?.any{((it.code=='unique') && (it.field=='name'))}}">
                <g:message code="entitlementRole.name.unique.error"/><br/>
                <g:if test="${entitlementRole?.errors?.allErrors?.size() > 1}">
                    <g:message code="blank.field.message"/>
                </g:if>
            </g:if>
            <g:else>
                <g:message code="blank.field.message"/>
            </g:else>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <g:hiddenField name="id" value="${entitlementRole?.id}"/>
        <g:hiddenField name="version" value="${entitlementRole?.version}"/>
        <div class="dialog">
            <g:render template="/entitlementRole/entitlementRoleForm" model="[entitlementRole: entitlementRole, statuses: statuses]"/>
        </div>
        <div class="buttons">
            <span class="button"><g:actionSubmit class="save" action="update"
                    value="${message(code: 'default.button.update.label', default: 'Update')}"/></span>
            <g:render template="/permission/deleteButton" model="[permission: Permission.DELETE_ENTITLEMENT_ROLE]"/>
        </div>
    </g:form>
    <div class="requiredIndicator">
        &nbsp;<span style="color:red;">*</span><g:message code="required.field.text"/></div>
</div>
<script type="text/javascript">
    jQuery(document).ready(function() {
        jQuery('#entitlementPolicySelectBox').attr('disabled', true);
    });
</script>
</body>
</html>
