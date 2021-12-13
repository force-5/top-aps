import com.force5solutions.care.common.CareConstants
import com.force5solutions.care.common.CannedResponse

pre {
    CannedResponse cannedResponse
    (1..3).each {
        cannedResponse = new CannedResponse()
        cannedResponse.taskDescription = CareConstants.CANNED_RESPONSE_APS_ACCESS_VERIFICATION_JUSTIFICATION
        cannedResponse.priority = it
        cannedResponse.response = "Access Verification Response - " + it
        cannedResponse.responseDescription = "Dummy text for Access Verification - " + it
        cannedResponse.s()
    }

    (1..5).each {
        cannedResponse = new CannedResponse()
        cannedResponse.taskDescription = CareConstants.CANNED_RESPONSE_APS_ACCESS_REQUEST_GATEKEEPER_JUSTIFICATION
        cannedResponse.priority = it
        cannedResponse.response = "Access Request Gatekeeper Response - " + it
        cannedResponse.responseDescription = "Dummy text for Access Request - " + it
        cannedResponse.s()
    }

    (1..2).each {
        cannedResponse = new CannedResponse()
        cannedResponse.taskDescription = CareConstants.CANNED_RESPONSE_APS_REVOKE_REQUEST_GATEKEEPER_JUSTIFICATION
        cannedResponse.priority = it
        cannedResponse.response = "Revoke Request Gatekeeper Response - " + it
        cannedResponse.responseDescription = "Dummy text for Gatekeeper Response for Access Revoke - " + it
        cannedResponse.s()
    }

    (1..2).each {
        cannedResponse = new CannedResponse()
        cannedResponse.taskDescription = CareConstants.CANNED_RESPONSE_APS_ACCESS_REQUEST_PROVISIONER_JUSTIFICATION
        cannedResponse.priority = it
        cannedResponse.response = "Access Request Provisioner Response - " + it
        cannedResponse.responseDescription = "Dummy text for Access Request - " + it
        cannedResponse.s()
    }

    (1..2).each {
        cannedResponse = new CannedResponse()
        cannedResponse.taskDescription = CareConstants.CANNED_RESPONSE_APS_REVOKE_REQUEST_PROVISIONER_JUSTIFICATION
        cannedResponse.priority = it
        cannedResponse.response = "Revoke Request Deprovisioner Response - " + it
        cannedResponse.responseDescription = "Dummy text for Deprovisioner Response for Access Revoke - " + it
        cannedResponse.s()
    }

    (1..2).each {
        cannedResponse = new CannedResponse()
        cannedResponse.taskDescription = CareConstants.CANNED_RESPONSE_APS_USER_RESPONSE
        cannedResponse.priority = it
        cannedResponse.response = "User Response - " + it
        cannedResponse.responseDescription = "Dummy text for User Response- " + it
        cannedResponse.s()
    }
}

fixture {
}