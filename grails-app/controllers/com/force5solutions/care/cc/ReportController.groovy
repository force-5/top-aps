package com.force5solutions.care.cc

import com.force5solutions.care.ArchivedEntitlementVO
import com.force5solutions.care.aps.EntitlementRole
import com.force5solutions.care.aps.Entitlement
import com.force5solutions.care.common.CareConstants
import com.force5solutions.care.workflow.ApsWorkflowTask
import com.force5solutions.care.workflow.ApsWorkflowType
import com.force5solutions.care.workflow.CentralWorkflowTask
import com.google.gson.Gson
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import java.text.SimpleDateFormat

class ReportController {

    def reportService

    def index = {
        render(view: 'index', model: [entitlementRoles: EntitlementRole.listOrderByName(), entitlements: Entitlement.listOrderByName(), workers: Worker.list().sort { it.lastName }])
    }

    def accountPasswordChange = {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 1024)
        Writer writer = new BufferedWriter(new PrintWriter(baos));
        writer.println 'Date Created,Account,Security Role(s),Status,Response,Action Date,Actor SLID,Comment'
        ApsWorkflowTask.findAllByWorkflowTypeAndNodeName(ApsWorkflowType.ACCOUNT_PASSWORD_CHANGE,  'Confirm Password Update', [sort: 'dateCreated']).each {
            String securityRoles = it.securityRoles.join('|')
            def actionDate = it.response ? (it.actionDate ?: it.lastUpdated) : null
            writer.println "${it.dateCreated.format('yyyy-MM-dd HH:mm')},\"${it.entitlement?.name ?: ''}\",\"${securityRoles}\",${it.status},\"${it.response ?: ''}\",${actionDate?.format('yyyy-MM-dd HH:mm') ?: ''},${it.actorSlid ?: ''},\"${it.message ?: ''}\""
        }
        writer.flush()
        writer.close()

