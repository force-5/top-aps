<div id="subVendorList" class="namerow">
    <span>Sub Vendor</span>
    <span style="float:right;">
    %{--<select class="auto-resize listbox ${hasErrors(bean: contractorInstance, field: 'subVendor', 'errors')}"--}%
            %{--style="width:130px;" onchange="showNewDialog(this,--}%
                %{--'${createLink(controller:'vendor', action:'createVendor')}',--}%
                %{--'note_subVendor','create_subVendor','subVendor');changeSubSupervisorList(this,--}%
                %{--'${createLink(controller:'contractorSupervisor', action:'supervisorsByVendor')}'); deselectSubSupervisor();"--}%
            %{--name="subVendor" id="subVendor">--}%
        %{--<g:render template="/vendor/selectBox"--}%
                %{--model="[vendors:vendors, selectedVendorId:contractorInstance?.subVendor?.toLong()]"/>--}%
    %{--</select>--}%
        </span>
</div>