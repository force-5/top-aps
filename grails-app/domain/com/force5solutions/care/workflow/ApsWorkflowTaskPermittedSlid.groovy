package com.force5solutions.care.workflow

class ApsWorkflowTaskPermittedSlid {
    ApsWorkflowTask task
    String slid
    Date dateCreated
    Date lastUpdated
    String guid = UUID.randomUUID().toString()
    boolean isArchived = false

    void setSlid(String slid){
        this.slid = slid?.toUpperCase()
    }

    static constraints = {
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
    }
    static belongsTo = [task: ApsWorkflowTask]

    public static void markArchived(ApsWorkflowTask task, String slid) {
        ApsWorkflowTaskPermittedSlid userSpecificTaskArchiveStatus = ApsWorkflowTaskPermittedSlid.findBySlidAndTask(slid, task);
        userSpecificTaskArchiveStatus?.isArchived = true
        userSpecificTaskArchiveStatus?.s();
    }
}
