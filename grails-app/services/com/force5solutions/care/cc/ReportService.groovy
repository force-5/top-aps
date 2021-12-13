package com.force5solutions.care.cc

import com.force5solutions.care.aps.Entitlement
import com.force5solutions.care.aps.EntitlementRole
import com.force5solutions.care.common.CareConstants

class ReportService {

    boolean transactional = true

    File createWorkerArchiveReport(String focusArea, String workerId, Date fromDate = null, Date toDate = null) {
        List<WorkerArchiveEntitlementFocusVO> workerArchiveEntitlementFocusVOList = []
        File csvFile = null
        switch (focusArea) {
            case 'Entitlement':
                workerArchiveEntitlementFocusVOList = generateEntitlementFocusReport(workerId, fromDate, toDate)
                csvFile = createCsvFileForEntitlementFocus(workerArchiveEntitlementFocusVOList)
                break
            case 'Certification':
                workerArchiveEntitlementFocusVOList = generateCertificationFocusReport(workerId, fromDate, toDate)
                csvFile = createCsvFileForCertificationFocus(workerArchiveEntitlementFocusVOList)
                break
            case 'Profile':
                workerArchiveEntitlementFocusVOList = generateProfileFocusReport(workerId, fromDate, toDate)
                csvFile = createCsvFileForProfileFocus(workerArchiveEntitlementFocusVOList)
                break
        }
        return csvFile
    }

    List<WorkerArchiveEntitlementFocusVO> generateEntitlementFocusReport(String workerId, Date fromDate = null, Date toDate = null) {
        List<WorkerArchiveEntitlementFocusVO> workerArchiveEntitlementFocusVOList = []
        if (fromDate && toDate) {
            workerArchiveEntitlementFocusVOList = generateEntitlementFocusReportForDateRange(workerId, fromDate, toDate)
        } else {
            workerArchiveEntitlementFocusVOList = generateEntitlementFocusReportForAllDates(workerId)
        }
        return workerArchiveEntitlementFocusVOList
    }

    List<WorkerArchiveEntitlementFocusVO> generateCertificationFocusReport(String workerId, Date fromDate = null, Date toDate = null) {
        List<WorkerArchiveEntitlementFocusVO> workerArchiveEntitlementFocusVOList = []
        if (fromDate && toDate) {
            workerArchiveEntitlementFocusVOList = generateCertificationFocusReportForDateRange(workerId, fromDate, toDate)
        } else {
            workerArchiveEntitlementFocusVOList = generateCertificationFocusReportForAllDates(workerId)
        }
        return workerArchiveEntitlementFocusVOList
    }

    List<WorkerArchiveEntitlementFocusVO> generateProfileFocusReport(String workerId, Date fromDate = null, Date toDate = null) {
        List<WorkerArchiveEntitlementFocusVO> workerArchiveEntitlementFocusVOList = []
        if (fromDate && toDate) {
            workerArchiveEntitlementFocusVOList = generateProfileFocusReportForDateRange(workerId, fromDate, toDate)
        } else {
            workerArchiveEntitlementFocusVOList = generateProfileFocusReportForAllDates(workerId)
        }
        return workerArchiveEntitlementFocusVOList
    }

    List<WorkerArchiveEntitlementFocusVO> generateEntitlementFocusReportForDateRange(String workerSelectId, Date fromDate, Date toDate) {
        List<WorkerArchiveEntitlementFocusVO> workerArchiveEntitlementFocusVOList = []
        List<WorkerEntitlementArchive> workerEntitlementArchiveList = []
        if (!workerSelectId.equalsIgnoreCase("allWorkers")) {
            workerEntitlementArchiveList = WorkerEntitlementArchive.findAllByWorkerIdAndDateCreatedLessThanEquals(workerSelectId.toLong(), toDate).sort { it.dateCreated }
            workerArchiveEntitlementFocusVOList = populateEntitlementFocusVOList(workerEntitlementArchiveList, workerSelectId, fromDate, toDate)
        } else {
            workerEntitlementArchiveList = WorkerEntitlementArchive.findAllByDateCreatedLessThan(toDate).sort { workerEntitlementArchive, workerEntitlementArchive1 -> workerEntitlementArchive.workerId <=> workerEntitlementArchive1.workerId ?: workerEntitlementArchive.dateCreated <=> workerEntitlementArchive1.dateCreated ?: workerEntitlementArchive.actionDate <=> workerEntitlementArchive1.actionDate }
            workerArchiveEntitlementFocusVOList = populateEntitlementFocusVOList(workerEntitlementArchiveList, null, fromDate, toDate)
        }
        return workerArchiveEntitlementFocusVOList
    }

