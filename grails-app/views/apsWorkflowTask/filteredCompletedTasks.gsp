<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Completed Tasks</title>
</head>

<body>
<br/>

<div id="wrapper">
    <div id="right-panel">
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
            <a class="filterbutton" href="${createLink(action: 'list')}"><span>Pending</span></a>
            <a class="filterbutton" href="${createLink(action: 'filterCompletedTasks')}"><span>Completed</span></a>
        </div>

        <div class="body">
            <g:if test="${tasks}">
                <h1>Completed Tasks</h1>
                <g:if test="${flash.message}">
                    <div class="message">${flash.message}</div>
                </g:if>
                <div class="list">
                    <g:form action="storeAndAttachEvidence" name="groupResponse" id="groupResponse" method="post" enctype="multipart/form-data">
                        <table class="tablesorter" id="tablesorter">
                            <thead>
                            <tr>
                                <th>Workflow Type</th>
                                <th>Date Created</th>
                                <th>Worker</th>
                                <th>Entitlement Role</th>
                                <th>Entitlement</th>
                                <th>Node Name</th>
                                <th>Select</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${tasks}" status="i" var="task">
                                <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                                    <td><g:link action="getUserResponse" id="${task.id}">${task?.workflowType}</g:link></td>
                                    <td><g:link action="getUserResponse" id="${task.id}">${task?.dateCreated?.myDateTimeFormat()}</g:link></td>
                                    <td><g:link action="getUserResponse" id="${task.id}">${task?.worker}</g:link></td>
                                    <td><g:link action="getUserResponse" id="${task.id}">${task.entitlementRole}</g:link></td>
                                    <td><g:link action="getUserResponse" id="${task.id}">${task?.entitlement}</g:link></td>
                                    <td><g:link action="getUserResponse" id="${task.id}">${task?.nodeName}</g:link></td>
                                    <td><g:checkBox name="taskIds" checked="false" value="${task?.id}"
                                                    class="${task.abbreviatedCodeForGroupResponse}"/></td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>
                        <g:render template="/apsWorkflowTask/attachment"/>
                        <g:if test="${tasks}">
                            <div style="text-align:center;">
                                <input class="button" type="submit" value="Attach"/>
                                <input class="button" type="reset" value="Reset"/>
                            </div>
                        </g:if>
                    </g:form>
                </div>
            </g:if>
            <g:else>
                <h1>Attach Evidence - No corresponding tasks found</h1>
            </g:else>
        </div>

        <div id="filterDialog" class="popupWindowContractorFilter">
        </div>
    </div>
</div>
<script type="text/javascript">
    jQuery(document).ready(function () {
        if (jQuery("#tablesorter tr").size() > 1) {
            jQuery("#tablesorter").tablesorter({textExtraction:myTextExtraction, sortList:[
                [1, 1]
            ], headers:{
                5:{
                    sorter:false
                }}});
        }
        jQuery('#groupResponse').submit(function () {
            if (jQuery(':checkbox:checked').length < 1) {
                alert("Please select any one check-box");
                return false;
            }
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
</body>
</html>
