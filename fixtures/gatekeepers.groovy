import com.force5solutions.care.aps.ApsPerson
import com.force5solutions.care.aps.Gatekeeper


pre {
    if (!Gatekeeper.count()) {
        createAdminAsPerson()
        createGatekeepers(10)
    }
}
fixture {}

void createAdminAsPerson() {
    ApsPerson person = new ApsPerson()
    person.firstName = "admin-FN-1"
    person.lastName = "admin-LN-1"
    person.slid = "admin"
    person.s()
}

void createGatekeepers(Integer count) {
    (1..count).each {
        ApsPerson person = new ApsPerson()
        person.firstName = "fnGatekeeper-${it}"
        person.lastName = "lnGatekeeper-${it}"
        person.slid = "gatekeeper-${it}"
        person.s()
        new Gatekeeper(person: person).s()
    }
}