    List<WorkerArchiveEntitlementFocusVO> generateEntitlementFocusReportForAllDates(String workerSelectId) {
        List<WorkerArchiveEntitlementFocusVO> workerArchiveEntitlementFocusVOList = []
        List<WorkerEntitlementArchive> workerEntitlementArchiveList = []
        if (!workerSelectId.equalsIgnoreCase("allWorkers")) {
            workerEntitlementArchiveList = WorkerEntitlementArchive.findAllByWorkerId(workerSelectId.toLong()).sort { it.dateCreated }
            workerArchiveEntitlementFocusVOList = populateEntitlementFocusVOList(workerEntitlementArchiveList, workerSelectId)
        } else {
            workerEntitlementArchiveList = WorkerEntitlementArchive.list().sort { workerEntitlementArchive, workerEntitlementArchive1 -> workerEntitlementArchive.workerId <=> workerEntitlementArchive1.workerId ?: workerEntitlementArchive.dateCreated <=> workerEntitlementArchive1.dateCreated ?: workerEntitlementArchive.actionDate <=> workerEntitlementArchive1.actionDate }
            workerArchiveEntitlementFocusVOList = populateEntitlementFocusVOList(workerEntitlementArchiveList)
        }
        return workerArchiveEntitlementFocusVOList
    }

    public List<WorkerArchiveEntitlementFocusVO> populateEntitlementFocusVOList(List<WorkerEntitlementArchive> workerEntitlementArchiveList, String workerSelectId = null, Date fromDate = null, Date toDate = null) {
        List<WorkerArchiveEntitlementFocusVO> workerArchiveEntitlementFocusVOList = []
        Map<Long, List<WorkerEntitlementArchive>> recordsGroupedByWorkerId = workerEntitlementArchiveList.groupBy { it.workerId }
        recordsGroupedByWorkerId.each { Long workerId, List<WorkerEntitlementArchive> archiveList ->
            Map<String, List<WorkerEntitlementArchive>> recordsGroupedByEntitlementIdForAWorker = archiveList.groupBy { it.entitlementId }
            recordsGroupedByEntitlementIdForAWorker.each { String entitlementId, List<WorkerEntitlementArchive> archives ->
                archives = archives.findAll { it.userResponse.equalsIgnoreCase('CONFIRM') }.sort { it.dateCreated }
                List<WorkerEntitlementArchive> confirmedAccessRequestArchives = archives.findAll { it.actionType.equalsIgnoreCase(CareConstants.ACCESS_REQUEST) }.sort { it.dateCreated }
                List<WorkerEntitlementArchive> confirmedRevokeRequestArchives = archives.findAll { it.actionType.equalsIgnoreCase(CareConstants.REVOKE_REQUEST) }.sort { it.dateCreated }
                confirmedAccessRequestArchives.eachWithIndex { WorkerEntitlementArchive accessRequestArchive, int index ->

                    WorkerArchiveEntitlementFocusVO workerArchiveEntitlementFocusVO = new WorkerArchiveEntitlementFocusVO()
                    if (!(fromDate && toDate)) {
                        if (!(confirmedAccessRequestArchives.size() >= (index + 1))) {
                            workerArchiveEntitlementFocusVO = createActiveEntitlementEntry(accessRequestArchive, fromDate)
                        } else {
                            workerArchiveEntitlementFocusVO = populateWorkerArchiveEntitlementFocusVO(accessRequestArchive, workerArchiveEntitlementFocusVO)
                            workerArchiveEntitlementFocusVO = populateMoreDetailsInWorkerArchiveEntitlementFocusVO(index, workerArchiveEntitlementFocusVO, accessRequestArchive, confirmedRevokeRequestArchives, fromDate)
                        }
                    } else {
                        if (!(confirmedRevokeRequestArchives.size() >= (index + 1))) {
                            workerArchiveEntitlementFocusVO = createActiveEntitlementEntry(accessRequestArchive, fromDate)
                        } else if (confirmedRevokeRequestArchives.get(index).dateCreated > fromDate) {
                            if (confirmedRevokeRequestArchives.get(index).dateCreated < toDate) {
                                workerArchiveEntitlementFocusVO = populateWorkerArchiveEntitlementFocusVO(accessRequestArchive, workerArchiveEntitlementFocusVO)
                                workerArchiveEntitlementFocusVO = populateMoreDetailsInWorkerArchiveEntitlementFocusVO(index, workerArchiveEntitlementFocusVO, accessRequestArchive, confirmedRevokeRequestArchives, fromDate)
                            } else {
                                workerArchiveEntitlementFocusVO = createActiveEntitlementEntry(accessRequestArchive, fromDate)
                            }
                        }
                    }
                    workerArchiveEntitlementFocusVOList.add(workerArchiveEntitlementFocusVO)
                }
            }
        }
        return workerArchiveEntitlementFocusVOList.findAll { it.archiveRecordDateCreated }
    }

