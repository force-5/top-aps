package com.force5solutions.care.aps

import static com.force5solutions.care.common.CareConstants.*
import com.force5solutions.care.common.SessionUtils
import com.force5solutions.care.ldap.*

class PermissionService {

    boolean transactional = true

    private Boolean validateIfGatekeeper(String slid) {
        return Gatekeeper.countBySlid(slid)
    }

    private Boolean validateIfRoleOwner(String slid, EntitlementRole entitlementRole) {
        return (slid == entitlementRole.owner.slid)
    }

    private Boolean isPermitted(String slid, Long level, EntitlementRole entitlementRole = null) {
        if (level == NOT_AUTHORIZED_PERMISSION_LEVEL) {
            return false
        }
        if (level == UNRESTRICTED_ACCESS_PERMISSION_LEVEL) {
            return true
        }
        if ((level % ACCESS_IF_GATEKEEPER_PERMISSION_LEVEL) == 0 && (validateIfGatekeeper(slid))) {
            return true
        } else if ((level % ACCESS_IF_ROLE_OWNER_OWNS_PERMISSION_LEVEL) == 0 && (validateIfRoleOwner(slid, entitlementRole))) {
            return true
        }
        return false
    }

    public Boolean hasPermission(Permission permission) {
        String slid = SessionUtils.session?.loggedUser
        if (!slid) {
            return false
        }
        List<SecurityRole> roles = SessionUtils.session?.roles ? SecurityRole.findAllByNameInList(SessionUtils.session?.roles) : []
        Boolean result = roles?.any {SecurityRole role ->
            PermissionLevel permissionLevel = PermissionLevel.findByPermissionAndRole(permission, role)
            if (!permissionLevel) {return false}
            Long level = permissionLevel.level
            return isPermitted(slid, level)
        }
        return result
    }

}
