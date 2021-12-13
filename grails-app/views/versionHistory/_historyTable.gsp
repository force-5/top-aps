<br/>
<div id="history">History</div>

<div id="tableContainer" class="tableContainer">
    <table border="0" cellpadding="0" cellspacing="0" width="100%" class="tablesorter" id="tablesorter">
        <thead class="fixedHeader">
            <tr>
                <th>Type</th>
                <th>Date</th>
                <th>User ID</th>
                <th>Description</th>
            </tr>
        </thead>
        <tbody>
            <g:each in="${histories}" var="history">
                <g:if test="${history.showChanges}">
                        <tr>
                            <td>
                                <g:remoteLink controller="versionHistory" action="showHistory" onSuccess="showHistoryPopup(e)" id="${history.id}">
                                    ${history.type}
                                </g:remoteLink>
                            </td>
                            <td>
                                <g:remoteLink controller="versionHistory" action="showHistory" onSuccess="showHistoryPopup(e)" id="${history.id}">
                                    ${history.date?.myDateTimeFormat()}
                                </g:remoteLink>
                            </td>
                            <td>
                                <g:remoteLink controller="versionHistory" action="showHistory" onSuccess="showHistoryPopup(e)" id="${history.id}">
                                    ${history.userId}
                                </g:remoteLink>
                            </td>
                            <td>
                                <g:remoteLink controller="versionHistory" action="showHistory" onSuccess="showHistoryPopup(e)" id="${history.id}">
                                    ${history.description}
                                </g:remoteLink>
                            </td>
                        </tr>
                </g:if>
                <g:else>
                    <tr>
                        <td>${history.type}</td>
                        <td>${history.date?.myDateTimeFormat()}</td>
                        <td>${history.userId}</td>
                        <td>${history.description}</td>
                    </tr>
                </g:else>
            </g:each>

        </tbody>
    </table>

</div>
<span id="workerHistoryPopup" style="display:none;"></span>
<script type="text/javascript">
    jQuery(document).ready(function() {
        if (jQuery("#tablesorter tr").size() > 1) {
            jQuery("#tablesorter").tablesorter({textExtraction: myTextExtraction});

            if (jQuery("#tablesorter tr").size() > 10) {
                jQuery("#tablesorter").Scrollable(205, 792);
            }
        }
    });
</script>