    private WorkerArchiveEntitlementFocusVO populateMoreDetailsInWorkerArchiveEntitlementFocusVO(int index, WorkerArchiveEntitlementFocusVO workerArchiveEntitlementFocusVO, WorkerEntitlementArchive accessRequestArchive, List<WorkerEntitlementArchive> confirmedRevokeRequestArchives, Date fromDate) {
        if (confirmedRevokeRequestArchives.size() >= (index + 1)) {
            WorkerEntitlementArchive correspondingRevokeArchive = confirmedRevokeRequestArchives.get(index)
            workerArchiveEntitlementFocusVO.accessRevokedDate = correspondingRevokeArchive.actionDate
            workerArchiveEntitlementFocusVO.accessRevokedNotes = correspondingRevokeArchive.notes.replaceAll("\\r\\n", " ").replaceAll("\\r", " ").replaceAll("\\n", " ")
            workerArchiveEntitlementFocusVO = populateOtherDetails(workerArchiveEntitlementFocusVO, correspondingRevokeArchive, null, fromDate)
        } else {
            workerArchiveEntitlementFocusVO = populateOtherDetails(workerArchiveEntitlementFocusVO, accessRequestArchive, null, fromDate)
        }
        workerArchiveEntitlementFocusVO
    }

    private WorkerArchiveEntitlementFocusVO populateWorkerArchiveEntitlementFocusVO(WorkerEntitlementArchive accessRequestArchive, WorkerArchiveEntitlementFocusVO workerArchiveEntitlementFocusVO) {
        workerArchiveEntitlementFocusVO.accessGrantedDate = accessRequestArchive.actionDate
        workerArchiveEntitlementFocusVO.workerId = accessRequestArchive.workerId
        workerArchiveEntitlementFocusVO.slid = accessRequestArchive.workerSlid
        workerArchiveEntitlementFocusVO.firstName = accessRequestArchive.workerFirstName
        workerArchiveEntitlementFocusVO.lastName = accessRequestArchive.workerLastName
        workerArchiveEntitlementFocusVO.entitlementName = accessRequestArchive.entitlementName
        workerArchiveEntitlementFocusVO.entitlementAction = accessRequestArchive.actionType
        workerArchiveEntitlementFocusVO.accessGrantedNotes = accessRequestArchive?.notes?.replaceAll("\\r\\n", " ")?.replaceAll("\\r", " ")?.replaceAll("\\n", " ")
        return workerArchiveEntitlementFocusVO
    }

    WorkerArchiveEntitlementFocusVO createActiveEntitlementEntry(WorkerEntitlementArchive accessRequestArchive, Date fromDate) {
        WorkerArchiveEntitlementFocusVO workerArchiveEntitlementFocusVO = new WorkerArchiveEntitlementFocusVO()
        workerArchiveEntitlementFocusVO = populateWorkerArchiveEntitlementFocusVO(accessRequestArchive, workerArchiveEntitlementFocusVO)
        workerArchiveEntitlementFocusVO = populateOtherDetails(workerArchiveEntitlementFocusVO, accessRequestArchive, null, fromDate)
        return workerArchiveEntitlementFocusVO
    }

