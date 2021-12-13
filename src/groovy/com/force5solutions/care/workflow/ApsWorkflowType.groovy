package com.force5solutions.care.workflow

import org.codehaus.groovy.grails.commons.ConfigurationHolder

public enum ApsWorkflowType {

    ROLE_ACCESS_REQUEST('ROLE ACCESS REQUEST', "roleAccessWorkflow"),
    ROLE_ACCESS_REQUEST_FOR_CONTRACTOR('ROLE ACCESS REQUEST FOR CONTRACTOR', "roleAccessWorkflowForContractor"),
    ROLE_REVOKE_REQUEST("ROLE REVOKE REQUEST", "roleRevokeWorkflow"),
    ADD_ROLE("ADD ROLE", "addRoleWorkflow"),
    ADD_ENTITLEMENT("ADD ENTITLEMENT", "addEntitlementWorkflow"),
    ACCOUNT_PASSWORD_CHANGE("ACCOUNT PASSWORD CHANGE", "accountPasswordChangeWorkflow"),
    CANCEL_ACCESS_REVOCATION("CANCEL ACCESS REVOCATION", "roleAccessWorkflow"),
    UPDATE_ENTITLEMENT("UPDATE ENTITLEMENT", "updateEntitlementWorkflow"),
    CANCEL_ACCESS_APPROVAL("CANCEL ACCESS APPROVAL", "roleRevokeWorkflow"),
    ACCESS_VERIFICATION("ACCESS VERIFICATION", "accessVerificationWorkflow"),
    TERMINATE_REQUEST("TERMINATE REQUEST", "terminateWorkflow"),
    UPDATE_ROLE("UPDATE ROLE", "updateRoleWorkflow"),
    PROVISIONER_DEPROVISIONER_TASKS_ON_ROLE_UPDATE("PROVISIONER DEPROVISIONER TASKS ON ROLE UPDATE", "provisionerDeprovisionerTasksOnRoleUpdateWorkflow"),
    UPDATE_ENTITLEMENT_EXCEPTION_FROM_FEED("ENTITLEMENT UPDATE EXCEPTION FROM FEED", "updateEntitlementFeedExceptionWorkflow"),
    CREATE_ENTITLEMENT_EXCEPTION_FROM_FEED("ENTITLEMENT CREATE EXCEPTION FROM FEED", "createEntitlementFeedExceptionWorkflow")

    private final String name
    private final workflowFilePathConfigProperty

    ApsWorkflowType(String name, String workflowFilePathConfigProperty) {
        this.workflowFilePathConfigProperty = workflowFilePathConfigProperty
        this.name = name
    }

    @Override
    public String toString() {
        return name
    }

    public String getWorkflowFilePath() {
        return ConfigurationHolder.config[workflowFilePathConfigProperty]
    }

    String getWorkflowProcessId() {
        String filePath = getWorkflowFilePath()
        String processId = filePath.tokenize('/').last() - '.rf'
        return ('com.force5solutions.care.workflow.' + processId)
    }

    String getKey() {
        return name()
    }

    public static ApsWorkflowType findKey(String nameString) {
        return (nameString ? ApsWorkflowType.values().find {ApsWorkflowType type -> type.name.equals(nameString)} : null)
    }

    public static List<ApsWorkflowType> list() {
        return [ROLE_ACCESS_REQUEST,
                ROLE_ACCESS_REQUEST_FOR_CONTRACTOR,
                ROLE_REVOKE_REQUEST,
                ADD_ROLE,
                ADD_ENTITLEMENT,
                ACCOUNT_PASSWORD_CHANGE,
                CANCEL_ACCESS_REVOCATION,
                UPDATE_ENTITLEMENT,
                CANCEL_ACCESS_APPROVAL,
                ACCESS_VERIFICATION,
                TERMINATE_REQUEST,
                UPDATE_ROLE,
                PROVISIONER_DEPROVISIONER_TASKS_ON_ROLE_UPDATE
        ]
    }
}