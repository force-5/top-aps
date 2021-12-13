<div id="vendorList" class="namerow">
    <span>Prime Vendor</span><span class="asterisk">*</span>
    <span style="${hasErrors(bean: contractorInstance, field: 'primeVendor', '#border: 1px solid red;')}float:right;">

    %{--<select class="auto-resize listbox ${hasErrors(bean: contractorInstance, field: 'primeVendor', 'errors')}"--}%
            %{--onchange="showNewDialog(this,--}%
                %{--'${createLink(controller:'vendor', action:'createVendor')}',--}%
                %{--'note_primeVendor','create_primeVendor','primeVendor');changeSupervisorList(this,--}%
                %{--'${createLink(controller:'contractorSupervisor', action:'supervisorsByVendor')}'); deselectSupervisor();"--}%
            %{--name="primeVendor" id="primeVendor">--}%
        %{--<g:render template="/vendor/selectBox"--}%
                %{--model="[vendors:vendors, selectedVendorId:contractorInstance?.primeVendor?.toLong()]"/>--}%
    %{--</select>--}%
        </span>
</div>