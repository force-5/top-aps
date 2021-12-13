function showEntitlement(entitlementId, url) {
     jQuery.post(url,
     {'id':entitlementId},
             function(htmlText) {
                 jQuery('#show_entitlement_content').html(htmlText);
             });
     showModalDialog('show_entitlement', false);
     return false;
 }

 function editEntitlement(entitlementId, url) {
     jQuery.post(url,
     {'id':entitlementId},
             function(htmlText) {
                 jQuery('#edit_entitlement_content').html(htmlText);
             });
     showModalDialog('edit_entitlement', false);
     return false;
 }

 function updateEntitlementTree(htmlResponse) {
     var result = htmlResponse.responseText;
     if (result.startsWith('<div id=')) {
         jQuery('#successful_operation').html(result);
         var newId = jQuery('#entitlement_id').text();
         var newName = jQuery('#entitlement_name').text();
         jQuery("a[rel='" + newId + "'] >span").text(newName);
         jQuery('#successful_operation').html('');
         jQuery.modal.close();
     } else {
         jQuery('#edit_entitlement_content').html(result);
     }
 }

 function deleteEntitlement(entitlementId, url) {
     jQuery.post(url,
     {'id':entitlementId},
             function(htmlResponse) {
                 var result = htmlResponse;
                 if (result.startsWith('<div id=')) {
                     jQuery('#successful_operation').html(result);
                     var deletedId = jQuery('#entitlement_id').text();
                     var y = jQuery("a[@rel='" + deletedId + "']").parent('li');
                     if(y.length>0){
                         y.remove();
                     }else{
                         y = jQuery("a[@rel='" + deletedId + "']");
                         y.remove();
                     }
                     jQuery('#successful_operation').html('');
                 } else {
                     alert(result);
                 }
             });
     return false;
 }

 function createEntitlement(entitlementPolicyId, parentId, url) {
     jQuery.post(url,
     {'entitlementPolicyId':entitlementPolicyId,'parentId':parentId},
             function(htmlText) {
                 jQuery('#create_entitlement_content').html(htmlText);
                 jQuery("form#createEntitlementForm").
                        prepend("<input type='hidden' name='requestFrom' value='newTree' ");
             });
     showModalDialog('create_entitlement', false);

     return false;
 }

 function saveEntitlement(htmlResponse) {
     var result = htmlResponse.responseText;
     if (result.startsWith('<div id=')) {
         jQuery('div#container_id').html(result);
         jQuery.modal.close();
     } else {
         jQuery('#create_entitlement_content').html(result);
         jQuery("form#createEntitlementForm").
                prepend("<input type='hidden' name='requestFrom' value='newTree' ");
     }
 }
