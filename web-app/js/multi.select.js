function removeMe(anchor) {
    var liElement = jQuery(anchor).parent('li');
    var optionText = jQuery.trim(liElement.text().split('(').first());
    var optionValue = jQuery(anchor).siblings('input:hidden').val();
    var selectElement = jQuery(anchor).parent().parent().parent().prev().find("select");
    jQuery(selectElement).append("<option value='" + optionValue + "'>" + optionText + "</option>");
    liElement.remove();
    jQuery('#entitlementHtml-' + optionValue).remove();
}

function updateSelectBox(selectBoxId, crossImgSrc) {

    var v = jQuery.trim(jQuery('#' + selectBoxId + '-select').val());
    if (v.length < 1) return;
    var t = jQuery('#' + selectBoxId + '-select option:selected').text();
    var str = '<li><input type="hidden" name="' + selectBoxId + '" value="' + v + '"/><span>' + t +
              '</span><a class="removeButton"><img src="' + crossImgSrc + '"/></a></li>';
    jQuery('#' + selectBoxId + '-ul').append(str);
    jQuery('#' + selectBoxId + '-select option[value=' + v + ']').remove();
    jQuery('#' + selectBoxId + '-ul>li>a:last').click(function() {
        removeMe(this);
    });
}


function disableMultiSelect(multiSelectId) {
    jQuery('#' + multiSelectId + '-ul a').unbind('click');
    jQuery('#' + multiSelectId + '-select').attr('disabled', true);
}
function enableMultiSelect(multiSelectId) {
    jQuery('#' + multiSelectId + '-ul>li>a.removeButton').bind('click', function() {
        removeMe(this);
    });
    jQuery('#' + multiSelectId + '-select').removeAttr('disabled');
}