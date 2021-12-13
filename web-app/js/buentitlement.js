function removeCustomProperty(anchor) {
    jQuery(anchor).parent('div').remove()
}
function removeStandard(anchor) {
    jQuery(anchor).parent('div').remove()
}

function showCustomPropertyPopUp() {
    jQuery('#new-validation-error-div').hide();
    jQuery('#new-custom-unique-error-div').hide();
    jQuery('#custom-property-size').hide();
    jQuery('#not-integer-error').hide()
    jQuery('#customPropertyName').css('border', '1px solid gray').focus();
    jQuery('#customPropertyType').css('border', '1px solid gray');
    jQuery('#customPropertySize').css('border', '1px solid gray');
    jQuery('#customPropertyName').val('')
    jQuery('#customPropertyType').val('')
    jQuery('#customPropertySize').val('')
    jQuery('#isCustomPropertyRequired').attr('checked', false)
    showModalDialog('custom-property-popup', true);
}

function showStandardPopUp() {
    jQuery('#standardName').css('border', '1px solid gray').focus();
    jQuery('#standardName').val('');
    jQuery('#standard-validation-error-div').hide()
    jQuery('#standard-unique-error-div').hide()
    showModalDialog('standard-popup', true);
}