    public WorkerArchiveEntitlementFocusVO populateOtherDetails(WorkerArchiveEntitlementFocusVO workerArchiveEntitlementFocusVO, WorkerEntitlementArchive workerEntitlementArchive, String workerSelectId = null, Date fromDate = null) {
        WorkerProfileArchive workerProfileArchive = getRelevantWorkerProfileArchiveEntry(workerSelectId ?: workerEntitlementArchive.workerId.toString(), workerEntitlementArchive.dateCreated)
        workerArchiveEntitlementFocusVO.archiveRecordDateCreated = (fromDate && workerEntitlementArchive.dateCreated < fromDate) ? fromDate.toTimestamp() : workerEntitlementArchive.dateCreated
        if (workerProfileArchive) {
            workerArchiveEntitlementFocusVO = populatedArchiveFocusVOFromProfileArchive(workerProfileArchive, workerArchiveEntitlementFocusVO)
        }
        WorkerCertificationArchive workerCertificationArchive = getRelevantWorkerCertificationArchiveEntry(workerSelectId ?: workerEntitlementArchive.workerId.toString(), workerEntitlementArchive.dateCreated)
        if (workerCertificationArchive) {
            workerArchiveEntitlementFocusVO.certificationNames = workerCertificationArchive.certificationNames
        }
        return workerArchiveEntitlementFocusVO
    }

    WorkerProfileArchive getRelevantWorkerProfileArchiveEntry(String workerSelectId, Date dateCreated) {
        List<WorkerProfileArchive> workerProfileArchiveList = WorkerProfileArchive.findAllByWorkerIdAndDateCreatedLessThanEquals(workerSelectId.toLong(), dateCreated).sort { it.dateCreated }
        return workerProfileArchiveList ? workerProfileArchiveList.last() : null
    }

    WorkerCertificationArchive getRelevantWorkerCertificationArchiveEntry(String workerSelectId, Date dateCreated) {
        List<WorkerCertificationArchive> workerCertificationArchiveList = WorkerCertificationArchive.findAllByWorkerIdAndDateCreatedLessThanEquals(workerSelectId.toLong(), dateCreated).sort { it.dateCreated }
        return workerCertificationArchiveList ? workerCertificationArchiveList.last() : null
    }

    public File createCsvFileForEntitlementFocus(List<WorkerArchiveEntitlementFocusVO> workerArchiveEntitlementFocusVOList) {
        File csvFile = new File(System.getProperty('java.io.tmpdir'), "workerArchiveReportEntitlementFocus.csv")
        if (csvFile.exists()) {
            csvFile.delete()
        }
        List<String> columnNames = ['Date', 'SLID', 'Worker Number', 'First Name', 'Last Name', 'Certifications', 'Entitlement Name', 'Access Granted Date', 'Access Revoked Date', 'Access Granted Justification', 'Access Revoked Justification', 'Supervisor SLID',
                'Supervisor Name', 'Status', 'Title', 'Department', 'Area Number', 'Org. Unit Number', 'Org. Unit Description', 'Supervisor\'s Supv SLID', 'Business Unit',
                'Business Unit Requester SLID', 'Notes', 'Badge Number', 'Sub Area Number', 'Sub Area Description', 'Vendor', 'Office Phone', 'Email',
                'Phone', 'Cell Phone', 'Pager']
        columnNames.each { String columnName ->
            csvFile.append(columnName)
            csvFile.append('|')
        }
        csvFile.append("\r\n")
        workerArchiveEntitlementFocusVOList = workerArchiveEntitlementFocusVOList.sort { it.archiveRecordDateCreated }
        workerArchiveEntitlementFocusVOList.each { def workerArchiveEntitlementFocusVO ->
            csvFile.append(workerArchiveEntitlementFocusVO.archiveRecordDateCreated ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.slid ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.workerNumber ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.firstName ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.lastName ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.certificationNames ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.entitlementName ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.accessGrantedDate ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.accessRevokedDate ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.accessGrantedNotes ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.accessRevokedNotes ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.supervisorSlid ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.supvFullName ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.personStatus ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.title ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.department ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.persAreaNum ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.orgUnitNum ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.orgUnitDesc ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.supvSupvSlid ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.businessUnit ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.businessUnitRequesterSlid ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.notes ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.badgeNumber ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.persSubAreaNum ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.persSubAreaDesc ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.vendorName ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.officePhoneNum ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.email ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.phone ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.cellPhoneNum ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.pagerNum ?: "")
            csvFile.append('|')
            csvFile.append("\r\n")
        }
        return csvFile
    }

