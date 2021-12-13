<%@ page import="com.force5solutions.care.cc.EntitlementPolicy" %>
<table>
    <thead>
    <tr>
        <g:sortableColumn property="name" title="Name" params="[max: max, offset: offset]"/>
        <g:sortableColumn property="alias" title="Alias" params="[max: max, offset: offset]"/>
        <g:sortableColumn property="isExposed" title="Exposed in CARE Central" params="[max: max, offset: offset]"/>
        <g:sortableColumn property="status" title="Status" params="[max: max, offset: offset]"/>
        <g:sortableColumn property="origin" title="Origin" params="[max: max, offset: offset]"/>
        <g:sortableColumn property="type" title="Entitlement Policy" params="[max: max, offset: offset]"/>
        <g:sortableColumn property="owner" title="Owner" params="[max: max, offset: offset]"/>
    </tr>
    </thead>
    <tbody>
    <g:each in="${entitlementList}" status="i" var="entitlement">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td class="breakWord"><versionable:hasUnapprovedChanges object="${entitlement}"><span
                    class="asterisk">*</span></versionable:hasUnapprovedChanges>
                <g:link action="show"
                        id="${entitlement.id}">${fieldValue(bean: entitlement, field: "name")}</g:link></td>
            <td class="breakWord"><g:link action="show"
                                          id="${entitlement.id}">${fieldValue(bean: entitlement, field: "alias")}</g:link></td>
            <td><g:link action="show"
                        id="${entitlement.id}">${fieldValue(bean: entitlement, field: "isExposed") ? "Yes" : "No"}</g:link></td>
            <td><g:link action="show"
                        id="${entitlement.id}">${fieldValue(bean: entitlement, field: "status")}</g:link></td>
            <td><g:link action="show"
                        id="${entitlement.id}">${fieldValue(bean: entitlement, field: "origin")}</g:link></td>
            <td><g:link action="show"
                        id="${entitlement.id}">${EntitlementPolicy.findById(entitlement.type).name}</g:link></td>
            <td><g:link action="show"
                        id="${entitlement.id}">${fieldValue(bean: entitlement, field: "owner")}</g:link></td>
        </tr>
    </g:each>
    </tbody>
</table>
<g:if test="${entitlementTotal}">
    <div class="paginateButtons">
        <g:paginate total="${entitlementTotal}" offset="${offset}" max="${max}" params="[order: order, sort: sort]"/>
    </div>
</g:if>
<script type="text/javascript">
    jQuery(document).ready(function () {
        jQuery('#filterButton').click(function () {
            jQuery.post("${createLink(controller:'entitlement', action:'filterDialog')}",
                    { ajax:'true'}, function (htmlText) {
                        jQuery('#filterDialog').html(htmlText);
                    });
            showModalDialog('filterDialog', true);
        });
    });
</script>

<g:render template="/shared/defaultListViewSizeScript" model="[controller: 'entitlement']"/>