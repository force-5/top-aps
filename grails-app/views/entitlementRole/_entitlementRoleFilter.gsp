%{--<%@ page import="com.force5solutions.care.cc.WorkerStatus; com.force5solutions.care.cc.EntitlementRoleAccessStatus; com.force5solutions.care.*" %>--}%
<meta name="layout" content="someLayoutThatDoesNotExist"/>

<div class="popupWindowTitle">Filter Entitlement Role List</div>
<br/>
<g:form action="filterList">
    <div class="form-section">
        <div class="namerowbig"><span>Name</span>
            <input type="text" class="inp" style="width:170px;"
                    name="name" value="${fieldValue(bean: entitlementRoleCO, field: 'name')}"/>
        </div>
        <div class="namerowbig"><span>Exposed in CARE Central</span>
            <g:radio name="isExposed" value="true"/> &nbsp; Yes
            <g:radio name="isExposed" value="false"/> &nbsp; No
        </div>
        <div class="namerowbig"><span>Propagated</span>
            <g:radio name="isPropagated" value="true"/> &nbsp; Yes
            <g:radio name="isPropagated" value="false"/> &nbsp; No
        </div>
        <div class="namerowbig"><span>Notes</span>
            <input type="text" class="inp" style="width:170px;"
                    name="notes" value="${fieldValue(bean: entitlementRoleCO, field: 'notes')}"/>
        </div>
    </div>
    <div class="form-section">
        <div class="namerowbig">
            <span>Owner</span>
            <g:select class="listbox" style="width:170px;" name="owner" from="${owners}"
                    optionKey="id" value="${entitlementRoleCO?.owner}"
                    noSelection="['':'(Select One)']"/>
        </div>
        <div class="namerowbig">
            <span>Role</span>
            <g:select class="listbox" style="width:170px;" name="role" from="${roles}"
                    optionKey="id" value="${entitlementRoleCO?.role}"
                    noSelection="['':'(Select One)']"/>
        </div>
        <div class="namerowbig">
            <span>Gatekeeper</span>
            <g:select class="listbox" style="width:170px;" name="gatekeeper" from="${gatekeepers}"
                    optionKey="id" value="${entitlementRoleCO?.gatekeeper}"
                    noSelection="['':'(Select One)']"/>
        </div>
        <div class="namerowbig">
            <span>Entitlement</span>
            <g:select class="listbox" style="width:170px;" name="entitlement" from="${entitlements}"
                    optionKey="id" value="${entitlementRoleCO?.entitlement}"
                    noSelection="['':'(Select One)']"/>
        </div>

    </div>
    <div style="clear:both;text-align:center;"></div>
    <br/>
    <br/>
    <div style="text-align:center;">
    <input type="submit" class="button" value="Filter"/>
    <input type="button" class="button" value="Close" onclick="jQuery.modal.close();"/>
    </div>
</g:form>
