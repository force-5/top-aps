<%@ page import="com.force5solutions.care.ldap.Permission; com.force5solutions.care.aps.Entitlement" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'entitlement.label', default: 'Entitlement')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    <g:render template="/permission/listButton"
              model="[permission: Permission.READ_ENTITLEMENT, label: 'Entitlement List']"/>
    <g:render template="/permission/createButton"
              model="[permission: Permission.CREATE_ENTITLEMENT, label: 'New Entitlement']"/>
</div>

<div class="body">
    <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:if test="${entitlement?.hasErrors() || missingValueCustomProperties}">
        <div class="errors">
            <g:message code="blank.field.message"/>
        </div>
    </g:if>
    <g:form method="post">
        <g:hiddenField name="id" value="${entitlement?.id}"/>
        <g:hiddenField name="version" value="${entitlement?.version}"/>
        <div class="dialog">
            <g:render template="/entitlement/entitlementForm" model="[entitlement: entitlement]"/>
        </div>

        <div class="buttons">
            <span class="button"><g:actionSubmit class="save" action="update"
                                                 value="${message(code: 'default.button.update.label', default: 'Update')}"/></span>
            <g:render template="/permission/deleteButton" model="[permission: Permission.DELETE_ENTITLEMENT]"/>
        </div>
    </g:form>
    <div class="requiredIndicator">
        &nbsp;<span style="color:red;">*</span><g:message code="required.field.text"/></div>
</div>
<script type="text/javascript">
    var data =${missingValueCustomProperties*.id};
    jQuery.each(data, function () {
        jQuery('#custom-property-textbox-' + this).css('border', '1px solid red');
    });
</script>
</body>
</html>
