<table>
    <thead>
    <tr>
        <g:sortableColumn property="name" title="Name"/>
        <g:sortableColumn property="value" title="Description"/>
    </tr>
    </thead>
    <tbody>
    <g:each in="${roles}" status="i" var="role">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td><g:link action="show" id="${role.id}">${fieldValue(bean: role, field: 'name')}</g:link></td>
            <td><g:link action="show" id="${role.id}">${fieldValue(bean: role, field: 'description')}</g:link></td>
        </tr>
    </g:each>
    </tbody>
</table>

<g:if test="${securityRoleTotal}">
    <div class="paginateButtons">
        <g:paginate total="${securityRoleTotal}" offset="${offset}" max="${max}"/>
    </div>
</g:if>