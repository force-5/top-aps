package com.force5solutions.care.aps

import com.force5solutions.care.common.CareConstants
import com.force5solutions.care.workflow.ApsWorkflowTask
import com.force5solutions.care.workflow.ApsWorkflowTaskPermittedSlid
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.force5solutions.care.cc.*
import com.force5solutions.care.workflow.ApsWorkflowTaskTemplate
import com.force5solutions.care.workflow.ApsWorkflowTaskType

import org.codehaus.groovy.grails.commons.ApplicationHolder

public class ApsUtilService {
    def asynchronousMailService
    private static final Date MAX_DATE = new Date(2101, 0, 0, 0, 0, 0)
    static transactional = true


    private static getSecurityRolesOrSlidsByApplicationRoleForEntitlementRole(Collection<ApsApplicationRole> applicationRoles, EntitlementRole entitlementRole, WorkerEntitlementRole workerEntitlementRole = null) {
        Set<String> securityRolesOrSlids = [] as Set
        if (entitlementRole) {
            applicationRoles.each { ApsApplicationRole apsApplicationRole ->
                if (apsApplicationRole.equals(ApsApplicationRole.GATEKEEPER)) {
                    if (entitlementRole.gatekeepers) {
                        securityRolesOrSlids.addAll(entitlementRole.gatekeepers*.name)
                    } else {
                        entitlementRole.entitlements.each {
                            securityRolesOrSlids.addAll(it.gatekeepers*.name)
                        }
                    }
                } else if (apsApplicationRole.equals(ApsApplicationRole.PROVISIONER) && entitlementRole.entitlements*.provisioners) {
                    securityRolesOrSlids.addAll((entitlementRole.entitlements*.provisioners*.name).flatten())
                } else if (apsApplicationRole.equals(ApsApplicationRole.DEPROVISIONER) && entitlementRole.entitlements*.deProvisioners) {
                    securityRolesOrSlids.addAll((entitlementRole.entitlements*.deProvisioners*.name).flatten())
                } else if (apsApplicationRole.equals(ApsApplicationRole.ROLE_OWNER)) {
                    securityRolesOrSlids.add(entitlementRole?.owner?.slid)
                } else if (apsApplicationRole.equals(ApsApplicationRole.SUPERVISOR)) {
                    if (workerEntitlementRole) {
                        securityRolesOrSlids.add(workerEntitlementRole?.worker?.supervisor?.slid)
                    }
                } else if (apsApplicationRole.equals(ApsApplicationRole.BUSINESS_UNIT_REQUESTER)) {
                    def slid = workerEntitlementRole?.worker?.businessUnitRequester?.slid
                    if (slid) {
                        securityRolesOrSlids.add(slid)
                    }
                } else if (apsApplicationRole.equals(ApsApplicationRole.WORKER)) {
                    if (workerEntitlementRole) {
                        if (workerEntitlementRole?.worker?.isEmployee()) {
                            securityRolesOrSlids.add(workerEntitlementRole?.worker?.slid)
                        }
                    }
                }
            }
        }
        return securityRolesOrSlids
    }

    private static getSecurityRolesOrSlidsByApplicationRoleForEntitlement(Collection<ApsApplicationRole> applicationRoles, Entitlement entitlement) {
        Set<String> securityRoleOrSlids = [] as Set
        if (entitlement) {
            applicationRoles.each { ApsApplicationRole apsApplicationRole ->
                if (apsApplicationRole.equals(ApsApplicationRole.GATEKEEPER)) {
                    securityRoleOrSlids.addAll(entitlement?.gatekeepers*.name)
                } else if (apsApplicationRole.equals(ApsApplicationRole.PROVISIONER)) {
                    securityRoleOrSlids.addAll(entitlement?.provisioners*.name)
                } else if (apsApplicationRole.equals(ApsApplicationRole.DEPROVISIONER)) {
                    securityRoleOrSlids.addAll(entitlement?.deProvisioners*.name)
                } else if (apsApplicationRole.equals(ApsApplicationRole.ROLE_OWNER)) {
                    securityRoleOrSlids.addAll(entitlement?.owner?.slid)
                }
            }
        }
        return securityRoleOrSlids
    }

