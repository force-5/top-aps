//Re-populate the selectbox so that it includes the newly added record
function updateList(divToUpdate, selectBoxId, htmlResponse) {
    if (htmlResponse.responseText.startsWith("<option")) {
        if (selectBoxId == 'businessUnitRequesters') {
            jQuery(htmlResponse.responseText).appendTo(jQuery('#businessUnitRequesters-select'));
        } else {
            var htmlText = htmlResponse.responseText;
            var i = htmlText.lastIndexOf('</option>');
            htmlText = htmlText.substring(0, i + 9);
            (jQuery('#' + selectBoxId + '\\.id')).html(htmlText);
        }
        jQuery.modal.close();
    }
    else {
        jQuery('#' + divToUpdate).html(htmlResponse.responseText);
    }
}

function updateSupervisorList(divToUpdate, htmlResponse) {
    if (htmlResponse.responseText.startsWith("<option")) {
        jQuery.modal.close();
        var htmlText = htmlResponse.responseText;
        if (jQuery('#primeVendor').val() == jQuery('#subVendor').val() && jQuery('#primeVendor').val() != -2) {
            var oldSubSupervisor = jQuery('#subSupervisorList select').val();
            jQuery('#subSupervisorList option').remove();
            jQuery('#subSupervisorList select').append(htmlText);
            jQuery('#subSupervisorList option[value=' + oldSubSupervisor + ']').attr('selected', 'true');
        }

        jQuery('#supervisorList option').remove();
        jQuery('#supervisorList select').append(htmlText);
    } else {
        jQuery('#' + divToUpdate).html(htmlResponse.responseText);
    }
}

function updateSubSupervisorList(divToUpdate, htmlResponse) {
    if (htmlResponse.responseText.startsWith("<option")) {
        jQuery.modal.close();
        var htmlText = htmlResponse.responseText;
        if (jQuery('#primeVendor').val() == jQuery('#subVendor').val() && jQuery('#primeVendor').val() != -2) {
            var oldSupervisor = jQuery('#supervisorList select').val();
            jQuery('#supervisorList option').remove();
            jQuery('#supervisorList select').append(htmlText);
            (jQuery('#supervisorList option[value=' + oldSupervisor + ']')).attr('selected', 'true');
        }
        jQuery('#subSupervisorList option').remove();
        jQuery('#subSupervisorList select').append(htmlText);
    } else {
        jQuery('#' + divToUpdate).html(htmlResponse.responseText);
    }
}

function updateVendorList(divToUpdate, selectBoxId, htmlResponse) {
    if (htmlResponse.responseText.startsWith("<option")) {
        var htmlText = htmlResponse.responseText;
        var i = htmlText.lastIndexOf('</option>');
        htmlText = htmlText.substring(0, i + 9);
        var oldPrimeVendor = (jQuery('#primeVendor')).val();
        var oldSubVendor = (jQuery('#subVendor')).val();
        var oldSupervisor = (jQuery('#supervisor')).val();
        var oldSubSupervisor = (jQuery('#subSupervisor')).val();
        (jQuery('#primeVendor')).html(htmlText);
        (jQuery('#subVendor')).html(htmlText);
        if (selectBoxId == "primeVendor") {
            (jQuery('#subVendor option[value=' + oldSubVendor + ']')).attr('selected', 'true');
            (jQuery('#subSupervisor option[value=' + oldSubSupervisor + ']')).attr('selected', 'true');
        } else if (selectBoxId == "subVendor") {
            (jQuery('#primeVendor option[value=' + oldPrimeVendor + ']')).attr('selected', 'true');
            (jQuery('#supervisor option[value=' + oldSupervisor + ']')).attr('selected', 'true');
        }
        jQuery.modal.close();
    }
    else {
        jQuery('#' + divToUpdate).html(htmlResponse.responseText);
    }
}

function showCertificationHistoryPopup(htmlResponse, divName) {
    jQuery.modal.close();
    jQuery('#' + divName).html(htmlResponse.responseText);
    showModalDialog(divName);
}

// Utility function to change the selected item index from selectbox
function selectOptionByIndex(selectBox, idx) {
    var selectmenu = document.getElementById(selectBox);
    if (idx == 'first')
        idx = 0;
    else if (idx == 'last')
        idx = selectmenu.length - 1;
    selectmenu.selectedIndex = idx;
}

function addItemToSelectBox(selectBox, id, v, isSelected) {
    var selectmenu = document.getElementById(selectBox);
    selectmenu.options[selectmenu.length] = new Option(v, id, false, isSelected);
}

function addItemToList(listId, newId, newValue, func) {
    newValue = newValue.replace("<", "&lt;");
    jQuery('<li id=' + newId + '>' + newValue + '<a href="#" ' + func + '></a></li>').appendTo('#' + listId);
}


function hideMe(x) {
    jQuery(x).remove();
    addItemToSelectBox('businessUnitRequester.id', jQuery(x).attr('id'), jQuery(x).text(), true);
}

