<div id="subSupervisorList" class="namerow">
    <span>Sub Supervisor</span>
    <span style="float:right;">
    %{--<select class="auto-resize listbox ${hasErrors(bean: contractorInstance, field: 'subSupervisor', 'errors')}"--}%
            %{--style="width:110px;"--}%
            %{--onchange="showSubSupervisorDialog(this,--}%
                    %{--'${createLink(controller:'contractorSupervisor', action:'createSupervisor')}',--}%
                    %{--'note_subSupervisor', 'create_subSupervisor', 'subSupervisor.id');"--}%
            %{--name="subSupervisor" id="subSupervisor">--}%
        %{--<g:render template="/contractorSupervisor/selectBox"--}%
                %{--model="[supervisors: subSupervisors, selectedSupervisorId: contractorInstance?.subSupervisor?.toLong()]"/>--}%
    %{--</select>--}%
    </span>
</div>
