package com.force5solutions.care.aps

import com.force5solutions.care.common.CareConstants
import com.force5solutions.care.common.EntitlementStatus
import com.force5solutions.care.cc.CcCustomPropertyValue
import com.force5solutions.care.cc.Person
import com.force5solutions.care.cc.Worker
import com.force5solutions.care.cc.Employee
import com.force5solutions.care.cc.Contractor
import com.force5solutions.care.cc.WorkerEntitlementRole
import com.force5solutions.care.ldap.SecurityRole
import com.google.gson.Gson
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.force5solutions.care.cc.EntitlementPolicy
import com.force5solutions.care.workflow.ApsWorkflowTask
import com.force5solutions.care.workflow.ApsWorkflowType
import com.force5solutions.care.workflow.WorkflowTaskStatus
import org.grails.plugins.versionable.VersionHistory

import java.text.SimpleDateFormat

public class Entitlement {

    static requireApprovalObjects = ['status', 'name', 'type']

    String id
    String name
    String alias
    Boolean isApproved = false
    Boolean isExposed = false
    Boolean isPropagated = false
    RoleOwner owner
    EntitlementStatus status = EntitlementStatus.ACTIVE
    Long type
    Origin origin
    String notes
    Date dateCreated
    Date lastUpdated
    List<SecurityRole> gatekeepers = []
    List<SecurityRole> provisioners = []
    List<SecurityRole> deProvisioners = []
    List<CcCustomPropertyValue> customPropertyValues = []
    List<EntitlementAttribute> entitlementAttributes = []
    Integer accessLayer
    Boolean toBeAutoProvisioned = false
    Boolean toBeAutoDeprovisioned = false
    static hasMany = [gatekeepers: SecurityRole, provisioners: SecurityRole, customPropertyValues: CcCustomPropertyValue, deProvisioners: SecurityRole, entitlementAttributes: EntitlementAttribute]

    static List<Entitlement> listApproved() {
        return Entitlement.findAllByIsApproved(true, [sort: 'name'])
    }

    static constraints = {
        name(unique: true)
        alias(unique: true)
        notes(maxSize: 5000, nullable: true, blank: true)
        origin(nullable: false)
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
        accessLayer(nullable: true)
        toBeAutoProvisioned(nullable: true)
        toBeAutoDeprovisioned(nullable: true)
    }

    static mapping = {
        id generator: 'uuid'
        entitlementAttributes cascade: "all-delete-orphan"
    }

    static transients = ['onePhysicalAndOneCyberEntitlement', 'lastPasswordChangeAttributeValue']

    String toString() {
        return name
    }

    boolean equals(o) {
        if (this.is(o)) return true;
        if (!(o?.instanceOf(Entitlement.class))) return false;
        Entitlement p = (Entitlement) o;
        return (this.ident() == p.ident())
    }

    public static Set<Entitlement> getActiveEntitlementsForSlid(String slid) {
        Set<Entitlement> entitlements = []
        Person person = Person.findBySlid(slid)
        if (person) {
            Worker worker = Employee.findByPerson(person)
            if (!worker) {
                worker = Contractor.findByPerson(person)
            }
            if (worker) {
                Set<WorkerEntitlementRole> activeRoles = worker.activeEntitlementRoles
                if (activeRoles) {
                    Set<EntitlementRole> roles = EntitlementRole.findAllByIdInList(activeRoles*.entitlementRole.id)
                    if (roles) {
                        entitlements = (roles*.entitlements).flatten()
                    }
                }
            }
        }
        return entitlements
    }

    static boolean isEntitlementOriginatedFromTimFeed(def entitlementId) {
        Entitlement entitlement = Entitlement.get(entitlementId)
        return (entitlement.origin.name == Origin.TIM_FEED)
    }

    static void activateEntitlement(String entitlementId) {
        Entitlement entitlement = Entitlement.findById(entitlementId)
        entitlement.isApproved = true
        def entitlementService = ApplicationHolder.getApplication().getMainContext().getBean('entitlementService')
        entitlementService.saveEntitlement(entitlement)
    }

