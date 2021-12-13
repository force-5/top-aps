<%@ page import="com.force5solutions.care.common.CareConstants" %>
<li>
    <div class="checkbox-container">
        <g:checkBox value="${CareConstants.UNRESTRICTED_ACCESS_PERMISSION_LEVEL}" name="${permission}" class="checkbox7"
                checked="${care.isPermissionChecked(role: role, permission: permission, value: CareConstants.UNRESTRICTED_ACCESS_PERMISSION_LEVEL)}"/>
    </div>
    <div class="checkbox-text-container">${title}</div>
</li>
