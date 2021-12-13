<table>
    <thead>
    <tr>

        <g:sortableColumn property="firstName"
                          title="${message(code: 'provisioner.firstName.label', default: 'First Name')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="lastName"
                          title="${message(code: 'provisioner.lastName.label', default: 'Last Name')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="slid" title="${message(code: 'provisioner.slid.label', default: 'SLID')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="email" title="${message(code: 'provisioner.email.label', default: 'Email')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="name" title="${message(code: 'provisioner.name.label', default: 'Name')}" params="[max: max, offset: offset]"/>

    </tr>
    </thead>
    <tbody>
    <g:each in="${provisionerList}" status="i" var="provisioner">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

            <td><g:link action="show"
                        id="${provisioner.id}">${fieldValue(bean: provisioner, field: "firstName")}</g:link></td>


            <td><g:link action="show"
                        id="${provisioner.id}">${fieldValue(bean: provisioner, field: "lastName")}</g:link></td>


            <td><g:link action="show"
                        id="${provisioner.id}">${fieldValue(bean: provisioner, field: "slid")}</g:link></td>


            <td><g:link action="show"
                        id="${provisioner.id}">${fieldValue(bean: provisioner, field: "email")}</g:link></td>


            <td><g:link action="show"
                        id="${provisioner.id}">${fieldValue(bean: provisioner, field: "name")}</g:link></td>

        </tr>
    </g:each>
    </tbody>
</table>
<g:if test="${provisionerTotal}">
    <div class="paginateButtons">
        <g:paginate total="${provisionerTotal}" offset="${offset}" max="${max}" params="[order: order, sort: sort]"/>
    </div>
</g:if>

<g:render template="/shared/defaultListViewSizeScript" model="[controller: 'provisioner']"/>