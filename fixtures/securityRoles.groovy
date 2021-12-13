import com.force5solutions.care.ldap.Permission
import com.force5solutions.care.ldap.PermissionLevel
import com.force5solutions.care.ldap.SecurityRole
import static com.force5solutions.care.common.CareConstants.*

fixture {
    careAdmin(SecurityRole) {
        name = CAREADMIN
        description = 'Admin with all permissions'
    }
    careEditor(SecurityRole) {
        name = CAREEDITOR
        description = 'CARE Editor'
    }
    provisioner(SecurityRole) {
        name = PROVISIONER
        description = 'Provisioner'
    }
    deProvisioner(SecurityRole) {
        name = DEPROVISIONER
        description = 'Deprovisioner'
    }
}

post {
    Permission.values().each {Permission permission ->
        new PermissionLevel(role: careAdmin, permission: permission, level: UNRESTRICTED_ACCESS_PERMISSION_LEVEL).s()
    }
    Permission.values().each {Permission permission ->
        new PermissionLevel(role: careEditor, permission: permission, level: UNRESTRICTED_ACCESS_PERMISSION_LEVEL).s()
    }
    Permission.values().each {Permission permission ->
        new PermissionLevel(role: provisioner, permission: permission, level: UNRESTRICTED_ACCESS_PERMISSION_LEVEL).s()
    }
    Permission.values().each {Permission permission ->
        new PermissionLevel(role: deProvisioner, permission: permission, level: UNRESTRICTED_ACCESS_PERMISSION_LEVEL).s()
    }
}
