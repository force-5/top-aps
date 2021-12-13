<div id="create_primeVendor">
    <g:formRemote name="vendorForm" onSuccess="updateVendorList('note_primeVendor','primeVendor', e)"
            method="post" url="${[controller:'vendor',action:'saveVendor']}">
        <div id="note_primeVendor">
        </div>
    </g:formRemote>
</div>
<div id="create_subVendor">
    <g:formRemote name="vendorForm" onSuccess="updateVendorList('note_subVendor','subVendor', e)"
            method="post" url="${[controller:'vendor',action:'saveVendor']}">
        <div id="note_subVendor">
        </div>
    </g:formRemote>
</div>
<div id="create_supervisor">
    <g:formRemote name="supervisorForm" onSuccess="updateSupervisorList('note_supervisor', e)"
            method="post" url="${[controller:'contractorSupervisor',action:'saveSupervisor']}">
        <div id="note_supervisor">
        </div>
    </g:formRemote>
</div>
<div id="create_subSupervisor">
    <g:formRemote name="supervisorForm" onSuccess="updateSubSupervisorList('note_subSupervisor', e)"
            method="post" url="${[controller:'contractorSupervisor',action:'saveSupervisor']}">
        <div id="note_subSupervisor">
        </div>
    </g:formRemote>
</div>

<div id="create_businessUnitRequester">
    <g:formRemote name="businessUnitRequesterForm" onSuccess="updateList('note_businessUnitRequester','businessUnitRequesters', e)"
            method="post" url="${[controller:'businessUnitRequester', action:'saveBusinessUnitRequester']}">
        <div id="note_businessUnitRequester">
        </div>
    </g:formRemote>
</div>
