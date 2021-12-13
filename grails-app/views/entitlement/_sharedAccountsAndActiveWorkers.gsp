<g:each in="${sharedAccountsAndProvisionedWorkersVOs}" var="sharedAccountsAndProvisionedWorkersVO">

    <h1>Entitlement: ${sharedAccountsAndProvisionedWorkersVO.entitlement}
        <g:if test="${isRevocation}">
            <span style="display: inline; font-size: 11px; color: #333;margin-left:20px; line-height: 14px;">
                <g:checkBox name="passwordUpdatedEntitlementIds" class="passwordUpdate" checked="false"
                            value="${sharedAccountsAndProvisionedWorkersVO.entitlement.id}"/>
                Password Updated
            </span>
        </g:if>
    </h1>
    <br/>

    <div class="header-text-over-table">Shared Accounts</div>

    <div id="tableContainer-shared-accounts">
        <table width="100%">
            <thead class="fixedHeader">
            <tr>
                <th>Name</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${sharedAccountsAndProvisionedWorkersVO?.sharedAccounts}" var="sharedAccount">
                <tr>
                    <td>${sharedAccount}</td>
                </tr>
            </g:each>
            </tbody>
        </table>

    </div>

    <div class="header-text-over-table" style="padding-top: 15px;">Active Workers</div>

    <div id="tableContainer-active-workers">
        <table width="100%">
            <thead class="fixedHeader">
            <tr>
                <th>Name</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${sharedAccountsAndProvisionedWorkersVO?.provisionedWorkers}" var="worker">
                <tr>
                    <td>${worker?.getFirstMiddleLastName()}</td>
                </tr>
            </g:each>
            </tbody>
        </table>

    </div>
    <hr style="margin-top: 10px;margin-bottom: 10px;"/>
</g:each>