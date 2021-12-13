<%@ page import="com.force5solutions.care.ldap.Permission;" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create Security Role</title>
</head>
<body>
<br/>
<div id="wrapper">
    <div id="right-panel">
        <div class="nav">
            <span class="menuButton"><g:link class="home" url="${createLink(uri: '/')}">Home</g:link></span>
            <g:if test="${care.hasPermission(permission: Permission.READ_SECURITY_ROLE)}">
                <span class="menuButton"><g:link class="list" action="list">Security Role List</g:link></span>
            </g:if>
            <g:else>
                <span class="menuButton"><a class="list-disabled">Security Role List</a></span>
            </g:else>
        </div>
        <div class="body">
            <h1>Create Security Role</h1>
            <g:render template="/shared/errors" model="[instance: role]"/>
            <g:form name="securityRoleForm" action="save" method="post">
                <div class="securityForm">
                    <div>
                        <div class="rolesaddscreen-input-text">Name <input type="text" id="name" name="name"
                                class="input6 ${hasErrors(bean: role, field: 'name', 'errors')}"
                                value="${fieldValue(bean: role, field: 'name')}"/></div>
                        <div class="rolesaddscreen-input-text">Description
                            <label>
                                <textarea id="roleDescription" name="roleDescription"
                                        class="input7 ${hasErrors(bean: role, field: 'description', 'errors')}">${fieldValue(bean: role, field: 'description')}</textarea>
                            </label>
                        </div>
                        <div class="rolesaddscreen-input-text">E-mails
                            <label>
                                <textarea id="emails" name="emails" class="input7">${role?.emails}</textarea>
                            </label>
                        </div>
                    </div>

                    <div class="rolesaddscreen-header-text">Permissions For Security Role</div>
                    <div class="check-all-text">
                        <a href="#" onclick="checkAllCheckbox();
                        return false;">Check All</a>  |  <a href="#" onclick="unCheckAllCheckbox();
                    return false;">Uncheck All</a> | <span>Copy Permissions From</span>
                        <g:select name="applicationRole" id="applicationRole"
                                optionKey="id"
                                value="${id}"
                                onchange="getPermissionsForSecurityRole( this.value,'${createLink(action:'getPermissionsForSecurityRole', controller:'securityRole')}')"
                                from="${roles}" optionValue="name"
                                noSelection="['noSelection':'(Select One)']"/>
                    </div>
                    <div id="permissions-div">
                        <g:render template="/securityRole/permissions"/>
                    </div>
                </div>
                <div class="buttons">
                    <span class="button"><input type="submit" class="save" id="submitSecurityRole" value="Save"/></span>
                    <g:if test="${care.hasPermission(permission: Permission.READ_SECURITY_ROLE)}">
                        <span class="button"><g:actionSubmit class="delete" value="Cancel" action="list"/></span>
                    </g:if>
                    <g:else>
                        <span class="button"><input type="button" class="delete" style="color:gray;" value="Cancel"/></span>
                    </g:else>
                </div>
            </g:form>
        </div>
    </div>

</div>
</body>
</html>

