import com.force5solutions.care.cc.EntitlementPolicy

fixture {
    physical(EntitlementPolicy) {
        name = "Physical"
        standards = ['s-1', 's-2']
    }

    cyber(EntitlementPolicy) {
        name = "Cyber"
        standards = ['s-1', 's-2']
    }
}
