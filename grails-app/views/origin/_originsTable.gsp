<table>
    <thead>
    <tr>
        <g:sortableColumn property="name" title="${message(code: 'origin.name.label', default: 'Name')}"
                          params="[max: max, offset: offset]"/>
    </tr>
    </thead>
    <tbody>
    <g:each in="${originList}" status="i" var="origin">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td><g:link action="show" id="${origin.id}">${fieldValue(bean: origin, field: "name")}</g:link></td>
        </tr>
    </g:each>
    </tbody>
</table>
<g:if test="${originTotal}">
    <div class="paginateButtons">
        <g:paginate total="${originTotal}" offset="${offset}" max="${max}" params="[order: order, sort: sort]"/>
    </div>
</g:if>

<g:render template="/shared/defaultListViewSizeScript" model="[controller: 'origin']"/>