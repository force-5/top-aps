package com.force5solutions.care.workflow

import java.beans.XMLEncoder

class CareCentralResponse {

    Long careCentralTaskId
    String response
    Date dateCreated
    Date lastUpdated

    CareCentralResponse(){
    }

    CareCentralResponse(Long careCentralTaskId, Map response){
        this.careCentralTaskId = careCentralTaskId
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLEncoder xmlEncoder = new XMLEncoder(bos);
        xmlEncoder.writeObject(response);
        xmlEncoder.flush();

        this.response = bos.toString()
    }

    static constraints = {
        response(maxSize: 8000)
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
    }
}
