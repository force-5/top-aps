<%@ page import="grails.converters.JSON; com.force5solutions.care.*; com.force5solutions.care.ldap.Permission" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <title>Edit Message Template</title>
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
        <g:if test="${care.hasPermission(permission: Permission.READ_MESSAGE_TEMPLATE)}">
            <span class="menuButton"><g:link class="list" controller="apsMessageTemplate" action="list">Message Templates</g:link></span>
        </g:if>
        <g:else>
            <span class="menuButton"><a class="list-disabled">Message Templates</a></span>
        </g:else>
    </div>
    <div class="body">
      <h1>Edit Message Template</h1>
      <div id="errorDiv" class="errors" style="display:none;">
        Please enter valid values in the required fields
      </div>
      <div class="dialog">
        <g:form method="post" action="update" id="${messageTemplate?.id}" enctype="multipart/form-data">
          <g:render template="newMessageTemplateForm" model="[messageTemplate:messageTemplate]"/>
          <div id="submit-button">
            <input type="submit" class="button" value="Update"
                    onClick="return isNameUnique('${createLink(controller:'apsMessageTemplate',action:'validateName')}', '${messageTemplate?.id}');"/>
            <g:actionSubmit class="button" value="Cancel" action="show"/>
          </div>
        </g:form>
      </div>
    </div>
  </div>
</div>
</body>
</html>
