%{--this meta tag fixes javascript error on all the pages using "scroll table plugin"--}%
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>

<calendar:resources lang="en" theme="blue"/>
<link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"/>
<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
<link rel="stylesheet" href="${resource(dir: 'css', file: 'common.css')}"/>
<link rel="stylesheet" href="${resource(dir: 'css', file: 'style.css')}"/>
%{--<link rel="stylesheet" href="${resource(dir: 'css', file: 'datepicker.css')}"/>--}%
<link rel="stylesheet" href="${resource(dir: 'css', file: 'dialogbox.css')}"/>
<link rel="stylesheet" href="${resource(dir: 'css', file: 'tableSorter.css')}"/>
<link rel="stylesheet" href="${resource(dir: 'css', file: 'multi.select.css')}"/>
<link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery.autocomplete.css')}"/>
<link rel="stylesheet" href="${resource(dir: 'css', file: 'flexcrollstyles.css')}"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'tutorsty.css')}"/>
<g:javascript library="jquery"/>
<script type="text/javascript">jQuery.noConflict();</script>
<g:javascript library="prototype"/>
<script type="text/javascript" src="${createLinkTo(dir: 'js/prototype', file: 'scriptaculous.js')}"></script>
<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'jquery.simplemodal.js')}"></script>
<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'profile.js')}"></script>
<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'jQuery.tablesorter.js')}"></script>
<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'webtoolkit.scrollabletable.js')}"></script>
<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'webtoolkit.jscrollable.js')}"></script>
%{--<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'datepicker.js')}"></script>--}%
%{--<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'care.profile.js')}"></script>--}%
<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'care.access.js')}"></script>
<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'multi.select.js')}"></script>
<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'jquery.autocomplete.js')}"></script>
<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'chartplugin.js')}"></script>
<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'chart.js')}"></script>
<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'canvaschartpainter.js')}"></script>
<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'easyTooltip.js')}"></script>
<link rel="stylesheet" href="${resource(dir:'css', file:'core.css')}" type="text/css" />
<link rel="stylesheet" href="${resource(dir:'css', file:'jquery-ui.cupertino.css')}" type="text/css" />
<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'drop-dwn.js')}"></script>
<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'flexcroll.js')}"></script>
<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'entitlementrole.js')}"></script>
<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'buentitlement.js')}"></script>


 <style type="text/css" media="screen">
                #jMonthCalendar .Meeting { background-color: #DDFFFF;}
                #jMonthCalendar .Birthday { background-color: #DD00FF;}
                #jMonthCalendar #Event_3 { background-color:#0000FF; }
            </style>

 
%{--Firebug for Internet Explorer .  Uncomment to enable this--}%
%{--<script type='text/javascript' src='http://getfirebug.com/releases/lite/1.2/firebug-lite-compressed.js'></script>--}%

<g:javascript library="application"/>

<script type="text/javascript">
    function adjustDropDownWidth() {
        if (jQuery.browser.msie) {
            jQuery('select.auto-resize').each(function() {
                var originalWidth = jQuery(this).css('width');
                jQuery(this).parent('div').css('overflow', 'hidden');
                jQuery(this).parent('span').css('overflow', 'hidden');
                jQuery(this).blur(function() {
                    jQuery(this).width(originalWidth);
                });
                jQuery(this).change(function() {
                    jQuery(this).width(originalWidth);
                });
                jQuery(this).mousedown(function() {
                    jQuery(this).css("width", "auto");
                });
            });
        }
    }

    function removeBorderFromInputBoxes(){
        jQuery('input[type=radio]').css('border', 'none');
        jQuery('input[type=radio]').css('background-color', 'transparent');
        jQuery('input[type=checkbox]').css('border', 'none');
        jQuery('input[type=checkbox]').css('background-color', 'transparent');
    }

    jQuery(document).ready(function() {
        jQuery('select option:selected').each(function(i) {
            jQuery(this).parent('select').attr('title', jQuery(this).text());
        });
        jQuery("select option").each(function(i) {
            this.title = this.text;
        });

        removeBorderFromInputBoxes();
        adjustDropDownWidth();
        //        Focus the first normal or errorneous text field
        if (jQuery('.errors').size() > 0) {
            jQuery('input.errors:first').focus();
            jQuery('.errors input:visible:first').focus();
        }
        else {
            jQuery('input:text:visible:first').focus();
        }

        Ajax.Responders.register({
            onCreate: function() {
                jQuery("#ajax_spinner").show();
            },
            onComplete: function() {
                jQuery("#ajax_spinner").hide();
                adjustDropDownWidth();
            }
        });
        jQuery("#ajax_spinner").ajaxStart(function() {
            jQuery(this).show();
        });
        jQuery("#ajax_spinner").ajaxStop(function() {
            jQuery(this).hide();
            removeBorderFromInputBoxes();
            adjustDropDownWidth();
        });
        jQuery.ajaxSetup({cache: false});

    });
</script>
<script type="text/javascript">
    function showNews(response) {
        var text = response.responseText;
        jQuery('#showNewsAndNotes').html(text);
        showModalDialog('showNewsAndNotes', false);
    }
</script>
<script type="text/javascript" >
    function updateNewsDivAfterDeletion(e) {
        var res=e.responseText;
        jQuery("a[rel="+res+"]").remove();
        jQuery.modal.close();
    }
</script>