    List<WorkerArchiveEntitlementFocusVO> generateCertificationFocusReportForDateRange(String workerSelectId, Date fromDate, Date toDate) {
        List<WorkerArchiveEntitlementFocusVO> workerArchiveEntitlementFocusVOList = []
        List<WorkerCertificationArchive> workerCertificationArchiveList = []
        if (!workerSelectId.equalsIgnoreCase("allWorkers")) {
            workerCertificationArchiveList = WorkerCertificationArchive.findAllByWorkerIdAndDateCreatedBetween(workerSelectId.toLong(), fromDate, toDate).sort { it.dateCreated }
            workerArchiveEntitlementFocusVOList = populateCertificationFocusVOList(workerCertificationArchiveList, workerSelectId)
        } else {
            workerCertificationArchiveList = WorkerCertificationArchive.findAllByDateCreatedBetween(fromDate, toDate).sort { workerCertificationArchive, workerCertificationArchive1 -> workerCertificationArchive.workerId <=> workerCertificationArchive1.workerId ?: workerCertificationArchive.dateCreated <=> workerCertificationArchive1.dateCreated }
            workerArchiveEntitlementFocusVOList = populateCertificationFocusVOList(workerCertificationArchiveList)
        }
        return workerArchiveEntitlementFocusVOList
    }

    List<WorkerArchiveEntitlementFocusVO> generateCertificationFocusReportForAllDates(String workerSelectId) {
        List<WorkerArchiveEntitlementFocusVO> workerArchiveEntitlementFocusVOList = []
        List<WorkerCertificationArchive> workerCertificationArchiveList = []
        if (!workerSelectId.equalsIgnoreCase("allWorkers")) {
            workerCertificationArchiveList = WorkerCertificationArchive.findAllByWorkerId(workerSelectId.toLong()).sort { it.dateCreated }
            workerArchiveEntitlementFocusVOList = populateCertificationFocusVOList(workerCertificationArchiveList, workerSelectId)
        } else {
            workerCertificationArchiveList = WorkerCertificationArchive.list().sort { workerCertificationArchive, workerCertificationArchive1 -> workerCertificationArchive.workerId <=> workerCertificationArchive1.workerId ?: workerCertificationArchive.dateCreated <=> workerCertificationArchive1.dateCreated }
            workerArchiveEntitlementFocusVOList = populateCertificationFocusVOList(workerCertificationArchiveList)
        }
        return workerArchiveEntitlementFocusVOList
    }

    public List<WorkerArchiveEntitlementFocusVO> populateCertificationFocusVOList(List<WorkerCertificationArchive> workerCertificationArchiveList, String workerSelectId = null) {
        List<WorkerArchiveEntitlementFocusVO> workerArchiveEntitlementFocusVOList = []
        workerCertificationArchiveList.each { WorkerCertificationArchive workerCertificationArchive ->
            WorkerArchiveEntitlementFocusVO workerArchiveEntitlementFocusVO = new WorkerArchiveEntitlementFocusVO()
            workerArchiveEntitlementFocusVO.dateCreated = workerCertificationArchive.dateCreated
            workerArchiveEntitlementFocusVO.workerId = workerCertificationArchive.workerId
            workerArchiveEntitlementFocusVO.slid = workerCertificationArchive.workerSlid
            workerArchiveEntitlementFocusVO.firstName = workerCertificationArchive.workerFirstName
            workerArchiveEntitlementFocusVO.lastName = workerCertificationArchive.workerLastName
            workerArchiveEntitlementFocusVO.certificationNames = workerCertificationArchive.certificationNames

            WorkerProfileArchive workerProfileArchive = getRelevantWorkerProfileArchiveEntry(workerSelectId ?: workerCertificationArchive.workerId.toString(), workerCertificationArchive.dateCreated)
            if (workerProfileArchive) {
                workerArchiveEntitlementFocusVO = populatedArchiveFocusVOFromProfileArchive(workerProfileArchive, workerArchiveEntitlementFocusVO)
            }
            workerArchiveEntitlementFocusVOList.add(workerArchiveEntitlementFocusVO)
        }
        return workerArchiveEntitlementFocusVOList
    }

