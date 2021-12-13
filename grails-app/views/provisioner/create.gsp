<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder; com.force5solutions.care.aps.Provisioner" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'provisioner.label', default: 'Provisioner')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></span>
</div>
<div class="body">
    <h1><g:message code="default.create.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${provisioner}">
        <div class="errors">
            <g:if test="${provisioner?.errors?.allErrors?.any{((it.code=='unique') && (it.field=='person'))}}">
                <g:message code="provisioner.person.unique.error"/><br/>
                <g:if test="${provisioner?.errors?.allErrors?.size() > 1}">
                    <g:message code="blank.field.message"/>
                </g:if>
            </g:if>
            <g:else>
                <g:message code="blank.field.message"/>
            </g:else>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post">
        <div class="dialog">
            <table>
                <tbody>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="person.firstName"><g:message code="provisioner.firstName.label" default="First Name"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: provisioner, field: 'person.firstName', 'errors')}">
                        <g:if test="${ConfigurationHolder?.config?.isEmployeeEditable == 'true'}">
                            <g:textField id="firstName" name="person.firstName" value="${provisioner?.firstName}"/>
                        </g:if>
                        <g:else>
                            <g:textField id="firstName" name="person.firstName" class="readOnlyField" value="${provisioner?.firstName}" readonly="true"/>
                        </g:else>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="person.lastName"><g:message code="provisioner.lastName.label" default="Last Name"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: provisioner, field: 'person.lastName', 'errors')}">
                        <g:if test="${ConfigurationHolder?.config?.isEmployeeEditable == 'true'}">
                            <g:textField id="lastName" name="person.lastName" value="${provisioner?.lastName}"/>
                        </g:if>
                        <g:else>
                            <g:textField id="lastName" name="person.lastName" class="readOnlyField" value="${provisioner?.lastName}" readonly="true"/>
                        </g:else>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="slid"><g:message code="provisioner.slid.label" default="SLID"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: provisioner, field: 'person.slid', 'errors')}">
                        <g:textField id="slid" name="person.slid" value="${provisioner?.slid}"/>
                        <g:if test="${ConfigurationHolder?.config?.isEmployeeEditable != 'true'}">
                            <a id="checkSlid" class="filterbutton" href="#" onclick="checkSlid(jQuery('#slid').val());">
                                <span>Check SLID</span>
                            </a>
                        </g:if>
                        <div id="errorMessage" style="display: none; color: red;">No Employee found with this SLID</div>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="email"><g:message code="email.label" default="Email"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: provisioner, field: 'email', 'errors')}">

                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="name"><g:message code="name.label" default="Name"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: provisioner, field: 'name', 'errors')}">

                    </td>
                </tr>

                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}"/></span>
        </div>
    </g:form>
</div>

<g:if test="${fail == 'true'}">
    <script type="text/javascript">
        jQuery('#errorMessage').show();
    </script>
</g:if>

<script type="text/javascript">

    jQuery(document).ready(function () {
        jQuery("#slid").focus();
    });

    function checkSlid(slid) {
        emptyValues();
        jQuery.getJSON('${createLink(action: "checkSlid")}', {slid:slid}, function(jsonData) {
            if (jsonData.fail != 'true') {
                populateValues(jsonData);
                jQuery('#errorMessage').hide();
            } else {
                jQuery('#errorMessage').show();
            }
        });
    }
</script>
</body>
</html>
