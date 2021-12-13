<table>
    <thead>
    <tr>

        <g:sortableColumn property="firstName"
                          title="${message(code: 'roleOwner.firstName.label', default: 'First Name')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="lastName"
                          title="${message(code: 'roleOwner.lastName.label', default: 'Last Name')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="slid" title="${message(code: 'roleOwner.slid.label', default: 'Slid')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="email" title="${message(code: 'roleOwner.email.label', default: 'Email')}" params="[max: max, offset: offset]"/>

        <g:sortableColumn property="name" title="${message(code: 'roleOwner.name.label', default: 'Name')}" params="[max: max, offset: offset]"/>

    </tr>
    </thead>
    <tbody>
    <g:each in="${roleOwnerList}" status="i" var="roleOwner">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

            <td><g:link action="show"
                        id="${roleOwner.id}">${fieldValue(bean: roleOwner, field: "firstName")}</g:link></td>


            <td><g:link action="show"
                        id="${roleOwner.id}">${fieldValue(bean: roleOwner, field: "lastName")}</g:link></td>


            <td><g:link action="show" id="${roleOwner.id}">${fieldValue(bean: roleOwner, field: "slid")}</g:link></td>


            <td><g:link action="show" id="${roleOwner.id}">${fieldValue(bean: roleOwner, field: "email")}</g:link></td>


            <td><g:link action="show" id="${roleOwner.id}">${fieldValue(bean: roleOwner, field: "name")}</g:link></td>

        </tr>
    </g:each>
    </tbody>
</table>
<g:if test="${roleOwnerTotal}">
    <div class="paginateButtons">
        <g:paginate total="${roleOwnerTotal}" offset="${offset}" max="${max}" params="[order: order, sort: sort]"/>
    </div>
</g:if>

<g:render template="/shared/defaultListViewSizeScript" model="[controller: 'roleOwner']"/>