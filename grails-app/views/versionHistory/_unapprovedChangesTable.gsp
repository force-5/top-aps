<br/>
<div id="history">Unapproved Changes</div>

<div id="tableContainer" class="tableContainer">
    <table border="0" cellpadding="0" cellspacing="0" width="100%" class="tablesorter" id="tablesorter">
        <thead class="fixedHeader">
        <tr>
            <th>Date</th>
            <th>User ID</th>
            <th>Property Name</th>
            <th>Old Value</th>
            <th>New Value</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${changes}" var="change">
                <tr>
                    <td>${change.dateCreated?.myDateTimeFormat()}</td>
                    <td>${change.userId}</td>
                    <td>${change.propertyNaturalName}</td>
                    <td>${change.oldValue}</td>
                    <td>${change.newValue}</td>
                </tr>
        </g:each>

        </tbody>
    </table>

</div>
<span id="workerChangePopup" style="display:none;">test</span>
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
