<%@ page import="grails.converters.JSON; com.force5solutions.care.*" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <title>New Message Template</title>
  <script type="text/javascript" src="${createLinkTo(dir: 'tiny_mce', file: 'tiny_mce.js')}"></script>
  <script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'message.template.js')}"></script>
  <script type="text/javascript">
    initializeTinyMCE(${tagsMap as JSON});
  </script>
</head>
<body>
<br/>
<div id="wrapper">
  <div id="right-panel">
    <div class="nav">
      <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
      <span class="menuButton"><g:link controller="apsMessageTemplate" action="list" class="list">Message Templates</g:link></span>
    </div>
    <div class="body">
      <h1>New Message Template</h1>
      <div id="errorDiv" class="errors" style="display:none;">
        Please enter valid values in the required fields
      </div>
      <div class="dialog">
        <g:form method="post" action="save" enctype="multipart/form-data">
          <g:render template="newMessageTemplateForm"/>
          <div id="submit-button">
            <input type="submit" class="button" value="Submit"
                    onClick="return isNameUnique('${createLink(controller:'apsMessageTemplate',action:'validateName')}', '');"/>
          </div>
        </g:form>
      </div>
    </div>
  </div>
</div>
</body>
</html>
