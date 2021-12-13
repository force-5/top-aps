<html>
<head>
    <meta name="layout" content="main"/>
    <title>TOP By Force 5 : Trigger Feeds</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
</div>
<div id="wrapper">
    <g:if test="${flash.message}">
        <div align="center"><b>${flash.message}</b></div>
    </g:if>
</div>
<br/>
<div>
    <h1><g:link action="triggerEntitlementFeed" target="_blank">Trigger Picture Perfect Entitlement Feed</g:link></h1>
    <span><strong>NOTE:</strong> <h4>Please make sure that you have changed the value of 'ppOwner' config property to the SLID for which there is an existing Role Owner</h4></span>
    <h1><g:link action="triggerEntitlementAccessFeed" target="_blank">Trigger Picture Perfect Entitlement Access Feed</g:link></h1>
    <h1><g:link action="triggerTimEntitlementFeed" target="_blank">Trigger TIM Entitlement Feed</g:link></h1>
    <span><strong>NOTE:</strong> <h4>Please make sure that you have changed the value of 'timOwner' config property to the SLID for which there is an existing Role Owner</h4></span>
    <h1><g:link action="triggerCategoryAreaReaderFileFeed" target="_blank">Trigger Worker Area Reader File Feed</g:link></h1>
    <h1><g:link action="triggerCategoryWorkerFileFeed" target="_blank">Trigger Worker Category File Feed</g:link></h1>
    <h1><g:link action="triggerTimEntitlementWorkerFileFeedService" target="_blank">Trigger TIM Entitlement Worker File Feed</g:link></h1>
</div>

</body>
</html>