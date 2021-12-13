<%@ page import="com.force5solutions.care.cc.TransportType; com.force5solutions.care.cc.TransportType" %>

<div class="alert-con">
    <div class="alert-name5">Name</div>
    <div class="alert-input">
        <input type="text" class="input-message-template ${hasErrors(bean: messageTemplate, field: 'name', 'errors ')}" id="name" name="name"  value="${fieldValue(bean: messageTemplate, field: 'name')}"/>
    </div>
    <div class="clr"></div>
</div>
<div class="alert-con">
    <div class="alert-name5">Transport Type</div>
    <div class="alert-input">
        <label>
            <g:select name="transportType"
                    class="${hasErrors(bean: messageTemplate, field: 'transportType', 'errors ')} select listbox"
                    id="transportType" value="${TransportType.getTransportType(messageTemplate?.transportType)}"
                    from="${TransportType.list()}"/>
        </label>
    </div>
    <div class="clr"></div>
</div>

<div class="alert-con">
    <div class="alert-name4">Subject Template</div><br/>
   <div id="subjectDropDown">
     <select class="listbox" onchange="appendToSubject(this.value);">
       <option>Select Tag</option>
        <g:each in="${tagsMap}">
        <option value="${it.value}">${it.key}</option>
        </g:each>
    </select></div>
    <div class="clr ${hasErrors(bean: messageTemplate, field: 'subjectTemplate', 'errors ')}">
        <input type="text" style="width:600px;" name="subjectTemplate" id="subjectTemplate" value="${fieldValue(bean: messageTemplate, field: 'subjectTemplate')}"/>
    </div>
</div>
<br/>
<div class="alert-con clr">
    <div class="alert-name4">Body Template</div>
    <div class="clr ${hasErrors(bean: messageTemplate, field: 'bodyTemplate', 'errors ')}">
        <textarea rows="25" cols="40" class="textarea1 mceEditor2" name="bodyTemplate">${fieldValue(bean: messageTemplate, field: 'bodyTemplate')}</textarea>
    </div>
</div>
<br/>
<g:render template="attachment" model="[attachments:attachments]"/>
