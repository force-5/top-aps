<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Filter Completed Tasks</title>
</head>

<body>
<br/>

<div id="wrapper">
    <div id="right-panel">
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
            <a class="filterbutton" href="${createLink(action: 'list')}"><span>Pending</span></a>
        </div>
    <div class="body">
        <div class="popupWindowTitle">Filter Completed Tasks</div>
        <br/>
        <g:form action="filteredCompletedTasks">
            <div class="form-section" style="padding-left:40px;">
                <div class="namerowbig"><span>Workflow Type</span>
                    <g:select class="listbox" style="width:150px;" name="workflowType" from="${apsWorkflowTypeList}"
                              optionKey="name" optionValue="name" value="${filterVO?.workflowType}"
                              noSelection="['':'(Select One)']"/>

                </div>

                <div class="namerowbig"><span>Worker</span>
                    <g:select class="listbox" style="width:170px;" name="workerId" from="${workers.findAll {it}}"
                              optionKey="id" value="${filterVO?.workerId}" noSelection="['':'(Select One)']"/>

                </div>

                <div class="namerowbig"><span>Entitlement</span>
                    <g:select class="listbox" name="entitlementId" style="width:120px;" from="${entitlements}"
                              optionKey="id" optionValue="name" value="${filterVO?.entitlementId}"
                              noSelection="['':'(Select One)']"/>

                </div>

                <div class="namerowbig"><span>Entitlement Policy</span>
                    <g:select class="listbox" name="entitlementPolicyId" style="width:120px;"
                              from="${entitlementPolicyList}"
                              optionKey="id" optionValue="name" value="${filterVO?.entitlementPolicyId}"
                              noSelection="['':'(Select One)']"/>

                </div>
            </div>

            <div class="form-section" style="padding-left:80px;">
                <div class="namerowbig""><span>Node Name</span>
                <g:select class="listbox" style="width:170px;" name="currentNodeName"
                          from="${currentNodeNames.findAll {it}}"
                          value="${filterVO?.currentNodeName}" noSelection="['':'(Select One)']"/>
            </div>

            <div class="namerowbig"><span>Security Role</span>
                <g:select class="listbox" name="securityRoleId" style="width:120px;" from="${securityRolesList}"
                          optionKey="id" optionValue="name" value="${filterVO?.securityRoleId}"
                          noSelection="['':'(Select One)']"/>
            </div>

            <div class="namerowbig"><span>Actor SLID</span>
                <g:select class="listbox" name="actorSlid" style="width:120px;"
                          from="${actorSlids}"
                          value="${filterVO?.actorSlid}"
                          noSelection="['':'(Select One)']"/>
            </div>
            </div>



            <div style="clear:both;text-align:center;">
                <br/>
                <br/>
                <input type="submit" class="button" value="Filter"/>
                <a href="${createLink(controller: 'apsWorkflowTask', action: 'list')}"><input type="button"
                                                                                              class="button"
                                                                                              value="Back"/></a>
            </div>
        </g:form>
    </div>

    <div id="filterDialog" class="popupWindowContractorFilter">
    </div>
</div>
</div>
<script type="text/javascript">
    jQuery(document).ready(function () {
        if (jQuery("#tablesorter tr").size() > 1) {
            jQuery("#tablesorter").tablesorter({textExtraction:myTextExtraction, sortList:[
                [1, 1]
            ], headers:{
                5:{
                    sorter:false
                }}});
        }
        jQuery('#groupResponse').submit(function () {
            if (jQuery(':checkbox:checked').length < 1) {
                alert("Please select any one check-box");
                return false;
            }
        });
        jQuery(':checkbox').change(function () {
            if (jQuery(':checked').length < 1) {
                jQuery(':checkbox').removeAttr('disabled');
            } else {
                jQuery(':checkbox').attr('disabled', 'true');
                jQuery("." + jQuery(this).attr("class")).removeAttr('disabled');
            }
        });
    });
</script>
</body>
</html>