    static Collection<String> getSecurityRolesOrSlidsByApplicationRole(Collection<ApsApplicationRole> applicationRoles, Object object, Boolean respectExclusionList = false) {
        Set<String> securityRolesOrSlids = [] as Set
        if (object) {
            if (WorkerEntitlementRole.isAssignableFrom(object.class)) {
                securityRolesOrSlids.addAll(getSecurityRolesOrSlidsByApplicationRoleForEntitlementRole(applicationRoles, EntitlementRole.findById(object?.entitlementRole?.id), object))
            } else if (EntitlementRole.isAssignableFrom(object.class)) {
                securityRolesOrSlids.addAll(getSecurityRolesOrSlidsByApplicationRoleForEntitlementRole(applicationRoles, object as EntitlementRole))
            } else if (Entitlement.isAssignableFrom(object.class)) {
                securityRolesOrSlids.addAll(getSecurityRolesOrSlidsByApplicationRoleForEntitlement(applicationRoles, object as Entitlement))
            }
        }
        if (respectExclusionList) {
            List<String> exclusionSlids = ConfigurationHolder.config.exclusionSlids ? ConfigurationHolder.config.exclusionSlids.toUpperCase().tokenize(', ').findAll { it } : []
            securityRolesOrSlids = securityRolesOrSlids.findAll { it && !(it in exclusionSlids) }
        }
        return securityRolesOrSlids
    }

    static Collection<String> getRecipientsByApplicationRoles(Collection<ApsApplicationRole> applicationRoles, Object object, Boolean respectExclusionList = false) {
        Collection<String> recipients = []
        if (WorkerEntitlementRole.isAssignableFrom(object.class)) {
            recipients.addAll(getEmailsFromApplicationRoles(applicationRoles.findAll { it in [ApsApplicationRole.GATEKEEPER, ApsApplicationRole.PROVISIONER, ApsApplicationRole.DEPROVISIONER] }, EntitlementRole.findById(object?.entitlementRole?.id)))
        } else if (EntitlementRole.isAssignableFrom(object.class) || Entitlement.isAssignableFrom(object.class)) {
            recipients.addAll(getEmailsFromApplicationRoles(applicationRoles.findAll { it in [ApsApplicationRole.GATEKEEPER, ApsApplicationRole.PROVISIONER, ApsApplicationRole.DEPROVISIONER] }, object))
        }
        recipients.addAll(getSecurityRolesOrSlidsByApplicationRole(applicationRoles.findAll { it in [ApsApplicationRole.WORKER, ApsApplicationRole.BUSINESS_UNIT_REQUESTER, ApsApplicationRole.ROLE_OWNER, ApsApplicationRole.SUPERVISOR] }, object, respectExclusionList)?.collect { AppUtil.getEmailFromSlid(it) })
        if (WorkerEntitlementRole.isAssignableFrom(object.class) && ((object as WorkerEntitlementRole).worker.isContractor()) && (applicationRoles.any { it.equals(ApsApplicationRole.WORKER) })) {
            recipients.add(addContractorEmail(object as WorkerEntitlementRole))
        }
        return recipients
    }

    static List<String> getEmailsFromApplicationRoles(Collection<ApsApplicationRole> applicationRoles, Object object) {
        Collection<String> recipients = []
        applicationRoles.each { ApsApplicationRole applicationRole ->
            switch (applicationRole) {
                case ApsApplicationRole.GATEKEEPER:
                    if (EntitlementRole.isAssignableFrom(object.class) && !object.gatekeepers) {
                        object.entitlements.each {
                            recipients.addAllNotNull(it.gatekeepers*.emails*.tokenize(','))
                        }
                    } else {
                        recipients.addAllNotNull(object.gatekeepers*.emails*.tokenize(','))
                    }
                    break
                case ApsApplicationRole.PROVISIONER:
                    recipients.addAllNotNull(object.provisioners*.emails*.tokenize(','))
                    break
                case ApsApplicationRole.DEPROVISIONER:
                    recipients.addAllNotNull(object.deProvisioners*.emails*.tokenize(','))
                    break
                default:
                    break
            }
        }
        recipients = recipients.flatten().unique()
        return recipients
    }

    static private String addContractorEmail(WorkerEntitlementRole workerEntitlementRole) {
        return workerEntitlementRole.worker.email
    }

    static Collection<String> getRecipients(String emails, String slids, Collection<ApsApplicationRole> applicationRoles, Object object, Boolean respectExclusionList = false) {
        Collection<String> recipients = []
        if (emails) {
            recipients.addAll(emails.tokenize(', ')?.findAll { it })
        }
        if (slids) {
            recipients.addAll(slids.tokenize(', ')?.findAll { it }?.collect { AppUtil.getEmailFromSlid(it?.toString()) })
        }
        recipients.addAll(getRecipientsByApplicationRoles(applicationRoles, object, respectExclusionList))
        return recipients
    }

    static String getConfirmationLink(ApsWorkflowTask task, String slid) {
        if (!task) {
            return null
        }
        if (!slid) {
            return null
        }
        ApsWorkflowTaskPermittedSlid taskPermittedSlid = task.permittedSlids.find { it.slid == slid }
        return "${ConfigurationHolder.config.grails.serverURL}/apsWorkflowTask/confirm/${taskPermittedSlid?.guid}"
    }

