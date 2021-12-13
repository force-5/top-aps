<%@ page import="com.force5solutions.care.ldap.SecurityRole; com.force5solutions.care.aps.Entitlement;" %>
<table>
    <tbody>

    <tr>
        <td valign="top" width="35%">
            <label for="name">Name&nbsp;<span class="asterisk">*</span></label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlementRole, field: 'name', 'errors')}">
            <g:textField size="48" name="name" value="${entitlementRole?.name}"/>
        </td>
    </tr>

    <tr>
        <td valign="top">
            <label for="status">Status&nbsp;<span class="asterisk">*</span></label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlementRole, field: 'status', 'errors')}">
            <g:each in="${statuses}" var="status">
                <g:radio name="status" value="${status.name()}" checked="${status == entitlementRole.status}"/>
                <span>${status}</span>
            </g:each>
        </td>
    </tr>

    <tr>
        <td valign="top">
            <label for="status">Exposed in CARE Central&nbsp;<span class="asterisk">*</span></label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlementRole, field: 'isExposed', 'errors')}">
            <g:if test="${entitlementRole.isExposed}">
                <g:radio name="isExposed" value="true" checked="true"/>&nbsp; Yes
                <g:radio name="isExposed" value="false"/>&nbsp; No
            </g:if>
            <g:else>
                <g:radio name="isExposed" value="true"/>&nbsp; Yes
                <g:radio name="isExposed" value="false" checked="true"/>&nbsp; No
            </g:else>
        </td>
    </tr>

    <tr>
        <td valign="top">
            <label for="owner">Owner&nbsp;<span class="asterisk">*</span></label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlementRole, field: 'owner', 'errors')}">
            <g:select class="listbox" name="owner.id" from="${com.force5solutions.care.aps.RoleOwner.list()}"
                    noSelection="['null':'(Select One)']"
                    optionKey="id" value="${entitlementRole?.owner?.id}"/>
        </td>
    </tr>

    <tr>
        <td valign="top">
            <label for="gatekeepers">Gatekeepers&nbsp;<span class="asterisk">*</span></label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlementRole, field: 'gatekeepers', 'errors')}">
            <div style="width:300px;">
                <ui:multiSelect name="gatekeepers" from="${SecurityRole.list()}"
                        noSelection="['':'(Select One)']"
                        class="listbox" style="width:300px;"
                        multiple="yes" optionKey="id" size="1" value="${entitlementRole?.gatekeepers}"/>
            </div>
        </td>
    </tr>

    <tr>
        <td valign="top">
            <label for="entitlementIds">Entitlements&nbsp;<span class="asterisk">*</span></label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlementRole, field: 'entitlements', 'errors')}">
            <div style="width:300px;">
                <g:select name="entitlementIds" from="${Entitlement.listApproved()}"
                          value="${entitlementRole.entitlements}"
                          class="listbox" style="width:300px;height: 200px;"
                          multiple="yes" optionKey="id"/>
            </div>
        </td>
    </tr>

    <tr>
        <td valign="top">
            <label for="roles">Roles</label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlementRole, field: 'roles', 'errors')}">
            <div style="width:300px;">
                <ui:multiSelect name="roles" from="${remainingActiveRoles}"
                        noSelection="['':'(Select One)']"
                        class="listbox" style="width:300px;"
                        multiple="yes" optionKey="id" size="1" value="${entitlementRole?.roles}"/>
            </div>
        </td>
    </tr>
    <tr>
        <td valign="top">
            <label for="notes">Notes</label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlementRole, field: 'notes', 'errors')}">
            <g:textArea name="notes" cols="40" rows="5" style="width:300px; height:50px;" value="${entitlementRole?.notes}"/>
        </td>
    </tr>
    <tr>
        <td valign="top">
            <label for="tags">Tags</label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlementRole, field: 'tags', 'errors')}">
             <g:textField size="48" name="tags" value="${entitlementRole?.tags}"/>
        </td>
    </tr>

    </tbody>
</table>
