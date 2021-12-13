<div class="body" style="position:relative;">
    <h1>Documents</h1>
    <table>
        <thead>
        <tr>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <g:if test="${system == 'APS'}">
            <g:set value='com.force5solutions.care.aps.ApsDataFile' var='className'/>
        </g:if>
        <g:else>
            <g:set value='com.force5solutions.care.cc.CentralDataFile' var='className'/>
        </g:else>
        <g:each in="${documents}" status="i" var="document">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td><g:link action="downloadFile" id="${document.id}" params="[className: className, fieldName: 'bytes']">
                    ${document?.fileName}
                </g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <br/>
    <div>
        <span>Message: ${message}</span>
    </div>
    <input type="button" style="position:absolute;bottom:-15px;left:45%;" class="button" value="Close" onclick="jQuery.modal.close();"/>
</div>
