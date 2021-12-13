<tr>
    <td valign="top">
        <label>Attributes</label>
    </td>
    <td valign="top">
        <div class="entitlement-attribute">
            <g:if test="${entitlementAttributes}">
                <g:each in="${entitlementAttributes}" var="attribute">
                    <g:if test="${attribute?.keyName}">
                        <g:hiddenField name="attributeIds" value="${attribute?.id}"/>
                        <input name="updateAttributeKey-${attribute?.id}" type="text" value="${attribute?.keyName}"/>
                        <textarea name="updateAttributeValue-${attribute?.id}" cols="" class="area"
                                  style="width:200px; height:17px;"
                                  rows="3">${attribute?.value}</textarea>
                        <a class="updateAttributeRemoveButton" rel="${attribute?.id}"><img src="../images/cross.gif"/>
                        </a>
                        <br/>
                    </g:if>
                </g:each>
            </g:if>
        </div>
        <a class="filterbutton" name="addAttribute" href="javascript:void(0);">
            <span>Add More</span>
        </a>
    </td>
</tr>
<script type="text/javascript">
    function clickBindingOnAttributeInputs() {
        jQuery('input[name|="attribute-"], textarea[name|="attribute-"]').click(function () {
            if ((jQuery(this).val() == 'Key') || (jQuery(this).val() == 'Value')) {
                jQuery(this).val('')
            }
        });
        jQuery('.newAttributeRemoveButton').click(function () {
            var elementId = jQuery(this).attr('rel');
            jQuery('input[name$="' + elementId + '"]').remove();
            jQuery('textarea[name$="' + elementId + '"]').remove();
            jQuery(this + 'br').remove();
            jQuery(this).remove();
        });
        jQuery('.updateAttributeRemoveButton').click(function () {
            var elementId = jQuery(this).attr('rel');
            jQuery('input[name$="' + elementId + '"]').remove();
            jQuery('input[name="attributeIds"][value="' + elementId + '"]').remove();
            jQuery('textarea[name$="' + elementId + '"]').remove();
            jQuery(this + 'br').remove();
            jQuery(this).remove();
        });

        jQuery('textarea[name^="newAttributeValue"]').unbind('blur');
        jQuery('textarea[name^="newAttributeValue"]').blur(function () {
            checkForLatsPasswordChangeAttribute(jQuery(this));
        });
    }
    jQuery(document).ready(function () {
        clickBindingOnAttributeInputs();
        jQuery('a[name|="addAttribute"]').click(function () {
            var index = Math.floor(Math.random() * 100);
            jQuery('div.entitlement-attribute').append('<input name="newAttributeKey-' + index + '" type="text" value="Key"/> <textarea name="newAttributeValue-' + index + '" cols="" class="area" style="width:200px; height:17px;" rows="3">Value</textarea> <a class="newAttributeRemoveButton" rel="' + index + '"><img src="../images/cross.gif"/></a><br/>');
            clickBindingOnAttributeInputs();
        });
    });

    function checkForLatsPasswordChangeAttribute(jQueryObject) {
        var nameAttr = jQueryObject.attr('name');
        var indexValue = nameAttr.split('-')[1];
        var keyObject = jQuery("input[name='newAttributeKey-" + indexValue + "\']");
        var crossObject = jQuery('a[rel="' + indexValue + '\"]');
        var lastPasswordInput = keyObject.val();
        if (lastPasswordInput == 'Last Password Change') {
            var dateRegex = /^(0[1-9]|1[0-2])\/(0[1-9]|1\d|2\d|3[01])\/(19|20)\d{2}$/;
            if (!dateRegex.test(jQueryObject.val())) {
                alert("Value of the attribute 'Last Password Change' should be in the format of 'MM/dd/yyyy'. Please re-add the attribute with the proper value.");
                jQueryObject.remove();
                keyObject.prev().remove();
                keyObject.remove();
                crossObject.find('br').remove();
                crossObject.remove();
            }
        }
        return false;
    }
</script>
