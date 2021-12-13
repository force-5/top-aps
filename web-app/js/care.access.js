function showPopup(divName, response) {
    jQuery('#' + divName).html(response.responseText);
    showModalDialog(divName)
}

function confirmDelete() {
    if (confirm("Remove entitlement?", "Yes", "No")) {
        jQuery('#contractorEntitlementAction').attr('value', 'remove');
        return true;
    }
    return false;
}

function isCommentEmpty(inputControl, errorDiv) {
    var a = jQuery('#' + inputControl).val();
    if (jQuery.trim(a).length == 0) {
        jQuery('#' + errorDiv).show();
        jQuery('#' + inputControl).css('border', '1px solid red').focus();
        return true;
    } else {
        return false;
    }
}
function isStatusChangeCommentEmpty(inputControl, errorDiv) {
    var a = jQuery('#' + inputControl).val();
    var isCommentValid = false;
    if (jQuery.trim(a).length == 0) {
        jQuery('#' + errorDiv).show();
        jQuery('#' + inputControl).css('border', '1px solid red').focus();
        isCommentValid = false;
    } else {
        jQuery('#boxborder.affidavitList').css('border', '1px solid black');
        jQuery('#' + inputControl).css('border', '1px solid black').focus();
        isCommentValid = true;
    }
    var isAttachmentValid = isValidStatusForm();
    return (isCommentValid && isAttachmentValid);
}

function isValidStatusForm() {
    if (!jQuery('#supportDocument').is(':visible')) {
        return true;
    }
    var a = jQuery('#boxborder.affidavitList li').size();
    if (a == 0) {
        jQuery('#boxborder.affidavitList').css('border', '1px solid red');
        return false;
    } else {
        jQuery('#boxborder.affidavitList').css('border', '1px solid black');
        return true;
    }
}

//div with id #editContractorCertificationDiv is in _missingCertifications.gsp template
function editWorkerCertification(responseObj) {
    var editPageValue = responseObj.responseText;
    jQuery('#editWorkerCertificationDiv').empty().html(editPageValue);
    jQuery.modal.close();
    showModalDialog('editWorkerCertificationDiv', true);
}
function showEditCertificationPopup() {
    jQuery.modal.close();
    showModalDialog('editWorkerCertificationDiv', true);
}

function showChangeStatusDialog() {
    if (jQuery('td>input:checkbox:enabled:checked').size() < 1) {
        alert('No entitlement selected.');
        return false;
    }
    jQuery('#change-status-error3').hide();
    jQuery('#accessJustification3').val('');
    jQuery('#accessJustification3').css('border', '1px solid #7f7f7f').focus();
    showModalDialog('change-status-multiple', true);
    jQuery('input[name=newAccessStatusForMultiple]').eq(3).attr('checked', true);
    return true;
}

function changeStatusMultipleSetup(inputControl, errorDiv) {
    if (isCommentEmpty(inputControl, errorDiv)) {
        return true;
    }
    jQuery('#contractorEntitlementHiddenFields').html("");
    jQuery('td>input:checkbox:enabled:checked').each(function() {
        if (jQuery(this).val()) {
            jQuery('#contractorEntitlementHiddenFields').
                    append("<input type='hidden' name='checkecdContractorEntitlements' value='" + jQuery(this).val() + "'/>");
        }
    });
    return false;
}


function addEntitlementToworker(workerId, entitlementId, entitlementName) {
    jQuery('#entitlementId').val(entitlementId);
    jQuery('#head-add_contractor').text("Add Entitlement (" + entitlementName + ")");
    showAccessJustificationComment();
}

function hideAccessJustificationComment() {
    jQuery('#container_id').show();
    jQuery('#commentArea').hide();
    jQuery('#close-button').show();
    jQuery('#head-add_contractor').text('Add Entitlement');
}
function showAccessJustificationComment() {
    jQuery('#change-status-error2').hide();
    jQuery('#accessJustification2').val('')
            .css('border', '1px solid #7f7f7f')
            .focus();

    jQuery('#container_id').hide();
    jQuery('#close-button').hide();
    jQuery('#commentArea').show();
}

function postEntitlementAdded(e, tickMarkImagePath) {
    if (e.responseText.startsWith('failure')) {
        alert('This entitlement can not be added.');
    } else {
        var alink = jQuery.trim(e.responseText);
        var y = jQuery("a[@rel='" + alink + "']");
        jQuery(y).parents('span').html('&nbsp;&nbsp;<img src="' + tickMarkImagePath + '"  height="12" alt="Tick Mark"/>');
    }
    hideAccessJustificationComment();
}

