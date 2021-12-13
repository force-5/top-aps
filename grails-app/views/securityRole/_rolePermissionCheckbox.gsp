<%@ page import="com.force5solutions.care.common.CareConstants" %>
<div class="sub-container">
    <div class="checkbox-container">
        <g:checkBox value="${permissionValue}" name="${permission}" class="checkbox7 child-check-box"
                checked="${care.isPermissionChecked(role: role, permission: permission, value: permissionValue)}"/>
    </div>
    <div class="checkbox-text-container vendor-sub-text">${title}</div>
    <div class="clr"></div>
</div>
