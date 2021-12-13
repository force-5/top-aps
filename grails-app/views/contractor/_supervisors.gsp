<div id="supervisorList" class="namerow">
    <span>Prime Supervisor</span><span class="asterisk">*</span>
    <span style="${hasErrors(bean: contractorInstance, field: 'supervisor', '#border: 1px solid red;')}float:right;">
    %{--<select class="auto-resize listbox ${hasErrors(bean: contractorInstance, field: 'supervisor', 'errors')}"--}%
            %{--style="width:100px;"--}%
            %{--onchange="showSupervisorDialog(this,--}%
                    %{--'${createLink(controller:'contractorSupervisor', action:'createSupervisor')}',--}%
                    %{--'note_supervisor', 'create_supervisor', 'supervisor.id');"--}%
            %{--name="supervisor" id="supervisor">--}%
        %{--<g:render template="/contractorSupervisor/selectBox"--}%
                %{--model="[supervisors: supervisors, selectedSupervisorId: contractorInstance?.supervisor?.toLong()]"/>--}%
    %{--</select>--}%
    </span>
</div>
