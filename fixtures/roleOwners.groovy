import com.force5solutions.care.aps.RoleOwner
import com.force5solutions.care.aps.ApsPerson


pre {
    if (!RoleOwner.count()) {
        createRoleOwners(10)
    }
}
fixture {}


void createRoleOwners(Integer count) {
    (1..count).each {
        ApsPerson person = new ApsPerson()
        person.firstName = "EMP_FN${it}"
        person.lastName = "EMP_LN${it}"
        person.slid = "owner-${it}"
        person.phone = "444555777${it}"
        person.s()
        new RoleOwner(person: person).s()
    }

    // TODO: Sample Role Owner for TIM and PP entitlements. Can be deleted later
    ApsPerson person = new ApsPerson()
    person.firstName = "TIM"
    person.lastName = "Owner"
    person.slid = "timOwner"
    person.s()
    new RoleOwner(person: person).s()

    person = new ApsPerson()
    person.firstName = "PP"
    person.lastName = "Owner"
    person.slid = "ppOwner"
    person.s()
    new RoleOwner(person: person).s()
}