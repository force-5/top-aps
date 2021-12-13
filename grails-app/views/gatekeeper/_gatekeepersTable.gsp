<table>
    <thead>
    <tr>

        <g:sortableColumn property="firstName"
                          title="${message(code: 'gatekeeper.firstName.label', default: 'First Name')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="lastName"
                          title="${message(code: 'gatekeeper.lastName.label', default: 'Last Name')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="slid" title="${message(code: 'gatekeeper.slid.label', default: 'Slid')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="email" title="${message(code: 'gatekeeper.email.label', default: 'Email')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="name" title="${message(code: 'gatekeeper.name.label', default: 'Name')}" params="[max: max, offset: offset]"/>

    </tr>
    </thead>
    <tbody>
    <g:each in="${gatekeeperList}" status="i" var="gatekeeper">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

            <td><g:link action="show"
                        id="${gatekeeper.id}">${fieldValue(bean: gatekeeper, field: "firstName")}</g:link></td>


            <td><g:link action="show"
                        id="${gatekeeper.id}">${fieldValue(bean: gatekeeper, field: "lastName")}</g:link></td>


            <td><g:link action="show" id="${gatekeeper.id}">${fieldValue(bean: gatekeeper, field: "slid")}</g:link></td>


            <td><g:link action="show"
                        id="${gatekeeper.id}">${fieldValue(bean: gatekeeper, field: "email")}</g:link></td>


            <td><g:link action="show" id="${gatekeeper.id}">${fieldValue(bean: gatekeeper, field: "name")}</g:link></td>

        </tr>
    </g:each>
    </tbody>
</table>

<g:if test="${gatekeeperTotal}">
    <div class="paginateButtons">
        <g:paginate total="${gatekeeperTotal}" offset="${offset}" max="${max}" params="[order: order, sort: sort]"/>
    </div>
</g:if>

<g:render template="/shared/defaultListViewSizeScript" model="[controller: 'gatekeeper']"/>