    public File createCsvFileForCertificationFocus(List<WorkerArchiveEntitlementFocusVO> workerArchiveEntitlementFocusVOList) {
        File csvFile = new File(System.getProperty('java.io.tmpdir'), "workerArchiveReportCertificationFocus.csv")
        if (csvFile.exists()) {
            csvFile.delete()
        }
        List<String> columnNames = ['SLID', 'Worker Number', 'First Name', 'Last Name', 'Certification Names', 'Record Created', 'Active Entitlements', 'Supervisor SLID',
                'Supervisor Name', 'Status', 'Title', 'Department', 'Area Number', 'Org. Unit Number', 'Org. Unit Description', 'Supervisor\'s Supv SLID', 'Business Unit',
                'Business Unit Requester SLID', 'Notes', 'Badge Number', 'Sub Area Number', 'Sub Area Description', 'Vendor', 'Office Phone', 'Email',
                'Phone', 'Cell Phone', 'Pager']
        columnNames.each { String columnName ->
            csvFile.append(columnName)
            csvFile.append('|')
        }
        csvFile.append("\r\n")

        workerArchiveEntitlementFocusVOList.each { def workerArchiveEntitlementFocusVO ->
            csvFile.append(workerArchiveEntitlementFocusVO.slid ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.workerNumber ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.firstName ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.lastName ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.certificationNames ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.dateCreated ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.entitlementNames ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.supervisorSlid ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.supvFullName ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.personStatus ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.title ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.department ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.persAreaNum ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.orgUnitNum ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.orgUnitDesc ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.supvSupvSlid ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.businessUnit ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.businessUnitRequesterSlid ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.notes ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.badgeNumber ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.persSubAreaNum ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.persSubAreaDesc ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.vendorName ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.officePhoneNum ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.email ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.phone ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.cellPhoneNum ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.pagerNum ?: "")
            csvFile.append('|')
            csvFile.append("\r\n")
        }
        return csvFile
    }

    List<WorkerArchiveEntitlementFocusVO> generateProfileFocusReportForDateRange(String workerSelectId, Date fromDate, Date toDate) {
        List<WorkerArchiveEntitlementFocusVO> workerArchiveEntitlementFocusVOList = []
        List<WorkerProfileArchive> workerProfileArchiveList = []
        if (!workerSelectId.equalsIgnoreCase("allWorkers")) {
            workerProfileArchiveList = WorkerProfileArchive.findAllByWorkerIdAndDateCreatedBetween(workerSelectId.toLong(), fromDate, toDate).sort { it.dateCreated }
            workerArchiveEntitlementFocusVOList = populateProfileFocusVOList(workerProfileArchiveList, workerSelectId)
        } else {
            workerProfileArchiveList = WorkerProfileArchive.findAllByDateCreatedBetween(fromDate, toDate).sort { workerProfileArchive, workerProfileArchive1 -> workerProfileArchive.workerId <=> workerProfileArchive1.workerId ?: workerProfileArchive.dateCreated <=> workerProfileArchive1.dateCreated }
            workerArchiveEntitlementFocusVOList = populateProfileFocusVOList(workerProfileArchiveList)
        }
        return workerArchiveEntitlementFocusVOList
    }

    List<WorkerArchiveEntitlementFocusVO> generateProfileFocusReportForAllDates(String workerSelectId) {
        List<WorkerArchiveEntitlementFocusVO> workerArchiveEntitlementFocusVOList = []
        List<WorkerProfileArchive> workerProfileArchiveList = []
        if (!workerSelectId.equalsIgnoreCase("allWorkers")) {
            workerProfileArchiveList = WorkerProfileArchive.findAllByWorkerId(workerSelectId.toLong()).sort { it.dateCreated }
            workerArchiveEntitlementFocusVOList = populateProfileFocusVOList(workerProfileArchiveList, workerSelectId)
        } else {
            workerProfileArchiveList = WorkerProfileArchive.list().sort { workerProfileArchive, workerProfileArchive1 -> workerProfileArchive.workerId <=> workerProfileArchive1.workerId ?: workerProfileArchive.dateCreated <=> workerProfileArchive1.dateCreated }
            workerArchiveEntitlementFocusVOList = populateProfileFocusVOList(workerProfileArchiveList)
        }
        return workerArchiveEntitlementFocusVOList
    }

    public List<WorkerArchiveEntitlementFocusVO> populateProfileFocusVOList(List<WorkerProfileArchive> workerProfileArchiveList, String workerSelectId = null) {
        List<WorkerArchiveEntitlementFocusVO> workerArchiveEntitlementFocusVOList = []
        workerProfileArchiveList.each { WorkerProfileArchive workerProfileArchive ->
            WorkerArchiveEntitlementFocusVO workerArchiveEntitlementFocusVO = new WorkerArchiveEntitlementFocusVO()
            workerArchiveEntitlementFocusVO.dateCreated = workerProfileArchive.dateCreated
            workerArchiveEntitlementFocusVO.workerId = workerProfileArchive.workerId
            workerArchiveEntitlementFocusVO.slid = workerProfileArchive.slid
            workerArchiveEntitlementFocusVO.firstName = workerProfileArchive.firstName
            workerArchiveEntitlementFocusVO.lastName = workerProfileArchive.lastName
            workerArchiveEntitlementFocusVO = populatedArchiveFocusVOFromProfileArchive(workerProfileArchive, workerArchiveEntitlementFocusVO)
            workerArchiveEntitlementFocusVOList.add(workerArchiveEntitlementFocusVO)
        }
        return workerArchiveEntitlementFocusVOList
    }

