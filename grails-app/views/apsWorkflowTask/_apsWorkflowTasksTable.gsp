<%@ page import="com.force5solutions.care.workflow.ApsWorkflowType" %>
<table class="tablesorter" id="tablesorter">
    <thead>
    <tr>
        <th>Workflow Type</th>
        <th>Date Created</th>
        <th>Worker</th>
        <th>Entitlement Role</th>
        <th>Entitlement</th>
        <th>Current Node</th>
        <th>Select</th>
    </tr>
    </thead>
    <tbody>
    <g:each in="${tasks}" status="i" var="task">
        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
            <td><g:link action="getUserResponse" id="${task.id}">${task.workflowTypeName}</g:link></td>
            <td><g:link action="getUserResponse"
                        id="${task.id}">${task.dateCreated.myDateTimeFormat()}</g:link></td>
            <td><g:link action="getUserResponse"
                        id="${task?.id}">${task?.worker} ${task?.worker?.slid ? "(" + task?.worker?.slid + ")" : ""}</g:link></td>
            <td class="breakWord"><g:link action="getUserResponse"
                                          id="${task.id}">${task.entitlementRole}</g:link></td>
            <td class="breakWord">
                <g:if test="${task.workflowType in [ApsWorkflowType.CREATE_ENTITLEMENT_EXCEPTION_FROM_FEED, ApsWorkflowType.UPDATE_ENTITLEMENT_EXCEPTION_FROM_FEED]}">
                    <g:link controller="entitlementInfoFromFeed" action="show" id="${task.entitlementInfoFromFeedId}" target="_blank">See details</g:link>
                </g:if>
                <g:else>
                    <g:link action="getUserResponse" id="${task.id}">${task.entitlement}</g:link>
                </g:else>
            </td>
            <td><g:link action="getUserResponse" id="${task.id}">${task.nodeName}</g:link></td>
            <td><g:checkBox name="taskIds" checked="false" value="${task?.id}"
                            class="${task.abbreviatedCodeForGroupResponse}"/>
                <g:if test="${task.isProvisionerTask()}">
                    <img src="../images/plus.png" alt="Provisioner Task" width="14px;">
                </g:if>
                <g:elseif test="${task.isDeprovisionerTask()}">
                    <img src="../images/minus.png" alt="Deprovisioner Task" width="14px;">
                </g:elseif>
            </td>
        </tr>
    </g:each>
    </tbody>
</table>

<script type="text/javascript">
    jQuery(document).ready(function () {
        if (jQuery("#tablesorter tr").size() > 1) {
            jQuery("#tablesorter").tablesorter({textExtraction: myTextExtraction, sortList: [
                [1, 1]
            ], headers: {
                6: {
                    sorter: false
                }}});
        }
        jQuery('#groupResponse').submit(function () {
            if (jQuery(':checkbox:checked').length < 1) {
                alert("Please select any one check-box");
                return false;
            }
        });
        jQuery('#filterButton').click(function () {
            jQuery.post("${createLink(controller:'apsWorkflowTask', action:'filterDialog')}",
                    { ajax: 'true'}, function (htmlText) {
                        jQuery('#filterDialog').html(htmlText);
                    });
            showModalDialog('filterDialog', true);
        });
        jQuery(':checkbox').change(function () {
            if (jQuery(':checked').length < 1) {
                jQuery(':checkbox').removeAttr('disabled');
            } else {
                jQuery(':checkbox').attr('disabled', 'true');
                jQuery("." + jQuery(this).attr("class")).removeAttr('disabled');
            }
        });
    });
</script>