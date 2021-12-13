package com.force5solutions.care.aps

class News {

    Date dateCreated
    Date lastUpdated
    String description
    String headline
    
    static constraints = {
        description(maxSize: 5000, blank:true,nullable:true)
        headline(maxSize: 5000)
    }

    boolean equals(o) {
        if (this.is(o)) return true;
        if (!(o.instanceOf(News.class))) return false;
        News n = (News) o;
        return (this.ident() == n.ident())
    }


}
