<%@ page import="com.force5solutions.care.*; com.force5solutions.care.ldap.Permission" %>
<div id="borbox" style="width:100%;">
    <table id="tablesorter"
            style="width:inherit;" border="0"
            cellpadding="0" cellspacing="0"
            class="scrollTable tablesorter">
        <thead>
        <tr>
            <th>Certification</th>
            <th>Date Completed</th>
            <th>Expiration Date</th>
            <th>Status</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${workerCertifications}" var="workerCertification">
            <tr>
                <td>${workerCertification.certification}</td>
                <td>${workerCertification?.dateCompleted?.myFormat()}</td>
                <td>${workerCertification?.fudgedExpiry?.myFormat()}</td>
                <td>${workerCertification?.currentStatus}</td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
<script type="text/javascript">
    jQuery.each(jQuery('#tablesorter tbody tr'), function() {
        var status = jQuery(this).children().eq(3).text();
        var className;
        switch (status) {
            case 'Completed': className = 'green'; break;
            case 'Expired': className = 'red'; break;
            case 'Pending': className = 'sky'; break;
            default:className = 'yellow';
        }
        jQuery(this).children().eq(3).addClass(className);
    });

    if (jQuery("#tablesorter tr").size() > 1) {
        jQuery("#tablesorter").tablesorter({textExtraction: myTextExtraction,sortList: [[1,0]],headers: {4: {sorter: false}}});

        if (jQuery("#tablesorter tr").size() > 10) {
            jQuery("#tablesorter").Scrollable(205, 792);
        }
    }
</script>