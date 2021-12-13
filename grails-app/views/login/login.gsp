<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Login Screen</title>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'login.css')}"/>
</head>
<body onLoad="document.loginForm.slid.focus();">
<div class="login-screen-password">
    <div class="workerlogo"><img alt="logo" src="${resource(dir: 'images', file: 'apsLogo.png')}"/></div>
    <div class="slid-name">
        <g:form name="loginForm" action="login" controller="login" method="post">
            <input type="hidden" name="targetUri" value="${params.targetUri}"/>
            <g:if test="${flash.errorMessage}">
                <div class="errorMessage">${flash.errorMessage}</div>
            </g:if>
            <table border="0" cellspacing="0" cellpadding="4">
                <tr>
                    <td width="20%"><strong>Login:</strong>
                        <input type="text" class="slid-name-input" name="slid" id="slid" value="${fieldValue(bean: loginCO, field: 'slid')}"/>
                    </td>
                </tr>
                <tr>
                    <td><strong>Password:</strong>
                        <input type="password" class="login-screen-password-input" name="password" id="password" value="${fieldValue(bean: loginCO, field: 'password')}"/></td>
                </tr>
                <tr>
                    <td height="40">
                        <span id="loginbutton" style="text-align:center;">
                            <input type="submit" style="margin:15px 0px 0px 80px;" class="button" name="loginbtn" id="loginbtn" value="Login"/>
                        </span>
                    </td>
                </tr>
            </table>
        </g:form>
        <g:render template="/layouts/newFooter"/>
    </div>
</div>
<div id="updateBrowser" style="color: red; margin: auto; display: none; font-size:12px; font-weight:bold; text-align:center;">
    Please upgrade your browser. The browser you are currently using is not supported by the APS application. Portions of the application may not display or function properly when used with your current browser.
</div>
</body>
<script type="text/javascript">
    if (navigator.appName == "Microsoft Internet Explorer") {
        if ((navigator.appVersion.toString().indexOf("MSIE 6.0") != -1)) {
            document.getElementById("updateBrowser").style.display = "block";
        }
    }
</script>
</html>









