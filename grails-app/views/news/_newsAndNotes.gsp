<div class="dashboarddemo2-box-container" style="padding-top:25px;">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td width="86%" height="23" align="center" class="header1"><div align="center">News and Notes</div></td>
        <td width="14%">
                <input type="image" style="border:none;" src="${createLinkTo(dir: 'images', file: 'add.jpg')}"
                        onclick="showModalDialog('addNewsAndNotes', false);"/></td>
        </tr>
    </table>
    <div class="dashboarddemo2-box">
        <div class="dashboarddemo2-box1"></div><div class="dashboarddemo2-box22"></div>
        <div class="dashboarddemo2-box23">
            <div class="dashboarddemo2-overflow-box">
                <div id='content'>
                    <div id='mycustomscroll' class='flexcroll'>
                        <div class='lipsum' id="newsListDiv">
                            <g:each in="${news}" var="news">
                                <g:render template="/news/newsAndNotesLink" model="[news:news]"/>
                            </g:each>
                        </div>
                    </div>
                </div>
            </div>

            <div class="dashboarddemo2-img3"></div><div class="dashboarddemo2-img4"></div></div>
    </div>
</div>
<div id="add-news">
    <g:render template="/news/addNewsAndNotes"/>
</div>

