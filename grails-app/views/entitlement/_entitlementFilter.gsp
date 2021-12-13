%{--<%@ page import="com.force5solutions.care.cc.WorkerStatus; com.force5solutions.care.cc.EntitlementRoleAccessStatus; com.force5solutions.care.*" %>--}%
<meta name="layout" content="someLayoutThatDoesNotExist"/>

<div class="popupWindowTitle">Filter Entitlement List</div>
<br/>
<g:form action="filterList">
    <div class="form-section" style="width:270px">
        <div class="namerowbig"><span>Name</span>
            <input type="text" class="inp" style="width:170px;"
                   name="name" value="${fieldValue(bean: entitlementCO, field: 'name')}"/>
        </div>

        <div class="namerowbig"><span>Alias</span>
            <input type="text" class="inp" style="width:170px;"
                   name="alias" value="${fieldValue(bean: entitlementCO, field: 'alias')}"/>
        </div>

        <div class="namerowbig"><span>Notes</span>
            <input type="text" class="inp" style="width:170px;"
                   name="notes" value="${fieldValue(bean: entitlementCO, field: 'notes')}"/>
        </div>

        <div class="namerowbig">
            <span>Status</span>
            <select class="listbox" id="status" name="status" style="width:175px;">
                <option value="" selected="selected">--Select One--</option>
                <option value="ACTIVE">Active</option>
                <option value="INACTIVE">Inactive</option>
                <option value="RETIRED">Retired</option>
            </select>
        </div>


        <div class="namerowbig">
            <span>Origin</span>
            <g:select class="listbox" style="width:175px;" name="origin" from="${origins}"
                      optionKey="id" value="${entitlementCO?.origin}"
                      noSelection="['':'(Select One)']"/>
        </div>

    </div>

    <div class="form-section" style="width:270px">

        <div class="namerowbig">
            <span>Owner</span>
            <g:select class="listbox" style="width:170px;" name="owner" from="${owners}"
                      optionKey="id" value="${entitlementCO?.owner}"
                      noSelection="['':'(Select One)']"/>
        </div>

        <div class="namerowbig">
            <span>Type</span>
            <g:select class="listbox" style="width:170px;" name="entitlementPolicy" from="${entitlementPolicies}"
                      optionKey="id" value="${entitlementCO?.entitlementPolicy}"
                      noSelection="['':'(Select One)']"/>
        </div>

        <div class="namerowbig">
            <span>Gatekeeper</span>
            <g:select class="listbox" style="width:170px;" name="gatekeeper" from="${gatekeepers}"
                      optionKey="id" value="${entitlementCO?.gatekeeper}"
                      noSelection="['':'(Select One)']"/>
        </div>

        <div class="namerowbig">
            <span>Provisioner</span>
            <g:select class="listbox" style="width:170px;" name="provisioner" from="${provisioners}"
                      optionKey="id" value="${entitlementCO?.provisioner}"
                      noSelection="['':'(Select One)']"/>
        </div>

        <div class="namerowbig">
            <span>Deprovisioner</span>
            <g:select class="listbox" style="width:170px;" name="deProvisioner" from="${deProvisioners}"
                      optionKey="id" value="${entitlementCO?.deProvisioner}"
                      noSelection="['':'(Select One)']"/>
        </div>

    </div>

    <div style="clear:both;text-align:center;">
        <br/>
        <br/>

        <div style="text-align:center;">
            <input type="submit" class="button" value="Filter"/>
            <input type="button" class="button" value="Close" onclick="jQuery.modal.close();"/>
        </div>
    </div>
</g:form>
