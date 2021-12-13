package com.force5solutions.care.workflow

import com.force5solutions.care.aps.Entitlement
import com.force5solutions.care.aps.EntitlementRole
import com.force5solutions.care.aps.GenericAndSharedEntitlementApsWorkflowTask
import com.force5solutions.care.cc.UploadedFile
import com.force5solutions.care.cc.AppUtil
import com.force5solutions.care.cc.WorkerEntitlementRole
import com.force5solutions.care.common.SessionUtils
import com.force5solutions.care.cc.Worker
import com.force5solutions.care.cc.EntitlementPolicy
import com.force5solutions.care.aps.ApsDataFile
import com.force5solutions.care.ldap.SecurityRole

class ApsWorkflowTaskController {

    def apsWorkflowTaskService
    def entitlementService

    def list = {
        List<ApsWorkflowTask> tasks = ApsWorkflowTask.getPermittedTasks()
        tasks = session.filterVO ? filterWorkflowTaskList(tasks, (session.filterVO as WorkflowTaskFilterVO)) : tasks
        [tasks: tasks]
    }

    public List<ApsWorkflowTask> filterWorkflowTaskList(List<ApsWorkflowTask> apsWorkflowTasks, WorkflowTaskFilterVO workflowTaskFilterVO) {
        if (workflowTaskFilterVO?.workflowType) {
            apsWorkflowTasks = apsWorkflowTasks.findAll { it?.workflowType == ApsWorkflowType.findKey(workflowTaskFilterVO?.workflowType) }
        }

        if (workflowTaskFilterVO?.workerId) {
            apsWorkflowTasks = apsWorkflowTasks.findAll { it?.worker?.id == workflowTaskFilterVO?.workerId }
        }

        if (workflowTaskFilterVO?.entitlementPolicyId) {
            apsWorkflowTasks = apsWorkflowTasks.findAll { it?.entitlement?.type == workflowTaskFilterVO?.entitlementPolicyId }
        }

        if (workflowTaskFilterVO?.entitlementId) {
            apsWorkflowTasks = apsWorkflowTasks.findAll { it?.entitlementId == workflowTaskFilterVO?.entitlementId }
        }
        if (workflowTaskFilterVO?.currentNodeName) {
            apsWorkflowTasks = apsWorkflowTasks.findAll { it?.nodeName == workflowTaskFilterVO?.currentNodeName }
        }
        if (workflowTaskFilterVO?.securityRoleId) {
            apsWorkflowTasks = apsWorkflowTasks.findAll { it?.securityRoles?.contains(SecurityRole.get(workflowTaskFilterVO?.securityRoleId).name) }
        }
        if (workflowTaskFilterVO?.actorSlid) {
            apsWorkflowTasks = apsWorkflowTasks.findAll { it?.actorSlid == workflowTaskFilterVO?.actorSlid }
        }
        return apsWorkflowTasks
    }

    def listAll = {
        List<ApsWorkflowTask> tasks = ApsWorkflowTask.list()
        render(view: 'listAll', model: [tasks: tasks])
    }

    def getUserResponse = {
        ApsWorkflowTask task = ApsWorkflowTask.get(params.long('id'))
        if (task.status in [WorkflowTaskStatus.COMPLETE, WorkflowTaskStatus.CANCELLED]) {
            render(view: 'actionAlreadyTaken', model: [task: task])
        } else if (task.workflowType == ApsWorkflowType.ACCESS_VERIFICATION) {
            Worker worker = Worker.get(task.workerId)
            String slid = worker.slid
            Map rolesMap = apsWorkflowTaskService.accessVerificationReport(slid)
            String supervisorName = task.worker.firstName + ' ' + task.worker.lastName
            render(view: task.responseForm, model: [task: task, rolesMap: rolesMap, supervisorName: supervisorName])
        } else {
            render(view: task.responseForm, model: [task: task])
        }
    }

