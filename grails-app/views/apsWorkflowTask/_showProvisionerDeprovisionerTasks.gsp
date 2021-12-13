<g:if test="${taskVOs}">
    <div style="margin-top: 20px;">
        <h3>Provisioner/De-provisioner Tasks</h3>
        <table class="tablesorter" id="tablesorter1">
            <thead>
            <tr>
                <th>Worker</th>
                <th>Role</th>
                <th>Entitlement</th>
                <th>Type</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${taskVOs}" status="i" var="taskVO">
                <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                    <td>${taskVO.workerName}</td>
                    <td>${taskVO.entitlementRoleName}</td>
                    <td>${taskVO.entitlementName}</td>
                    <td>${taskVO.type}</td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>

    <script type="text/javascript">
        jQuery(document).ready(function () {
            if (jQuery("#tablesorter1 tr").size() > 1) {
                jQuery("#tablesorter1").tablesorter({textExtraction:myTextExtraction, sortList:[
                    [1, 1]
                ], headers:{
                    6:{
                        sorter:false
                    }}});
            }
        });
    </script>
</g:if>