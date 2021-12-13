<g:if test="${missingCertifications.size()>0}">
	<table>
		<div>
	<g:each in="${missingCertifications}" var="missingCertification" status="i">
		<tr><td>${missingCertification}</td></tr>
	</g:each>
	</div>
	</div>
	</table>
</g:if>

