import com.force5solutions.care.aps.ApsPerson
import com.force5solutions.care.aps.Provisioner


pre {
    if (!Provisioner.count()) {
        createProvisioners(10)
    }
}
fixture {}

void createProvisioners(Integer count) {
    (1..count).each {
        ApsPerson person = new ApsPerson()
        person.firstName = "fnProvisioner-${it}"
        person.lastName = "lnProvisioner-${it}"
        person.slid = "provisioner-${it}"
        person.s()
        new Provisioner(person: person).s()
    }
}