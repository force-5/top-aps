<div id="addEntitlementAccessPopup" style="width:400px;">
    <div id="head-add_contractor">Edit Rss Feed </div>
    <div class="contractor_cert-dept">
    <div class="contract_cert" style="height:130px;">
        <div id="rss-validation-error" class="error-status" style="margin-top:0; padding-top:0; display:none;"><span>Field can not be left blank.</span></div>

        <g:formRemote name="rssFeedForm" method="post"
                url="${[controller:'news',action:'editRssFeed']}" onSuccess="updateRssFeedDiv(e);">
            <div class="alert-con-popup">
                <div class="news-name"><g:message code="news.heading" default="Rss Feed Url"/></div>
                <div id='news-headline'>
                    <input type="text" name="feedUrl" id="feedUrl" style="width:260px"/>
                </div>
                <div class="clr"></div>
            </div>
            </div>
            <div class="department1">
                <input type="submit" class="button" name="okButton" value="OK" onclick="return validateRssForm()">&nbsp;&nbsp;
                <input type="button" class="button simplemodal-close" value="Cancel" onclick="jQuery.modal.close();"/>
            </div>
        </g:formRemote>
    </div>
    <div id="close-add_contractor">
        <img src="${createLinkTo(dir: 'images', file: 'popup-bot-close1.gif')}"/>
    </div>
</div>
<script type="text/javascript">
    function validateRssForm() {
        var result = true;
        var feedUrl = jQuery('#feedUrl').val();
        if (feedUrl == "") {
            jQuery('#rss-validation-error').show()
            jQuery('#feedUrl').css('border', '1px solid red').focus();
            result = false;
        }
        return result;
    }
    function updateRssFeedDiv(e) {
        jQuery('#rssFeedDiv').html(e.responseText);
        jQuery.modal.close();
    }
</script>