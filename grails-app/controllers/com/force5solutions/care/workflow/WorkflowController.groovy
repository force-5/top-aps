package com.force5solutions.care.workflow

import com.force5solutions.care.cc.Worker
import com.force5solutions.care.cc.Employee
import com.force5solutions.care.aps.EntitlementRole
import com.force5solutions.care.aps.Entitlement
import com.force5solutions.care.cc.Contractor
import com.force5solutions.care.ldap.Permission
import com.force5solutions.care.common.Secured

@Secured(value = Permission.READ_MANAGE_WORKFLOW)
class WorkflowController {

    def index = {
        redirect(action: 'manageWorkflowTasks')
    }

    def manageWorkflowTasks = {
        List filteredList = session?.filteredTaskList?.flatten()
        def isFiltered = params['isFiltered'] ?: ""

        if (filteredList || isFiltered) {
            render(view: 'manageWorkflow', model: [taskList: filteredList, isFiltered: isFiltered])
        } else {
            def tasks = ApsWorkflowTask.createCriteria().list {
                'in'('status', [WorkflowTaskStatus.NEW, WorkflowTaskStatus.PENDING])
                projections {
                    min('id')
                    groupProperty('workflowGuid')
                }
            }
            List<ApsWorkflowTask> apsWorkflowTasks = []
            if (tasks) {
                apsWorkflowTasks = ApsWorkflowTask.getAll(tasks*.first())
            }
            render(view: 'manageWorkflow', model: [taskList: apsWorkflowTasks])
        }
    }

    def filterWorkflowTasks = {
        String name = params['name']?.trim()
        String workflowTypeString = params['workflowType']
        ApsWorkflowType workflowType = workflowTypeString ? ApsWorkflowType."${workflowTypeString}" : null
        String slid = params['slid']?.trim()
        String taskNodeName = params['taskNodeName']
        def entitlementRoleId = params['entitlementRole']
        def entitlementId = params['entitlement']
        List<ApsWorkflowTask> taskList = []
        List taskIds = []
        List<Worker> workerList = []

        taskIds = ApsWorkflowTask.createCriteria().list {
            and {
                if (workflowType) eq('workflowType', workflowType)
                if (taskNodeName) eq('nodeName', taskNodeName)
                if (entitlementRoleId) eq('entitlementRoleId', entitlementRoleId)
                if (entitlementId) eq('entitlementId', entitlementId)
                'in'('status', [WorkflowTaskStatus.NEW, WorkflowTaskStatus.PENDING])
            }
            projections {
                min('id')
                groupProperty('workflowGuid')
            }
        }

        if (taskIds) {
            taskList = ApsWorkflowTask.getAll(taskIds*.first())
        }

        if (name?.length() || slid?.length()) {
            def employeeList = Employee.createCriteria().list {
                and {
                    person {
                        or {
                            ilike('firstName', '%' + name + '%')
                            ilike('middleName', '%' + name + '%')
                            ilike('lastName', '%' + name + '%')
                        }
                        if (slid) ilike('slid', slid)
                    }
                }
            }

            def contractorList = Contractor.createCriteria().list {
                and {
                    person {
                        or {
                            ilike('firstName', '%' + name + '%')
                            ilike('middleName', '%' + name + '%')
                            ilike('lastName', '%' + name + '%')
                        }
                        if (slid) ilike('slid', slid)
                    }
                }
            }
            workerList = (employeeList + contractorList).flatten()
            taskList = taskList.findAll { ApsWorkflowTask task ->
                task.worker in workerList && (task.status == WorkflowTaskStatus.NEW || task.status == WorkflowTaskStatus.PENDING)
            }
        }

        session.filteredTaskList = taskList
        redirect(action: 'manageWorkflowTasks', params: [isFiltered: true])
    }

    def filterWorkflowDialog = {
        List<ApsWorkflowType> workflowTypes = [ApsWorkflowType.ADD_ROLE, ApsWorkflowType.ADD_ENTITLEMENT, ApsWorkflowType.UPDATE_ROLE, ApsWorkflowType.UPDATE_ENTITLEMENT, ApsWorkflowType.ROLE_ACCESS_REQUEST, ApsWorkflowType.ROLE_ACCESS_REQUEST_FOR_CONTRACTOR, ApsWorkflowType.ROLE_REVOKE_REQUEST, ApsWorkflowType.CANCEL_ACCESS_REVOCATION, ApsWorkflowType.CANCEL_ACCESS_APPROVAL, ApsWorkflowType.TERMINATE_REQUEST]
        List<String> nodeNames = ApsWorkflowTask.createCriteria().list {
            projections {
                distinct "nodeName"
            }
        }
        List<EntitlementRole> entitlementRoles = EntitlementRole.list()
        List<Entitlement> entitlements = Entitlement.list()
        render(template: 'workflowFilter', model: [workflowTypes: workflowTypes, nodeNames: nodeNames, entitlementRoles: entitlementRoles, entitlements: entitlements])
    }

    def showAllWorkflowTasks = {
        session.filteredTaskList = null
        redirect(action: 'manageWorkflowTasks')
    }

    def terminateWorkflowTasks = {
        List<String> selectedWorkflowGuids = params.list('selectedWorkflowGuids')
        selectedWorkflowGuids.unique()
        selectedWorkflowGuids.each {
            ApsWorkflowUtilService.abortWorkflow(it)
            ApsWorkflowUtilService.startCancelWorkflow(it)
        }
        redirect(action: 'manageWorkflowTasks')
    }
}
