<%@ page import="com.force5solutions.care.aps.Entitlement; com.force5solutions.care.workflow.CentralWorkflowTask; com.force5solutions.care.cc.Worker" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Report</title>
</head>

<body>
<br/>

<div id="wrapper">
<div id="right-panel">
<div class="nav">
    <span class="menuButton"><g:link class="home" url="${resource(dir: '')}">Home</g:link></span>
</div>

<div>
    <h1>Reports</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <table>
        <thead>
        <th width="40%">
            Choose Report
        </th>
        <th align="left">
            Define Filters
        </th>
        </thead>
        <tbody>
        <tr>
            <td>
                <input id="entitlementRoleToEntitlementsAnchor" name="vendorAnchor"
                       type="radio"/>Entitlement Role To Entitlements<br/>
                <input id="entitlementToEntitlementRolesAnchor" name="vendorAnchor"
                       type="radio"/>Entitlements To Entitlement Roles<br/>
                <input id="bookEndAuditAnchor" name="vendorAnchor" type="radio"/>Book End Auditing<br/>
                <input id="workerArchiveAnchor" name="vendorAnchor" type="radio"/>Worker Archive<br/>
                <input id="entitlementsAccessReportAnchor" name="vendorAnchor"
                       type="radio"/>Entitlements Access Report<br/>
                <input id="criticalAssetAuditReportAnchor" name="vendorAnchor"
                       type="radio"/>Critical Asset Audit Report<br/>
                <input id="workerAuditReportAnchor" name="vendorAnchor"
                       type="radio"/>Worker Audit Report<br/>
                <input id="accountPasswordChangeAnchor" name="vendorAnchor"
                       type="radio"/>Account Password Change<br/>
            </td>
            <td style="border-left-color:#333333;">
                <div id="entitlementRoleToEntitlementsFilter" class="reportFilter" align="left"
                     style="display:none">
                    <g:form action="entitlementRoleToEntitlements">
                        <div>
                            <input type="hidden" name="SUBREPORT_DIR"
                                   value="${application.getRealPath('/reports') + File.separator}"/>
                            <input type="hidden" name="_format" value="PDF"/>
                        </div>
                        <input type="submit" id="entitlementRoleToEntitlementsSubmit" class="button"
                               value="OK"/>
                    </g:form>
                </div>

                <div id="entitlementToEntitlementRolesFilter" class="reportFilter" align="left"
                     style="display:none">
                    <g:form action="entitlementToEntitlementRoles">
                        <div>
                            <input type="hidden" name="SUBREPORT_DIR"
                                   value="${application.getRealPath('/reports') + File.separator}"/>
                            <input type="hidden" name="_format" value="PDF"/>
                        </div>
                        <input type="submit" id="entitlementToEntitlementRolesSubmit" class="button"
                               value="OK"/>
                    </g:form>
                </div>

                <div id="bookEndAuditFilter" class="reportFilter" align="left" style="display:none">
                    <g:form controller="util" action="createBookEndAuditEvidencePackage">
                        <div>
                            <input type="hidden" name="SUBREPORT_DIR"
                                   value="${application.getRealPath('/reports') + File.separator}"/>
                            <input type="hidden" name="_format" value="PDF"/>

                        </div>
                        <%
                            List<Worker> workers = Worker.findAll("from Worker as w where w.id in (select distinct wer.worker.id from WorkerEntitlementRole as wer where wer.wasEverActive = true) order by w.person.lastName")
                        %>
                        <div>
                            Select Worker : <g:select from="${workers}" style="width:142px;"
                                      optionKey="id" name="workerId"
                                      noSelection="['': '(Select One)']"/>
                        </div>

                        <div>
                            <input type="submit" id="complianceSubmit" class="button" value="OK"/>
                        </div>
                    </g:form>
                </div>

                <div id="workerAuditReportFilter" class="reportFilter" align="left" style="display:none">
                    <g:form controller="util" action="createNewWorkerAuditReport">
                        <div>
                            <input type="hidden" name="SUBREPORT_DIR"
                                   value="${application.getRealPath('/reports') + File.separator}"/>
                            <input type="hidden" name="_format" value="PDF"/>

                        </div>

                        <div>
                            Select Worker : <g:select from="${workers}" style="width:142px;"
                                      optionKey="id" name="workerId"
                                      noSelection="['': '(Select One)']"/>
                        </div>

                        <div>
                            <input type="submit" id="workerAuditReportSubmit" class="button" value="OK"/>
                        </div>
                    </g:form>
                </div>

                <div id="entitlementsAccessReportFilter" class="reportFilter" align="left" style="display:none">
                    <g:form action="entitlementsAccessReport">
                        <div style="font-size: 12px;padding-bottom: 5px;">Choose Entitlements:</div>
                        <g:select from="${Entitlement.list(sort: 'name')}" style="width:300px; height: 200px;"
                                  multiple="true"
                                  optionKey="id" name="entitlementIds" id="entitlementIds"/>

                        <div>
                            <input type="submit" id="entitlementsAccessReportSubmit" class="button" value="OK"/>
                        </div>
                    </g:form>
                </div>

                <div id="workerArchiveReportFilter" class="reportFilter" align="left" style="display:none">
                    <g:form action="workerArchiveReports">
                        <div>
                            <input type="hidden" name="_format" value="XLS"/>

                        </div>

                        <div>
                            Select Worker : <g:select from="${workers}"
                                                      noSelection="['allWorkers': 'All Workers']"
                                                      style="width:142px;" optionKey="id" optionValue="name"
                                                      id="workerSelectId"
                                                      name="workerSelectId"/><br/>

                            <div style="width:300px;">
                                Date Range : <br/><input type="radio" id="workerArchiveAllDateRadio"
                                                         name="workerArchiveDateRadio"
                                                         value="workerArchiveAllDateRadio"
                                                         checked='true'/> All Dates<br/>

                                <input type="radio" id="workerArchiveDateRangeRadio"
                                       name="workerArchiveDateRadio"
                                       value="workerArchiveDateRangeRadio"/>
                                From:<calendar:datePicker name="workerArchiveFromDate"
                                                          id="workerArchiveFromDate"
                                                          defaultValue="${new Date() - 365}"/><br/>

                                <div style="padding-left:31px">
                                    To:<calendar:datePicker name="workerArchiveToDate" id="workerArchiveToDate"
                                                            defaultValue="${new Date()}"/>
                                </div>
                            </div>

                            <br/>
                            Select Focus: <g:select from="${['Entitlement', 'Certification', 'Profile']}"
                                                    name="focusArea"
                                                    id="focusArea"/>
                        </div>
                        <br/>

                        <div>
                            <input type="submit" id="workerArchiveSubmit" class="button" value="OK"/>
                        </div>
                    </g:form>
                </div>

                <div id="criticalAssetAuditReportFilter" class="reportFilter" align="left"
                     style="display:none">
                    <g:form action="createCriticalAssetAuditReport">
                        <div>
                            <input type="radio" id="createCriticalAssetAuditReportForADateRadio"
                                   name="createCriticalAssetAuditReportRadio"
                                   value="createCriticalAssetAuditReportForADateRadio"/>
                            Date: <calendar:datePicker name="createCriticalAssetAuditReportForADate"
                                                       id="createCriticalAssetAuditReportForADate"
                                                       defaultValue="${new Date()}"/>
                            <br/>

                            <input type="radio" id="createCriticalAssetAuditReportForADateRangeRadio"
                                   name="createCriticalAssetAuditReportRadio"
                                   value="createCriticalAssetAuditReportForADateRangeRadio"/> Date Range:
                            <br/>

                            <div style="padding-left:31px">
                                From:<calendar:datePicker name="createCriticalAssetAuditReportForADateRangeFrom"
                                                          id="createCriticalAssetAuditReportForADateRangeFrom"
                                                          defaultValue="${new Date() - 365}"/><br/>
                            </div>

                            <div style="padding-left:48px">
                                To:<calendar:datePicker name="createCriticalAssetAuditReportForADateRangeTo"
                                                        id="createCriticalAssetAuditReportForADateRangeTo"
                                                        defaultValue="${new Date()}"/>
                            </div>
                        </div>
                        <br/>

                        <div>
                            <input type="submit" id="createCriticalAssetAuditReportSubmit"
                                   class="button" value="OK"/>
                        </div>
                    </g:form>
                </div>

                <div id="accountPasswordChangeFilter" class="reportFilter" align="left"
                     style="display:none">
                    <g:form action="accountPasswordChange">
                        <div>
                        <input type="submit" id="accountPasswordChangeSubmit" class="button" value="OK"/>
                    </g:form>
                </div>

            </td>
        </tr>
        </tbody>
    </table>
