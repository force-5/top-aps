package com.force5solutions.care.aps

class Gatekeeper {

    ApsPerson person

    Date dateCreated
    Date lastUpdated

    static constraints = {
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
        person(unique:true)
    }

    public def getFirstName() {
        person?.firstName
    }

    public def getName() {
        person?.name
    }

    public def getLastName() {
        person?.lastName
    }

    public def getMiddleName() {
        person?.middleName
    }

    public def getNotes() {
        person?.notes
    }

    public def getPhone() {
        person?.phone
    }

    public def getFirstMiddleLastName(){
        person?.firstMiddleLastName
    }

    public def getSlid() {
        person?.slid
    }

    public def getEmail() {
        person?.email
    }

    String toString() {
        return name
    }

    boolean equals(o) {
        if (this.is(o)) return true;
        if (!(o.instanceOf(Gatekeeper.class))) return false;
        Gatekeeper g = (Gatekeeper) o;
        return (this.ident() == g.ident())
    }

}