        response.setContentLength(baos.size())
        response.setHeader("Content-disposition", "attachment; filename=" + 'Account_Password_Change_Report.csv')
        response.setContentType('text/plain')
        OutputStream out = response.getOutputStream()
        baos.writeTo(out)
        out.flush()
        out.close()
    }

    def entitlementRoleToEntitlements = {
        params._name = 'entitlementRoleToEntitlements'
        params._file = 'entitlementRoleToEntitlements'
        params.FOOTER_IMAGE_FILE = "reportFooter.png"
        def entitlementRoleToEntitlementsVOs = []

        EntitlementRole.listOrderByName().each { EntitlementRole role ->
            entitlementRoleToEntitlementsVOs << new EntitlementRoleToEntitlementsVO(role)
        }

        chain(controller: 'jasper', action: 'index', model: [data: entitlementRoleToEntitlementsVOs], params: params)
    }

    def entitlementToEntitlementRoles = {
        params._name = 'entitlementToEntitlementRoles'
        params._file = 'entitlementToEntitlementRoles'
        params.FOOTER_IMAGE_FILE = "reportFooter.png"
        def entitlementToEntitlementRolesVOs = []

        Entitlement.listOrderByName().each { Entitlement entitlement ->
            entitlementToEntitlementRolesVOs << new EntitlementToEntitlementRolesVO(entitlement)
        }

        chain(controller: 'jasper', action: 'index', model: [data: entitlementToEntitlementRolesVOs], params: params)
    }

    def workerArchiveReports = {
        Date fromDate = null, toDate = null
        String workerId = params.workerSelectId.toString()
        String focusArea = params.focusArea.toString()
        Boolean dateRange = params.workerArchiveDateRadio.toString().equalsIgnoreCase('workerArchiveDateRangeRadio')
        if (dateRange) {
            fromDate = new SimpleDateFormat("MM/dd/yyyy").parse(params.'workerArchiveFromDate_value')
            toDate = new SimpleDateFormat("MM/dd/yyyy").parse(params.'workerArchiveToDate_value')
        }
        File csvFile = reportService.createWorkerArchiveReport(focusArea, workerId, fromDate, toDate ? (toDate + 1) : toDate)
        if (csvFile) {
            response.setContentLength(csvFile.bytes.size())
            response.setHeader("Content-disposition", "attachment; filename=" + csvFile.name)
            response.setContentType(AppUtil.getMimeContentType(csvFile.name.tokenize(".").last().toString()))
            OutputStream out = response.getOutputStream()
            out.write(csvFile.bytes)
            out.flush()
            out.close()
        }
        redirect(action: 'index')
    }

    def entitlementsAccessReport = {
        List<String> entitlementIds = params.list('entitlementIds')
        File csvFile = reportService.createEntitlementAccessReport(entitlementIds)
        if (csvFile) {
            response.setContentLength(csvFile.bytes.size())
            response.setHeader("Content-disposition", "attachment; filename=" + csvFile.name)
            response.setContentType(AppUtil.getMimeContentType(csvFile.name.tokenize(".").last().toString()))
            OutputStream out = response.getOutputStream()
            out.write(csvFile.bytes)
            out.flush()
            out.close()
        }
    }

    def createCriticalAssetAuditReport = {
        Date startReportDate, endReportDate
        File file
        Boolean dateRange = params.createCriticalAssetAuditReportRadio.toString().equalsIgnoreCase('createCriticalAssetAuditReportForADateRangeRadio')
        if (dateRange) {
            startReportDate = new SimpleDateFormat("MM/dd/yyyy hh:mm").parse(params.'createCriticalAssetAuditReportForADateRangeFrom_value' + ' 00:00')
            endReportDate = new SimpleDateFormat("MM/dd/yyyy hh:mm").parse(params.'createCriticalAssetAuditReportForADateRangeTo_value' + ' 23:59')
        } else {
            startReportDate = new SimpleDateFormat("MM/dd/yyyy hh:mm").parse(params.'createCriticalAssetAuditReportForADate_value' + ' 00:00')
            endReportDate = new SimpleDateFormat("MM/dd/yyyy hh:mm").parse(params.'createCriticalAssetAuditReportForADate_value' + ' 23:59')
        }
        Map<Worker, List<ArchivedEntitlementVO>> finalEntries = createFinalEntries(startReportDate, endReportDate)
        file = dateRange ? createFileForADateRange(finalEntries, startReportDate) : createFileForADate(finalEntries)
        try {
            byte[] fileContent = file.bytes
            response.setContentLength(fileContent.size())
            response.setHeader("Content-disposition", "attachment; filename=" + file.name.substring(file.name.lastIndexOf(System.getProperty("file.separator")) + 1))
            response.setContentType(AppUtil.getMimeContentType(file.name.tokenize(".").last().toString()))
            OutputStream outputStream = response.getOutputStream()
            outputStream.write(fileContent)
            outputStream.flush()
            outputStream.close()
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false
    }

    Map<Worker, List<ArchivedEntitlementVO>> createFinalEntries(Date startReportDate, Date endReportDate) {
        Map<Worker, List<ArchivedEntitlementVO>> finalEntries = [:]
        Map<Worker, List<WorkerEntitlementArchive>> archivesGroupedByWorker = [:]
        List<WorkerEntitlementArchive> provisionedArchives = []
        List<WorkerEntitlementArchive> deProvisionedArchives = []
        Map<Worker, List<WorkerEntitlementArchive>> finalProvisionedArchives = [:]
        List<WorkerEntitlementArchive> workerEntitlementArchives = getFilteredEntitlementArchives(endReportDate)
        Map<String, List<WorkerEntitlementArchive>> archivesGroupedByEntitlementId = workerEntitlementArchives.groupBy { it.entitlementId }

        archivesGroupedByEntitlementId.each { String entitlementId, List<WorkerEntitlementArchive> archives ->
            Worker worker = null
            archives.each { WorkerEntitlementArchive archive ->
                worker = Worker.get(archive?.workerId)
                if (archivesGroupedByWorker.containsKey(worker)) {
                    archivesGroupedByWorker?.get(worker)?.add(archive)
                } else {
                    archivesGroupedByWorker.put(worker, [archive])
                }
            }
        }

        archivesGroupedByWorker.each { Worker groupedWorker, List<WorkerEntitlementArchive> archives ->
            provisionedArchives = archives.findAll { it.actionType.equals(CareConstants.ACCESS_REQUEST) }
            deProvisionedArchives = archives.findAll { it.actionType.equals(CareConstants.REVOKE_REQUEST) }
            provisionedArchives.each { WorkerEntitlementArchive archive ->
                List<WorkerEntitlementArchive> filteredArchives = deProvisionedArchives.findAll { it.entitlementId.equals(archive.entitlementId) }
                if ((!filteredArchives.any { it.actionDate >= archive.actionDate && it.actionDate <= startReportDate })) {
                    if (finalProvisionedArchives.containsKey(groupedWorker)) {
                        finalProvisionedArchives?.get(groupedWorker)?.add(archive)
                    } else {
                        finalProvisionedArchives.put(groupedWorker, [archive])
                    }
                }
            }
        }
        finalProvisionedArchives.each { Worker groupedWorker, List<WorkerEntitlementArchive> archives ->
            List<ArchivedEntitlementVO> archivedEntitlementVOs = getActiveCriticalEntitlements(archives)
            if (archivedEntitlementVOs) {
                finalEntries.put(groupedWorker, archivedEntitlementVOs)
            }
        }
        return finalEntries
    }

    public File createFileForADate(Map<Worker, List<ArchivedEntitlementVO>> finalEntries) {
        XSSFWorkbook workbook = new XSSFWorkbook()
        XSSFSheet sheet = workbook.createSheet("Sheet 1")
        Row row = sheet.createRow(0)
        Cell cell
        CreationHelper createHelper = workbook.getCreationHelper()
        CellStyle dateCellStyle = workbook.createCellStyle()
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy h:mm"))
        CellStyle fontStyle = workbook.createCellStyle()
        XSSFFont font = workbook.createFont()
        font.setColor(HSSFColor.BLUE.index)
        fontStyle.setFont(font)
        List<String> headers = ["Name", "Cyber Access to CCAs?", "Unescorted Physical Access?", "Access to PACs (CIP-006 R2.2)?", "Access to EACMs (CIP-005 R1.5)?", "Employee or Contractor", "Vendor or Contractor Company", "Access to shared accounts?", "Evidence Reference (Task ID)"]
        headers.eachWithIndex { String header, int cellNumber ->
            cell = row.createCell(cellNumber++)
            cell.setCellValue(header)
        }
        finalEntries.eachWithIndex { Worker worker, List<ArchivedEntitlementVO> archivedEntitlementVOs, int index ->
            row = sheet.createRow(++index)
            int cellNumber = 0
            cell = row.createCell(cellNumber++)
            cell.setCellValue(worker.toString())
            cell.setCellStyle(fontStyle);
            cell = row.createCell(cellNumber++)
            cell.setCellValue(archivedEntitlementVOs.any { it.hasCyberCCAAccess } ? 'yes' : 'no')
            cell = row.createCell(cellNumber++)
            cell.setCellValue(archivedEntitlementVOs.any { it.hasUnescortedPhysicalAccess } ? 'yes' : 'no')
            cell = row.createCell(cellNumber++)
            cell.setCellValue(archivedEntitlementVOs.any { it.hasPACAccess } ? 'yes' : 'no')
            cell = row.createCell(cellNumber++)
            cell.setCellValue(archivedEntitlementVOs.any { it.hasEACMAccess } ? 'yes' : 'no')
            cell = row.createCell(cellNumber++)
            cell.setCellValue((worker instanceof Employee) ? 'Employee' : 'Contractor')
            cell = row.createCell(cellNumber++)
            cell.setCellValue((worker instanceof Contractor) ? (worker?.primeVendor?.companyName ?: "No vendor") : 'N/A')
            cell = row.createCell(cellNumber++)
            cell.setCellValue(archivedEntitlementVOs.any { it.hasSharedAccountAccess } ? 'yes' : 'no')
            cell = row.createCell(cellNumber++)
            ArchivedEntitlementVO archivedEntitlementVO = archivedEntitlementVOs.find { it.workflowTaskClass && it.workflowTaskId }
            cell.setCellValue(archivedEntitlementVO?.workflowTaskId + " (${archivedEntitlementVO?.workflowTaskClass})")

        }
        File file = new File(System.getProperty('java.io.tmpdir'), "activeEntitlementsForADate.xlsx")
        file.delete()
        FileOutputStream out = new FileOutputStream(file)
        workbook.write(out)
        out.close()
        return file
    }

    public File createFileForADateRange(Map<Worker, List<ArchivedEntitlementVO>> finalEntries, Date startReportDate) {
        XSSFWorkbook workbook = new XSSFWorkbook()
        XSSFSheet sheet = workbook.createSheet("Sheet 1")
        Row row = sheet.createRow(0)
        Cell cell
        CreationHelper createHelper = workbook.getCreationHelper()
        CellStyle dateCellStyle = workbook.createCellStyle()
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy h:mm"))
        CellStyle fontStyle = workbook.createCellStyle()
        XSSFFont font = workbook.createFont()
        font.setColor(HSSFColor.BLUE.index)
        fontStyle.setFont(font)
        List<String> headers = ["Name", "Cyber Access to CCAs?", "Unescorted Physical Access?", "Access to PACs (CIP-006 R2.2)?", "Access to EACMs (CIP-005 R1.5)?", "Employee or Contractor", "Vendor or Contractor Company", "Access to shared accounts?", "Date access was granted (if after ${startReportDate.myFormat()})", "Evidence Reference (Task ID)"]
        headers.eachWithIndex { String header, int cellNumber ->
            cell = row.createCell(cellNumber++)
            cell.setCellValue(header)
        }
        finalEntries.eachWithIndex { Worker worker, List<ArchivedEntitlementVO> archivedEntitlementVOs, int index ->
            row = sheet.createRow(++index)
            int cellNumber = 0
            cell = row.createCell(cellNumber++)
            cell.setCellValue(worker.toString())
            cell.setCellStyle(fontStyle);
            cell = row.createCell(cellNumber++)
            cell.setCellValue(archivedEntitlementVOs.any { it.hasCyberCCAAccess } ? 'yes' : 'no')
            cell = row.createCell(cellNumber++)
            cell.setCellValue(archivedEntitlementVOs.any { it.hasUnescortedPhysicalAccess } ? 'yes' : 'no')
            cell = row.createCell(cellNumber++)
            cell.setCellValue(archivedEntitlementVOs.any { it.hasPACAccess } ? 'yes' : 'no')
            cell = row.createCell(cellNumber++)
            cell.setCellValue(archivedEntitlementVOs.any { it.hasEACMAccess } ? 'yes' : 'no')
            cell = row.createCell(cellNumber++)
            cell.setCellValue((worker instanceof Employee) ? 'Employee' : 'Contractor')
            cell = row.createCell(cellNumber++)
            cell.setCellValue((worker instanceof Contractor) ? (worker?.primeVendor?.companyName ?: "No vendor") : 'N/A')
            cell = row.createCell(cellNumber++)
            cell.setCellValue(archivedEntitlementVOs.any { it.hasSharedAccountAccess } ? 'yes' : 'no')
            cell = row.createCell(cellNumber++)
            ArchivedEntitlementVO archivedEntitlementVO = archivedEntitlementVOs.find { it.actionType.equals(CareConstants.ACCESS_REQUEST) && it.actionDate >= startReportDate }
            cell.setCellValue(archivedEntitlementVO ? archivedEntitlementVO.actionDate.myFormat() : "n/a")
            cell = row.createCell(cellNumber++)
            archivedEntitlementVO = archivedEntitlementVOs.find { it.workflowTaskClass && it.workflowTaskId }
            cell.setCellValue(archivedEntitlementVO?.workflowTaskId + " (${archivedEntitlementVO?.workflowTaskClass})")
        }
        File file = new File(System.getProperty('java.io.tmpdir'), "activeEntitlementsForADateRange.xlsx")
        file.delete()
        FileOutputStream out = new FileOutputStream(file)
        workbook.write(out)
        out.close()
        return file
    }

    public List<ArchivedEntitlementVO> getActiveCriticalEntitlements(List<WorkerEntitlementArchive> workerEntitlementArchives) {
        Map<Entitlement, List<OuterCircleEntitlementVO>> outerCircleEntitlementsWithTheirAttributes = [:]
        List<ArchivedEntitlementVO> activeCriticalEntitlements = []
        List<Entitlement> archivedEntitlements = Entitlement.getAll(workerEntitlementArchives*.entitlementId)

        archivedEntitlements = archivedEntitlements.unique { it.id }

        workerEntitlementArchives.each { WorkerEntitlementArchive archive ->
            //TODO: We can make this report independent of any other domain except WEA. For ex: we can let go of the entitlement domain object below.
            Entitlement entitlement = archivedEntitlements.find { it.id == archive?.entitlementId }
            Map<String, String> entitlementAttributes = new Gson().fromJson(archive?.entitlementAttributes as String, Map.class)
            if (entitlementAttributes.any { it.key in ['Cyber', 'Physical', 'CIP', 'CIP Outer', 'PAC', 'EACM', 'Shared Account'] }) {
                OuterCircleEntitlementVO outerCircleEntitlementVO = new OuterCircleEntitlementVO()
                outerCircleEntitlementVO.entitlementAttributes = entitlementAttributes
                outerCircleEntitlementVO.actionType = archive.actionType
                outerCircleEntitlementVO.actionDate = archive.actionDate
                outerCircleEntitlementVO.workflowTaskClass = archive.apsWorkflowTaskId ? ApsWorkflowTask.class.simpleName : (archive.centralWorkflowTaskId ? CentralWorkflowTask.class.simpleName : null)
                outerCircleEntitlementVO.workflowTaskId = archive.apsWorkflowTaskId ?: (archive.centralWorkflowTaskId ?: null)

                if (!outerCircleEntitlementsWithTheirAttributes.containsKey(entitlement)) {
                    outerCircleEntitlementsWithTheirAttributes.put(entitlement, [outerCircleEntitlementVO])
                } else {
                    List<OuterCircleEntitlementVO> outerCircleEntitlementVOList = outerCircleEntitlementsWithTheirAttributes.get(entitlement)
                    outerCircleEntitlementVOList.add(outerCircleEntitlementVO)
                    outerCircleEntitlementsWithTheirAttributes.put(entitlement, outerCircleEntitlementVOList)
                }
            }
        }
        outerCircleEntitlementsWithTheirAttributes.each { Entitlement entitlement, List<OuterCircleEntitlementVO> outerCircleEntitlementVOs ->
            outerCircleEntitlementVOs.each { OuterCircleEntitlementVO outerCircleEntitlementVO ->
                Boolean hasPhysicalAccess = false
                Boolean hasCriticalAccess = false
                Boolean hasPACAccess = false
                Boolean hasEACMAccess = false
                Boolean hasSharedAccountAccess = false

                Map<String, String> attributes = outerCircleEntitlementVO.entitlementAttributes
                if (attributes?.get('Physical')?.equalsIgnoreCase('true') && attributes?.get('CIP')?.equalsIgnoreCase('true') && attributes?.get('CIP Outer')?.equalsIgnoreCase('true')) {
                    hasPhysicalAccess = true
                }
                if (attributes?.get('Cyber')?.equalsIgnoreCase('true') && attributes?.get('CIP')?.equalsIgnoreCase('true') && attributes?.get('CIP Outer')?.equalsIgnoreCase('true')) {
                    hasCriticalAccess = true
                }

                if (attributes?.get('PAC')?.equalsIgnoreCase('true')) {
                    hasPACAccess = true
                }

                if (attributes?.get('EACM')?.equalsIgnoreCase('true')) {
                    hasEACMAccess = true
                }

                if (attributes?.get(CareConstants.SHARED_ACCOUNT_ATTRIBUTE)?.equalsIgnoreCase('true')) {
                    hasSharedAccountAccess = true
                }

                if (hasPhysicalAccess || hasCriticalAccess || hasPACAccess || hasEACMAccess || hasSharedAccountAccess) {
                    ArchivedEntitlementVO archivedEntitlementVO = new ArchivedEntitlementVO()
                    archivedEntitlementVO.actionType = outerCircleEntitlementVO.actionType
                    archivedEntitlementVO.actionDate = outerCircleEntitlementVO.actionDate
                    archivedEntitlementVO.workflowTaskClass = outerCircleEntitlementVO.workflowTaskClass
                    archivedEntitlementVO.workflowTaskId = outerCircleEntitlementVO.workflowTaskId
                    archivedEntitlementVO.hasUnescortedPhysicalAccess = hasPhysicalAccess
                    archivedEntitlementVO.hasCyberCCAAccess = hasCriticalAccess
                    archivedEntitlementVO.hasPACAccess = hasPACAccess
                    archivedEntitlementVO.hasEACMAccess = hasEACMAccess
                    archivedEntitlementVO.hasSharedAccountAccess = hasSharedAccountAccess
                    activeCriticalEntitlements.add(archivedEntitlementVO)
                }
            }
        }
        return activeCriticalEntitlements
    }

    public List<WorkerEntitlementArchive> getFilteredEntitlementArchives(Date endReportDate) {
        return WorkerEntitlementArchive.createCriteria().list {
            isNotNull('entitlementId')
            le('actionDate', endReportDate)
        }
    }
}

