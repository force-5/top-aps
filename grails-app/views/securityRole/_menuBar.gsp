<%@ page import="com.force5solutions.care.ldap.Permission; com.force5solutions.care.common.CareConstants;" %>
<div class="vendor">
    <span>Menus</span>
    <ul>
        <g:render template="/securityRole/permissionCheckbox" model="[permission: 'CAN_ACCESS_ENTITLEMENT_ROLE_MENU', title: 'Entitlement Roles', role: role]" />
        <g:render template="/securityRole/permissionCheckbox" model="[permission: 'CAN_ACCESS_ENTITLEMENT_MENU', title: 'Entitlements', role: role]" />
        <g:render template="/securityRole/permissionCheckbox" model="[permission: 'CAN_ACCESS_REPORTS_MENU', title: 'Reports', role: role]" />
        <g:render template="/securityRole/permissionCheckbox" model="[permission: 'CAN_ACCESS_ADMIN_MENU', title: 'Admin', role: role]" />
    </ul>
    <div class="clr"></div>
</div>
