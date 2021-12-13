<span class="button">
    <g:if test="${care.hasPermission(permission: permission)}">
        <g:actionSubmit class="delete" action="delete"
                value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
    </g:if>
    <g:else>
        <input type="button" class="delete" style="color:gray;" value="Delete"/>
    </g:else>
</span>