    private WorkerArchiveEntitlementFocusVO populatedArchiveFocusVOFromProfileArchive(WorkerProfileArchive workerProfileArchive, WorkerArchiveEntitlementFocusVO workerArchiveEntitlementFocusVO) {
        workerArchiveEntitlementFocusVO.supervisorSlid = workerProfileArchive.supervisorSlid
        workerArchiveEntitlementFocusVO.supvSupvSlid = workerProfileArchive.supvSupvSlid
        workerArchiveEntitlementFocusVO.email = workerProfileArchive.email
        workerArchiveEntitlementFocusVO.phone = workerProfileArchive.phone
        workerArchiveEntitlementFocusVO.notes = workerProfileArchive?.notes?.replaceAll("\\r\\n", " ")?.replaceAll("\\r", " ")?.replaceAll("\\n", " ")
        workerArchiveEntitlementFocusVO.personStatus = workerProfileArchive.personStatus
        workerArchiveEntitlementFocusVO.orgUnitNum = workerProfileArchive.orgUnitNum
        workerArchiveEntitlementFocusVO.orgUnitDesc = workerProfileArchive.orgUnitDesc
        workerArchiveEntitlementFocusVO.workerNumber = workerProfileArchive.workerNumber
        workerArchiveEntitlementFocusVO.title = workerProfileArchive.title
        workerArchiveEntitlementFocusVO.persAreaNum = workerProfileArchive.persAreaNum
        workerArchiveEntitlementFocusVO.department = workerProfileArchive.department
        workerArchiveEntitlementFocusVO.persSubAreaNum = workerProfileArchive.persSubAreaNum
        workerArchiveEntitlementFocusVO.persSubAreaDesc = workerProfileArchive.persSubAreaDesc
        workerArchiveEntitlementFocusVO.officePhoneNum = workerProfileArchive.officePhoneNum
        workerArchiveEntitlementFocusVO.cellPhoneNum = workerProfileArchive.cellPhoneNum
        workerArchiveEntitlementFocusVO.pagerNum = workerProfileArchive.pagerNum
        workerArchiveEntitlementFocusVO.supvFullName = workerProfileArchive.supvFullName
        workerArchiveEntitlementFocusVO.supvSupvFullName = workerProfileArchive.supvSupvFullName
        workerArchiveEntitlementFocusVO.badgeNumber = workerProfileArchive.badgeNumber
        workerArchiveEntitlementFocusVO.businessUnitRequesterSlid = workerProfileArchive.businessUnitRequesterSlid
        workerArchiveEntitlementFocusVO.businessUnit = workerProfileArchive.businessUnit
        workerArchiveEntitlementFocusVO.vendorName = workerProfileArchive.vendorName
        return workerArchiveEntitlementFocusVO
    }

    public File createCsvFileForProfileFocus(List<WorkerArchiveEntitlementFocusVO> workerArchiveEntitlementFocusVOList) {
        File csvFile = new File(System.getProperty('java.io.tmpdir'), "workerArchiveReportProfileFocus.csv")
        if (csvFile.exists()) {
            csvFile.delete()
        }
        List<String> columnNames = ['SLID', 'Worker Number', 'First Name', 'Last Name', 'Record Created', 'Supervisor SLID',
                'Supervisor Name', 'Status', 'Title', 'Department', 'Area Number', 'Org. Unit Number', 'Org. Unit Description', 'Supervisor\'s Supv SLID', 'Business Unit',
                'Business Unit Requester SLID', 'Notes', 'Badge Number', 'Sub Area Number', 'Sub Area Description', 'Vendor', 'Office Phone', 'Email',
                'Phone', 'Cell Phone', 'Pager']
        columnNames.each { String columnName ->
            csvFile.append(columnName)
            csvFile.append('|')
        }
        csvFile.append("\r\n")

        workerArchiveEntitlementFocusVOList.each { def workerArchiveEntitlementFocusVO ->
            csvFile.append(workerArchiveEntitlementFocusVO.slid ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.workerNumber ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.firstName ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.lastName ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.dateCreated ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.supervisorSlid ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.supvFullName ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.personStatus ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.title ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.department ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.persAreaNum ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.orgUnitNum ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.orgUnitDesc ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.supvSupvSlid ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.businessUnit ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.businessUnitRequesterSlid ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.notes ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.badgeNumber ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.persSubAreaNum ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.persSubAreaDesc ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.vendorName ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.officePhoneNum ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.email ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.phone ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.cellPhoneNum ?: "")
            csvFile.append('|')
            csvFile.append(workerArchiveEntitlementFocusVO.pagerNum ?: "")
            csvFile.append('|')
            csvFile.append("\r\n")
        }
        return csvFile
    }