    def getGroupResponse = {
        List<Long> apsWorkflowTaskIds = params.list('taskIds')
        List<ApsWorkflowTask> apsWorkflowTasks = apsWorkflowTaskIds ? ApsWorkflowTask.getAll(apsWorkflowTaskIds) : []
        ApsWorkflowTask firstTask = apsWorkflowTasks.first()
        if (firstTask.workflowType != ApsWorkflowType.ACCESS_VERIFICATION) {
            render view: firstTask.responseForm, model: [tasks: apsWorkflowTasks, task: firstTask]
        } else {
            flash.message = 'Access Verification tasks can not be completed in groups'
            redirect(action: 'list')
        }
    }

    def sendGroupResponse = {
        List<Entitlement> entitlements = params.list('passwordUpdatedEntitlementIds') ? Entitlement.findAllByIdInList(params.list('passwordUpdatedEntitlementIds')*.toString()).unique() : []
        if (entitlements) {
            entitlementService.setLastPasswordChangeAttribute(entitlements, params['actionDate'] as String)
        }
        List<ApsWorkflowTask> apsWorkflowTaskList = params.list('taskIds') ? ApsWorkflowTask.getAll(params.list('taskIds')*.toLong()) : []
        Map responseElements = params as HashMap
        responseElements.remove('action')
        responseElements.remove('controller')
        responseElements.remove('id')
        List<UploadedFile> uploadedFiles = AppUtil.populateAttachments(params)
        List<ApsDataFile> apsDataFiles = []
        responseElements = responseElements.findAll { String key, value ->
            (!key.startsWith("multiFile"))
        }
        uploadedFiles?.each { UploadedFile uploadedFile ->
            apsDataFiles.add(new ApsDataFile(uploadedFile))
        }
        apsWorkflowTaskList.each { ApsWorkflowTask apsWorkflowTask ->
            apsWorkflowTask.documents = apsDataFiles as Set
            if (apsWorkflowTask.status in [WorkflowTaskStatus.COMPLETE, WorkflowTaskStatus.CANCELLED]) {
                ApsWorkflowTaskPermittedSlid.markArchived(ApsWorkflowTask.read(apsWorkflowTask.id), SessionUtils.getSession()?.loggedUser);
            } else {
                apsWorkflowTaskService.sendResponse(apsWorkflowTask, responseElements, uploadedFiles, true)
            }
        }
        redirect(action: 'list')
    }

    def changeTaskStatus = {
        ApsWorkflowTaskPermittedSlid.markArchived(ApsWorkflowTask.read(params.long('id')), SessionUtils.getSession()?.loggedUser);
        redirect(action: 'list')
    }

    def sendUserResponse = {
        List<Entitlement> entitlements = params.list('passwordUpdatedEntitlementIds') ? Entitlement.findAllByIdInList(params.list('passwordUpdatedEntitlementIds')*.toString()).flatten().unique() : []
        if (entitlements) {
            entitlementService.setLastPasswordChangeAttribute(entitlements, params['actionDate'] as String)
        }
        ApsWorkflowTask task = ApsWorkflowTask.get(params.long('id'))
        Map responseElements = params as HashMap
        responseElements.remove('action')
        responseElements.remove('controller')
        responseElements.remove('id')
        List<UploadedFile> uploadedFiles = AppUtil.populateAttachments(params)
        responseElements = responseElements.findAll { String key, value ->
            (!key.startsWith("multiFile"))
        }
        apsWorkflowTaskService.sendResponse(task, responseElements, uploadedFiles)
        redirect(action: 'list')
    }