</div>
</div>
</div>
<script type="text/javascript">
    jQuery(document).ready(function () {
///////////////////////////////////////////////////////////////////////////

        jQuery('#entitlementRoleToEntitlementsAnchor').bind('click', function () {
            jQuery('.reportFilter').hide();
            jQuery('#entitlementRoleToEntitlementsFilter').show();
        });

        jQuery('#entitlementToEntitlementRolesAnchor').bind('click', function () {
            jQuery('.reportFilter').hide();
            jQuery('#entitlementToEntitlementRolesFilter').show();
        });

        jQuery('#bookEndAuditAnchor').bind('click', function () {
            jQuery('.reportFilter').hide();
            jQuery('#bookEndAuditFilter').show();
        });
        jQuery('#entitlementsAccessReportAnchor').bind('click', function () {
            jQuery('.reportFilter').hide();
            jQuery('#entitlementsAccessReportFilter').show();
        });
        jQuery('#criticalAssetAuditReportAnchor').bind('click', function () {
            jQuery('.reportFilter').hide();
            jQuery('#criticalAssetAuditReportFilter').show();
        });
        jQuery('#workerAuditReportAnchor').bind('click', function () {
            jQuery('.reportFilter').hide();
            jQuery('#workerAuditReportFilter').show();
        });
        jQuery('#accountPasswordChangeAnchor').bind('click', function () {
            jQuery('.reportFilter').hide();
            jQuery('#accountPasswordChangeFilter').show();
        });
    });
    ///////////////////////////////////////////////////////////////////////////

    jQuery('#workerArchiveAnchor').bind('click', function () {
        jQuery('.reportFilter').hide();
        jQuery('#workerArchiveReportFilter').show();
    });

    jQuery('#workerArchiveAllDateRadio').click(function () {
        jQuery('#workerArchiveFromDate-trigger').hide();
        jQuery('#workerArchiveToDate-trigger').hide();
    });
    jQuery('#workerArchiveDateRangeRadio').click(function () {
        jQuery('#workerArchiveFromDate-trigger').show();
        jQuery('#workerArchiveToDate-trigger').show();
    });

    jQuery('#workerArchiveFromDate-trigger').hide();
    jQuery('#workerArchiveToDate-trigger').hide();

    jQuery('#workerArchiveSubmit').bind('click', function () {
        var selectedId = jQuery('#workerSelectId').val();
        if (selectedId.length < 1) {
            alert('Please select a Worker first.');
            return false;
        }
        var fromDateLen = jQuery.trim(jQuery('#workerArchiveFromDate_value').val()).length;
        var toDateLen = jQuery.trim(jQuery('#workerArchiveToDate_value').val()).length;
        if ((jQuery('#workerArchiveDateRangeRadio').attr('checked')) && (fromDateLen < 1 || toDateLen < 1)) {
            alert('Please specify a valid date range.');
            return false;
        }
    });

    ///////////////////////////////////////////////////////////////////////////

    jQuery('#createCriticalAssetAuditReportForADateRadio').click(function () {
        jQuery('#createCriticalAssetAuditReportForADateRangeFrom-trigger').hide();
        jQuery('#createCriticalAssetAuditReportForADateRangeTo-trigger').hide();
        jQuery('#createCriticalAssetAuditReportForADate-trigger').show();
    });
    jQuery('#createCriticalAssetAuditReportForADateRangeRadio').click(function () {
        jQuery('#createCriticalAssetAuditReportForADateRangeFrom-trigger').show();
        jQuery('#createCriticalAssetAuditReportForADateRangeTo-trigger').show();
        jQuery('#createCriticalAssetAuditReportForADate-trigger').hide();
    });

    jQuery('#createCriticalAssetAuditReportForADateRangeFrom-trigger').hide();
    jQuery('#createCriticalAssetAuditReportForADateRangeTo-trigger').hide();
    jQuery('#createCriticalAssetAuditReportForADate-trigger').hide();

    jQuery('#createCriticalAssetAuditReportSubmit').bind('click', function () {
        var fromDateLen;
        var toDateLen;

        if (jQuery('#createCriticalAssetAuditReportForADateRadio').attr('checked')) {
            fromDateLen = jQuery.trim(jQuery('#createCriticalAssetAuditReportForADate_value').val()).length;
        } else {
            fromDateLen = jQuery.trim(jQuery('#createCriticalAssetAuditReportForADateRangeFrom_value').val()).length;
            toDateLen = jQuery.trim(jQuery('#createCriticalAssetAuditReportForADateRangeTo_value').val()).length;
        }
        if (((jQuery('#createCriticalAssetAuditReportForADateRadio').attr('checked')) && (fromDateLen < 1)) || ((jQuery('#createCriticalAssetAuditReportForADateRangeRadio').attr('checked')) && (fromDateLen < 1 || toDateLen < 1))) {
            alert('Please specify a valid date range.');
            return false;
        }
        if ((!jQuery('#createCriticalAssetAuditReportForADateRadio').attr('checked')) && (!jQuery('#createCriticalAssetAuditReportForADateRangeRadio').attr('checked'))) {
            alert('Please select one option.');
            return false;
        }
    });

    ///////////////////////////////////////////////////////////////////////////

    jQuery('#entitlementsAccessReportSubmit').bind('click', function () {
        if (jQuery('#entitlementIds option:selected').length < 1) {
            alert('Please select an Entitlement');
            return false;
        }
    });

</script>
</body>
</html>