    static Map getParametersForMessageTemplate(Object object) {
        Map parameters = [employeeListLink: "${ConfigurationHolder.config.grails.serverURL}/employee/list",
                link: ConfigurationHolder.config.grails.serverURL]
        def applicationContext = ApplicationHolder.getApplication().getMainContext()
        def apsWorkflowTaskService = applicationContext.getBean('apsWorkflowTaskService')
        if (object) {
            if (WorkerEntitlementRole.isAssignableFrom(object.class)) {
                parameters.putAll(apsWorkflowTaskService.populateWorkflowParametersFromWorkerEntitlementRole(object as WorkerEntitlementRole, parameters))
            } else if (EntitlementRole.isAssignableFrom(object.class)) {
                parameters['entitlementRole'] = object
            } else if (Entitlement.isAssignableFrom(object.class)) {
                parameters['entitlement'] = object
            } else if (Worker.isAssignableFrom(object.class)) {
                parameters['worker'] = object
                parameters['workerAsSupervisor'] = (object ? EmployeeSupervisor.findBySlid(object.slid) : null)
            }
        }
        return parameters
    }

    static String getActorNameOfLastHumanTask(WorkerEntitlementRole workerEntitlementRole) {
        List<ApsWorkflowTask> tasks = ApsWorkflowTask.findAllByWorkerEntitlementRoleIdAndType(workerEntitlementRole.id, ApsWorkflowTaskType.HUMAN).sort { it.lastUpdated }
        return tasks.size() ? ApsPerson.findBySlid(tasks?.last()?.actorSlid?.trim() ?: "")?.firstMiddleLastName : ""
    }

    void sendNotification(ApsWorkflowTaskTemplate taskTemplate, ApsMessageTemplate messageTemplate, Object object, Map parameters) {
        log.info "*****************Inside send Notification: ${taskTemplate?.id} : ${messageTemplate} : ${object}"
        Collection<String> toRecipients = getRecipients(taskTemplate.toNotificationEmails, taskTemplate.toNotificationSlids, taskTemplate.toNotificationApplicationRoles, object, taskTemplate.respectExclusionList)
        Collection<String> ccRecipients = getRecipients(taskTemplate.ccNotificationEmails, taskTemplate.ccNotificationSlids, taskTemplate.ccNotificationApplicationRoles, object, taskTemplate.respectExclusionList)

        if (messageTemplate) {
            String emailSubject = prepareEmailSubject(messageTemplate, parameters)
            String emailBody = prepareEmailBody(messageTemplate, parameters)
            sendEmailAndHandleAnyException(toRecipients, ccRecipients, emailSubject, emailBody)
        } else {
            log.warn("Message template not defined.")
        }
    }


    void sendEmailAndHandleAnyException(Collection<String> toRecipients, Collection<String> ccRecipients, String emailSubject, String emailBody) {
        try {
            sendEmail(toRecipients, ccRecipients, emailSubject, emailBody)
        } catch (Throwable t) {
            // TODO : What else to do?
            t.printStackTrace();
        }
    }

    public String prepareEmailSubject(ApsMessageTemplate messageTemplate, Map parameters) {
        String subjectPrefix = (ConfigurationHolder.config.testMode == 'true') ? "*** TEST ***" : ""
        String emailSubject = subjectPrefix + messageTemplate.getSubject(parameters)
        return emailSubject
    }

    public String prepareEmailBody(ApsMessageTemplate messageTemplate, Map parameters) {
        String bodyPrefix = (ConfigurationHolder.config.testMode == 'true') ? "<span style='color: #e67412'> *** THIS IS ONLY A TEST ***</span><br/>" : ""
        String emailBody = bodyPrefix + messageTemplate.getBody(parameters)
        return emailBody
    }

    public sendEmail(Collection<String> toRecipients, Collection<String> ccRecipients, String emailSubject, String emailBody) {
        toRecipients = toRecipients*.trim() - null
        ccRecipients = ccRecipients*.trim() - null
        if ((toRecipients) || (ConfigurationHolder.config.bccAlertRecipient)) {
            asynchronousMailService.sendAsynchronousMail {
                if (toRecipients) {
                    to toRecipients as List
                    if (ConfigurationHolder.config.bccAlertRecipient) {
                        bcc ConfigurationHolder.config.bccAlertRecipient
                    }
                } else {
                    to ConfigurationHolder.config.bccAlertRecipient
                }
                if (ccRecipients) {
                    cc ccRecipients as List
                }
                // set message headers in this case priority mail( priorities are from 1-5 in descending order i.e.high to low, with 3 as normal)
                headers(CareConstants.CARE_PRIORITY_MAIL_HEADERS)
                subject emailSubject
                html emailBody
                if (ConfigurationHolder.config.sendEmail != "true") {
                    beginDate MAX_DATE
                    endDate MAX_DATE + 1
                }
            }
        }
    }
}
