<%@ page import="com.force5solutions.care.ldap.Permission;" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta name="layout" content="main"/>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
    <title>CARE By Force 5 : Profile</title>
</head>
<body>
%{--<care:recentStatus workerId="${contractorInstance?.id}"/>--}%
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    <g:if test="${care.hasPermission(permission: Permission.READ_CONTRACTOR_ACCESS, worker: worker)}">
        <span class="menuButton"><g:link action="access" controller="workerEntitlementRole" params="[id: contractorInstance.id]"
                class="list contractorAccessLink">Access</g:link></span>
    </g:if>
    <g:else>
        <span class="menuButton"><a class="create-disabled  contractorAccessLink">Access</a></span>
    </g:else>
    <g:if test="${care.hasPermission(permission: Permission.READ_CONTRACTOR_CERTIFICATION, worker: worker)}">
        <span class="menuButton"><g:link action="certification" controller="workerCertification" params="[id: contractorInstance.id]"
                class="list contractorCertificationLink">Certification</g:link></span>
    </g:if>
    <g:else>
        <span class="menuButton"><a class="list-disabled  contractorCertificationLink">Certification</a></span>
    </g:else>
    </span>
</div>
<br/>
<div id="wrapper">
    <g:if test="${flash.message}">
        <div align="center"><b>${flash.message}</b></div>
    </g:if>
    <g:hasErrors bean="${contractorInstance}">
        <div class="error-status" style="text-align:center;">
            Please enter values into the required fields<br/>
            <g:if test="${contractorInstance.slid}">
                <g:fieldError bean="${contractorInstance}" field="slid"/>
            </g:if>
        </div>
    </g:hasErrors>
</div>
<div id="right-panel">
    <g:form name="contractorEditForm" controller="contractor" method="post" enctype="multipart/form-data" onsubmit="setupBusinessUnitRequesterIds();">
        <g:hiddenField name="id" value="${contractorInstance?.id}"/>
        <g:hiddenField name="terminateForCause" value="${contractorInstance?.terminateForCause}"/>
        <div id="right-top">
            <g:render template="contractorCreateForm" model="[contractorInstance:contractorInstance]"/>
        </div>
        <br/>
        %{--<g:if test="${!contractorInstance?.terminateForCause}">--}%
            %{--<g:if test="${care.hasPermission(permission: Permission.UPDATE_CONTRACTOR_PROFILE)}">--}%
                %{--<div id="submit-button">--}%
                    %{--<g:actionSubmit class="button" id="updateContractor" value="Update"/>&nbsp;--}%
                    %{--<g:actionSubmit class="button" value="Cancel"/>&nbsp;--}%
                %{--</div>--}%
                %{--<g:if test="${!editFlag}">--}%
                    %{--<div id="edit-button">--}%
                        %{--<input id="editButton" type="button" class="button" value="Edit"/>--}%
                        %{--<g:if test="${care.canDeleteWorker(workerId: contractorInstance?.id).toBoolean()}">--}%
                            %{--<g:actionSubmit class="button" id="deleteWorkerButton" onclick="return confirm('Are you sure?');" value="Delete"/>--}%
                        %{--</g:if>--}%
                    %{--</div>--}%
                %{--</g:if>--}%
            %{--</g:if>--}%
            %{--<g:else>--}%
                %{--<g:if test="${!editFlag}">--}%
                    %{--<div id="edit-button">--}%
                        %{--<input type="button" class="button" style="color:gray;" value="Edit"/>--}%
                        %{--<g:if test="${care.canDeleteWorker(workerId: contractorInstance?.id).toBoolean()}">--}%
                            %{--<input type="button" class="button deleteContractorLink" style="color:gray;" value="Delete"/>--}%
                        %{--</g:if>--}%
                    %{--</div>--}%
                %{--</g:if>--}%

            %{--</g:else>--}%
        %{--</g:if>--}%
    </g:form>
    <div id="requiredIndicator">&nbsp;<span style="color:red;">*</span> indicates a required field</div>

</div>
<g:render template="/contractor/remoteForms"/>
<script type="text/javascript">
    var normalButtonColor = jQuery('div#upload-button input').css('color');
    <g:if test="${!editFlag}">
    jQuery(document).ready(function() {
        jQuery('.flashmessage').html("");
        jQuery.each(jQuery('#right-panel input:text, #right-panel select, #right-panel textarea'), function() {
            jQuery(this).attr('disabled', 'true');
        });
        disableMultiSelect('businessUnitRequesters');
        jQuery('div#submit-button').hide();//attr('display','block');
        jQuery('div#upload-button input').attr('disabled', 'true');
        jQuery('div#upload-button input').css('color', 'gray');

        jQuery('#editButton').click(function() {
            jQuery('.asterisk').show();
            jQuery('#requiredIndicator').show();

            jQuery('div#submit-button').show();//attr('display','block');
            jQuery('div#edit-button').hide();//attr('display','block');
            jQuery('div#upload-button input').removeAttr('disabled');
            jQuery('div#upload-button input').css('color', normalButtonColor);

            jQuery.each(jQuery('#right-panel input:text, #right-panel select, #right-panel textarea'), function() {
                jQuery(this).removeAttr('disabled');
            });

            enableMultiSelect('businessUnitRequesters');
            jQuery("#workerNumber").attr('disabled', 'true');

            jQuery.each(jQuery('#selectedBusinessUnitRequestersList li a'), function() {
                jQuery(this).click(function() {
                    hideMe(jQuery(this).parent());
                    return false;
                });
            });
            if (jQuery('.errors').size() > 0) {
                jQuery('.errors :first').focus();
            }
            else {
                jQuery('input:text:first:visible').focus();
            }
        });
        jQuery('.asterisk').hide();
        jQuery('#requiredIndicator').hide();
    });
    </g:if>
    <g:else>
    jQuery(document).ready(function() {
        jQuery('div#submit-button').show();
        jQuery('div#edit-button').hide();

        jQuery.each(jQuery('#selectedBusinessUnitRequestersList li a'), function() {
            jQuery(this).click(function() {
                hideMe(jQuery(this).parent());
                return false;
            });
        });

        if (jQuery('.errors').size() > 0) {
            jQuery('.errors :first').focus();
        }
        else {
            jQuery('input:text:first:visible').focus();
        }
        jQuery('.asterisk').show();
        jQuery('#requiredIndicator').show();
    });
    </g:else>
</script>
</body>
</html>
