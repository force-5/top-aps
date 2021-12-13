<table>
    <thead>
    <tr>
        <g:sortableColumn property="name" title="${message(code: 'entitlementRole.name.label', default: 'Name')}" params="[max: max, offset: offset]"/>
        <g:sortableColumn property="isExposed" title="${message(code: 'isExposed.label', default: 'Exposed')}" params="[max: max, offset: offset]"/>
        <g:sortableColumn property="status"
                          title="${message(code: 'entitlementRole.status.label', default: 'Status')}" params="[max: max, offset: offset]"/>
        <g:sortableColumn property="owner" title="${message(code: 'entitlementRole.owner.label', default: 'Owner')}" params="[max: max, offset: offset]"/>
    </tr>
    </thead>
    <tbody>
    <g:each in="${entitlementRoleList}" status="i" var="entitlementRole">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td class="breakWord"><versionable:hasUnapprovedChanges object="${entitlementRole}"><span
                    class="asterisk">*</span></versionable:hasUnapprovedChanges>
                <g:link action="show"
                        id="${entitlementRole.id}">${fieldValue(bean: entitlementRole, field: "name")}</g:link></td>
            <td><g:link action="show"
                        id="${entitlementRole.id}">${(entitlementRole?.isExposed) ? 'Yes' : 'No'}</g:link></td>
            <td><g:link action="show"
                        id="${entitlementRole.id}">${fieldValue(bean: entitlementRole, field: "status")}</g:link></td>
            <td><g:link action="show"
                        id="${entitlementRole.id}">${fieldValue(bean: entitlementRole, field: "owner")}</g:link></td>
        </tr>
    </g:each>
    </tbody>
</table>

<g:if test="${entitlementRoleTotal}">
    <div class="paginateButtons">
        <g:paginate total="${entitlementRoleTotal}" offset="${offset}" max="${max}" params="[order: order, sort: sort]"/>
    </div>
</g:if>

<script type="text/javascript">
    jQuery(document).ready(function() {
        jQuery('#filterButton').click(function() {
            jQuery.post("${createLink(controller:'entitlementRole', action:'filterDialog')}",
                    { ajax: 'true'}, function(htmlText) {
                        jQuery('#filterDialog').html(htmlText);
                    });
            showModalDialog('filterDialog', true);
        });
    });
</script>

<g:render template="/shared/defaultListViewSizeScript" model="[controller: 'entitlementRole']"/>