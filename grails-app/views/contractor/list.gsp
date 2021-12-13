<%@ page import="com.force5solutions.care.ldap.Permission" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="contractor"/>
    <title>Contractor List</title>
</head>
<body>
<br/>
<div id="wrapper">
    <g:if test="${flash.message}">
        <div align="center"><b>${flash.message}</b></div>
    </g:if>
    <div id="right-panel">
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
            <g:if test="${care.hasPermission(permission: Permission.CREATE_CONTRACTOR_PROFILE)}">
                <span class="menuButton"><g:link class="create createContractorLink" controller="contractor" action="create">New Contractor</g:link></span>
            </g:if>
            <g:else>
                <span class="menuButton"><a class="create-disabled createContractorLink">New Contractor</a></span>
            </g:else>
        </div>
        <div class="body">
            <h1>Contractor List
                <g:if test="${session.filterContractorCommand}">
                    <g:link class="filterbutton" controller="contractor" action="showAllContractor">
                        <span>Show All</span></g:link>
                </g:if>
                <g:else>
                    <a href="#" class="filterbutton" id="filterButton"><span>Filter</span></a>
                </g:else>
            </h1>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                            <g:sortableColumn property="primeVendor" title="Vendor" defaultOrder="desc"/>
                            <g:sortableColumn property="workerNumber" title="Contractor Number" defaultOrder="desc"/>
                            <g:sortableColumn property="lastName" title="Name" defaultOrder="desc"/>
                            <g:sortableColumn property="badgeNumber" title="Badge Number" defaultOrder="desc"/>
                            <g:sortableColumn property="slid" title="SLID" defaultOrder="desc"/>
                            %{--<th class="sortable"><strong>Status</strong></th>--}%
                        </tr>
                    </thead>
                    <tbody>
                        <g:each in="${contractorInstanceList}" status="i" var="contractor">
                            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                <td><g:link action="show" id="${contractor.id}">${fieldValue(bean: contractor, field: 'primeVendor')}</g:link></td>
                                <td><g:link action="show" id="${contractor.id}">${fieldValue(bean: contractor, field: 'workerNumber')}</g:link></td>
                                <td><g:link action="show" id="${contractor.id}">${fieldValue(bean: contractor, field: 'person')}</g:link></td>
                                <td><g:link action="show" id="${contractor.id}">${fieldValue(bean: contractor, field: 'badgeNumber')}</g:link></td>
                                <td><g:link action="show" id="${contractor.id}">${fieldValue(bean: contractor, field: 'slid')}</g:link></td>
                                %{--<td><care:recentStatusWithoutDate contractorId="${contractor?.id}"/></td>--}%
                            </tr>
                        </g:each>
                    </tbody>
                </table>

            </div>
            <div class="paginateButtons">
                <g:paginate total="${contractorInstanceTotal}" max="10"/>
            </div>
        </div>

        <div id="filterDialog" class="popupWindowContractorFilter">
        </div>

    </div>
</div>
<script type="text/javascript">
    jQuery(document).ready(function() {
        jQuery('#filterButton').click(function() {
            jQuery.post("${createLink(controller:'contractor', action:'filterDialog')}",
            { ajax: 'true'}, function(htmlText) {
                jQuery('#filterDialog').html(htmlText);
            });
            showModalDialog('filterDialog', true);
        });
    });
</script>
</body>
</html>