function setupBusinessUnitRequesterIds() {
    jQuery("#selectedBusinessUnitRequestersList li").each(function() {
        var idValue = jQuery(this).attr("id");
        jQuery("<input type='hidden' name='burIds' value='" + idValue + "' />")
                .appendTo('div#chesterbox');
    });
}
// Make the popup draggable and put focus on the first form field
function initializePopup(popupDivId) {
    if (jQuery('div#' + popupDivId + ' .errors').size() > 0) {
        jQuery('div#' + popupDivId + ' .errors input:text:visible:first').focus();
    }
    else {
        jQuery('div#' + popupDivId + ' input:text:visible:first').focus();
    }

    //    jQuery('#' + popupDivId + ' input:text:first:visible').focus();
}

// Make a div to appear like a modal dialog box i.e. only the specified div area will be enabled.
function showModalDialog(divId, isPersistent) {
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

function showModalDialogOfWidth(divId, width) {
    jQuery('#' + divId).modal({
        overlay:100,
        escClose:false,
        position:["20%","30%"],
        overlayCss: {backgroundColor:"#3C3638"},
        persist: false,
        minHeight:50,
        minWidth:width
    });
}

function showModalDialogWithPosition(divId, isPersistent, top, left) {
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

function showModalDialogWithPositionAndWidth(divId, isPersistent, top, left, width) {
    jQuery('#' + divId).modal({
        overlay:100,
        escClose:false,
        position:[top,left],
        overlayCss: {backgroundColor:"#3C3638"},
        persist:isPersistent,
        minHeight:50,
        minWidth:width
    });
}

// show create new popup for vendor, procurementOwner, supervisor and BUR
function showNewDialog(optionValue, ajaxURL, divToUpdate, modalDialogDivId, selectBoxId) {
    if (optionValue.value == -1) {
        jQuery.post(ajaxURL,
        { ajax: 'true'}, function(htmlText) {
            jQuery('#' + divToUpdate).html(htmlText);
        });
        showModalDialog(modalDialogDivId, false);
        selectOptionByIndex(selectBoxId, 'first');
    }
}

function showSupervisorDialog(optionValue, ajaxURL, divToUpdate, modalDialogDivId, selectBoxId) {
    if (optionValue.value == -1) {
        var vendorId = jQuery('#vendorList option:selected').val();
        if (vendorId > 0) {
            jQuery.post(ajaxURL + "?vendorId=" + vendorId,
            { ajax: 'true'}, function(htmlText) {
                jQuery('#' + divToUpdate).html(htmlText);
            });
            showModalDialog(modalDialogDivId, false);
            selectOptionByIndex(selectBoxId, 'first');
        }
        else {
            alert('Please choose a vendor first');
        }
    }
}

function showSubSupervisorDialog(optionValue, ajaxURL, divToUpdate, modalDialogDivId, selectBoxId) {
    if (optionValue.value == -1) {
        var vendorId = jQuery('#subVendorList option:selected').val();
        if (vendorId > 0) {
            jQuery.post(ajaxURL + "?vendorId=" + vendorId,
            { ajax: 'true'}, function(htmlText) {
                jQuery('#' + divToUpdate).html(htmlText);
            });
            showModalDialog(modalDialogDivId, false);
            selectOptionByIndex(selectBoxId, 'first');
        }
        else {
            alert('Please choose a sub vendor first');
        }
    }
}

function changeSupervisorList(sel, url) {
    jQuery('#supervisorList option[value!=-1][value!=-2]').remove();
    jQuery.post(url,
    { ajax: 'true', id: sel.value}, function(data) {
        jQuery('#supervisorList select').append(data);
    });
}
function changeSubSupervisorList(sel, url) {
    jQuery('#subSupervisorList option[value!=-1][value!=-2]').remove();
    jQuery.post(url,
    { ajax: 'true', id: sel.value}, function(data) {
        jQuery('#subSupervisorList select').append(data);
    });
}

function deselectSupervisor() {
    jQuery('#supervisorList option.vendor-2').attr('selected', 'selected');
}
function deselectSubSupervisor() {
    jQuery('#subSupervisorList option.vendor-2').attr('selected', 'selected');
}

function updateImage(imagePath) {
    if (jQuery('#fileContent').val()) {
        jQuery('#photo img').attr('src', imagePath);
    }
}

function showHistoryPopup(e) {
    var res = e.responseText;
    document.getElementById('workerHistoryPopup').innerHTML = res;
    showModalDialog('workerHistoryPopup', false);
}

function showHistoryPopupWithWidth(e, width) {
    var res = e.responseText;
    document.getElementById('workerHistoryPopup').innerHTML = res;
    showModalDialogOfWidth('workerHistoryPopup', width);
}
function showPopupWithResponseAndWidth(htmlSelector, e, width) {
    var res = e.responseText;
    document.getElementById(htmlSelector).innerHTML = res;
    showModalDialogWithPositionAndWidth(htmlSelector, false, '5%', '30%', width);
}

function handleBusinessUnitRequester(url) {
    var selectedText = jQuery.trim(jQuery('#businessUnitRequesters-select option:selected').text());
    if (selectedText == "(Add One)") {
        jQuery.post(url,
        { ajax: 'true'}, function(htmlText) {
            jQuery('#note_businessUnitRequester').html(htmlText);
        });
        showModalDialog('create_businessUnitRequester', false);
    }
}

var myTextExtraction = function(node)
{
    return jQuery.trim(jQuery(node).text());
};

function showHideCertificationDiv(selectedValue, valueToCompare) {
    selectedValue = jQuery.trim(selectedValue);
    if (selectedValue == valueToCompare) {
        jQuery('#certificationDiv').show();
    } else {
        jQuery('#certificationDiv').hide();
    }
}

function addRawEmailAddress() {
    var eamilAddressToadd = jQuery('#newEmailAddress').val();
    if (!isBlank(eamilAddressToadd) && isValidEmailAddress(eamilAddressToadd)) {
        var str = '<li><input type="hidden" name="rawEmailRecipients" value="' + eamilAddressToadd + '"/>' + eamilAddressToadd +
                  ' <a>&nbsp;</a></li>';
        jQuery('.rawEmailRecipients ul').append(str);
        jQuery('.rawEmailRecipients ul>li>a:last').click(function() {
            removeRawEmailAddress(this);
        });
        jQuery('#newEmailAddress').val('');
        jQuery('#newEmailAddress').removeClass('errorBorder');
    } else {
        jQuery('#newEmailAddress').addClass('errorBorder');
    }
}

function removeRawEmailAddress(link) {
    jQuery(link).parent('li').remove();
}
function isValidEmailAddress(emailAddress) {
    var pattern = new RegExp(/^(("[\w-\s]+")|([\w-]+(?:\.[\w-]+)*)|("[\w-\s]+")([\w-]+(?:\.[\w-]+)*))(@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][0-9]\.|1[0-9]{2}\.|[0-9]{1,2}\.))((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){2}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\]?$)/i);
    return pattern.test(emailAddress);
}

