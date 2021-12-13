<div class="popupWindowTextForNews" style="clear:both; height:75px; overflow:auto;">
    <span style="float:left; display:block;">${news.description}</span>
</div>
<br/>

<input type="button" class="button-news" value="Close" onclick="jQuery.modal.close();"/>
<g:formRemote name="newsAndNotesForm" method="post"
        url="${[controller:'news',action:'delete']}" onSuccess="updateNewsDivAfterDeletion(e);">
    <input type='hidden' name="id" value="${news.id}">
    <input type="submit" class="button-delete" name="okButton" value="Delete" onclick="return confirm('Are you sure?');
    jQuery.modal.close();"/>
</g:formRemote>