function changeWorkerPerimetetStatusMultipleSetup(inputControl, errorDiv) {
    if (isCommentEmpty(inputControl, errorDiv)) {
        return true;
    }
    jQuery('#workerEntitlementHiddenFields').html("");
    jQuery('td>input:checkbox:enabled:checked').each(function() {
        if (jQuery(this).val()) {
            jQuery('#workerEntitlementHiddenFields').
                    append("<input type='hidden' name='checkedWorkerEntitlements' value='" + jQuery(this).val() + "'/>");
        }
    });
    return false;
}

function updateThisEntitlement(entitlementId, entitlementTitle) {
    var y = jQuery("a[@rel='" + entitlementId + "']");
    jQuery(y).parents('span').html('&nbsp;&nbsp;<img src="../images/tickmark.jpg" height="12" alt="Tick Mark"/>');
    jQuery("<li class='li-input1'><input type='hidden' name='selectedEntitlements' value='"
            + entitlementId + "'/>" + entitlementTitle + "<a href='#' class='cross-link' onclick='jQuery(this).parent(\"li\").remove();'>&nbsp;</a></li>").
            appendTo('#selectedEntitlementsList');
}

function populatePopup(ajaxURL) {
    var workerNumber = jQuery.trim(jQuery('#workerNumber').val());
    var employeeSlid = jQuery.trim(jQuery('#employeeSlid').val());
    if (workerNumber.length < 1 && employeeSlid.length < 1) {
        alert('Please enter employee# or slid');
        return false;
    }
    jQuery.post(ajaxURL,
    { ajax: 'true', workerNumber:workerNumber, employeeSlid:employeeSlid}, function(htmlText) {
        if (htmlText == "error") {
            alert('Employee not found');
        } else {
            jQuery('#container_id').html(htmlText);
            showModalDialogWithPosition('add_entitlement_tree', false);
        }
    });
}

function isBlank(x) {
    return jQuery.trim(x).length < 1;
}

function validateForm() {
    var isValid = true;
    if (isBlank(jQuery('#workerNumber').val()) && isBlank(jQuery('#employeeSlid').val())) {
        isValid = false;
        jQuery('#workerNumber').addClass('errorBorder');
        jQuery('#employeeSlid').addClass('errorBorder');
    } else {
        jQuery('#workerNumber').removeClass('errorBorder');
        jQuery('#employeeSlid').removeClass('errorBorder');
    }
    if (isBlank(jQuery('#comment').val())) {
        isValid = false;
        jQuery('#comment').addClass('errorBorder');
    } else {
        jQuery('#comment').removeClass('errorBorder');
    }
    if (jQuery('#selectedEntitlementsList>li').size() < 1) {
        isValid = false;
        jQuery('#selectEntitlement a.department').addClass('errorBorder');
    } else {
        jQuery('#selectEntitlement a.department').removeClass('errorBorder');
    }

    if (isValid) jQuery('#access-request-error').hide(); else jQuery('#access-request-error').show();
    return isValid;
}

function checkForDeleteCertification() {
    if (confirm("Delete this Certification?")) {
        return true;
    }
    return false;
}

function showTerminateForCauseDialog(divId) {
    jQuery('#iagreediv').removeClass('errorBorder');
    jQuery('#terminateForCauseComment').removeClass('errorBorder');
    jQuery('#terminateForCauseComment').val('');
    showModalDialog(divId, true);
    return false;
}

function validateTerminateForCauseForm() {
    var isValid = true;
    if (isBlank(jQuery('#terminateForCauseComment').val())) {
        isValid = false;
        jQuery('#terminateForCauseComment').addClass('errorBorder');
    } else {
        jQuery('#terminateForCauseComment').removeClass('errorBorder');
    }
    if (!jQuery('#iagree').attr('checked')) {
        isValid = false;
    }
    return isValid;
}

function emptyValues() {
    jQuery('#firstName').val('');
    jQuery('#middleName').val('');
    jQuery('#lastName').val('');
    jQuery('#phone').val('');
    jQuery('#notes').val('');
}

function populateValues(jsonData) {
    jQuery('#firstName').val(jsonData.firstName);
    jQuery('#middleName').val(jsonData.middleName);
    jQuery('#lastName').val(jsonData.lastName);
    jQuery('#phone').val(jsonData.phone);
    jQuery('#notes').val(jsonData.notes);
}
