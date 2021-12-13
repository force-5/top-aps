<g:render template="/securityRole/crud" model="[title: 'Origin', role: role, createPermission: 'CREATE_ORIGIN',
readPermission: 'READ_ORIGIN', updatePermission: 'UPDATE_ORIGIN',deletePermission: 'DELETE_ORIGIN']"/>
<g:render template="/securityRole/separatorImage"/>

<g:render template="/securityRole/crud" model="[title: 'Role Owner', role: role, createPermission: 'CREATE_ROLE_OWNER',
readPermission: 'READ_ROLE_OWNER', updatePermission: 'UPDATE_ROLE_OWNER',deletePermission: 'DELETE_ROLE_OWNER']"/>
<g:render template="/securityRole/separatorImage"/>

<g:render template="/securityRole/crud" model="[title: 'Provisioner', role: role, createPermission: 'CREATE_PROVISIONER',
readPermission: 'READ_PROVISIONER', updatePermission: 'UPDATE_PROVISIONER',deletePermission: 'DELETE_PROVISIONER']"/>
<g:render template="/securityRole/separatorImage"/>

<g:render template="/securityRole/crud" model="[title: 'Deprovisioner', role: role, createPermission: 'CREATE_DEPROVISIONER',
readPermission: 'READ_DEPROVISIONER', updatePermission: 'UPDATE_DEPROVISIONER',deletePermission: 'DELETE_DEPROVISIONER']"/>
<g:render template="/securityRole/separatorImage"/>

<g:render template="/securityRole/crud" model="[title: 'Gatekeeper', role: role, createPermission: 'CREATE_GATEKEEPER',
readPermission: 'READ_GATEKEEPER', updatePermission: 'UPDATE_GATEKEEPER',deletePermission: 'DELETE_GATEKEEPER']"/>
<g:render template="/securityRole/separatorImage"/>

<g:render template="/securityRole/crud" model="[title: 'Entitlement', role: role, createPermission: 'CREATE_ENTITLEMENT',
readPermission: 'READ_ENTITLEMENT', updatePermission: 'UPDATE_ENTITLEMENT',deletePermission: 'DELETE_ENTITLEMENT']"/>
<g:render template="/securityRole/separatorImage"/>

<g:render template="/securityRole/crud" model="[title: 'Entitlement Role', role: role, createPermission: 'CREATE_ENTITLEMENT_ROLE',
readPermission: 'READ_ENTITLEMENT_ROLE', updatePermission: 'UPDATE_ENTITLEMENT_ROLE',deletePermission: 'DELETE_ENTITLEMENT_ROLE']"/>
<g:render template="/securityRole/separatorImage"/>

<g:render template="/securityRole/crud" model="[title: 'Security Roles', role: role, createPermission: 'CREATE_SECURITY_ROLE',
readPermission: 'READ_SECURITY_ROLE', updatePermission: 'UPDATE_SECURITY_ROLE',deletePermission: 'DELETE_SECURITY_ROLE']"/>
<g:render template="/securityRole/separatorImage"/>

<g:render template="/securityRole/crud" model="[title: 'Message Templates', role: role, createPermission: 'CREATE_MESSAGE_TEMPLATE',
readPermission: 'READ_MESSAGE_TEMPLATE', updatePermission: 'UPDATE_MESSAGE_TEMPLATE',deletePermission: 'DELETE_MESSAGE_TEMPLATE']"/>
<g:render template="/securityRole/separatorImage"/>

<g:render template="/securityRole/readManageWorkflow" model="[role: role]"/>
<g:render template="/securityRole/separatorImage"/>

<g:render template="/securityRole/menuBar" model="[role: role]"/>

<script type="text/javascript">
    function checkAllCheckbox() {
        jQuery(':checkbox').attr('checked', true);
        jQuery('.child-check-box').attr('checked', false);
    }
    function unCheckAllCheckbox() {
        jQuery(':checkbox').attr('checked', false);
    }

    jQuery(document).ready(function() {
        jQuery.each(jQuery('.parent-check-box'), function() {
            jQuery(this).click(function() {
                disableChildrenIfUnchecked(this);
            });
            disableChildrenIfUnchecked(this);
        });
    });


    function disableChildrenIfUnchecked(parent) {
        if (jQuery(parent).is(':checked')) {
            jQuery.each(jQuery(parent).parents('li').find('.child-check-box'), function() {
                jQuery(this).removeAttr('disabled');
            })
        } else {
            jQuery.each(jQuery(parent).parents('li').find('.child-check-box'), function() {
                jQuery(this).removeAttr('checked');
                jQuery(this).attr('disabled', 'true');
            })
        }
    }

    function getPermissionsForSecurityRole(roleId, ajaxUrl) {
        unCheckAllCheckbox();
        jQuery.get(ajaxUrl,
        { ajax: 'true',roleId:roleId}, function(data) {
            jQuery('#permissions-div').html(data);
            jQuery.each(jQuery('.parent-check-box'), function() {
                disableChildrenIfUnchecked(this);
            });
        });
        jQuery('#applicationRole option[value="noSelection"]').attr('selected', 'true');
    }
</script>

