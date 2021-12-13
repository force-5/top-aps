<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Upload/Execute Fixture</title>
</head>
<body>
<br/>
<div id="wrapper">
    <div id="right-panel">
        <div class="body">
            <h1>Upload/Execute Fixture</h1>
            <g:if test="${flash.message}">
                <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <g:form name="myUpload" controller="util"
                 method="post" enctype="multipart/form-data">
                    <input type="file" name="fixtureFile"/>
                    <br/>
                    <br/>
                    <g:checkBox name="executeFixtureCheckBox"/> Execute the fixture after the upload.
                    <br/>
                    <br/>
                    <g:actionSubmit action="executeFixture" value="Upload"/>
                </g:form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
