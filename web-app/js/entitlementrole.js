function addRoleToEntitlement(entitlementId, roleRequiredList) {
    if(roleRequiredList && (jQuery.inArray(entitlementId, roleRequiredList) != -1)){

    jQuery('input[name=role]').removeAttr('checked');
    showModalDialogWithPosition('roleTemplate', true);
    jQuery('input[name=okButton]').click(function() {
        var role = jQuery('input[name=role]:checked').attr('value');
        var divHtml = '<div class="entitlementWithRole"><input type="hidden" name="selectedEntitlementId" value="' + entitlementId + '"><input type="hidden" name="roleName" value="' + role + '"></div>'
        jQuery('#roleTemplate').append(divHtml);
        var x = jQuery('#entitlements-ul input[value=' + entitlementId + ']').next().text();
        if (x) {
            x = jQuery.trim(x) + " (" + role + ") ";
            jQuery('#entitlements-ul input[value=' + entitlementId + ']').next().text(x);
        }
        jQuery.modal.close();
        jQuery('input[name=okButton]').unbind('click');
    })
    } else {
         var divHtml = '<div class="entitlementWithRole"><input type="hidden" name="selectedEntitlementId" value="' + entitlementId + '"><input type="hidden" name="roleName" value=" "></div>'
        jQuery('#roleTemplate').append(divHtml);
        var x = jQuery('#entitlements-ul input[value=' + entitlementId + ']').next().text();
    }

}