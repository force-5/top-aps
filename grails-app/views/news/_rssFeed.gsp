<div id="rssFeedDiv">
<g:each id="set1" in="${rssFeedsVOs}" var="rssFeedsVO" status="i">
    <span id="spanrssFeed${i}" style="display:none;">${rssFeedsVO.description}</span>
    <a id="rssFeed${i}" href="${rssFeedsVO.uri}" target="_blank" title="">${rssFeedsVO.title}</a>
</g:each>
</div>