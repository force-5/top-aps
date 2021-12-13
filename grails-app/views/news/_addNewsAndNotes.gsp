<div id="addEntitlementAccessPopup" style="width:400px;">
    <div id="head-add_contractor">Add News and Notes</div>
    <div class="contractor_cert-dept">
    <div class="contract_cert" style="height:150px;">
        <div id="news-validation-error" class="error-status" style="display:none;"><span>Field can not be left blank.</span></div>

        <g:formRemote name="newsAndNotesForm" method="post"
                url="${[controller:'news',action:'save']}" onSuccess="updateNewsDiv(e);">
            <div class="alert-con-popup">
                <div class="news-name"><g:message code="news.heading" default="News"/>&nbsp;<span class="asterisk">*</span></div>
                <div id='news-headline'>
                    <input type="text" name="headline" id="headline" style="width:260px"/>
                </div>
                <div class="clr"></div>
            </div>
            <div class="alert-con-popup">
                <div class="news-name"><g:message code="news.description" default="Description"/></div>
                <div id='news-description'>
                    <textarea rows="2" cols="3" name="description" id="description" style="height:70px; width:260px;" value=""></textarea>
                </div>
                <div class="clr"></div>
            </div>
            </div>
            <div class="department1">
                <input type="submit" class="button" name="okButton" value="OK" onclick="return validateNewsForm()">&nbsp;&nbsp;
                <input type="button" class="button simplemodal-close" value="Cancel" onclick="jQuery.modal.close();"/>
            </div>
        </g:formRemote>
    </div>
    <div id="close-add_contractor">
        <img src="${createLinkTo(dir: 'images', file: 'popup-bot-close1.gif')}"/>
    </div>
</div>
<script type="text/javascript">
    function validateNewsForm() {
        var result = true;
        var headline = jQuery('#headline').val();
        if (headline == "") {
            jQuery('#news-validation-error').show()
            jQuery('#headline').css('border', '1px solid red').focus();
            result = false;
        }
        return result;
    }
    function updateNewsDiv(e) {
        jQuery('#headerNewsListDiv').prepend(e.responseText);
        jQuery.modal.close();
    }
</script>