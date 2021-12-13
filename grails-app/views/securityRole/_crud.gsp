<div class="vendor">
    <span>${title}</span>
    <ul>
        <g:render template="/securityRole/permissionCheckbox" model="[permission: createPermission, title: 'Create', role: role]" />
        <g:render template="/securityRole/permissionCheckbox" model="[permission: readPermission, title: 'Read', role: role]" />
        <g:render template="/securityRole/permissionCheckbox" model="[permission: updatePermission, title: 'Update', role: role]" />
        <g:render template="/securityRole/permissionCheckbox" model="[permission: deletePermission, title: 'Delete', role: role]" />
    </ul>
    <div class="clr"></div>
</div>
