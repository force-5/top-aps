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
            <td><g:link action="getUserResponse" id="${task.id}">${task.workflowType}</g:link></td>
            <td><g:link action="getUserResponse"
                        id="${task.id}">${task.dateCreated.myDateTimeFormat()}</g:link></td>
            <td><g:link action="getUserResponse" id="${task?.id}">${task?.worker} ${task?.worker?.slid ? "(" + task?.worker?.slid + ")" :""}</g:link></td>
            <td><g:link action="getUserResponse"
                        id="${task.id}">${task.entitlementRole}</g:link></td>
            <td><g:link action="getUserResponse" id="${task.id}">${task.entitlement}</g:link></td>
            <td><g:link action="getUserResponse" id="${task.id}">${task.nodeName}</g:link></td>
            <td><g:checkBox name="taskIds" value="${task?.id}"/></td>
        </tr>
    </g:each>
    </tbody>
</table>