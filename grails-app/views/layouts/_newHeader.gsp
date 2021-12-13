<div id="container">
<div id="headerNew">
    <div class="dashboarddemo2-box-container21">
        <a href="${createLink(uri: '/')}">
            <img alt="logo" height="82" style="border: none;"
                    src="${createLinkTo(dir: 'images', file: ('apsLogo.png'))}"/>
        </a>
</div>
%{--<care:headerNews/>--}%

</div>
<div id="addNotesAndNewsDiv" style="display:none">
    <g:render template="/news/addNewsAndNotes"/>
</div>
<div id="editRssFeedDiv" style="display:none">
    <g:render template="/news/editRssFeed"/>
</div>
<div id="showNewsAndNotes" class="popupWindowShowNews" style="text-align:center;width:350px; padding-bottom:30px;">
    <div style="clear:both;"></div>
</div>
<div style="clear: both;"></div>
<g:if test="${session.loggedUser}">
    <span style="float:right;margin:10px 5px;">
        ${session.loggedUser}&nbsp;${(session.roles) ? '(' + session.roles?.join(',') + ')' : ''}&nbsp;&nbsp;&nbsp;&nbsp;
        <a name="logout" href="${createLink(controller: 'login', action: 'logout')}">Logout</a></span>
</g:if><g:else>
    <span style="float:right;margin:10px 5px;">
        <a name="login" href="${createLink(controller: 'login', action: 'index')}">Login</a>
    </span>
</g:else>
<br/><br/>
<g:render template="/layouts/topMenuBar"/>
