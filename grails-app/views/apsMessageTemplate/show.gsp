<%@ page import="com.force5solutions.care.*; com.force5solutions.care.ldap.Permission" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show Message Template</title>
    <script type="text/javascript" src="${createLinkTo(dir: 'tiny_mce', file: 'tiny_mce.js')}"></script>
    <script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'message.template.js')}"></script>
    <script type="text/javascript">
        initializeTinyMCEReadOnly();
    </script>
</head>
<body>
<br/>
<div id="wrapper">
    <div id="right-panel">
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>

            <g:if test="${care.hasPermission(permission: Permission.CREATE_MESSAGE_TEMPLATE)}">
                <span class="menuButton"><g:link class="create" action="create">New Message Template</g:link></span>
            </g:if>
            <g:else>
                <span class="menuButton"><a class="create-disabled">New Message Template</a></span>
            </g:else>
            <g:if test="${care.hasPermission(permission: Permission.READ_MESSAGE_TEMPLATE)}">
                <span class="menuButton"><g:link class="list" controller="messageTemplate" action="list">Message Templates</g:link></span>
            </g:if>
            <g:else>
                <span class="menuButton"><a class="list-disabled">Message Templates</a></span>
            </g:else>
        </div>
        <div class="body">
            <h1>Show Message Template</h1>
            <div class="dialog">
                <g:if test="${flash.message}">
                    <div class="message">${flash.message}</div>
                </g:if>
                <g:hasErrors bean="${messageTemplate}">
                    <div class="errors">
                        Please enter valid values in the required fields
                    </div>
                </g:hasErrors>
                <g:render template="messageTemplateReadOnly"/>

                <br/>
                <div id="preview" class="popupWindow" style="width:500px;">
                    <care:messagePreview messageTemplate="${messageTemplate}"/>
                    <br/>
                    <div style="text-align: center;"><input type="button" value="Close" class="button simplemodal-close"/></div>
                </div>
                <div id="submit-button">
                    <g:form action="edit" id="${messageTemplate?.id}">
                        <g:if test="${care.hasPermission(permission: Permission.UPDATE_MESSAGE_TEMPLATE)}">
                            <input class="button" type="submit" value="Edit"/>
                        </g:if>
                        <g:else>
                            <input type="button" class="button" style="color:gray;" value="Edit"/>
                        </g:else>
                        <g:if test="${care.hasPermission(permission: Permission.DELETE_MESSAGE_TEMPLATE)}">
                            <g:actionSubmit class="button" onclick="return confirm('Are you sure?');" value="Delete"/>
                        </g:if>
                        <g:else>
                            <input type="button" class="button" style="color:gray;" value="Delete"/>
                        </g:else>
                    </g:form>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    jQuery(document).ready(function() {

        if (jQuery("#tablesorter tr").size() > 1) {
            jQuery("#tablesorter").tablesorter({textExtraction: myTextExtraction,sortList: [
                [1,0]
            ]});

            if (jQuery("#tablesorter tr").size() > 10) {
                jQuery("#tablesorter").Scrollable(205, 624);
            }
        }

        jQuery('input:text').attr('disabled', true);
        jQuery('textarea').attr('readonly', true);
        jQuery('select').attr('disabled', true);
    });

</script>
</body>
</html>
