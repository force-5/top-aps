<script type="text/javascript">
    jQuery(document).ready(function () {
        jQuery('#rowCount').unbind('change');
        jQuery('#rowCount').change(function () {
            var rowCount = jQuery('#rowCount').val();
            jQuery.post("${createLink(action: 'list', controller: controller)}", {rowCount:rowCount, ajax:true}, function (htmlText) {
                jQuery('#list').html(htmlText);
            })
        });
    });
</script>