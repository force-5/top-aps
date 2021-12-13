import com.force5solutions.care.ldap.TopUser

fixture {
    topUser1(TopUser) {
        slid = "topuser1"
        password = "topuser1"
        rolesString = "CAREADMIN"
    }

    admin(TopUser) {
        slid = "admin"
        password = "admin"
        rolesString = "CAREADMIN"
    }

    admin1(TopUser) {
        slid = "admin1"
        password = "admin1"
        rolesString = "CAREUSER"
    }

    admin2(TopUser) {
        slid = "admin2"
        password = "admin2"
        rolesString = "CAREEDITOR"
    }

    admin3(TopUser) {
        slid = "admin3"
        password = "admin3"
        rolesString = "CAREEDITOR"
    }

    admin4(TopUser) {
        slid = "admin4"
        password = "admin4"
        rolesString = "CAREEDITOR"
    }

    admin5(TopUser) {
        slid = "admin5"
        password = "admin5"
        rolesString = "CAREEDITOR"
    }
}
