<g:set var="monthList" value="${['(Month)','January','February','March','April','May', 'June', 'July', 'August', 'September','October','November','December']}"/>
<select name="birthMonth"
        class="listbox ${hasErrors(bean: contractorInstance, field: 'birthMonth', ' errors')}">
    <g:each var="month" in="${monthList}" status="index">
        <g:if test="${index==contractorInstance?.birthMonth}">
            <option value="${index}" selected>${month}</option>
        </g:if>
        <g:else>
            <option value="${index}">${month}</option>
        </g:else>
    </g:each>
</select>
&nbsp;<g:select name="birthDay"
        class="listbox ${hasErrors(bean: contractorInstance, field: 'birthDay', ' errors')}"
        from="${1..31}"
        noSelection="[0:'(Day)']"
        value="${contractorInstance?.birthDay}"/>
