package com.force5solutions.care.aps

enum ApsApplicationRole {

    WORKER('Worker'),
    ROLE_OWNER('Role Owner'),
    PROVISIONER('Provisioner'),
    DEPROVISIONER('Deprovisioner'),
    GATEKEEPER('Gatekeeper'),
    BUSINESS_UNIT_REQUESTER('Business Unit Requester'),
    SUPERVISOR('Supervisor')

    private final String name;

    ApsApplicationRole(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    static list() {
        [WORKER, ROLE_OWNER, PROVISIONER, GATEKEEPER, SUPERVISOR, BUSINESS_UNIT_REQUESTER, DEPROVISIONER]
    }

    static ApsApplicationRole get(String name){
        return (ApsApplicationRole.list().find{it.name == name})
    }

    public String getKey() {
        return name()
    }


}
