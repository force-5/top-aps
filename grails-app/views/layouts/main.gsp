<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <title><g:layoutTitle default="Grails"/></title>
    <g:render template="/layouts/importCssAndJs"/>
    <g:layoutHead/>
</head>
<body>
<g:render template="/layouts/newHeader"/>
<g:layoutBody/>
<span id="ajax_spinner" style="display: none;position:absolute; top:40%; left:50%; z-index:3000;">
    <img src="${createLinkTo(dir: 'images', file: 'spinner.gif')}"/>
</span>
<g:render template="/layouts/newFooter"/>
<script type="text/javascript">
     jQuery(".body form input:visible:first").focus();
</script>
</body>
</html>