class EntitlementRoleToEntitlementsVO {
    String name
    List<NameVO> entitlementNames = []

    EntitlementRoleToEntitlementsVO(EntitlementRole entitlementRole) {
        name = entitlementRole.name
        entitlementRole.entitlements.each { Entitlement entitlement ->
            entitlementNames << new NameVO(entitlement.name)
        }
    }

    public String toString() {
        return "EntitlementRoleToEntitlementsVO{" +
                "name='" + name + '\'' +
                ", entitlementNames=" + entitlementNames +
                '}';
    }
}

class EntitlementToEntitlementRolesVO {
    String name
    List<NameVO> entitlementRoleNames = []

    EntitlementToEntitlementRolesVO(Entitlement entitlement) {
        name = entitlement.name
        EntitlementRole.list().each { EntitlementRole role ->
            if (role.allEntitlements.contains(entitlement)) {
                entitlementRoleNames << new NameVO(role.name)
            }
        }
    }

    public String toString() {
        return "EntitlementToEntitlementRolesVO{" +
                "name='" + name + '\'' +
                ", entitlementRoleNames=" + entitlementRoleNames +
                '}';
    }
}

class NameVO {
    String name

    NameVO(String name) {
        this.name = name
    }


    public String toString() {
        return name
    }
}

class WorkerArchiveEntitlementFocusVO {
    Long workerId
    String slid
    String firstName
    String lastName
    String supervisorSlid
    String supvSupvSlid
    String certificationNames
    String entitlementName
    String entitlementNames
    String entitlementAction
    Date actionDate
    Date archiveRecordDateCreated
    Date accessGrantedDate
    Date accessRevokedDate
    Date dateCreated
    String email
    String phone
    String notes
    String accessGrantedNotes
    String accessRevokedNotes
    String personStatus
    String orgUnitNum
    String orgUnitDesc
    String workerNumber
    String title
    String persAreaNum
    String department
    String persSubAreaNum
    String persSubAreaDesc
    String officePhoneNum
    String cellPhoneNum
    String pagerNum
    String supvFullName
    String supvSupvFullName
    String badgeNumber
    String businessUnitRequesterSlid
    String businessUnit
    String vendorName
}

class EntitlementAccessReportVO {
    String entitlementName
    String firstName
    String lastName
    String middleName
    String slid
    String badgeNumber
    String positionTitle

    EntitlementAccessReportVO(String entitlementName, String firstName, String lastName, String middleName, String slid, String badgeNumber, String positionTitle) {
        this.entitlementName = entitlementName
        this.firstName = firstName
        this.lastName = lastName
        this.middleName = middleName
        this.slid = slid
        this.badgeNumber = badgeNumber
        this.positionTitle = positionTitle
    }
}

class OuterCircleEntitlementVO {
    Map<String, String> entitlementAttributes
    String actionType
    Date actionDate
    String workflowTaskClass
    Long workflowTaskId
}

class ArchivedEntitlementVO {
    boolean hasCyberCCAAccess
    boolean hasUnescortedPhysicalAccess
    boolean hasPACAccess
    boolean hasEACMAccess
    boolean hasSharedAccountAccess
    String actionType
    Date actionDate
    String workflowTaskClass
    Long workflowTaskId
}
