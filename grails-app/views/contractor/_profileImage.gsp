<%@ page import="com.force5solutions.care.ldap.Permission" %>
<div id="photo-section">
    <div id="photo">
        <g:hiddenField name="workerImageId" value="${worker?.workerImageId}"/>
        %{--<care:workerImage id="${worker?.workerImageId}"/>--}%
    </div>
    <g:if test="${care.hasPermission(permission: Permission.UPDATE_CONTRACTOR_PROFILE)}">
        <div id="upload-button">
            <input type="button" class="button" value="Upload" 
                    onClick="showModalDialog('create_uploadImage', true);"/>
        </div>
        <div id="create_uploadImage" class="popupWindow">
            <div class="popupWindowTitle">Select an Image to upload</div>
            <br/>
            <div align="center">
                <input type="file" id="fileContent" name="fileContent" size="30"/>
            </div>
            <br/>
            <div align="center">
                <input type="button" class="button simplemodal-close" value="Submit" onclick='updateImage("${createLinkTo(dir:'images',file:'imagepending.jpg')}");'/>
                &nbsp; &nbsp; <input type="button" class="button simplemodal-close" value="Cancel"/>
            </div>
        </div>
    </g:if>
    <g:else>
        <div id="upload-button">
            <input type="button" class="button" value="Upload" style="color:gray;"/>
        </div>
    </g:else>
</div>
