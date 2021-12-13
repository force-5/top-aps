<table>
    <thead>
    <tr>

        <g:sortableColumn property="name" title="${message(code: 'customTag.name.label', default: 'Name')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="displayValue"
                          title="${message(code: 'customTag.displayValue.label', default: 'Display Value')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="value" title="${message(code: 'customTag.value.label', default: 'Value')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="dummyData"
                          title="${message(code: 'customTag.dummyData.label', default: 'Dummy Data')}" params="[max: max, offset: offset]"/>

    </tr>
    </thead>
    <tbody>
    <g:each in="${customTagList}" status="i" var="customTag">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

            <td><g:link action="show" id="${customTag.id}">${fieldValue(bean: customTag, field: "name")}</g:link></td>


            <td class="breakWord"><g:link action="show"
                        id="${customTag.id}">${fieldValue(bean: customTag, field: "displayValue")}</g:link></td>


            <td class="breakWord"><g:link action="show" id="${customTag.id}">${fieldValue(bean: customTag, field: "value")}</g:link></td>


            <td class="breakWord"><g:link action="show"
                        id="${customTag.id}">${fieldValue(bean: customTag, field: "dummyData")}</g:link></td>

        </tr>
    </g:each>
    </tbody>
</table>

<g:if test="${customTagTotal}">
    <div class="paginateButtons">
        <g:paginate total="${customTagTotal}" offset="${offset}" max="${max}" params="[order: order, sort: sort]"/>
    </div>
</g:if>

<g:render template="/shared/defaultListViewSizeScript" model="[controller: 'customTag']"/>

