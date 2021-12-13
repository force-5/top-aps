<table border="1px;">
    <thead>
    <tr>
        <g:sortableColumn property="id"
                          title="${message(code: 'apsWorkflowTaskTemplate.id.label', default: 'ID')}"
                          width="200px;" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="messageTemplate"
                          title="${message(code: 'apsWorkflowTaskTemplate.messageTemplate.label', default: 'Message Template')}"
                          width="200px;" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="period"
                          title="${message(code: 'apsWorkflowTaskTemplate.period.label', default: 'Period')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="workflowTaskType"
                          title="${message(code: 'apsWorkflowTaskTemplate.workflowTaskType.label', default: 'Task Type')}"
                          width="20px;" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="escalationTemplate"
                          title="${message(code: 'apsWorkflowTaskTemplate.escalationTemplate.label', default: 'Escalation Template')}" params="[max: max, offset: offset]"/>

    </tr>
    </thead>
    <tbody>
    <g:each in="${filteredTemplateList}" status="i" var="apsWorkflowTaskTemplate">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

            <td><g:link action="show"
                        id="${apsWorkflowTaskTemplate.id}">${apsWorkflowTaskTemplate.id.replaceAll("_", " ")}</g:link></td>

            <td><g:link action="show"
                        id="${apsWorkflowTaskTemplate.id}">${apsWorkflowTaskTemplate?.messageTemplate?.name?.replaceAll("_", " ")}</g:link></td>

            <td><g:link action="show"
                        id="${apsWorkflowTaskTemplate.id}">${apsWorkflowTaskTemplate?.period ? apsWorkflowTaskTemplate?.period + " " + apsWorkflowTaskTemplate?.periodUnit : ""}</g:link></td>

            <td><g:link action="show"
                        id="${apsWorkflowTaskTemplate.id}">${fieldValue(bean: apsWorkflowTaskTemplate, field: "workflowTaskType")}</g:link></td>

            <td><g:link action="show"
                        id="${apsWorkflowTaskTemplate.id}">${apsWorkflowTaskTemplate?.escalationTemplate?.id?.replaceAll("_", " ")}</g:link></td>

        </tr>
    </g:each>
    </tbody>
</table>

<g:if test="${filteredTemplateListCount}">
    <div class="paginateButtons">
        <g:paginate total="${filteredTemplateListCount}" offset="${offset}" max="${max}" params="[order: order, sort: sort]"/>
    </div>
</g:if>

<g:render template="/shared/defaultListViewSizeScript" model="[controller: 'apsWorkflowTaskTemplate']"/>