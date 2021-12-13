<span class="button">
    <g:if test="${care.hasPermission(permission: permission)}">
        <g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}"/>
    </g:if>
    <g:else>
        <input type="button" class="edit" style="color:gray;" value="Edit"/>
    </g:else>
</span>