function filterSelectBox(selectBoxId, ajaxUrl) {
    //    '${createLink(controller:'cnt', action:'act')}'+'?thisParam='+this.value)"/>
    jQuery('#' + selectBoxId + ' option[value!=""][value!=-1][value!=-2]').remove();
    jQuery.getJSON(ajaxUrl, function(messageTemplateList) {
        jQuery.each(messageTemplateList, function(index, jsonData) {
            jQuery('#' + selectBoxId).append('<option value="' + jsonData.id + '">' + jsonData.name + '</option>');
        });
    });
}

function toggleActions(rowIndex) {
    var isChecked = jQuery('table tr:eq(' + rowIndex + ') td:eq(0) :checkbox:first').attr('checked');
    if (isChecked) {
        jQuery('table tr:eq(' + rowIndex + ') td li').css('color', '#333333');
    } else {
        jQuery('table tr:eq(' + rowIndex + ') td li').css('color', 'gray');
        jQuery('table tr:eq(' + rowIndex + ') td:eq(1) :checkbox').removeAttr('checked');
    }
    jQuery('table tr:eq(' + rowIndex + ') td:eq(1) :checkbox').attr('disabled', !isChecked);
}

function validateAutoCompleteField(elementId, dataArray) {
    var existingValue = jQuery('#' + elementId).val();
    var isValid = jQuery.inArray(existingValue, dataArray) != -1;
    if (isValid) {
        jQuery('#' + elementId).removeClass('errorBorder');
    } else {
        jQuery('#' + elementId).addClass('errorBorder');
    }
    return isValid;
}

function appendToSubject(selectedValue) {
    jQuery("#subjectTemplate").val(jQuery("#subjectTemplate").val() + selectedValue);
}

function isNameUnique(ajaxUrl, idOfAlertMessage) {
    var name = jQuery('#name').val();
    var isValid = true;
    var res = jQuery.ajax({
        url: ajaxUrl,
        data: "name=" + name + "&id=" + idOfAlertMessage,
        async: false
    }).responseText;

    if (res == "false") {
        isValid = false;
        jQuery('#name').addClass('errorBorder');
    } else {
        jQuery('#name').removeClass('errorBorder');
    }
    var subject = jQuery.trim(jQuery('#subjectTemplate').val());
    if (subject.length) {
        jQuery('#subjectTemplate').removeClass('errorBorder');
    } else {
        isValid = false;
        jQuery('#subjectTemplate').addClass('errorBorder');
    }
    if (jQuery.trim(tinyMCE.activeEditor.getContent()).length) {
        jQuery('#bodyTemplate').parent('div').removeClass('errorBorder');
    } else {
        isValid = false;
        jQuery('#bodyTemplate').parent('div').addClass('errorBorder');
    }
    jQuery('.errorBorder:first').focus();
    isValid ? jQuery('#errorDiv').hide() : jQuery('#errorDiv').show();
    return isValid;
}
