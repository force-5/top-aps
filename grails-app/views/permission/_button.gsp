<span class="menuButton">
    <g:if test="${care.hasPermission(permission: permission)}">
        <g:link class="${type}" action="${type}">${label}</g:link>
    </g:if>
    <g:else>
        <a class="${type}-disabled">${label}</a>
    </g:else>
</span>




