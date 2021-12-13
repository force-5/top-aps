<table>
    <thead>
    <tr>
        <g:sortableColumn property="name" title="Name" params="[max: max, offset: offset]"/>
        <g:sortableColumn property="subjectTemplate" title="Subject Template" params="[max: max, offset: offset]"/>
    </tr>
    </thead>
    <tbody>
    <g:each in="${apsMessageTemplateList}" status="i" var="messageTemplate">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td>
                <g:link action="show" id="${messageTemplate.id}">
                    ${messageTemplate?.name}
                </g:link>
            </td>
            <td>
                <g:link action="show" id="${messageTemplate.id}">
                    ${messageTemplate?.subjectTemplate}
                </g:link>
            </td>
        </tr>
    </g:each>
    </tbody>
</table>

<g:if test="${apsMessageTemplateTotal}">
    <div class="paginateButtons">
        <g:paginate total="${apsMessageTemplateTotal}" offset="${offset}" max="${max}" params="[order: order, sort: sort]"/>
    </div>
</g:if>

<g:render template="/shared/defaultListViewSizeScript" model="[controller: 'apsMessageTemplate']"/>
