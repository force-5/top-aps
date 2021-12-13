
function showDialog(divId, isPersistent) {
    jQuery('#' + divId).modal({
        overlay:100,
        escClose:false,
        position:["20%","40%"],
        overlayCss: {backgroundColor:"#3C3638"},
        persist:isPersistent,
        minHeight:50,
        minWidth:50
    });
}

function showDialogWithPosition(divId, isPersistent, top, left) {
    jQuery('#' + divId).modal({
        overlay:100,
        escClose:false,
        position:[top,left],
        overlayCss: {backgroundColor:"#3C3638"},
        persist:isPersistent,
        minHeight:50,
        minWidth:50
    });
}


function editEmployeeCertification(responseObj) {
    var editPageValue = responseObj.responseText;
    jQuery('#editEmployeeCertificationDiv').empty().html(editPageValue);
    showDialog('editEmployeeCertificationDiv', true);
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