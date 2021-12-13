<%@ page import="com.force5solutions.care.ldap.Permission" %>
<div class="dashboarddemo2-top-nav">
    <ul id="sddm">
        <g:if test="${care.hasPermission(permission: Permission.CAN_ACCESS_ENTITLEMENT_ROLE_MENU)}">
            <li class="menubar140"><g:link controller="entitlementRole" action="list">Entitlement Roles</g:link></li>
        </g:if>
        <g:else>
            <li class="menubar140"><a><span style="color:gray">Entitlement Roles</span></a></li>
        </g:else>
        <g:if test="${care.hasPermission(permission: Permission.CAN_ACCESS_ENTITLEMENT_MENU)}">
            <li class="sddmli"><g:link controller="entitlement" action="list">Entitlements</g:link></li>
        </g:if>
        <g:else>
            <li class="sddmli"><a><span style="color:gray">Entitlements</span></a></li>
        </g:else>
        <g:if test="${care.hasPermission(permission: Permission.CAN_ACCESS_REPORTS_MENU)}">
            <li class="sddmli"><g:link controller="report" action="index">Reports</g:link></li>
        </g:if>
        <g:else>
            <li class="sddmli"><a href="#"><span style="color:gray">Reports</span></a></li>
        </g:else>

        <g:if test="${care.hasPermission(permission: Permission.CAN_ACCESS_ADMIN_MENU)}">
            <li class="sddmli"><a onmouseover="mopen('m2')" onmouseout="mclosetime()">Admin</a>
                <div class="submenu" id="m2" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
                    <div><img src="${createLinkTo(dir: 'images', file: ('drp-dwn1.png'))}"/></div>
                    <div class="submenu-content1">
                        <div class="submenu-content3">
                            <g:if test="${care.hasPermission(permission: Permission.READ_ORIGIN)}">
                                <g:link class="originListLink" controller="origin" action="list">Origins</g:link>
                            </g:if>
                            <g:else>
                                <a class="originListLink" style="color: gray;">Origins</a>
                            </g:else>
                            <hr style="width:80px;position:relative;margin:0px auto;">
                            <g:if test="${care.hasPermission(permission: Permission.READ_ROLE_OWNER)}">
                                <g:link class="roleOwnerListLink" controller="roleOwner"
                                        action="list">Role Owners</g:link>
                            </g:if>
                            <g:else>
                                <a class="roleOwnerListLink" style="color: gray;">Role Owners</a>
                            </g:else>
                            <hr style="width:80px;position:relative;margin:0px auto;">
                            <g:if test="${care.hasPermission(permission: Permission.READ_PROVISIONER)}">
                                <g:link class="provisionerListLink" controller="provisioner"
                                        action="list">Provisioners</g:link>
                            </g:if>
                            <g:else>
                                <a class="provisionerListLink" style="color: gray;">Provisioners</a>
                            </g:else>
                            <hr style="width:80px;position:relative;margin:0px auto;">
                            <g:if test="${care.hasPermission(permission: Permission.READ_DEPROVISIONER)}">
                                <g:link class="provisionerListLink" controller="deProvisioner"
                                        action="list">Deprovisioners</g:link>
                            </g:if>
                            <g:else>
                                <a class="provisionerListLink" style="color: gray;">Deprovisioners</a>
                            </g:else>
                            <hr style="width:80px;position:relative;margin:0px auto;">
                            <g:if test="${care.hasPermission(permission: Permission.READ_GATEKEEPER)}">
                                <g:link class="gatekeeperListLink" controller="gatekeeper"
                                        action="list">Gatekeepers</g:link>
                            </g:if>
                            <g:else>
                                <a class="gatekeeperListLink" style="color: gray;">Gatekeepers</a>
                            </g:else>
                            <hr style="width:80px;position:relative;margin:0px auto;">
                            <g:if test="${care.hasPermission(permission: Permission.READ_SECURITY_ROLE)}">
                                <g:link class="securityRoleListLink" controller="securityRole"
                                        action="list">Security Roles</g:link>
                            </g:if>
                            <g:else>
                                <a class="securityRoleListLink" style="color: gray;">Security Roles</a>
                            </g:else>
                            <hr style="width:80px;position:relative;margin:0px auto;">
                            <g:if test="${care.hasPermission(permission: Permission.READ_MESSAGE_TEMPLATE)}">
                                <g:link class="messageTemplateListLink" controller="apsMessageTemplate"
                                        action="list">Message Templates</g:link>
                            </g:if>
                            <g:else>
                                <a class="messageTemplateListLink" style="color: gray;">Message Templates</a>
                            </g:else>
                            <hr style="width:80px;position:relative;margin:0px auto;">
                            <g:if test="${care.hasPermission(permission: Permission.READ_MANAGE_WORKFLOW)}">
                                <g:link class="manageWorkflowLink" controller="workflow"
                                        action="index">Manage Workflows</g:link>
                                <hr style="width:80px;position:relative;margin:0px auto;">
                                <g:link class="manageWorkflowLink" controller="util"
                                        action="chooseWorkerForWorkflowReport">Workflow Report</g:link>
                            </g:if>
                            <g:else>
                                <a class="manageWorkflowLink" style="color: gray;">Manage Workflows</a>
                                <hr style="width:80px;position:relative;margin:0px auto;">
                                <a class="manageWorkflowLink" style="color: gray;">Workflow Report</a>
                            </g:else>
                            <hr style="width:80px;position:relative;margin:0px auto;">
                            <g:link class="revocationPackageLink" controller="util"
                                    action="revocationPackage">Revocation Package</g:link>
                        </div>
                    </div>
                    <div><img src="${createLinkTo(dir: 'images', file: ('drp-dwn2.png'))}"/></div>
                </div></li>
        </g:if>
        <g:else>
            <li class="sddmli"><a><span style="color:gray">Admin</span></a></li>
        </g:else>
        <care:inbox/>
    </ul>
    <br/>
    <div style="clear:both"></div>
</div>
