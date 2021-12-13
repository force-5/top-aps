<%@ page import="com.force5solutions.care.ldap.SecurityRole; com.force5solutions.care.aps.RoleOwner;" %>
<table>
    <tbody>

    <tr>
        <td valign="top" width="35%">
            <label><g:message code="entitlement.name.label" default="Name"/>&nbsp;<span class="asterisk">*</span>
            </label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlement, field: 'name', 'errors')}">
            <g:textField size="48" name="name" value="${entitlement?.name}"/>
        </td>
    </tr>

    <tr>
        <td valign="top">
            <label for="alias"><g:message code="entitlement.alias.label" default="Alias"/>&nbsp;<span
                    class="asterisk">*</span></label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlement, field: 'alias', 'errors')}">
            <g:textField size="48" name="alias" value="${entitlement?.alias}"/>
        </td>
    </tr>

    <tr>
        <td valign="top">
            <label for="status">Exposed in CARE Central&nbsp;<span class="asterisk">*</span></label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlement, field: 'isExposed', 'errors')}">
            <g:if test="${entitlement.isExposed}">
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
            <label for="status"><g:message code="entitlement.status.label" default="Status"/>&nbsp;<span
                    class="asterisk">*</span></label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlement, field: 'status', 'errors')}">
            <g:each in="${statuses}" var="status">
                <g:radio name="status" value="${status.name()}" checked="${status == entitlement.status}"/>
                <span>${status}</span>
            </g:each>
        </td>
    </tr>


    <tr>
        <td valign="top">
            <label for="owner"><g:message code="entitlement.owner.label" default="Owner"/>&nbsp;<span
                    class="asterisk">*</span></label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlement, field: 'owner', 'errors')}">
            <g:select class="listbox" name="owner.id" from="${RoleOwner.list()}"
                      noSelection="['null': '(Select One)']"
                      optionKey="id" value="${entitlement?.owner?.id}"/>
        </td>
    </tr>

    <tr>
        <td valign="top">
            <label for="gatekeepers"><g:message code="entitlement.gatekeepers.label" default="Gatekeepers"/></label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlement, field: 'gatekeepers', 'errors')}">
            <div style="width:300px;">
                <ui:multiSelect name="gatekeepers" from="${SecurityRole.list()}"
                                noSelection="['': '(Select One)']"
                                class="listbox" style="width:300px;"
                                multiple="yes" optionKey="id" size="1"
                                value="${entitlement?.gatekeepers}"/>
            </div>
        </td>
    </tr>

    <tr>
        <td valign="top">
            <label for="provisioners"><g:message code="entitlement.provisioners.label" default="Provisioners"/></label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlement, field: 'provisioners', 'errors')}">
            <div style="width:300px;">
                <ui:multiSelect name="provisioners" from="${SecurityRole.list()}"
                                noSelection="['': '(Select One)']"
                                class="listbox" style="width:300px;"
                                multiple="yes" optionKey="id" size="1"
                                value="${entitlement?.provisioners}"/>
            </div>
        </td>
    </tr>

    <tr>
        <td valign="top">
            <label for="deProvisioners"><g:message code="entitlement.deProvisioners.label"
                                                   default="Deprovisioners"/></label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlement, field: 'deProvisioners', 'errors')}">
            <div style="width:300px;">
                <ui:multiSelect name="deProvisioners" from="${SecurityRole.list()}"
                                noSelection="['': '(Select One)']"
                                class="listbox" style="width:300px;"
                                multiple="yes" optionKey="id" size="1"
                                value="${entitlement?.deProvisioners}"/>
            </div>
        </td>
    </tr>

    <tr>
        <td valign="top">
            <label for="type.id"><g:message code="entitlement.type.label" default="Entitlement Policy"/>&nbsp;<span
                    class="asterisk">*</span></label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlement, field: 'type', 'errors')}">
            <g:select class="listbox" name="type" id="entitlementPolicySelectBox"
                      from="${com.force5solutions.care.cc.EntitlementPolicy.findAllByIsApproved(true)}"
                      optionKey="id"
                      noSelection="['': '(Select One)']"
                      onChange="showHideCustomPropertyDiv(this.value,'${createLink(action: 'getCustomProperties', controller: 'entitlementPolicy')}');"
                      value="${entitlement?.type}"/>
        </td>
    </tr>
    <tr>
        <td id="customPropertiesRow" colspan="2">
            <g:if test="${entitlement.customPropertyValues}">
                <g:render template="/customProperty/customPropertiesByEntitlement"
                          model="[customPropertyValues: entitlement.customPropertyValues]"/>
            </g:if>
        </td>
    </tr>
    <g:render template="entitlementAttributes" model="[entitlementAttributes: entitlement?.entitlementAttributes]"/>
    <tr>
        <td valign="top">
            <label for="accessLayer">Is Auto-provisioned</label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlement, field: 'toBeAutoProvisioned', 'errors')}">
            <g:checkBox name="toBeAutoProvisioned" value="${entitlement?.toBeAutoProvisioned}"/>
        </td>
    </tr>
    <tr>
        <td valign="top">
            <label for="accessLayer">Is Auto-deprovisioned</label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlement, field: 'toBeAutoDeprovisioned', 'errors')}">
            <g:checkBox name="toBeAutoDeprovisioned" value="${entitlement?.toBeAutoDeprovisioned}"/>
        </td>
    </tr>

    <tr>
        <td valign="top">
            <label for="notes"><g:message code="entitlement.notes.label" default="Notes"/></label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlement, field: 'notes', 'errors')}">
            <textarea name="notes" id="notes" cols="" class="area" style="width:300px; height:50px;"
                      rows="3">${fieldValue(bean: entitlement, field: 'notes')}</textarea>
        </td>
    </tr>
    <tr>
        <td valign="top">
            <label for="accessLayer">Access Layer #</label>
        </td>
        <td valign="top" class=" ${hasErrors(bean: entitlement, field: 'accessLayer', 'errors')}">
            <g:textField size="48" name="accessLayer" value="${entitlement?.accessLayer}"/>
        </td>
    </tr>
    </tbody>
</table>

<div id="entitlement-custom-properties">

</div>

<script type="text/javascript">
    function showHideCustomPropertyDiv(entitlementPolicyId, ajaxUrl) {
        jQuery.get(ajaxUrl,
                { ajax: 'true', entitlementPolicyId: entitlementPolicyId}, function (data) {
                    jQuery('#customPropertiesRow').html(data);

                });
    }
</script>
