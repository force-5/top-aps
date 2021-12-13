<h1>Worker Entitlement Archive Entries created:</h1>
<br/>
<g:each in="${workerEntitlementArchivesCreated}" var="workerEntitlementArchive">
    <div>
        <strong>${workerEntitlementArchive.workerFirstName + " " + workerEntitlementArchive.workerLastName}</strong>      -----------------------------------------------      <strong>${workerEntitlementArchive.entitlementName}</strong>
    </div>
</g:each>