<meta name="layout" content="someLayoutThatDoesNotExist"/>
<div id="add_contractor_cert">
    <div id="head-add_contractor"> ${contractorHistory?.type}</div>
    <div class="contractor_cert-dept">
        <div class="contract_head clearfix">
            <div class="contract_value">
                <div class="value-head">Property</div>
                <div class="value-head">Old Value</div>
                <div class="value-head">New Value</div>
            </div>
        </div>

        <div class="contract_bg clearfix">
            <g:each in="${propertyChanges}" var="propertyChange" status="i">
                <g:if test="${i%2==0}"><div class="contract_value"></g:if>
                <g:else><div class="contract_value1 clearfix"></g:else>
                <div class="value-head">${propertyChange.propertyName}&nbsp;</div>
                <div class="value-head">${propertyChange.oldValue}&nbsp;</div>
                <div class="value-head">${propertyChange.newValue}&nbsp;</div>
                </div>
            </g:each>
        </div>
            <div class="department1">
                <input type="button" class="button " name="button" id="button" value="Close" onclick="jQuery.modal.close();"/>
            </div>
        </div>
        <div id="close-add_contractor"><img src="${createLinkTo(dir: 'images', file: 'popup-bot-close1.gif')}"/></div>
    </div>
</div>