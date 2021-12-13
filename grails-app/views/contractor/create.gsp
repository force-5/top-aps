<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta name="layout" content="contractor"/>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
    <title>CARE By Force 5 : Profile</title>
</head>
<body>
<br/>
<div id="wrapper">
    <g:if test="${flash.message}">
        <div align="center"><b>${flash.message}</b></div>
    </g:if>
            <g:hasErrors bean="${contractorInstance}">
                <div class="error-status" style="text-align:center;">
                    Please enter values into the required fields<br/>
                    <g:if test="${contractorInstance.slid}">
                        <g:fieldError bean="${contractorInstance}" field= "slid"/>
                    </g:if>
                </div>
            </g:hasErrors>
    <div id="right-panel">
        <g:form name="contractorForm" controller="contractor" action="save" method="post" enctype="multipart/form-data" onsubmit="setupBusinessUnitRequesterIds();">
            <g:hiddenField name="id" value="${contractorInstance?.id}"/>
            <div id="right-top">
                <g:render template="/contractor/contractorCreateForm" model="['contractorInstance':contractorInstance]" />
            </div>
            <br/>
            <div id="submit-button" style="display:none;">
                <input type="submit" id="submit" class="button" value="Submit"/>
            </div>
            <div id="edit-button" style="display:none;">
                <input type="submit" class="button" value="Edit"/>
            </div>
        </g:form>
        <div id="requiredIndicator" >&nbsp;<span style="color:red;">*</span> indicates a required field</div>
    </div>
    <g:render template="/contractor/remoteForms"/>                       
</div>
<script type="text/javascript">
    jQuery('#submit-button').attr('style', 'display:block;');
</script>
</body>
</html>
