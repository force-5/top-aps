<select class="cannedSelectBox">
    <option value="">Select One</option>
    <g:each in="${cannedResponses}" var="cannedResponse">
        <option value="${cannedResponse.responseDescription}">${cannedResponse.response}</option>
    </g:each>
    <option value="user">Other</option>
</select>
<script type="text/javascript">
    jQuery(document).ready(function() {
        jQuery('#${target}').val(jQuery('#${target}').parents('div').children('select').val());
        jQuery('#${target}').parents('div').children('select').change(function() {
            var val = jQuery('#${target}').parents('div').children('select').val();
            jQuery('.error-status').hide();
            if (val == "user") {
                jQuery('#${target}').val("").css({background:'#fff'}).focus();
            }
            else {
                jQuery('#${target}').val(val).css({borderColor:'#000'});
            }
        });
    });
</script>