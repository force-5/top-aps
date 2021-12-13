<table>
    <thead>
    <tr>

        <g:sortableColumn property="name" title="${message(code: 'configProperty.name.label', default: 'Name')}"/>

        <g:sortableColumn property="value" title="${message(code: 'configProperty.value.label', default: 'Value')}"/>

    </tr>
    </thead>
    <tbody>
    <g:each in="${configPropertyInstanceList}" status="i" var="configPropertyInstance">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

            <td><g:link action="show"
                        id="${configPropertyInstance.id}">${fieldValue(bean: configPropertyInstance, field: "name")}</g:link></td>

            <td><g:link action="show"
                        id="${configPropertyInstance.id}">${fieldValue(bean: configPropertyInstance, field: "value")}</g:link></td>

        </tr>
    </g:each>
    </tbody>
</table>
<g:if test="${configPropertyTotal}">
    <div class="paginateButtons">
        <g:paginate total="${configPropertyTotal}" offset="${offset}" max="${max}"/>
    </div>
</g:if>