    def filterDialog = {
        List<ApsWorkflowTask> apsWorkflowTasks = ApsWorkflowTask.getPermittedTasks()
        render(template: 'workflowTaskFilter',
                model: [apsWorkflowTypeList: apsWorkflowTasks*.workflowType.unique().findAll { it }.sort { it.name() },
                        workers: apsWorkflowTasks*.worker.unique().findAll { it }.sort { it.lastName },
                        entitlements: apsWorkflowTasks*.entitlement.findAll { it }.unique().sort { it.name },
                        entitlementPolicyList: EntitlementPolicy.list().sort { it.name },
                        currentNodeNames: apsWorkflowTasks*.nodeName.unique().findAll { it }.sort(),
                        securityRolesList: SessionUtils.session?.roles ? SecurityRole.findAllByNameInList(SessionUtils.session?.roles) : [],
                        filterVO: session.filterVO
                ])
    }

    def filterList = {
        List<ApsWorkflowTask> apsWorkflowTaskList = ApsWorkflowTask.getPermittedTasks()
        WorkflowTaskFilterVO workflowTaskFilterVO = new WorkflowTaskFilterVO(params?.workflowType, params?.workerId ? params?.workerId?.toLong() : null, params?.entitlementPolicyId ? params?.entitlementPolicyId?.toLong() : null, params?.entitlementId, params?.currentNodeName, params?.securityRoleId ? params?.securityRoleId?.toLong() : null)
        apsWorkflowTaskList = filterWorkflowTaskList(apsWorkflowTaskList, workflowTaskFilterVO)
        session.filterVO = workflowTaskFilterVO
        render view: 'list', model: [tasks: apsWorkflowTaskList]
    }

    def showAllTasks = {
        session.filterVO = null
        redirect action: 'list'
    }

    def filterCompletedTasks = {
        List<ApsWorkflowTask> apsWorkflowTasks = ApsWorkflowTask.getPermittedTasksCompleted()
        render(view: 'filterCompletedTasks',
                model: [apsWorkflowTypeList: apsWorkflowTasks*.workflowType.unique().findAll { it }.sort { it.name() },
                        workers: apsWorkflowTasks*.worker.unique().findAll { it }.sort { it.lastName },
                        entitlements: apsWorkflowTasks*.entitlement.findAll { it }.unique().sort { it.name },
                        entitlementPolicyList: EntitlementPolicy.list().sort { it.name },
                        securityRolesList: SessionUtils.session?.roles ? SecurityRole.findAllByNameInList(SessionUtils.session?.roles) : [],
                        actorSlids: apsWorkflowTasks*.actorSlid.unique(),
                        currentNodeNames: apsWorkflowTasks*.nodeName.unique().findAll { it }.sort(),
                ])
    }

    def filteredCompletedTasks = {
        List<ApsWorkflowTask> apsWorkflowTaskList = ApsWorkflowTask.getPermittedTasksCompleted()
        WorkflowTaskFilterVO workflowTaskFilterVO = new WorkflowTaskFilterVO(params?.workflowType, params?.workerId ? params?.workerId?.toLong() : null, params?.entitlementPolicyId ? params?.entitlementPolicyId?.toLong() : null, params?.entitlementId, params?.currentNodeName, params?.securityRoleId ? params?.securityRoleId?.toLong() : null, params?.actorSlid)
        apsWorkflowTaskList = filterWorkflowTaskList(apsWorkflowTaskList, workflowTaskFilterVO)
        render view: 'filteredCompletedTasks', model: [tasks: apsWorkflowTaskList]
    }

    def storeAndAttachEvidence = {
        List<ApsWorkflowTask> apsWorkflowTaskList = params.list('taskIds') ? ApsWorkflowTask.getAll(params.list('taskIds')*.toLong()) : []
        List<UploadedFile> uploadedFiles = AppUtil.populateAttachments(params)
        List<ApsDataFile> apsDataFiles = []
        uploadedFiles?.each { UploadedFile uploadedFile ->
            apsDataFiles.add(new ApsDataFile(uploadedFile))
        }
        apsWorkflowTaskList.each { ApsWorkflowTask apsWorkflowTask ->
            apsWorkflowTask.documents.addAll(apsDataFiles as Set)
            apsWorkflowTask.s()
        }
        redirect(action: 'filterCompletedTasks')
    }
}