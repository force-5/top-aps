<div class="popupWindowTitle">Filter Workflow List</div>
<br/>
<g:form action="filterWorkflowTasks">
    <div class="form-section">
        <div class="namerow"><span>Name</span>
            <input type="text" class="inp" style="width:130px;"
                    name="name" value=" "/>
        </div>
        <div class="namerow"><span>SLID</span>
            <input type="text" class="inp" style="width:120px;"
                    name="slid" value=" "/>
        </div>
        <div class="namerowbig"><span>Entitlement Role</span>
            <g:select class="listbox" style="width:140px;" name="entitlementRole" from="${entitlementRoles}"
                    noSelection="['':'(Select One)']" optionKey="id" optionValue="name"/>
        </div>
        <div class="namerowbig"><span>Entitlement</span>
            <g:select class="listbox" style="width:170px;" name="entitlement" from="${entitlements}"
                    noSelection="['':'(Select One)']" optionKey="id" optionValue="name"/>
        </div>
        <div class="form-section">
            <div class="namerowbig"><span>Workflow</span>
                <g:select class="listbox" style="width:170px;" name="workflowType" from="${workflowTypes}"  optionKey="key"
                        noSelection="['':'(Select One)']"/>
            </div>
            <div class="namerowbig"><span>Node Name</span>
                <g:select class="listbox" style="width:170px;" name="taskNodeName" from="${nodeNames}"
                        noSelection="['':'(Select One)']"/>
            </div>
        </div>
        <div style="clear:both;text-align:center;"></div>
        <br/>
        <br/>
        <input type="submit" class="button" value="Filter"/>
        <input type="button" class="button" value="Close" onclick="jQuery.modal.close();"/>
    </div>
</g:form>
