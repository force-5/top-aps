<div class="selected-items">
  <select id="${name}-select" class="${cssClass}" style="${cssStyle}" onchange="${onchange}">
    <g:each in="${noSelection}" var="ns">
      <option value="${ns?.key}">${ns?.value?.encodeAsHTML()}</option>
    </g:each>

    <g:each in="${remainingList}" var="rl">
      <option value="${rl?.id}">${rl?.encodeAsHTML()}</option>
    </g:each>

  </select>
</div>
<div id="${name}-div" class="selected-items">
    <ul id="${name}-ul">
        <g:each var="sl" in="${selectedList}">
            <li>
                <g:hiddenField name="${name}" value="${sl?.id}"/>
                <g:if test="${isEditable}">
                    <a href="#" rel="entitlementId-${sl?.entitlement?.id}" style="position:relative;color:gray;">
                        ${sl?.encodeAsHTML()}</a>
                </g:if>
                <g:else>${sl?.encodeAsHTML()}</g:else>
                <a class="removeButton"><img src="${g.resource(dir: 'images', file: 'cross.gif')}"/></a>
            </li>
        </g:each>
    </ul>
</div>
<script type="text/javascript">
  jQuery('#${name}-select').bind('change', function() {
    updateSelectBox('${name}', "${g.resource(dir:'images', file:'cross.gif')}");
  });
  <g:if test="${disabled}">
  disableMultiSelect('${name}');
  </g:if>
  <g:else>
  enableMultiSelect('${name}');
  </g:else>
</script>

