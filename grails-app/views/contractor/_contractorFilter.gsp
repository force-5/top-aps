<%@ page import="com.force5solutions.care.cc.WorkerStatus; com.force5solutions.care.cc.EntitlementRoleAccessStatus; com.force5solutions.care.*" %>
<meta name="layout" content="someLayoutThatDoesNotExist"/>

<div class="popupWindowTitle">Filter Contractor List</div>
<br/>
<g:form action="filterList">
    <div class="form-section">
        <div class="namerow"><span>First Name</span>
            <input type="text" class="inp" style="width:130px;"
                    name="firstName" value="${fieldValue(bean: contractorCO, field: 'firstName')}"/>
        </div>
        <div class="namerow"><span>Middle Name</span>
            <input type="text" class="inp" style="width:120px;"
                    name="middleName" value="${fieldValue(bean: contractorCO, field: 'middleName')}"/>
        </div>
        <div class="namerow"><span>Last Name</span>
            <input type="text" class="inp" style="width:125px;"
                    name="lastName" value="${fieldValue(bean: contractorCO, field: 'lastName')}"/>
        </div>
        <div class="namerow"><span>Badge #</span>
            <input type="text" class="inp" style="width:125px;"
                    name="badgeNumber" value="${fieldValue(bean: contractorCO, field: 'badgeNumber')}"/>
        </div>
        <div class="namerowbig">
            <span>Prime Vendor</span>
            <g:select class="listbox" style="width:140px;" name="primeVendor" from="${vendors}"
                    optionKey="id" value="${contractorCO?.primeVendor}"
                    noSelection="['':'(Select One)']"/>
        </div>
        <div class="namerowbig">
            <span>Supervisor</span>
            <g:select class="listbox" style="width:170px;" name="supervisor" from="${supervisors}"
                    optionKey="id" value="${contractorCO?.supervisor}"
                    noSelection="['':'(Select One)']"/>
        </div>
        <div class="namerowbig">
            <span>Sub-Vendor</span>
            <g:select class="listbox" style="width:150px;" name="subVendor" from="${vendors}"
                    optionKey="id" value="${contractorCO?.subVendor}"
                    noSelection="['':'(Select One)']"/>
        </div>
        <div class="namerowbig">
            <span>Sub-Supervisor</span>
            <g:select class="listbox" style="width:150px;" name="subSupervisor" from="${supervisors}"
                    optionKey="id" value="${contractorCO?.subSupervisor}"
                    noSelection="['':'(Select One)']"/>
        </div>
    </div>
    <div class="form-section">
        <div class="namerowbig"><span>Business Unit Requester</span>
            <g:select class="listbox" style="width:95px;" name="businessUnitRequester" from="${businessUnitRequesters}"
                    optionKey="id" value="${contractorCO?.businessUnitRequester}"
                    noSelection="['':'(Select One)']"/>
        </div>
        <div class="namerowbig"><span>Phone</span>
            <input type="text" class="inp" style="width:185px;"
                    name="phone" value="${fieldValue(bean: contractorCO, field: 'phone')}"/>
        </div>
        <div class="namerowbig"><span>Email</span>
            <input type="text" class="inp" style="width:190px;"
                    name="email" value="${fieldValue(bean: contractorCO, field: 'email')}"/>
        </div>
        <div class="namerowbig"><span>Contractor #</span>
            <input type="text" class="inp" style="width:145px;"
                    name="workerNumber" value="${fieldValue(bean: contractorCO, field: 'workerNumber')}"/>
        </div>
        <div class="namerowbig"><span>Contractor SLID</span>
            <input type="text" class="inp" style="width:145px;"
                    name="slid" value="${fieldValue(bean: contractorCO, field: 'slid')}"/>
        </div>
        <div class="namerowbig"><span>Status</span>
            <g:select class="listbox" style="width:180px;" name="status" id="status" from="${WorkerStatus.list()}" noSelection="['':'(Select One)']"/>
        </div>
        <div class="namerowbig"><span>Location Status</span>
            <g:select class="listbox" style="width:140px;" name="locationStatusId" id="locationStatusId" optionKey="id" from="${EntitlementRoleAccessStatus.list()}" noSelection="['':'(Select One)']"/>
        </div>
    </div>
    <div style="clear:both;text-align:center;">
        <br/>
        <br/>

        <input type="submit" class="button" value="Filter"/>
        <input type="button" class="button" value="Close" onclick="jQuery.modal.close();"/>
    </div>
</g:form>
