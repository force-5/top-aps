package com.force5solutions.care.aps

class Origin {

    public static final String MANUAL = "Manual"
    public static final String TIM_FEED = "TIM Feed"
    public static final String PICTURE_PERFECT_FEED = "Picture Perfect Feed"

    String name
    Date dateCreated
    Date lastUpdated

    static constraints = {
        name(unique: true)
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
    }

    String toString(){
        return name
    }
    boolean equals(o) {
        if (this.is(o)) return true;
        if (!(o.instanceOf(Origin.class))) return false;
        Origin g = (Origin) o;
        return (this.ident() == g.ident())
    }


}
