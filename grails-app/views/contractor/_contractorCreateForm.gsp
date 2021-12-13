<%@ page import="com.force5solutions.care.*" %>

<g:render template="profileImage" model="[worker:contractorInstance]"/>
<div class="form-section" style="margin-left:70px;">
    <div class="namerow"><span>First Name</span><span class="asterisk">*</span>
        <input type="text" class="inp ${hasErrors(bean: contractorInstance, field: 'firstName', 'errors')}" style="width:130px;"
                name="firstName" value="${fieldValue(bean: contractorInstance, field: 'firstName')}"/>
    </div>
    <div class="namerow"><span>Middle Name</span>
        <input type="text" class="inp ${hasErrors(bean: contractorInstance, field: 'middleName', 'errors')}" style="width:120px;"
                name="middleName" value="${fieldValue(bean: contractorInstance, field: 'middleName')}"/>
    </div>
    <div class="namerow"><span>Last Name</span><span class="asterisk">*</span>
        <input type="text" class="inp ${hasErrors(bean: contractorInstance, field: 'lastName', 'errors')}" style="width:125px;"
                name="lastName" value="${fieldValue(bean: contractorInstance, field: 'lastName')}"/>
    </div>
    <div class="namerow"><span>Birth Day</span>
        <g:render template="birthDay" model="[contractorInstance:contractorInstance]"/>
    </div>
    <div class="namerow"><span>Badge #</span>
        <input type="text" class="inp ${hasErrors(bean: contractorInstance, field: 'badgeNumber', 'errors')}" style="width:125px;"
                name="badgeNumber" value="${fieldValue(bean: contractorInstance, field: 'badgeNumber')}"/>
    </div>
    <g:render template="/contractor/vendors"/>
    <g:render template="/contractor/supervisors"/>
    <g:render template="/contractor/subVendors"/>
    <g:render template="/contractor/subSupervisors"/>
    <g:render template="businessUnitRequesters" model="[worker:contractorInstance]"/>
</div>
<div class="form-section" style="float:right;">
    <div class="namerowbig"><span>Phone</span>
        <input type="text" class="inp ${hasErrors(bean: contractorInstance, field: 'phone', 'errors')}" style="width:185px;"
                name="phone" value="${fieldValue(bean: contractorInstance, field: 'phone')}"/>
    </div>
    <div class="namerowbig"><span>Email</span>
        <input type="text" class="inp ${hasErrors(bean: contractorInstance, field: 'email', 'errors')}" style="width:190px;"
                name="email" value="${fieldValue(bean: contractorInstance, field: 'email')}"/>
    </div>
    <g:if test="${contractorInstance?.id}">
        <div class="namerowbig"><span>Contractor #</span>
            <input type="text" class="inp ${hasErrors(bean: contractorInstance, field: 'workerNumber', 'errors')}" style="width:145px;"
                    id="workerNumber" name="workerNumber" value="${fieldValue(bean: contractorInstance, field: 'workerNumber')}"/>
        </div>
    </g:if>
    <div class="namerowbig"><span>ID Document #</span>
        <input type="text" class="inp ${hasErrors(bean: contractorInstance, field: 'formOfId', 'errors')}" style="width:128px;"
                name="formOfId" value="${fieldValue(bean: contractorInstance, field: 'formOfId')}"/>
    </div>
    <div class="namerowbig"><span>Contractor SLID</span>
        <input type="text" class="inp ${hasErrors(bean: contractorInstance, field: 'slid', 'errors')}" style="width:125px;"
                name="slid" value="${fieldValue(bean: contractorInstance, field: 'slid')}"/>
    </div>
    <div class="namearea" align="left"><span>Notes</span>
        <textarea name="notes" cols="" class="area" style="width:237px; height:110px; " rows="3">${fieldValue(bean: contractorInstance, field: 'notes')}</textarea>
    </div>
</div>
