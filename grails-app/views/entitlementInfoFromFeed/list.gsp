
<%@ page import="com.force5solutions.care.aps.EntitlementInfoFromFeed" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'entitlementInfoFromFeed.label', default: 'EntitlementInfoFromFeed')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="entitlementName" title="${message(code: 'entitlementInfoFromFeed.entitlementName.label', default: 'Entitlement Name')}" />
                        
                            <g:sortableColumn property="entitlementId" title="${message(code: 'entitlementInfoFromFeed.entitlementId.label', default: 'Entitlement Id')}" />
                        
                            <g:sortableColumn property="areaAttributes" title="${message(code: 'entitlementInfoFromFeed.areaAttributes.label', default: 'Area Attributes')}" />
                        
                            <g:sortableColumn property="isProcessed" title="${message(code: 'entitlementInfoFromFeed.isProcessed.label', default: 'Is Processed')}" />
                        
                            <g:sortableColumn property="readerAttributes" title="${message(code: 'entitlementInfoFromFeed.readerAttributes.label', default: 'Reader Attributes')}" />
                        
                            <g:sortableColumn property="workflowType" title="${message(code: 'entitlementInfoFromFeed.workflowType.label', default: 'Workflow Type')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${entitlementInfoFromFeedList}" status="i" var="entitlementInfoFromFeed">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${entitlementInfoFromFeed.id}">${fieldValue(bean: entitlementInfoFromFeed, field: "entitlementName")}</g:link></td>

                        
                            <td><g:link action="show" id="${entitlementInfoFromFeed.id}">${fieldValue(bean: entitlementInfoFromFeed, field: "entitlementId")}</g:link></td>

                        
                            <td><g:link action="show" id="${entitlementInfoFromFeed.id}">${fieldValue(bean: entitlementInfoFromFeed, field: "areaAttributes")}</g:link></td>

                        
                            <td><g:link action="show" id="${entitlementInfoFromFeed.id}">${fieldValue(bean: entitlementInfoFromFeed, field: "isProcessed")}</g:link></td>

                        
                            <td><g:link action="show" id="${entitlementInfoFromFeed.id}">${fieldValue(bean: entitlementInfoFromFeed, field: "readerAttributes")}</g:link></td>

                        
                            <td><g:link action="show" id="${entitlementInfoFromFeed.id}">${fieldValue(bean: entitlementInfoFromFeed, field: "workflowType")}</g:link></td>

                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${entitlementInfoFromFeedTotal}" />
            </div>
        </div>
    </body>
</html>
