<%@ page import="com.force5solutions.care.ldap.Permission" %>
<div class="dashboarddemo2-box-container30">
    <div class="dashboarddemo2-box">
        <div class="dashboarddemo2-box1"></div><div
            class="dashboarddemo2-box22"></div>
        <div class="dashboarddemo2-box31">

            <div id='content'>
                <div id='mycustomscroll-2' class='flexcroll'>
                    <div class='lipsum' id="headerNewsListDiv" style="width:480px;">
                        <care:rssFeed/>
                        <g:each in="${headerNews}" var="headerNews">
                            <g:render template="/news/newsAndNotesLink" model="[news:headerNews]"/>
                        </g:each>
                    </div>
                </div>
            </div>
            <div class="dashboarddemo2-img3"></div><div
                class="dashboarddemo2-img4"></div></div>
    </div>
    <div class="clr"></div>
</div>
<g:if test="${care.hasPermission(permission: Permission.ADD_NEWS_AND_NOTES)}">
    <input type="image" style="border:none;float:right;" src="${createLinkTo(dir: 'images', file: 'add-news.gif')}"
            onclick="AddNewsAndNotes()"/></td>
</g:if>
<g:if test="${care.hasPermission(permission: Permission.EDIT_RSS_FEEDS)}">
    <input type="image" style="border:none;float:right;" src="${createLinkTo(dir: 'images', file: 'edit-rss.gif')}"
            onclick="editRssFeed()"/></td>
</g:if>
<script type="text/javascript">
    function AddNewsAndNotes() {
        jQuery('#headline').val('')
        jQuery('#description').val('')
        jQuery('#news-validation-error').hide()
        jQuery('#headline').css('border', '1px solid #CCCCCC').focus();
        showModalDialog('addNotesAndNewsDiv', true);
    }
    function editRssFeed() {
        var rssFeedUrl = "${rssFeedUrl}";
        jQuery('#feedUrl').val(rssFeedUrl)
        jQuery('#rss-validation-error').hide()
        jQuery('#feedUrl').css('border', '1px solid #CCCCCC').focus();
        showModalDialog('editRssFeedDiv', true);
    }
</script>

