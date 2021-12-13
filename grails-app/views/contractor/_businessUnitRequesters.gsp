<div style="float:left;">Business Unit Requester<span class="asterisk">*</span></div>
    <ui:multiSelect
            name="businessUnitRequesters"
            from="${remainingBusinessUnitRequesters.sort{it?.name?.toLowerCase()}}"
            value="${selectedBusinessUnitRequesters}"
            noSelection="['':'(Select One)',' ':'(Add One)']"
            onchange="handleBusinessUnitRequester('${createLink(controller:'businessUnitRequester', action:'createBusinessUnitRequester')}');"
            class="listbox auto-resize"
            style="width:90px;"/>
<g:if test="${hasErrors(bean: worker, field: 'businessUnitRequesters','errors')=='errors'}">
    <script type="text/javascript">
        jQuery(document).ready(function(){
            jQuery('#businessUnitRequesters-select').wrap('<span style="border:1px solid red"></span>');
        });
    </script>
</g:if>