<%@ page import="com.force5solutions.care.aps.EntitlementInfoFromFeed" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'entitlementInfoFromFeed.label', default: 'EntitlementInfoFromFeed')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
</div>

<div class="body">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="dialog">
        <table>
            <tbody>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="entitlementInfoFromFeed.entitlementName.label" default="Entitlement Name"/></td>

                <td valign="top" class="value">${fieldValue(bean: entitlementInfoFromFeed, field: "entitlementName")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="entitlementInfoFromFeed.entitlementId.label" default="Entitlement Id"/></td>

                <td valign="top" class="value">${fieldValue(bean: entitlementInfoFromFeed, field: "entitlementId")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="entitlementInfoFromFeed.areaAttributes.label" default="Area Attributes"/></td>

                <td valign="top" class="value">${fieldValue(bean: entitlementInfoFromFeed, field: "areaAttributes")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="entitlementInfoFromFeed.isProcessed.label" default="Is Processed"/></td>

                <td valign="top" class="value"><g:formatBoolean boolean="${entitlementInfoFromFeed?.isProcessed}"/></td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="entitlementInfoFromFeed.readerAttributes.label" default="Reader Attributes"/></td>

                <td valign="top" class="value">${fieldValue(bean: entitlementInfoFromFeed, field: "readerAttributes")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="entitlementInfoFromFeed.workflowType.label" default="Workflow Type"/></td>

                <td valign="top" class="value">${fieldValue(bean: entitlementInfoFromFeed, field: "workflowType")}</td>

            </tr>

            </tbody>
        </table>
    </div>
</div>
</body>
</html>
