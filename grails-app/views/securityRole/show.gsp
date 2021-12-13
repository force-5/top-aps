<%@ page import="com.force5solutions.care.ldap.Permission;" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show Security Role</title>
</head>

<body>
<br/>

<div id="wrapper">
    <div id="right-panel">
        <div class="nav">
            <span class="menuButton"><g:link class="home" url="${createLink(uri: '/')}">Home</g:link></span>
            <span class="menuButton"><g:link class="list" action="list">Security Role List</g:link></span>
            <g:if test="${care.hasPermission(permission: Permission.CREATE_SECURITY_ROLE)}">
                <span class="menuButton"><g:link class="create" action="create">New Security Role</g:link></span>
            </g:if>
            <g:else>
                <span class="menuButton"><a class="create-disabled">New Security Role</a></span>
            </g:else>
        </div>

        <div class="body">
            <h1>Show Security Role</h1>
            <g:form name="securityRoleForm" action="edit" method="post" id="${role.id}">
                <div class="securityForm">
                    <table>
                        <tbody>

                        <tr class="prop">
                            <td valign="top" class="name1">Name</td>
                            <td valign="top" class="value1">${fieldValue(bean: role, field: 'name')}</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name1">Description</td>
                            <td valign="top" class="value1">${fieldValue(bean: role, field: 'description')}</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name1">Emails</td>
                            <td valign="top" class="value1">${fieldValue(bean: role, field: 'emails')}</td>
                        </tr>

                        </tbody>
                    </table>
                    <br/>

                    <div class="rolesaddscreen-header-text">Permissions For Security Role</div>
                    <g:render template="/securityRole/permissions" model="[role: role]"/>
                </div>

                <div class="buttons">
                    <g:if test="${care.hasPermission(permission: Permission.UPDATE_SECURITY_ROLE)}">
                        <span class="button"><input type="submit" class="edit editSecurityRoleLink" value="Edit"/>
                        </span>
                    </g:if>
                    <g:else>
                        <span class="button"><input type="button" class="edit" style="color:gray;" value="Edit"/></span>
                    </g:else>
                    <g:if test="${care.hasPermission(permission: Permission.DELETE_SECURITY_ROLE)}">
                        <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');"
                                                             value="Delete" action="delete"/></span>
                    </g:if>
                    <g:else>
                        <span class="button"><input type="button" class="delete" style="color:gray;" value="Delete"/>
                        </span>
                    </g:else>
                </div>
            </g:form>
        </div>
    </div>
</div>
<script type="text/javascript">
    jQuery(document).ready(function () {
        jQuery(':checkbox').attr('disabled', true)
    });
</script>
</body>
</html>
