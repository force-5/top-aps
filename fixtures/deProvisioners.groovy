import com.force5solutions.care.aps.ApsPerson
import com.force5solutions.care.aps.DeProvisioner


pre {
    if (!DeProvisioner.count()) {
        createDeProvisioners(10)
    }
}
fixture {}

void createDeProvisioners(Integer count) {
    (1..count).each {
        ApsPerson person = new ApsPerson()
        person.firstName = "fn-DeProvisioner-${it}"
        person.lastName = "ln-DeProvisioner-${it}"
        person.slid = "deProvisioner-${it}"
        person.s()
        new DeProvisioner(person: person).s()
    }
}