    public File createCsvFileForEntitlementAccessReport(List<EntitlementAccessReportVO> entitlementAccessReportVOList) {
        File csvFile = new File(System.getProperty('java.io.tmpdir'), "entitlementAccessReport.csv")
        if (csvFile.exists()) {
            csvFile.delete()
        }

        csvFile.append('Entitlement Name,Last Name,Middle Name,First Name,SLID,Badge Number,Position Title\r\n')
        entitlementAccessReportVOList.each { def entitlementAccessReportVO ->
            csvFile.append(entitlementAccessReportVO.entitlementName ?: "")
            csvFile.append(',')
            csvFile.append(entitlementAccessReportVO.lastName ?: "")
            csvFile.append(',')
            csvFile.append(entitlementAccessReportVO.middleName ?: "")
            csvFile.append(',')
            csvFile.append(entitlementAccessReportVO.firstName ?: "")
            csvFile.append(',')
            csvFile.append(entitlementAccessReportVO.slid ?: "")
            csvFile.append(',')
            csvFile.append(entitlementAccessReportVO.badgeNumber ?: "")
            csvFile.append(',"')
            csvFile.append(entitlementAccessReportVO.positionTitle ?: "")
            csvFile.append('"\r\n')
        }
        return csvFile
    }

    File createEntitlementAccessReport(List<String> entitlementIds) {
        List<EntitlementAccessReportVO> entitlementAccessReportVOList = []
        File csvFile = null
        List<Entitlement> entitlementList = Entitlement.findAllByIdInList(entitlementIds)
        List<EntitlementRole> entitlementRoleList = EntitlementRole.list()
        List<EntitlementRole> containingEntitlementRoles = []
        entitlementRoleList.each { EntitlementRole entitlementRole ->
            if (entitlementRole.allEntitlements.intersect(entitlementList as Set)) {
                containingEntitlementRoles.add(entitlementRole)
            }
        }
        List<CcEntitlementRole> ccEntitlementRoleList = CcEntitlementRole.findAllByIdInList(containingEntitlementRoles*.id)
        List<WorkerEntitlementRole> workerEntitlementRoleList = WorkerEntitlementRole.findAllByStatusAndEntitlementRoleInList(EntitlementRoleAccessStatus.ACTIVE, ccEntitlementRoleList)
        entitlementAccessReportVOList = populateEntitlementAccessReportVOList(entitlementList, containingEntitlementRoles, workerEntitlementRoleList)
        csvFile = createCsvFileForEntitlementAccessReport(entitlementAccessReportVOList)
        return csvFile
    }

    List<EntitlementAccessReportVO> populateEntitlementAccessReportVOList(List<Entitlement> entitlementList, List<EntitlementRole> containingEntitlementRoles, List<WorkerEntitlementRole> workerEntitlementRoleList) {
        List<EntitlementAccessReportVO> entitlementAccessReportVOList = []
        entitlementList.each { Entitlement entitlement ->
            containingEntitlementRoles.each { EntitlementRole entitlementRole ->
                if (entitlementRole.allEntitlements.contains(entitlement)) {
                    List<WorkerEntitlementRole> rolesContainingEntitlement = workerEntitlementRoleList.findAll { it.entitlementRole.id == entitlementRole.id }
                    rolesContainingEntitlement.each { WorkerEntitlementRole containingRole ->
                        Worker worker = containingRole.worker
                        entitlementAccessReportVOList.add(new EntitlementAccessReportVO(entitlement.name, worker.firstName as String, worker.lastName as String, worker.middleName as String, worker.slid as String, worker?.badgeNumber, worker instanceof Employee ? worker?.title : "Contractor"))
                    }
                }
            }
        }
        return entitlementAccessReportVOList
    }
}
