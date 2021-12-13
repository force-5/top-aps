<div class="alert-con">
    <div class="alert-name5">Name</div>
    <div class="alert-input">
        <input type="text" class="input-message-template" id="name" name="name"
                value="${fieldValue(bean: messageTemplate, field: 'name')}"/>
    </div>
    <div class="clr"></div>
</div>

<div class="alert-con">
    <div class="alert-name5">Transport Type</div>
    <div class="alert-input">
        <input type="text" class="input-message-template" id="transportType" name="transportType"
                value="${fieldValue(bean: messageTemplate, field: 'transportType')}"/>
    </div>

    <div class="clr"></div>
</div>

<div class="alert-con">
    <div class="alert-name4">Subject Template</div><br/>
    <textarea rows="5" cols="40" class="textarea2"
                name="subjectTemplate">${fieldValue(bean: messageTemplate, field: 'subjectTemplate')}</textarea>
</div>
 
<br/>

<div class="alert-con clr">
    <div class="alert-name4">Body Template</div>
    <div id="richTextEditor2" class="clr">
        <textarea rows="25" cols="40" class="textarea1"
                name="bodyTemplate">${fieldValue(bean: messageTemplate, field: 'bodyTemplate')}</textarea>
    </div>
</div>
<div class="clr">
    <a onclick="showModalDialog('preview', false);">
        <img alt="Preview Button" style="padding-right:5px;" border="0" align="right" vspace="10" src="${resource(dir: 'images', file: 'preview.jpg')}"/>
    </a>
</div>
<br/>
<span>Attachments</span>
<g:if test="${attachments}">
    <br/>
    <div id="boxborder" class="affidavitList" style="width:200px;">
        <ul>
            <g:each in="${attachments}" var="attachment">
                <li>
                    <g:link controller="util" action="downloadFile" id="${attachment.id}"
                            params="[className: 'com.force5solutions.care.aps.ApsDataFile', fieldName: 'bytes']">
                        <img src="${createLinkTo(dir: 'images', file: 'magnifing.gif')}" alt="Magnify" border="0"/>
                    </g:link>
                    <span>${attachment.fileName}</span>
                </li>
            </g:each>
        </ul>
    </div>
</g:if>
<g:else>&nbsp;:&nbsp;None</g:else>
