<table>
    <thead>
    <tr>

        <g:sortableColumn property="firstName"
                          title="${message(code: 'deProvisioner.firstName.label', default: 'First Name')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="lastName"
                          title="${message(code: 'deProvisioner.lastName.label', default: 'Last Name')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="slid" title="${message(code: 'deProvisioner.slid.label', default: 'SLID')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="email" title="${message(code: 'deProvisioner.email.label', default: 'Email')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="name" title="${message(code: 'deProvisioner.name.label', default: 'Name')}" params="[max: max, offset: offset]"/>

    </tr>
    </thead>
    <tbody>
    <g:each in="${deProvisionerList}" status="i" var="deProvisioner">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

            <td><g:link action="show"
                        id="${deProvisioner.id}">${fieldValue(bean: deProvisioner, field: "firstName")}</g:link></td>


            <td><g:link action="show"
                        id="${deProvisioner.id}">${fieldValue(bean: deProvisioner, field: "lastName")}</g:link></td>


            <td><g:link action="show"
                        id="${deProvisioner.id}">${fieldValue(bean: deProvisioner, field: "slid")}</g:link></td>


            <td><g:link action="show"
                        id="${deProvisioner.id}">${fieldValue(bean: deProvisioner, field: "email")}</g:link></td>


            <td><g:link action="show"
                        id="${deProvisioner.id}">${fieldValue(bean: deProvisioner, field: "name")}</g:link></td>

        </tr>
    </g:each>
    </tbody>
</table>

<g:if test="${deProvisionerTotal}">
    <div class="paginateButtons">
        <g:paginate total="${deProvisionerTotal}" offset="${offset}" max="${max}" params="[order: order, sort: sort]"/>
    </div>
</g:if>

<g:render template="/shared/defaultListViewSizeScript" model="[controller: 'deProvisioner']"/>