    static void updateEntitlement(String entitlementId) {
        def versioningService = ApplicationHolder.getApplication().getMainContext().getBean('versioningService')
        def entitlementRoleService = ApplicationHolder.getApplication().getMainContext().getBean('entitlementRoleService')
        Entitlement entitlement = Entitlement.findById(entitlementId)
        entitlement.isApproved = true
        boolean hasEntitlementPolicyChanged = doPendingChangesIncludeEntitlementPolicy(entitlement)
        versioningService.approvePendingChanges(entitlement)
        if (hasEntitlementPolicyChanged) {
            entitlementRoleService.updateSortedPolicyTypesOfRolesContainingEntitlement(entitlement)
        }
    }

    static private boolean doPendingChangesIncludeEntitlementPolicy(Entitlement entitlement) {
        def versioningService = ApplicationHolder.getApplication().getMainContext().getBean('versioningService')
        List<VersionHistory> versionHistories = versioningService.getPendingChanges(entitlement)
        return versionHistories.any { it.propertyName.equalsIgnoreCase('type') }
    }

    static List<Entitlement> getOnePhysicalAndOneCyberEntitlement(String physicalName, String cyberName) {
        List<Entitlement> entitlementList = []
        entitlementList.add(Entitlement.findByType(EntitlementPolicy.findByName(physicalName).id))
        entitlementList.add(Entitlement.findByType(EntitlementPolicy.findByName(cyberName).id))
        return entitlementList
    }

    public boolean hasPendingAddOrUpdateTask() {
        ApsWorkflowTask.createCriteria().list {
            inList('workflowType', [ApsWorkflowType.ADD_ENTITLEMENT, ApsWorkflowType.UPDATE_ENTITLEMENT])
            eq('status', WorkflowTaskStatus.NEW)
            eq('entitlementId', id)
        }.size() ? true : false
    }

    String jsonifyEntitlementAttributes() {
        HashMap<String, String> attributesMap = [:]
        entitlementAttributes.each { EntitlementAttribute entitlementAttribute ->
            if (entitlementAttribute?.keyName && entitlementAttribute?.value) {
                attributesMap.put(entitlementAttribute?.keyName, entitlementAttribute?.value)
            }
        }
        return new Gson().toJson(attributesMap)
    }

    boolean hasSharedAccountAttributeTrue() {
        return entitlementAttributes?.find { it?.keyName?.equalsIgnoreCase(CareConstants.SHARED_ACCOUNT_ATTRIBUTE) }?.value?.equalsIgnoreCase('true')
    }

    boolean hasGenericAccountAttributeTrue() {
        return entitlementAttributes?.find { it?.keyName?.equalsIgnoreCase(CareConstants.GENERIC_ACCOUNT_ATTRIBUTE) }?.value?.equalsIgnoreCase('true')
    }

    String getLastPasswordChangeAttributeValue() {
        return entitlementAttributes?.find { it?.keyName?.equalsIgnoreCase(CareConstants.LAST_PASSWORD_CHANGE_ATTRIBUTE) }?.value ?: null
    }

    void saveLastPasswordChangeAttribute(String dateString) {
        EntitlementAttribute entitlementAttribute = entitlementAttributes.find { it?.keyName?.equalsIgnoreCase(CareConstants.LAST_PASSWORD_CHANGE_ATTRIBUTE) } ?: new EntitlementAttribute(keyName: CareConstants.LAST_PASSWORD_CHANGE_ATTRIBUTE)
        entitlementAttribute.value = dateString
        if (!entitlementAttribute?.id) {
            entitlementAttributes.add(entitlementAttribute)
        }
        this.s()
    }

    static void saveLastPasswordChangeAttribute(String entitlementId, Map responseElements) {  // Called from account password update work-flow
        Entitlement entitlement = Entitlement.findById(entitlementId)
        EntitlementAttribute entitlementAttribute = entitlement.entitlementAttributes.find { it?.keyName?.equalsIgnoreCase(CareConstants.LAST_PASSWORD_CHANGE_ATTRIBUTE) } ?: new EntitlementAttribute(keyName: CareConstants.LAST_PASSWORD_CHANGE_ATTRIBUTE)
        entitlementAttribute.value = (responseElements['actionDate'] ? new SimpleDateFormat('MM/dd/yyyy hh:mm a').parse(responseElements['actionDate'] as String) : new Date()).format('MM/dd/yyyy')
        if (!entitlementAttribute?.id) {
            entitlement.entitlementAttributes.add(entitlementAttribute)
        }
        entitlement.s()
    }
}
