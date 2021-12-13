package com.force5solutions.care

import com.force5solutions.care.aps.ApsDataFile
import com.force5solutions.care.aps.EntitlementAttribute
import com.force5solutions.care.aps.Origin
import com.force5solutions.care.aps.RoleOwner
import com.force5solutions.care.cc.CentralDataFile
import com.force5solutions.care.cc.EntitlementPolicy
import com.force5solutions.care.cc.EntitlementRoleAccessStatus
import com.force5solutions.care.cc.WorkerCertificationArchive
import com.force5solutions.care.cc.WorkerEntitlementArchive
import com.force5solutions.care.feed.CategoryAreaReaderFileFeedService
import com.force5solutions.care.feed.CategoryWorkerFileFeedService
import com.force5solutions.care.feed.FeedRun
import com.force5solutions.care.feed.PicturePerfectEntitlementFeedService
import com.force5solutions.care.feed.TimEntitlementFeedService
import com.force5solutions.care.feed.TimEntitlementWorkerFileFeedService
import com.force5solutions.care.ldap.SecurityRole
import com.force5solutions.care.workflow.ApsWorkflowTask
import com.force5solutions.care.workflow.ApsWorkflowUtilService
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.force5solutions.care.feed.PicturePerfectEntitlementAccessFeedService
import com.force5solutions.care.workflow.WorkflowTask
import com.force5solutions.care.workflow.CentralWorkflowTask
import com.force5solutions.care.cc.AppUtil
import com.force5solutions.care.cc.WorkerEntitlementRole
import com.force5solutions.care.cc.Employee
import com.force5solutions.care.cc.Contractor
import com.force5solutions.care.cc.AnonymizeData
import com.force5solutions.care.workflow.WorkflowVO
import org.quartz.CronTrigger
import com.force5solutions.care.cc.JobDetailsDTO
import org.springframework.web.multipart.commons.CommonsMultipartFile
import com.force5solutions.care.workflow.ApsWorkflowTaskTemplate
import com.force5solutions.care.common.CareConstants
import com.force5solutions.care.aps.ApsPerson
import com.force5solutions.care.aps.Entitlement
import com.force5solutions.care.aps.EntitlementRole
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfCopyFields
import org.apache.commons.io.FileUtils
import com.force5solutions.care.common.DataFile
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import com.force5solutions.care.workflow.WorkflowTaskStatus
import com.force5solutions.care.workflow.ApsWorkflowType
import com.force5solutions.care.cc.PeriodUnit
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import com.force5solutions.care.cc.CcEntitlementRole
import com.force5solutions.care.feed.HrInfo
import groovy.time.TimeCategory
import com.force5solutions.care.workflow.ApsWorkflowTaskType
import com.force5solutions.care.cc.Person
import com.force5solutions.care.workflow.CentralWorkflowType
import java.util.zip.ZipException
import com.force5solutions.care.cc.Worker
import com.force5solutions.care.cc.WorkerCertification
import com.force5solutions.care.cc.Certification
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import net.sf.jasperreports.engine.design.JasperDesign
import net.sf.jasperreports.engine.xml.JRXmlLoader
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperExportManager
import net.sf.jasperreports.engine.JREmptyDataSource
import jxl.Sheet
import jxl.Workbook
import jxl.WorkbookSettings
import org.grails.plugins.versionable.VersioningContext
import com.force5solutions.care.common.EntitlementStatus
import com.google.gson.Gson

class UtilController {

    def careWebService
    def apsWorkflowTaskService
    def versioningService
    def entitlementService
    def escalationService
    def grailsApplication
    def jobManagerService
    def quartzScheduler
    def fixtureLoader
    def jasperService
    def entitlementRoleService
    def workerEntitlementArchiveService

    static config = ConfigurationHolder.config

    def timService

    public sendNotificationMail(String apsWorkflowTaskTemplateId) {
        log.info "Id: " + apsWorkflowTaskTemplateId
        ApsWorkflowTaskTemplate template = ApsWorkflowTaskTemplate.findById(apsWorkflowTaskTemplateId)
        log.info "template: " + template
    }

    def test = {
        sendNotificationMail(CareConstants.WORKFLOW_TASK_TEMPLATE_ACCESS_CONFIRM_BY_PROVISIONER)
        render "No code here"
    }

    def index = {}

    def triggerEntitlementFeed = {
        com.force5solutions.care.feed.PicturePerfectEntitlementFeedService service = new PicturePerfectEntitlementFeedService();
        FeedRun feedRun = service.execute()
        render "Feed Executed : <a href='${g.createLink(controller: 'feedRun', action: 'show', id: feedRun.id)}'>View Details</a> "
    }

    def feeds = {}

    def setUser = {
        if (params.id == 'NONE') {
            session.loggedUser = null
        } else {
            session.loggedUser = params.id
        }
        redirect(uri: '/')
    }

    def accessRequest = {
        apsWorkflowTaskService.processNewTasks()
    }

    def approveBootStrapTasks = {
        ApsWorkflowTask.list().each { ApsWorkflowTask task ->
            Map responseElements = ['accessJustification': 'Approved Bootstrap task by Central System', 'userAction': 'APPROVE']
            ApsWorkflowUtilService.sendResponseElements(task, responseElements)
        }
        render "Done"
    }

    def createEntitlementsFromTimRoles = {
        Origin origin = Origin.findByName(Origin.TIM_FEED)
        entitlementService.createTimEntitlements(timService.getRoles(), origin)
        redirect(controller: 'entitlement', action: 'list')
    }

    // TODO: Sample entities created for all environments esp. for QA environment to check the integration with TIM. Can be deleted later
    def createEntitiesForTim = {
        new RoleOwner(firstName: 'TIM', lastName: 'Owner', slid: 'timowner').s()
        new EntitlementPolicy(name: 'Cyber', isApproved: true).s()
        new Origin(name: Origin.TIM_FEED).s()
    }

    def triggerTimEntitlementFeed = {
        com.force5solutions.care.feed.TimEntitlementFeedService service = new TimEntitlementFeedService();
        boolean isPreviewMode = params.preview && params.preview.toString().toLowerCase().equals('true')
        FeedRun feedRun = service.executeFeed(isPreviewMode)
        render "Feed Executed : <a href='${g.createLink(controller: 'feedRun', action: 'show', id: feedRun.id)}'>View Details</a>"
    }

    def triggerEntitlementAccessFeed = {
        PicturePerfectEntitlementAccessFeedService service = new PicturePerfectEntitlementAccessFeedService();
        FeedRun feedRun = service.execute()
        render "Feed Executed : <a href='${g.createLink(controller: 'feedRun', action: 'show', id: feedRun.id)}'>View Details</a>"
    }

    def triggerCategoryAreaReaderFileFeed = {
        CategoryAreaReaderFileFeedService categoryAreaReaderFileFeedService = new CategoryAreaReaderFileFeedService()
        boolean isPreviewMode = params.preview && params.preview.toString().toLowerCase().equals('true')
        FeedRun feedRun = categoryAreaReaderFileFeedService.execute(isPreviewMode)
        render "Feed Executed : <a href='${g.createLink(controller: 'feedRun', action: 'show', id: feedRun.id)}'>View Details</a>"
    }

    def triggerCategoryWorkerFileFeed = {
        CategoryWorkerFileFeedService categoryWorkerFileFeedService = new CategoryWorkerFileFeedService()
        FeedRun feedRun = categoryWorkerFileFeedService.execute()
        render "Feed Executed : <a href='${g.createLink(controller: 'feedRun', action: 'show', id: feedRun.id)}'>View Details</a>"
    }


    def workflowReport = {
        String workflowGUID = params['id']
        List<WorkflowVO> workflowVOs = []

        if (workflowGUID == "latest") {
            List<WorkflowTask> tasks = ApsWorkflowTask.list()
            tasks.addAll(CentralWorkflowTask.list())
            WorkflowTask task = tasks.flatten().max { it.lastUpdated }
            workflowGUID = task.workflowGuid
        }

        List<ApsWorkflowTask> apsTasks = ApsWorkflowTask?.findAllByWorkflowGuid(workflowGUID)
        List<CentralWorkflowTask> centralTasks = CentralWorkflowTask?.findAllByWorkflowGuid(workflowGUID)
        apsTasks.each { ApsWorkflowTask apsWorkflowTask ->
            workflowVOs << new WorkflowVO(apsWorkflowTask, "APS")
        }
        centralTasks.each { CentralWorkflowTask centralWorkflowTask ->
            workflowVOs << new WorkflowVO(centralWorkflowTask, "Central")
        }
        workflowVOs = workflowVOs.sort { it.taskUpdated }
        render(view: 'workflowReport', model: [workflowGUID: workflowGUID, workflowType: workflowVOs.first().workflowType, workflowVOs: workflowVOs])
    }

    def workflowTaskDocuments = {
        Long taskId = params.long('taskId')
        String system = params['system']
        WorkflowTask task = (system == 'Central') ? CentralWorkflowTask.get(taskId) : ApsWorkflowTask.get(taskId)
        List documents = task?.documents as List
        render(template: 'documentPopUp', model: [documents: documents, system: system, message: task.message])
    }

    def downloadFile = {
        Class clazz = grailsApplication.getClassForName(params.className)
        Object object = clazz.get(params.id)
        byte[] fileContent = object[params.fieldName]
        String fileName = object['fileName']
        response.setContentLength(fileContent.size())
        response.setHeader("Content-disposition", "attachment; filename=" + fileName)
        response.setContentType(AppUtil.getMimeContentType(fileName.tokenize(".").last().toString()))
        OutputStream out = response.getOutputStream()
        out.write(fileContent)
        out.flush()
        out.close()
    }

    def chooseWorkerForWorkflowReport = {
        List<Worker> workers = Worker.list().sort { it.person.toString() }
        render view: 'chooseWorkerForWorkflowReport', model: [workers: workers]
    }

    def workflowReportBySlidOrId = {
        String workerSlidOrId = params.get('id')?.toString()
        Worker worker = Employee.findBySlid(workerSlidOrId?.toUpperCase()) ?: Employee.findByWorkerNumber(workerSlidOrId) ?: Contractor.findBySlid(workerSlidOrId?.toUpperCase()) ?: Contractor.findByWorkerNumber(workerSlidOrId)
        List<CentralWorkflowTask> centralWorkflowTasks = CentralWorkflowTask.findAllByWorkerEntitlementRoleIdIsNotNull()
        List<Long> selectedWorkeEntitlementRoleIds = []

        List<Long> workeEntitlementRoleIds = centralWorkflowTasks.unique { CentralWorkflowTask task -> task.workerEntitlementRoleId }*.workerEntitlementRoleId
        workeEntitlementRoleIds.each { Long workeEntitlementRoleId ->
            WorkerEntitlementRole workerEntitlementRole = WorkerEntitlementRole.get(workeEntitlementRoleId)
            if (workerEntitlementRole?.worker?.slid == workerSlidOrId || workerEntitlementRole?.worker?.id == worker?.id) {
                selectedWorkeEntitlementRoleIds << workeEntitlementRoleId
            }
        }

        List<CentralWorkflowTask> selectedCentralWorkflowTasks = CentralWorkflowTask.findAllByWorkerEntitlementRoleIdInList(selectedWorkeEntitlementRoleIds).unique { CentralWorkflowTask task -> task.workflowGuid }
        render(view: 'workflowReportBySlidOrId', model: [workerName: worker?.name, slid: workerSlidOrId, selectedCentralWorkflowTasks: selectedCentralWorkflowTasks])
    }

    def selectDateTimeForEscalation = {
        render(view: 'selectDateTimeForEscalation')
    }

    def triggerUptoDateTime = {
        String dateString = params.futureDate_value + " ${params.futureDate_hours}:${params.futureDate_minutes}:0"
        Date futureDateTime = Date.parse('MM/dd/yyyy HH:mm:ss', dateString)
        Date currentDateTime = new Date()
        Integer intervalInMinutes = 50

        while (currentDateTime < futureDateTime) {
            escalationService.escalateTasks(currentDateTime)
            apsWorkflowTaskService.autoConfirmAccessVerificationTasks(currentDateTime)
            apsWorkflowTaskService.autoConfirmGatekeeperResponseForAccessRevocationTasks(currentDateTime)
            currentDateTime.minutes = currentDateTime.minutes + intervalInMinutes
        }
        redirect(action: 'selectDateTimeForEscalation')
    }

    def kickOffJobs = {

        List<JobDetailsDTO> jobList = []

        jobManagerService.getAllJobs().each { currentJob ->
            String name = currentJob.name;
            if (name.contains("force5solutions")) {
                List tokenizeName = name.tokenize(".")
                name = tokenizeName.get(tokenizeName.size() - 1)
                String cronEx = "none"
                List triggers = quartzScheduler.getTriggersOfJob(currentJob.name, currentJob.group)
                if (triggers != null && triggers.size() > 0) {
                    cronEx = triggers.get(0).cronExpression
                }
                jobList.add(new JobDetailsDTO(name: name, triggerName: currentJob.triggerName, triggerGroup: currentJob.triggerGroup, status: currentJob.status, cronExpression: cronEx));
            }
        }
        [jobs: jobList]
    }

    def triggerJob = {
        CronTrigger trigger = quartzScheduler.getTrigger(params.triggerName, params.triggerGroup)
        quartzScheduler.triggerJob(trigger.jobName, trigger.jobGroup);
        redirect(controller: 'util', action: 'kickOffJobs')
    }

    def uploadFixture = {
        render(view: 'uploadFixture')
    }

    def executeFixture = {
        CommonsMultipartFile uploadedFile = params.fixtureFile
        File file = new File(AppUtil.getFixturesDirectoryPath() + System.getProperty("file.separator") + uploadedFile.originalFilename)
        file.bytes = uploadedFile.inputStream.bytes

        if (params.executeFixtureCheckBox) {
            fixtureLoader.load uploadedFile.originalFilename - ".groovy"
        }
        render "Fixture Uploaded and Executed"
    }

    def anonymizeData = {
        AnonymizeData.initializeAnonymization()
        ApsPerson.list().each { ApsPerson person ->
            // NOTE: Changing the slid results in custom validation error.
            //            person.slid = person.slid ? AnonymizeData.anonymize(person.slid, 'slid') : null
            person.firstName = person.firstName ? AnonymizeData.anonymize(person.firstName, 'firstName') : null
            person.lastName = person.lastName ? AnonymizeData.anonymize(person.lastName, 'lastName') : null
            person.phone = person.phone ? AnonymizeData.anonymize(person.phone, 'phone') : null
            person.email = person.email ? (AnonymizeData.anonymize(person.email, 'email') + "@fpl.com") : null
            person.s()
        }

        Entitlement.list().each { Entitlement entitlement ->
            entitlement.name = entitlement.name ? AnonymizeData.anonymize(entitlement.name, 'entitlementName') : null
            entitlement.alias = entitlement.alias ? AnonymizeData.anonymize(entitlement.alias, 'entitlementAlias') : null
            entitlement.s()
        }

        EntitlementRole.list().each { EntitlementRole entitlementRole ->
            entitlementRole.name = entitlementRole.name ? AnonymizeData.anonymize(entitlementRole.name, 'entitlementRole') : null
            entitlementRole.s()
        }
    }

    def revocationPackage = {
        List<ApsWorkflowTask> tasks = ApsWorkflowTask.findAllByWorkflowTypeInList([ApsWorkflowType.ROLE_REVOKE_REQUEST])
        List<String> pendingWorkflowGuids = []
        tasks.each { ApsWorkflowTask task ->
            if (task.status in [WorkflowTaskStatus.PENDING, WorkflowTaskStatus.NEW]) {
                pendingWorkflowGuids.add(task.workflowGuid)
            }
        }
        pendingWorkflowGuids.each { String s ->
            List<ApsWorkflowTask> tasksToBeRemoved = tasks.findAll { it.workflowGuid.equalsIgnoreCase(s) }.flatten()
            tasks.removeAll(tasksToBeRemoved)
        }
        tasks = tasks.unique { ApsWorkflowTask task -> task.workflowGuid }
        render(view: 'createRevocationEvidence', model: [tasks: tasks])
    }

    def createRevocationEvidencePackage = {
        params._name = "revocationEvidencePackage"
        params._file = "revocationEvidencePackage"
        ApsWorkflowTask task = ApsWorkflowTask.get(params.taskId)
        List<CentralWorkflowTask> centralWorkflowTasks = CentralWorkflowTask.findAllByWorkflowGuid(task?.workflowGuid)
        CentralWorkflowTask apsResponseTask = centralWorkflowTasks.sort { it.lastUpdated }.last()
        List<ApsWorkflowTask> apsWorkflowTasks = ApsWorkflowTask.findAllByWorkflowGuid(task?.workflowGuid)
        RevocationEvidencePackageVO evidencePackageVO = populateWorkerAndTaskDetails(centralWorkflowTasks, apsWorkflowTasks)
        evidencePackageVO.taskDetailsVOs.addAll(populateRevocationNotes(centralWorkflowTasks, apsWorkflowTasks).flatten())

        List<DataFile> filesToBeMergedInPdf = centralWorkflowTasks*.documents.toList().flatten()
        filesToBeMergedInPdf.addAll(apsWorkflowTasks*.documents.toList().flatten())

        String workerSlidOrNumber = task.worker.slid ?: task.worker.workerNumber
        String tempFolder = System.getProperty("java.io.tmpdir")
        File tempFile = new File(tempFolder, "temp-" + workerSlidOrNumber + ".pdf")
        String tempFileName = tempFile.absolutePath
        String apsResponseTaskDateInString = apsResponseTask.lastUpdated.myDateTimeFormat().toString().replaceAll('/', '_').replaceAll(' ', '_').replaceAll(':', '_')
        String finalEvidenceFileName = "Revocation__${task.worker.lastName}_${task.worker.firstName}__${workerSlidOrNumber}__${task.entitlementRole}__${apsResponseTaskDateInString}" + ".pdf"
        File tempEvidenceFile = new File(tempFolder, finalEvidenceFileName)
        String finalEvidenceFilePath = tempEvidenceFile.absolutePath
        List<DataFile> filesNotMergedInPdf = filesToBeMergedInPdf.findAll { !(['pdf', 'jpg', 'png', 'jpeg', 'gif'].contains(returnFileExtension(it.fileName))) }
        filesToBeMergedInPdf = filesToBeMergedInPdf.findAll { (['pdf', 'jpg', 'png', 'jpeg', 'gif'].contains(returnFileExtension(it.fileName))) }
        filesNotMergedInPdf.addAll(filesToBeMergedInPdf)
        filesNotMergedInPdf.flatten()

        Worker worker = apsResponseTask.worker
        boolean isRevocationPackage = true
        boolean isIntentionallyLeftBlankPage = true

        createJasperReport(evidencePackageVO, tempFileName, finalEvidenceFilePath, filesNotMergedInPdf, params)

        filesNotMergedInPdf.each { DataFile dataFile ->
            createAndMergeABlankHeaderAndFooterPage(tempFileName, finalEvidenceFilePath, worker, isRevocationPackage, isIntentionallyLeftBlankPage, dataFile)
            if ((['jpg', 'png', 'jpeg', 'gif'].contains(returnFileExtension(dataFile.fileName)))) {
                insertImage(tempFileName, dataFile, finalEvidenceFilePath)
            } else if ((['pdf'].contains(returnFileExtension(dataFile.fileName)))) {
                createFileInTemp(dataFile)
                mergePdfs(tempFileName, tempFolder + System.getProperty('file.separator') + dataFile.fileName, finalEvidenceFilePath)
                deleteTempFile(dataFile)
            } else {
                createAndMergeABlankHeaderAndFooterPage(tempFileName, finalEvidenceFilePath, worker, isRevocationPackage, !isIntentionallyLeftBlankPage, dataFile)
            }
        }

        new File(tempFileName).delete()
        String fileName
        if (filesNotMergedInPdf.size() > 0) {
            createZipFile(filesNotMergedInPdf, finalEvidenceFilePath)
            fileName = finalEvidenceFilePath.replace(".pdf", ".zip")
        } else {
            fileName = finalEvidenceFilePath
        }
        File file = new File(fileName)
        byte[] fileContent = file.bytes
        response.setContentLength(fileContent.size())
        response.setHeader("Content-disposition", "attachment; filename=" + fileName.substring(fileName.lastIndexOf(System.getProperty("file.separator")) + 1))
        response.setContentType(AppUtil.getMimeContentType(fileName.tokenize(".").last().toString()))
        OutputStream out = response.getOutputStream()
        out.write(fileContent)
        out.flush()
        out.close()
    }

    public List<TaskDetailsVO> populateRevocationNotes(List<WorkflowTask> centralWorkflowTasks, List<WorkflowTask> apsWorkflowTasks) {
        List<TaskDetailsVO> taskDetailsVOs = []
        CentralWorkflowTask centralWorkflowTask = centralWorkflowTasks.find { it.nodeName.equalsIgnoreCase('Pending Revocation by APS') } as CentralWorkflowTask
        int index = 0
        apsWorkflowTasks.each { ApsWorkflowTask apsWorkflowTask ->
            if (apsWorkflowTask.nodeName == 'Entitlement Revoke Request' && apsWorkflowTask.response != 'ESCALATED') {
                TaskDetailsVO taskDetailsVO = new TaskDetailsVO()
                if (centralWorkflowTask.periodUnit == PeriodUnit.DAYS) {
                    use(TimeCategory) {
                        taskDetailsVO.status =
                            apsWorkflowTask.lastUpdated <= (centralWorkflowTask.effectiveStartDate + 7.days) ? "De-provisioned/Compliant" : "De-provisioned"
                    }
                } else {
                    use(TimeCategory) {
                        taskDetailsVO.status =
                            apsWorkflowTask.lastUpdated <= (centralWorkflowTask.effectiveStartDate + 24.hours) ? "De-provisioned/Compliant" : "De-provisioned"
                    }
                }

                taskDetailsVO.entitlementHeading = "Entitlement " + (++index)
                taskDetailsVO.entitlementName = (apsWorkflowTask.entitlement.name ?: "N/A")
                taskDetailsVO.roleName = (centralWorkflowTask.entitlementRole.name ?: "N/A")
                taskDetailsVO.type = (apsWorkflowTask.entitlement.type ? EntitlementPolicy.get(apsWorkflowTask.entitlement.type) : "N/A")
                taskDetailsVO.createDateTime = apsWorkflowTask.dateCreated.myDateTimeFormat().toString()
                taskDetailsVO.completedDateTime = apsWorkflowTask.lastUpdated.myDateTimeFormat().toString()
                taskDetailsVO.provisionMethod = apsWorkflowTask.type.equals(ApsWorkflowTaskType.HUMAN) ? "Manual" : "System"
                taskDetailsVO.provisionerName = Person.findBySlid(apsWorkflowTask.actorSlid)?.firstMiddleLastName
                List<String> attachedFileNames = apsWorkflowTask.documents*.fileName
                AttachedFilesVO attachedFilesVO = new AttachedFilesVO()
                attachedFileNames.each {
                    attachedFilesVO.fileInfoVOs.add(new FileInfoVO(it))
                }
                taskDetailsVO.attachedFiles.add(attachedFilesVO)

                taskDetailsVO.notes = apsWorkflowTask.message
                taskDetailsVOs.add(taskDetailsVO)
            }
        }
        return taskDetailsVOs
    }

    public RevocationEvidencePackageVO populateWorkerAndTaskDetails(List<CentralWorkflowTask> centralWorkflowTasks,
                                                                    List<ApsWorkflowTask> apsWorkflowTasks) {
        CentralWorkflowTask centralWorkflowTask = centralWorkflowTasks.find { it.nodeName.equalsIgnoreCase('Pending Revocation by APS') }
        RevocationEvidencePackageVO revocationEvidencePackageVO = new RevocationEvidencePackageVO()
        revocationEvidencePackageVO.roleName = centralWorkflowTask.entitlementRole.name
        revocationEvidencePackageVO.workerName = centralWorkflowTask.worker
        revocationEvidencePackageVO.workerSlid = centralWorkflowTask.worker.slid ?: 'N/A'
        revocationEvidencePackageVO.badgeNumber = centralWorkflowTask.worker.badgeNumber
        revocationEvidencePackageVO.workerNumber = centralWorkflowTask.worker.workerNumber
        revocationEvidencePackageVO.supervisorName = centralWorkflowTask.worker.supervisor ?: 'N/A'
        revocationEvidencePackageVO.revokeType = ((centralWorkflowTask.periodUnit == PeriodUnit.DAYS) ? "7 Days Revoke Access" : "24 Hours Revoke Access")
        revocationEvidencePackageVO.effectiveDateTime = centralWorkflowTask.effectiveStartDate
        revocationEvidencePackageVO.completedDateTime = centralWorkflowTask.lastUpdated
        if (centralWorkflowTask.periodUnit == PeriodUnit.DAYS) {
            use(TimeCategory) {
                if (revocationEvidencePackageVO.completedDateTime <= (revocationEvidencePackageVO.effectiveDateTime + 7.days)) {
                    revocationEvidencePackageVO.complianceText = "COMPLIANT - CIP-004-2 R4.2 & CIP-004-3 R4.2"
                }
            }
        } else {
            use(TimeCategory) {
                if (revocationEvidencePackageVO.completedDateTime <= (revocationEvidencePackageVO.effectiveDateTime + 24.hours)) {
                    revocationEvidencePackageVO.complianceText = "COMPLIANT - CIP-004-2 R4.2 & CIP-004-3 R4.2"
                }
            }
        }
        // TODO make code work with contractors
        HrInfo hrInfo = HrInfo.findBySlid(revocationEvidencePackageVO.workerSlid)
        if (hrInfo) {
            revocationEvidencePackageVO.businessUnit = hrInfo.ORGUNIT_DESC
        }

        centralWorkflowTask = centralWorkflowTasks.find { it.nodeName.equalsIgnoreCase('Initial Task') }
        if (centralWorkflowTask) {
            revocationEvidencePackageVO.justification = centralWorkflowTask.message
            revocationEvidencePackageVO.requesterSlid = centralWorkflowTask.actorSlid ?: 'N/A'
            hrInfo = HrInfo.findBySlid(revocationEvidencePackageVO.requesterSlid)
            if (hrInfo) {
                revocationEvidencePackageVO.requesterName = hrInfo.FULL_NAME
            }
            revocationEvidencePackageVO.createDateTime = centralWorkflowTask.dateCreated
        }

        List<String> requesterAttachedFileNames = centralWorkflowTask.documents*.fileName
        AttachedFilesVO requesterAttachedFilesVO = new AttachedFilesVO()
        requesterAttachedFileNames.each {
            requesterAttachedFilesVO.fileInfoVOs.add(new FileInfoVO(it))
        }
        revocationEvidencePackageVO.requesterAttachedFiles.add(requesterAttachedFilesVO)

        revocationEvidencePackageVO.requesterNotes = centralWorkflowTask.message
        revocationEvidencePackageVO.reportDate = new Date().format('MMMM dd, yyyy')

        ApsWorkflowTask apsWorkflowTask = apsWorkflowTasks.find { it.nodeName.equalsIgnoreCase('Pending Revocation by Entitlement Role Gatekeeper') }
        if (apsWorkflowTask) {
            revocationEvidencePackageVO.approverSlid = apsWorkflowTask.actorSlid ?: 'N/A'
            hrInfo = HrInfo.findBySlid(revocationEvidencePackageVO.approverSlid)
            if (hrInfo) {
                revocationEvidencePackageVO.approverName = hrInfo.FULL_NAME
            }
            revocationEvidencePackageVO.approvalDateTime = apsWorkflowTask.lastUpdated
            revocationEvidencePackageVO.approvalResponse = apsWorkflowTask.response ?: 'N/A'
            revocationEvidencePackageVO.approvalMessage = apsWorkflowTask.message ?: 'N/A'
            List<String> approverAttachedFileNames = apsWorkflowTask.documents*.fileName
            AttachedFilesVO approverAttachedFilesVO = new AttachedFilesVO()
            approverAttachedFileNames.each {
                approverAttachedFilesVO.fileInfoVOs.add(new FileInfoVO(it))
            }
            revocationEvidencePackageVO.approverAttachedFiles.add(approverAttachedFilesVO)

            revocationEvidencePackageVO.approverCreateDateTime = apsWorkflowTask.dateCreated
        }

        return revocationEvidencePackageVO
    }

    String returnFileExtension(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1)
        return fileExtension
    }

    public void createZipFile(List<DataFile> files, String finalEvidenceFilePath) {
        File outputFile = new File(finalEvidenceFilePath.replace(".pdf", ".zip").replace(".xlsx", ".zip"))
        ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(outputFile))
        files.each { DataFile dataFile ->
            addToZipFile(zipFile, dataFile.fileName, dataFile.bytes)
        }
        // Adding final evidence PDF file to the zip file
        addToZipFile(zipFile, '00_' + finalEvidenceFilePath.substring(finalEvidenceFilePath.lastIndexOf(System.getProperty("file.separator")) + 1), new File(finalEvidenceFilePath).bytes)
        zipFile.close()
    }

    private void addToZipFile(ZipOutputStream zipFile, String fileName, byte[] data) {
        try {
            zipFile.putNextEntry(new ZipEntry(fileName))
            zipFile.write(data, 0, data.length);
            zipFile.closeEntry()
        } catch (ZipException zipException) {
            zipException.printStackTrace()
        }
    }

    public void insertImage(String fileName, DataFile dataFile, String finalEvidenceFileName) {
        File file = new File(fileName)
        ByteArrayOutputStream output = new ByteArrayOutputStream()
        PdfReader reader = new PdfReader(file.bytes)
        def stamper = new PdfStamper(reader, output)
        Rectangle rectangle = reader.getPageSize(1)
        Image image
        stamper.insertPage(1000, rectangle)
        PdfContentByte content = stamper.getOverContent(reader.getNumberOfPages())
        image = Image.getInstance(dataFile.bytes)
        image.setAbsolutePosition(50, 150)
        image.scaleAbsolute(300, 300)
        content.addImage(image)
        reader.close()
        stamper.close()
        file.bytes = output.toByteArray()
        createCopy(fileName, finalEvidenceFileName)
    }

    public void createFileInTemp(DataFile dataFile) {
        String fileName = getPathToTempFile(dataFile.fileName)
        OutputStream out = new FileOutputStream(fileName)
        out.write(dataFile.bytes)
        out.close()
    }

    public String getPathToTempFile(String fileName) {
        File tmpFile = new File(System.getProperty("java.io.tmpdir"), fileName)
        return tmpFile.absolutePath
    }

    public void deleteTempFile(DataFile dataFile) {
        String fileName = getPathToTempFile(dataFile.fileName)
        new File(fileName)?.delete()
    }

    public void createCopy(String source, String target) {
        FileUtils.copyFile(new File(source), new File(target))
    }

    public void mergePdfs(String parentPdf, String mergingPdf, String finalEvidencePdf) {
        PdfReader pdfReader1 = new PdfReader(parentPdf)
        PdfReader pdfReader2 = new PdfReader(mergingPdf)
        PdfCopyFields finalCopy = new PdfCopyFields(new FileOutputStream(finalEvidencePdf))
        finalCopy.open()
        [pdfReader1, pdfReader2].each { PdfReader pdfReader ->
            finalCopy.addDocument(pdfReader)
        }
        finalCopy.close()
        createCopy(finalEvidencePdf, parentPdf)
    }

    public void createAndMergeABlankHeaderAndFooterPage(String tempFileName, String finalEvidencePdf, Worker worker, boolean isRevocationPackage = true, boolean isIntentionallyLeftBlankPage = true, DataFile dataFile = null) {
        String heading = isRevocationPackage ? "REVOCATION EVIDENCE PACKAGE" : "BOOK END AUDIT"
        String jrxmlFile = isIntentionallyLeftBlankPage ? "blankWithBookEndHeaderAndFooter.jrxml" : "blankCheckZipFilePage.jrxml"
        String insertedPagePdf = isIntentionallyLeftBlankPage ? "blankHeaderAndFooterPage.pdf" : "blankCheckZipFilePage.pdf"
        File blankHeaderAndFooterPdf = new File(getPathToTempFile(insertedPagePdf))
        String pathToReportsFolder = "${SCH.servletContext.getRealPath("/reports")}"
        File headerAndFooterPageFile = new File(pathToReportsFolder + File.separator + jrxmlFile)
        InputStream input = new FileInputStream(headerAndFooterPageFile)
        JasperDesign design = JRXmlLoader.load(input)
        JasperReport report = JasperCompileManager.compileReport(design)
        Map parameters = new HashMap()
        parameters.put("workerName", worker.firstMiddleLastName)
        parameters.put("workerStatus", worker.status.toString())
        PdfReader reader = new PdfReader(new File(tempFileName).bytes)
        parameters.put("pageNumber", (reader.numberOfPages + 1).toString())
        parameters.put("reportDate", new Date().format('MMMM dd, yyyy'))
        parameters.put("fileName", dataFile.fileName)
        parameters.put("SUBREPORT_DIR", SCH.servletContext.getRealPath('/reports') + File.separator)
        parameters.put("heading", heading)
        JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource())
        OutputStream output = new FileOutputStream(blankHeaderAndFooterPdf)
        JasperExportManager.exportReportToPdfStream(print, output)
        mergePdfs(tempFileName, blankHeaderAndFooterPdf.path, finalEvidencePdf)
        createCopy(tempFileName, finalEvidencePdf)
    }

    public void createJasperReport(Object allWorkflowsVO, String evidenceFileName, String finalEvidenceFilePath, List<DataFile> filesNotMergedInPdf = null, Map params) {
        JasperReportDef reportDef = jasperService.buildReportDefinition(params, request.getLocale(), [data: [allWorkflowsVO]])
        File evidenceFile = new File(evidenceFileName)
        evidenceFile.bytes = reportDef.contentStream.toByteArray()
        createCopy(evidenceFileName, finalEvidenceFilePath)
    }

    def createBookEndAuditEvidencePackage = {
        params._name = "bookEndAudit"
        params._file = "bookEndAudit"

        Worker worker = Worker.get(params.workerId)
        List<WorkerEntitlementRole> workerEntitlementRoles = WorkerEntitlementRole.findAllByWorker(worker)
        List<CentralWorkflowType> workflowTypes = [CentralWorkflowType.EMPLOYEE_ACCESS_REQUEST, CentralWorkflowType.EMPLOYEE_ACCESS_REVOKE, CentralWorkflowType.CONTRACTOR_ACCESS_REQUEST, CentralWorkflowType.CONTRACTOR_ACCESS_REVOKE, CentralWorkflowType.ACCESS_GRANTED_BY_FEED, CentralWorkflowType.ACCESS_REVOKED_BY_FEED]
        List<CentralWorkflowTask> centralWorkflowTasks = CentralWorkflowTask.findAllByWorkerEntitlementRoleIdInListAndWorkflowTypeInList(workerEntitlementRoles*.id, workflowTypes)
        List<DataFile> filesToBeMergedInPdf = centralWorkflowTasks*.documents.toList().flatten()
        centralWorkflowTasks = centralWorkflowTasks.findAll { !it.hasAnyPendingTasks() }.unique { it.workflowGuid }
        centralWorkflowTasks.each {
            List<ApsWorkflowTask> apsWorkflowTasks = ApsWorkflowTask.findAllByWorkflowGuid(it.workflowGuid)
            filesToBeMergedInPdf.addAll(apsWorkflowTasks*.documents.toList().flatten())
        }

        AllWorkflowsVO allWorkflowsVO = populateDetailsOfAllWorkflows(centralWorkflowTasks, worker)

        String workerSlidOrNumber = worker.slid ?: worker.workerNumber
        String tempFolder = System.getProperty("java.io.tmpdir")
        File tempFile = new File(tempFolder, "temp-" + workerSlidOrNumber + ".pdf")
        String tempFileName = tempFile.absolutePath
        String finalEvidenceFileName = "Book_End_Audit__${worker.lastName}_${worker.firstName}__${workerSlidOrNumber}" + ".pdf"
        File tempEvidenceFile = new File(tempFolder, finalEvidenceFileName)
        String finalEvidenceFilePath = tempEvidenceFile.absolutePath
        List<DataFile> filesNotMergedInPdf = filesToBeMergedInPdf.findAll { !(['pdf', 'jpg', 'png', 'jpeg', 'gif'].contains(returnFileExtension(it.fileName))) }
        filesToBeMergedInPdf = filesToBeMergedInPdf.findAll { (['pdf', 'jpg', 'png', 'jpeg', 'gif'].contains(returnFileExtension(it.fileName))) }
        filesNotMergedInPdf.addAll(filesToBeMergedInPdf)
        filesNotMergedInPdf.flatten()

        boolean isRevocationPackage = false
        boolean isIntentionallyLeftBlankPage = true

        createJasperReport(allWorkflowsVO, tempFileName, finalEvidenceFilePath, filesNotMergedInPdf, params)

        filesNotMergedInPdf.each { DataFile dataFile ->
            createAndMergeABlankHeaderAndFooterPage(tempFileName, finalEvidenceFilePath, worker, isRevocationPackage, isIntentionallyLeftBlankPage, dataFile)
            if ((['jpg', 'png', 'jpeg', 'gif'].contains(returnFileExtension(dataFile.fileName)))) {
                insertImage(tempFileName, dataFile, finalEvidenceFilePath)
            } else if ((['pdf'].contains(returnFileExtension(dataFile.fileName)))) {
                createFileInTemp(dataFile)
                mergePdfs(tempFileName, tempFolder + System.getProperty('file.separator') + dataFile.fileName, finalEvidenceFilePath)
                deleteTempFile(dataFile)
            } else {
                createAndMergeABlankHeaderAndFooterPage(tempFileName, finalEvidenceFilePath, worker, isRevocationPackage, !isIntentionallyLeftBlankPage, dataFile)
            }
        }

        new File(tempFileName).delete()
        String fileName
        if (filesNotMergedInPdf.size() > 0) {
            createZipFile(filesNotMergedInPdf, finalEvidenceFilePath)
            fileName = finalEvidenceFilePath.replace(".pdf", ".zip")
        } else {
            fileName = finalEvidenceFilePath
        }
        File file = new File(fileName)
        byte[] fileContent = file.bytes
        response.setContentLength(fileContent.size())
        response.setHeader("Content-disposition", "attachment; filename=" + fileName.substring(fileName.lastIndexOf(System.getProperty("file.separator")) + 1))
        response.setContentType(AppUtil.getMimeContentType(fileName.tokenize(".").last().toString()))
        OutputStream out = response.getOutputStream()
        out.write(fileContent)
        out.flush()
        out.close()
    }

    public AllWorkflowsVO populateDetailsOfAllWorkflows(List<CentralWorkflowTask> centralWorkflowTasks, Worker worker) {
        AllWorkflowsVO allWorkflowsVO = new AllWorkflowsVO()
        allWorkflowsVO.workerName = worker.firstMiddleLastName
        allWorkflowsVO.workerStatus = worker.status.name
        allWorkflowsVO.lastStatusChange = worker.recentStatusChange.format('MM/dd/yy hh:mm a')
        allWorkflowsVO.workerSlid = worker.slid
        allWorkflowsVO.badgeNumber = worker.badgeNumber
        allWorkflowsVO.workerNumber = worker.workerNumber
        allWorkflowsVO.reportDate = new Date().format('MMMM dd, yyyy')
        HrInfo hrInfo = HrInfo.findBySlid(allWorkflowsVO.workerSlid)
        if (hrInfo) {
            allWorkflowsVO.businessUnit = hrInfo.ORGUNIT_DESC
        }

        centralWorkflowTasks = centralWorkflowTasks.sort { it.dateCreated }
        centralWorkflowTasks.each {
            if (it.workflowType in [CentralWorkflowType.EMPLOYEE_ACCESS_REQUEST, CentralWorkflowType.CONTRACTOR_ACCESS_REQUEST, CentralWorkflowType.ACCESS_GRANTED_BY_FEED]) {
                allWorkflowsVO.accessRequestAndRevokeWorkflows.add(populateAccessRequestWorkflowDetails(it))
            } else {
                allWorkflowsVO.accessRequestAndRevokeWorkflows.add(populateAccessRevokeWorkflowDetails(it))
            }
        }

//        allWorkflowsVO.accessRequestAndRevokeWorkflows = allWorkflowsVO.accessRequestAndRevokeWorkflows.sort {it.requesterCreateDateTime}
        return allWorkflowsVO
    }

    public EachWorkflowVO populateAccessRevokeWorkflowDetails(CentralWorkflowTask centralWorkflowTask) {
        EachWorkflowVO eachWorkflowVO = new EachWorkflowVO()

        CentralWorkflowTask requesterCentralWorkflowTask
        if (!centralWorkflowTask.workflowType.equals(CentralWorkflowType.ACCESS_REVOKED_BY_FEED)) {
            requesterCentralWorkflowTask = CentralWorkflowTask.findByWorkflowGuidAndNodeName(centralWorkflowTask.workflowGuid, "Initial Task")
            eachWorkflowVO.workflowName = requesterCentralWorkflowTask?.workflowType?.name
            eachWorkflowVO.requesterSlid = requesterCentralWorkflowTask.actorSlid
            Person requesterPerson = Person.findBySlid(requesterCentralWorkflowTask.actorSlid)
            eachWorkflowVO.requesterName = requesterPerson ? requesterPerson.firstMiddleLastName : "N/A"
            eachWorkflowVO.requesterMessage = requesterCentralWorkflowTask.message
            eachWorkflowVO.roleName = requesterCentralWorkflowTask.entitlementRole
            eachWorkflowVO.requesterCreateDateTime = requesterCentralWorkflowTask.dateCreated.myDateTimeFormat().toString()
            CentralWorkflowTask pendingRevocationTask = CentralWorkflowTask.findByWorkflowGuidAndNodeName(centralWorkflowTask.workflowGuid, "Pending Revocation by APS")
            eachWorkflowVO.effectiveDateTime = pendingRevocationTask.effectiveStartDate.myDateTimeFormat().toString()
            List<String> requesterAttachedFileNames = requesterCentralWorkflowTask.documents*.fileName
            AttachedFilesVO requesterAttachedFilesVO = new AttachedFilesVO()
            requesterAttachedFileNames.each {
                requesterAttachedFilesVO.fileInfoVOs.add(new FileInfoVO(it))
            }
            eachWorkflowVO.requesterAttachedFiles.add(requesterAttachedFilesVO)

            CentralWorkflowTask apsPendingWorkflowTask = CentralWorkflowTask.findByWorkflowGuidAndNodeName(centralWorkflowTask.workflowGuid, 'Pending Revocation by APS')
            eachWorkflowVO.revokeType = ((apsPendingWorkflowTask.periodUnit == PeriodUnit.DAYS) ? "7 Days Revoke Access" : "24 Hours Revoke Access")


            ApsWorkflowTask gatekeeperWorkflowTask = ApsWorkflowTask.findByWorkflowGuidAndNodeName(centralWorkflowTask.workflowGuid, "Pending Revocation by Entitlement Role Gatekeeper")
            if (gatekeeperWorkflowTask) {
                ApsPerson person = ApsPerson.findBySlid(gatekeeperWorkflowTask.actorSlid)
                eachWorkflowVO.approverName = person ? person.firstMiddleLastName : "N/A"
                eachWorkflowVO.approverSlid = gatekeeperWorkflowTask.actorSlid
                eachWorkflowVO.approvalMessage = gatekeeperWorkflowTask.message
                eachWorkflowVO.approvalResponse = gatekeeperWorkflowTask.response
                eachWorkflowVO.approvalDateTime = gatekeeperWorkflowTask.lastUpdated.myDateTimeFormat().toString()

                List<String> approverAttachedFileNames = gatekeeperWorkflowTask.documents*.fileName
                AttachedFilesVO approverAttachedFilesVO = new AttachedFilesVO()
                approverAttachedFileNames.each {
                    approverAttachedFilesVO.fileInfoVOs.add(new FileInfoVO(it))
                }
                eachWorkflowVO.approverAttachedFiles.add(approverAttachedFilesVO)
            } else {
                eachWorkflowVO.approverName = "N/A"
                eachWorkflowVO.approverSlid = "N/A"
                eachWorkflowVO.approvalMessage = "N/A"
                eachWorkflowVO.approvalResponse = "N/A"
            }

            eachWorkflowVO.taskDetailsVOs = populateTaskDetails(requesterCentralWorkflowTask)
        } else {
            eachWorkflowVO.workflowName = centralWorkflowTask?.workflowType?.name
            eachWorkflowVO.requesterSlid = centralWorkflowTask.actorSlid
            eachWorkflowVO.requesterName = centralWorkflowTask.actorSlid
            eachWorkflowVO.requesterMessage = centralWorkflowTask.message
            eachWorkflowVO.roleName = centralWorkflowTask.entitlementRole
            eachWorkflowVO.requesterCreateDateTime = centralWorkflowTask.dateCreated.myDateTimeFormat().toString()
            eachWorkflowVO.effectiveDateTime = centralWorkflowTask.dateCreated.myDateTimeFormat().toString()
            eachWorkflowVO.revokeType = "24 Hours Revoke Access"
        }
        eachWorkflowVO.isAccessRevokeWorkflow = true
        eachWorkflowVO.requestHeading = "Revoke Request"
        return eachWorkflowVO
    }

    public EachWorkflowVO populateAccessRequestWorkflowDetails(CentralWorkflowTask centralWorkflowTask) {
        EachWorkflowVO eachWorkflowVO = new EachWorkflowVO()
        CentralWorkflowTask requesterCentralWorkflowTask
        if (!centralWorkflowTask.workflowType.equals(CentralWorkflowType.ACCESS_GRANTED_BY_FEED)) {
            requesterCentralWorkflowTask = CentralWorkflowTask.findByWorkflowGuidAndNodeName(centralWorkflowTask.workflowGuid, "Initial Task")
            eachWorkflowVO.workflowName = requesterCentralWorkflowTask?.workflowType?.name
            eachWorkflowVO.requesterSlid = requesterCentralWorkflowTask.actorSlid
            Person requesterPerson = Person.findBySlid(requesterCentralWorkflowTask.actorSlid)
            eachWorkflowVO.requesterName = requesterPerson ? requesterPerson.firstMiddleLastName : "N/A"
            eachWorkflowVO.requesterMessage = requesterCentralWorkflowTask.message
            eachWorkflowVO.roleName = requesterCentralWorkflowTask.entitlementRole
            eachWorkflowVO.accessTypes = requesterCentralWorkflowTask.entitlementRole.types
            eachWorkflowVO.requesterCreateDateTime = requesterCentralWorkflowTask.dateCreated.myDateTimeFormat().toString()
            List<String> requesterAttachedFileNames = requesterCentralWorkflowTask.documents*.fileName
            AttachedFilesVO requesterAttachedFilesVO = new AttachedFilesVO()
            requesterAttachedFileNames.each {
                requesterAttachedFilesVO.fileInfoVOs.add(new FileInfoVO(it))
            }
            eachWorkflowVO.requesterAttachedFiles.add(requesterAttachedFilesVO)

//            CentralWorkflowTask supervisorCentralWorkflowTask = CentralWorkflowTask.findByWorkflowGuidAndNodeName(centralWorkflowTask.workflowGuid, "Get Supervisor Approval")
//            eachWorkflowVO.supervisorName = Person.findBySlid(supervisorCentralWorkflowTask.actorSlid).firstMiddleLastName
//            eachWorkflowVO.supervisorSlid = supervisorCentralWorkflowTask.actorSlid
//            eachWorkflowVO.supervisorMessage = supervisorCentralWorkflowTask.message
//            eachWorkflowVO.supervisorResponse = supervisorCentralWorkflowTask.response
//            eachWorkflowVO.supervisorApprovalDateTime = supervisorCentralWorkflowTask.lastUpdated.myDateTimeFormat().toString()
//
//            List<String> supervisorAttachedFileNames = supervisorCentralWorkflowTask.documents*.fileName
//            AttachedFilesVO supervisorAttachedFilesVO = new AttachedFilesVO()
//            supervisorAttachedFileNames.each {
//                supervisorAttachedFilesVO.fileInfoVOs.add(new FileInfoVO(it))
//            }
//            eachWorkflowVO.supervisorAttachedFiles.add(supervisorAttachedFilesVO)

            eachWorkflowVO.supervisorName = requesterCentralWorkflowTask.worker.getSupervisorName()
            eachWorkflowVO.supervisorSlid = requesterCentralWorkflowTask.worker.slid

            ApsWorkflowTask gatekeeperWorkflowTask = ApsWorkflowTask.findByWorkflowGuidAndNodeName(centralWorkflowTask.workflowGuid, "Pending Approval by Entitlement Role Gatekeeper")
            if (gatekeeperWorkflowTask) {
                ApsPerson person = ApsPerson.findBySlid(gatekeeperWorkflowTask.actorSlid)
                eachWorkflowVO.approverName = person ? person.firstMiddleLastName : "Not Found"
                eachWorkflowVO.approverSlid = gatekeeperWorkflowTask.actorSlid
                eachWorkflowVO.approvalMessage = gatekeeperWorkflowTask.message
                eachWorkflowVO.approvalResponse = gatekeeperWorkflowTask.response
                eachWorkflowVO.approvalDateTime = gatekeeperWorkflowTask.lastUpdated.myDateTimeFormat().toString()

                List<String> approverAttachedFileNames = gatekeeperWorkflowTask.documents*.fileName
                AttachedFilesVO approverAttachedFilesVO = new AttachedFilesVO()
                approverAttachedFileNames.each {
                    approverAttachedFilesVO.fileInfoVOs.add(new FileInfoVO(it))
                }
                eachWorkflowVO.approverAttachedFiles.add(approverAttachedFilesVO)
                CcEntitlementRole ccEntitlementRole = CcEntitlementRole.findByName(gatekeeperWorkflowTask.entitlementRole.name)
                eachWorkflowVO.workerCertificationVOs = ccEntitlementRole ? getWorkerCertificationInfoForEntitlementRole(ccEntitlementRole, gatekeeperWorkflowTask.worker, gatekeeperWorkflowTask.dateCreated) : []
            } else {
                eachWorkflowVO.approverName = "N/A"
                eachWorkflowVO.approverSlid = "N/A"
                eachWorkflowVO.approvalMessage = "N/A"
                eachWorkflowVO.approvalResponse = "N/A"
            }
            eachWorkflowVO.taskDetailsVOs = populateTaskDetails(requesterCentralWorkflowTask, gatekeeperWorkflowTask)
        } else {
            eachWorkflowVO.workerCertificationVOs = getWorkerCertificationInfoForEntitlementRole(centralWorkflowTask.entitlementRole, centralWorkflowTask.worker, centralWorkflowTask.dateCreated)
            eachWorkflowVO.workflowName = centralWorkflowTask?.workflowType?.name
            eachWorkflowVO.requesterSlid = centralWorkflowTask.actorSlid
            eachWorkflowVO.requesterName = centralWorkflowTask.actorSlid
            eachWorkflowVO.requesterMessage = centralWorkflowTask.message
            eachWorkflowVO.roleName = centralWorkflowTask.entitlementRole
            eachWorkflowVO.requesterCreateDateTime = centralWorkflowTask.dateCreated.myDateTimeFormat().toString()
            eachWorkflowVO.accessTypes = centralWorkflowTask.entitlementRole.types
            eachWorkflowVO.supervisorApprovalDateTime = "N/A"
            eachWorkflowVO.approvalDateTime = "N/A"
            eachWorkflowVO.taskDetailsVOs = populateTaskDetails(centralWorkflowTask)
        }
        eachWorkflowVO.isAccessRequestWorkflow = true
        eachWorkflowVO.requestHeading = "Access Request"
        return eachWorkflowVO
    }

    public List<TaskDetailsVO> populateTaskDetails(CentralWorkflowTask centralWorkflowTask, ApsWorkflowTask gatekeeperWorkflowTask = null) {
        List<TaskDetailsVO> taskDetailsVOs = []
        List<ApsWorkflowTask> apsWorkflowTasks = ApsWorkflowTask.findAllByWorkflowGuid(centralWorkflowTask.workflowGuid)
        int index = 0
        apsWorkflowTasks.each { ApsWorkflowTask apsWorkflowTask ->
            if ((apsWorkflowTask.nodeName == 'Pending Approval from Entitlement Provisioner' || apsWorkflowTask.nodeName == 'Entitlement Revoke Request') && apsWorkflowTask.response != 'ESCALATED') {
                TaskDetailsVO taskDetailsVO = new TaskDetailsVO()
                taskDetailsVO.entitlementHeading = "Entitlement " + (++index)
                taskDetailsVO.entitlementName = (apsWorkflowTask.entitlement.name ?: "N/A")
                taskDetailsVO.roleName = (centralWorkflowTask.entitlementRole.name ?: "N/A")
                taskDetailsVO.type = (apsWorkflowTask.entitlement.type ? EntitlementPolicy.get(apsWorkflowTask.entitlement.type) : "N/A")
                taskDetailsVO.createDateTime = apsWorkflowTask.dateCreated.myDateTimeFormat().toString()
                taskDetailsVO.completedDateTime = apsWorkflowTask.lastUpdated.myDateTimeFormat().toString()
                taskDetailsVO.provisionMethod = apsWorkflowTask.type.equals(ApsWorkflowTaskType.HUMAN) ? "Manual" : "System"
                taskDetailsVO.provisionerName = Person.findBySlid(apsWorkflowTask.actorSlid)?.firstMiddleLastName
                taskDetailsVO.status = apsWorkflowTask.status.toString()
                List<String> attachedFileNames = apsWorkflowTask.documents*.fileName
                AttachedFilesVO attachedFilesVO = new AttachedFilesVO()
                attachedFileNames.each {
                    attachedFilesVO.fileInfoVOs.add(new FileInfoVO(it))
                }
                taskDetailsVO.attachedFiles.add(attachedFilesVO)
                taskDetailsVO.notes = apsWorkflowTask.message
                taskDetailsVO.textForProvisionMethod = (apsWorkflowTask.nodeName == 'Pending Approval from Entitlement Provisioner') ? "Provision Method" : "Deprovision Method"
                taskDetailsVO.textForProvisionerName = (apsWorkflowTask.nodeName == 'Pending Approval from Entitlement Provisioner') ? "Provisioner Name" : "Deprovisioner Name"
                taskDetailsVO.entitlementCertificationVOs = gatekeeperWorkflowTask ? getWorkerCertificationInfoForEntitlement(apsWorkflowTask.entitlement, apsWorkflowTask.worker, gatekeeperWorkflowTask.dateCreated) : []
                taskDetailsVOs.add(taskDetailsVO)
            }
        }

        if (!gatekeeperWorkflowTask && !apsWorkflowTasks) {
            List<Entitlement> entitlements = EntitlementRole.findByName(centralWorkflowTask.entitlementRole.name).allEntitlements as List
            entitlements.each { Entitlement entitlement ->
                TaskDetailsVO taskDetailsVO = new TaskDetailsVO()
                taskDetailsVO.entitlementHeading = "Entitlement " + (++index)
                taskDetailsVO.entitlementName = (entitlement.name ?: "N/A")
                taskDetailsVO.roleName = (centralWorkflowTask.entitlementRole.name ?: "N/A")
                taskDetailsVO.type = (entitlement.type ? EntitlementPolicy.get(entitlement.type) : "N/A")
                taskDetailsVO.createDateTime = "N/A"
                taskDetailsVO.completedDateTime = "N/A"
                taskDetailsVO.provisionMethod = "N/A"
                taskDetailsVO.provisionerName = "N/A"
                taskDetailsVO.status = "N/A"
                taskDetailsVO.notes = "N/A"
                taskDetailsVO.textForProvisionMethod = "Provision Method"
                taskDetailsVO.textForProvisionerName = "Provisioner Name"
                taskDetailsVO.entitlementCertificationVOs = getWorkerCertificationInfoForEntitlement(entitlement, centralWorkflowTask.worker, centralWorkflowTask.dateCreated)
                taskDetailsVOs.add(taskDetailsVO)
            }
        }
        return taskDetailsVOs
    }

    List<WorkerCertificationVO> getWorkerCertificationInfoForEntitlement(Entitlement entitlement, Worker careWorker, Date date) {
        EntitlementPolicy entitlementPolicy = EntitlementPolicy.get(entitlement.type)
        String requiredCertificationsForEntitlementPolicyOnADate = careWebService.getRequiredCertificationIdsForEntitlementPolicyOnAGiveDate(entitlementPolicy, date, careWorker)
        List<Long> certificationIds = requiredCertificationsForEntitlementPolicyOnADate.tokenize(',')*.toLong()
        return getWorkerCertificationVOs(certificationIds, careWorker, date)
    }

    List<WorkerCertificationVO> getWorkerCertificationVOs(List<Long> certificationIds, Worker careWorker, Date date) {
        List<WorkerCertification> workerCertifications = WorkerCertification.createCriteria().list {
            if (certificationIds) {
                certification {
                    inList("id", certificationIds)
                }
            }
            eq('worker', careWorker)
            le("dateCompleted", date)
        }
        List<WorkerCertification> finalWorkerCertifications = []
        workerCertifications = workerCertifications.sort { it.dateCreated }.reverse()
        workerCertifications.each {
            if (!(it.certification.name in finalWorkerCertifications*.certification.name)) {
                finalWorkerCertifications.add(it)
            }
        }

        List<WorkerCertificationVO> workerCertificationVOs = []
        WorkerCertificationVO workerCertificationVO = new WorkerCertificationVO()
        List<WorkerCertificationNameAndDateVO> workerCertificationNameAndDateVOs = []
        certificationIds.each { Long certificationId ->
            WorkerCertificationNameAndDateVO workerCertificationNameAndDateVO = new WorkerCertificationNameAndDateVO()
            if (certificationId in finalWorkerCertifications*.certification.id) {
                WorkerCertification workerCertification = finalWorkerCertifications.find { it.certification.id.equals(certificationId) }
                workerCertificationNameAndDateVO.name = workerCertification.certification.name
                String complianceText = (workerCertification.fudgedExpiry > date) ? "COMPLIANT" : "                     "
                workerCertificationNameAndDateVO.formattedText = "${complianceText} - ${workerCertification.dateCompleted.myDateTimeFormat()} - ${workerCertification.fudgedExpiry.myDateTimeFormat()}"
            } else {
                workerCertificationNameAndDateVO.name = Certification.get(certificationId) ? Certification.get(certificationId).name : "Certification ID : ${certificationId}"
                workerCertificationNameAndDateVO.formattedText = "No Record Found"
            }
            workerCertificationNameAndDateVOs.add(workerCertificationNameAndDateVO)
        }
        workerCertificationNameAndDateVOs = workerCertificationNameAndDateVOs.sort { it.name }
        workerCertificationVO.workerCertificationNameAndDateVOs = workerCertificationNameAndDateVOs
        workerCertificationVOs.add(workerCertificationVO)
        return workerCertificationVOs
    }

    List<WorkerCertificationVO> getWorkerCertificationInfoForEntitlementRole(CcEntitlementRole entitlementRole, Worker careWorker, Date date) {
        List<Long> finalCertificationIds = entitlementRole.getInheritedCertificationsFromLocationsAndIncludedEntitlementRoles()*.id
        StringBuffer requiredCertificationsForEntitlementPolicyOnADate = new StringBuffer()
        entitlementRole.getInheritedEntitlementPolicies().each { EntitlementPolicy entitlementPolicy ->
            requiredCertificationsForEntitlementPolicyOnADate.append(careWebService.getRequiredCertificationIdsForEntitlementPolicyOnAGiveDate(entitlementPolicy, date, careWorker))
        }
        finalCertificationIds.addAll(requiredCertificationsForEntitlementPolicyOnADate.toString().tokenize(',')*.toLong())
        finalCertificationIds.flatten()
        return getWorkerCertificationVOs(finalCertificationIds, careWorker, date)
    }

    def createEntitlementRolesFromExcelSheet = {
        File excelFile = new File(System.getProperty('java.io.tmpdir'), 'APS_Roles.xls')
        WorkbookSettings workbookSettings = new WorkbookSettings();
        workbookSettings.setLocale(new Locale("en", "EN"));
        if (excelFile) {
            Workbook workbook = Workbook.getWorkbook(excelFile, workbookSettings);
            Sheet sheet = workbook?.sheets[0]
            (1..sheet.rows - 1)?.each { Integer index ->
                List<String> rowContents = sheet.getRow(index).toList()*.contents
                rowContents = rowContents*.trim()
                EntitlementRole entitlementRole = createEntitlementRole(rowContents)
                sleep(1000)
                if (entitlementRole) {
                    autoApproveRoleApsWorkflowTask(entitlementRole)
                }
            }
        } else {
            throw FileNotFoundException
        }
    }

    private EntitlementRole createEntitlementRole(List<String> rowContents) {
        EntitlementRole role = new EntitlementRole()
        List<String> nameEntry = rowContents.getAt(2).tokenize('-')
        nameEntry = nameEntry*.trim()
        switch (nameEntry.size()) {
            case 4:
                role.tags = nameEntry.get(0)
                role.name = nameEntry.get(1) + " - " + nameEntry.get(2) + " - " + nameEntry.get(3)
                break
            case 3:
                role.tags = nameEntry.get(0)
                role.name = nameEntry.get(1) + " - " + nameEntry.get(2)
                break
            case 2:
                role.name = nameEntry.get(0) + " - " + nameEntry.get(1)
                break
            default: break
        }
        def physicalName = 'Physical'
        def cyberName = 'Cyber'
        if (rowContents.get(0).equalsIgnoreCase("Yes")) {
            if (role.tags) {
                role.tags += ',CIP'
            } else {
                role.tags = 'CIP'
            }
            physicalName = 'Physical CIP'
            cyberName = 'Cyber CIP'

        }
        role.status = EntitlementStatus.ACTIVE
        role.isExposed = true
        role.owner = RoleOwner.get(new Random().nextInt(RoleOwner.count()) + 1)
        role.gatekeepers = [SecurityRole.get(new Random().nextInt(SecurityRole.count()) + 1)]
        if (rowContents.get(1).contains('Both')) {
            role.entitlements = Entitlement.getOnePhysicalAndOneCyberEntitlement(physicalName, cyberName)
        } else if (rowContents.get(1).contains('Physical')) {
            role.entitlements = [Entitlement.findByType(EntitlementPolicy.findByName(physicalName).id)]
        } else if (rowContents.get(1).contains('Cyber')) {
            role.entitlements = [Entitlement.findByType(EntitlementPolicy.findByName(cyberName).id)]
        }
        role.notes = rowContents.get(3)
        entitlementRoleService.save(role)
        return role
    }

    private void autoApproveRoleApsWorkflowTask(EntitlementRole role) {
        List<ApsWorkflowTask> apsWorkflowTasks = ApsWorkflowTask.findAllByEntitlementRoleIdAndNodeName(role?.id, "Get approval from Role Owner")
        if (apsWorkflowTasks) {
            VersioningContext.set(UUID.randomUUID().toString())
            Map responseElements = ['accessJustification': 'Approved by system during creation of roles from excel sheet', 'userAction': 'APPROVE']
            ApsWorkflowUtilService.sendResponseElements(apsWorkflowTasks.last(), responseElements)
        }
    }

    def assignRolesAndEntitlementsToBusinessRolesFromCsvFile = {
        File csvFile = new File(System.getProperty("java.io.tmpdir"), "APS_BUSINESS_ROLES.csv")
        EntitlementRole technologyAndManagementEntitlementRole = EntitlementRole.findByName("Technology & Management")
        EntitlementRole planningAndEngineeringEntitlementRole = EntitlementRole.findByName("Planning & Engineering")
        EntitlementRole officeAreasOnlyEntitlementRole = EntitlementRole.findByName("Office Areas Only")
        EntitlementRole systemOperationsEntitlementRole = EntitlementRole.findByName("System Operations")

        List<String> firstLineList = []
        csvFile.eachLine { String line, int index ->
            List<Entitlement> allEntitlementList = []
            List<String> tokenizedString = line.split("\\|")

            if (index == 1) {
                firstLineList = tokenizedString
                (7..21).each { Integer i ->
                    allEntitlementList.add(Entitlement.findByName(tokenizedString.getAt(i)))
                }
            } else {
                List<String> nameEntryList = tokenizedString.getAt(2).tokenize('-')
                nameEntryList = nameEntryList*.trim()
                String roleName = null
                switch (nameEntryList.size()) {
                    case 4:
                        roleName = nameEntryList.get(1) + " - " + nameEntryList.get(2) + " - " + nameEntryList.get(3)
                        break
                    case 3:
                        roleName = nameEntryList.get(1) + " - " + nameEntryList.get(2)
                        break
                    case 2:
                        roleName = nameEntryList.get(0) + " - " + nameEntryList.get(1)
                        break
                    default: break
                }

                List<Entitlement> currentLineEntitlementList = []
                (7..21).each { Integer i ->
                    if ((tokenizedString.size() > i) && tokenizedString.getAt(i).equalsIgnoreCase('x')) {
                        currentLineEntitlementList.add(Entitlement.findByName(firstLineList.getAt(i)))
                    }
                }
                currentLineEntitlementList = currentLineEntitlementList.sort { it.id }

                if (currentLineEntitlementList) {
                    EntitlementRole entitlementRole = EntitlementRole.findByName(roleName)
                    if (entitlementRole) {
                        entitlementRole.roles = []
                        entitlementRole.entitlements = []
                        if (technologyAndManagementEntitlementRole.entitlements.sort { it.id } == currentLineEntitlementList) {
                            entitlementRole.roles = [technologyAndManagementEntitlementRole]
                        } else if (planningAndEngineeringEntitlementRole.entitlements.sort { it.id } == currentLineEntitlementList) {
                            entitlementRole.roles = [planningAndEngineeringEntitlementRole]
                        } else if (officeAreasOnlyEntitlementRole.entitlements.sort { it.id } == currentLineEntitlementList) {
                            entitlementRole.roles = [officeAreasOnlyEntitlementRole]
                        } else if (systemOperationsEntitlementRole.entitlements.sort { it.id } == currentLineEntitlementList) {
                            entitlementRole.roles = [systemOperationsEntitlementRole]
                        } else {
                            entitlementRole.roles = []
                        }

                        if (!entitlementRole.roles) {
                            boolean goInALoop = true
                            [technologyAndManagementEntitlementRole, planningAndEngineeringEntitlementRole, officeAreasOnlyEntitlementRole, systemOperationsEntitlementRole].each { EntitlementRole physicalRole ->
                                if (goInALoop && currentLineEntitlementList.size() >= physicalRole.entitlements.size()) {
                                    if ((currentLineEntitlementList.intersect(physicalRole.entitlements)).size() == physicalRole.entitlements.size()) {
                                        entitlementRole.roles = [physicalRole]
                                    }
                                    List<Entitlement> standAloneEntitlements = currentLineEntitlementList.minus(physicalRole.entitlements)
                                    if (standAloneEntitlements) {
                                        entitlementRole.entitlements.addAll(standAloneEntitlements)
                                        entitlementRole.entitlements = entitlementRole.entitlements.flatten().unique()
                                    } else {
                                        entitlementRole.entitlements = []
                                        goInALoop = false
                                    }
                                }
                            }
                        }
                        entitlementRoleService.save(entitlementRole)
                        sleep(1000)
                        if (entitlementRole) {
                            autoApproveRoleApsWorkflowTask(entitlementRole)
                        }
                    }
                }
            }
        }
    }

    def createEntitlementsAndPhysicalRolesFromCsvFile = {
        File csvFile = new File(System.getProperty("java.io.tmpdir"), "APS_PHYSICAL_ROLES.csv")
        List<String> entitlementNames = []
        if (csvFile) {
            csvFile.eachLine { String line, int index ->
                if (index == 1) {
                    entitlementNames = line.tokenize("|")
                    createEntitlementsFromStringNames(entitlementNames)
                } else {
                    createEntitlementRoleAndAssignEntitlements(line, entitlementNames)
                }
            }
        }
    }

    List<WorkerEntitlementArchive> createEntitlementAndAssignToRolesAndCreateArchiveEntries(File csvFile, String attributeName, Boolean setLastPasswordChangeAttributeToCurrentDate = false) {
        List<SharedAccountEntitlementVO> sharedAccountEntitlementVOs = []
        Long entitlementPolicyType = EntitlementPolicy.findByName("Cyber CIP").id
        RoleOwner owner = RoleOwner.get(grailsApplication.config.sharedAccountEntitlementOwnerId.toLong())
        SecurityRole gatekeeper = SecurityRole.get(grailsApplication.config.sharedAccountEntitlementGatekeeperId.toLong())
        Origin origin = Origin.findByName("Manual")
        Map<Entitlement, List<EntitlementRole>> sharedAccountEntitlementAddedToRoles = [:]
        Map<Entitlement, List<WorkerNameActionDateVO>> archivesToBeCreated = [:]
        List<WorkerEntitlementArchive> workerEntitlementArchivesCreated = []

        if (csvFile) {
            csvFile.eachLine { String line ->
                sharedAccountEntitlementVOs.add(createSharedAccountEntitlementVO(line))
            }
            sharedAccountEntitlementAddedToRoles = createOrUpdateEntitlementsFromSharedAccountEntitlementVOAndAssignToRole(sharedAccountEntitlementVOs, entitlementPolicyType, owner, gatekeeper, origin, attributeName, setLastPasswordChangeAttributeToCurrentDate)
        }
        List<CcEntitlementRole> updatedRoles = CcEntitlementRole.findAllByIdInList(sharedAccountEntitlementAddedToRoles?.values()?.flatten()?.findAll { it }?.unique()*.id)
        List<WorkerEntitlementRole> workerEntitlementRoleList = WorkerEntitlementRole.findAllByStatusAndEntitlementRoleInList(EntitlementRoleAccessStatus.ACTIVE, updatedRoles)
        sharedAccountEntitlementAddedToRoles.each { Entitlement entitlement, List<EntitlementRole> entitlementRoles ->
            List<WorkerEntitlementRole> filteredWorkerEntitlementRoles = workerEntitlementRoleList?.findAll { it.entitlementRole.id in entitlementRoles*.id }
            filteredWorkerEntitlementRoles = filteredWorkerEntitlementRoles?.unique { it.worker }
            archivesToBeCreated.put(entitlement, (archivesToBeCreated.get(entitlement) ? (archivesToBeCreated.get(entitlement) << getWorkerNameActionDateVOs(filteredWorkerEntitlementRoles)).flatten() : getWorkerNameActionDateVOs(filteredWorkerEntitlementRoles)))
        }
        archivesToBeCreated.each { Entitlement entitlement, List<WorkerNameActionDateVO> workerNameActionDateVOs ->
            workerNameActionDateVOs.flatten().each { WorkerNameActionDateVO workerNameActionDateVO ->
                workerEntitlementArchivesCreated.add(createWorkerEntitlementArchiveEntry(entitlement, workerNameActionDateVO))
            }
        }
        return workerEntitlementArchivesCreated
    }

    def createOrUpdateSharedAccountsEntitlementsAndAssignmentToRoles = {
        quartzScheduler.pauseAll()
        File csvFile = new File(System.getProperty("java.io.tmpdir"), "SHARED_ACCOUNT_ENTITLEMENT.csv")
        List<WorkerEntitlementArchive> workerEntitlementArchivesCreated = createEntitlementAndAssignToRolesAndCreateArchiveEntries(csvFile, CareConstants.SHARED_ACCOUNT_ATTRIBUTE, true)
        render(template: 'sharedAccountEntitlementArchiveCreated', model: [workerEntitlementArchivesCreated: workerEntitlementArchivesCreated])
        quartzScheduler.resumeAll()
    }

    def createOrUpdateGenericAccountsEntitlementsAndAssignmentToRoles = {
        quartzScheduler.pauseAll()
        File csvFile = new File(System.getProperty("java.io.tmpdir"), "GENERIC_ACCOUNT_ENTITLEMENT.csv")
        List<WorkerEntitlementArchive> workerEntitlementArchivesCreated = createEntitlementAndAssignToRolesAndCreateArchiveEntries(csvFile, CareConstants.GENERIC_ACCOUNT_ATTRIBUTE, true)
        render(template: 'sharedAccountEntitlementArchiveCreated', model: [workerEntitlementArchivesCreated: workerEntitlementArchivesCreated])
        quartzScheduler.resumeAll()
    }

    def createOrUpdatePACEntitlementsAndAssignmentToRoles = {
        File csvFile = new File(System.getProperty("java.io.tmpdir"), "PAC_ENTITLEMENT.csv")
        String attributeName = 'PAC'
        List<WorkerEntitlementArchive> workerEntitlementArchivesCreated = createEntitlementAndAssignToRolesAndCreateArchiveEntries(csvFile, attributeName)
        render(template: 'sharedAccountEntitlementArchiveCreated', model: [workerEntitlementArchivesCreated: workerEntitlementArchivesCreated])
    }

    def createOrUpdateEACMEntitlementsAndAssignmentToRoles = {
        File csvFile = new File(System.getProperty("java.io.tmpdir"), "EACM_ENTITLEMENT.csv")
        String attributeName = 'EACM'
        List<WorkerEntitlementArchive> workerEntitlementArchivesCreated = createEntitlementAndAssignToRolesAndCreateArchiveEntries(csvFile, attributeName)
        render(template: 'sharedAccountEntitlementArchiveCreated', model: [workerEntitlementArchivesCreated: workerEntitlementArchivesCreated])
    }

    def loadSecurityRolesInEntitlements = {
        File csvFile = new File(System.getProperty('java.io.tmpdir'), 'LOAD_SECURITY_ROLES_IN_ENTITLEMENT.csv')
        String entitlementName = null
        String gatekeeperSecurityRole = null
        String provisionerSecurityRole = null
        String deProvisionerSecurityRole = null
        SecurityRole securityRole = null
        Entitlement entitlement = null
        csvFile.eachLine { String line ->
            List<String> tokenizedString = line.tokenize('|')*.trim()
            if (tokenizedString?.size()) {
                entitlementName = tokenizedString?.get(0)
                entitlement = Entitlement.findByName(entitlementName)
            }
            if (entitlement) {
                entitlement.gatekeepers.clear()
                entitlement.provisioners.clear()
                entitlement.deProvisioners.clear()
            }
        }
        csvFile.eachLine { String line ->
            List<String> tokenizedString = line.tokenize('|')*.trim()
            if (tokenizedString?.size()) {
                entitlementName = tokenizedString?.get(0)
            }
            if (tokenizedString?.size() > 1) {
                gatekeeperSecurityRole = tokenizedString?.get(1)
            }
            if (tokenizedString?.size() > 2) {
                provisionerSecurityRole = tokenizedString?.get(2)
            }
            if (tokenizedString?.size() > 3) {
                deProvisionerSecurityRole = tokenizedString?.get(3)
            }
            if (entitlementName) {
                entitlement = Entitlement.findByName(entitlementName)
                if (entitlement) {
                    if (gatekeeperSecurityRole) {
                        securityRole = SecurityRole.findByName(gatekeeperSecurityRole)
                        securityRole ? entitlement.gatekeepers.add(securityRole) : null
                        entitlement.gatekeepers.unique()
                    }
                    if (provisionerSecurityRole) {
                        securityRole = SecurityRole.findByName(provisionerSecurityRole)
                        securityRole ? entitlement.provisioners.add(securityRole) : null
                        entitlement.provisioners.unique()
                    }
                    if (deProvisionerSecurityRole) {
                        securityRole = SecurityRole.findByName(deProvisionerSecurityRole)
                        securityRole ? entitlement.deProvisioners.add(securityRole) : null
                        entitlement.deProvisioners.unique()
                    }
                    entitlement.s()
                }
            }
        }
        render "The security roles have been successfully added to the entitlements."
    }

    def loadSecurityRolesInEntitlementRoles = {
        File csvFile = new File(System.getProperty('java.io.tmpdir'), 'LOAD_SECURITY_ROLES_IN_ENTITLEMENT_ROLE.csv')
        String roleName = null
        String gatekeeperSecurityRole = null
        SecurityRole securityRole = null
        EntitlementRole entitlementRole = null
        csvFile.eachLine { String line ->
            List<String> tokenizedString = line.tokenize('|')*.trim()
            if (tokenizedString.size()) {
                roleName = tokenizedString?.get(0)
                entitlementRole = EntitlementRole.findByName(roleName)
            }
            if (entitlementRole) {
                entitlementRole.gatekeepers.clear()
            }
        }

        csvFile.eachLine { String line ->
            List<String> tokenizedString = line.tokenize('|')*.trim()
            if (tokenizedString.size()) {
                roleName = tokenizedString?.get(0)
            }
            if (tokenizedString.size() > 1) {
                gatekeeperSecurityRole = tokenizedString?.get(1)
            }
            if (roleName) {
                entitlementRole = EntitlementRole.findByName(roleName)
                if (entitlementRole) {
                    if (gatekeeperSecurityRole) {
                        securityRole = SecurityRole.findByName(gatekeeperSecurityRole)
                        securityRole ? entitlementRole.gatekeepers.add(securityRole) : null
                        entitlementRole.gatekeepers.unique()
                    }
                    entitlementRole.s()
                }
            }
        }
        render "The security roles have been successfully added to the entitlement roles."
    }

    public List<WorkerNameActionDateVO> getWorkerNameActionDateVOs(List<WorkerEntitlementRole> filteredWorkerEntitlementRoles) {
        List<WorkerNameActionDateVO> workerNameActionDateVOs = []
        filteredWorkerEntitlementRoles.each { WorkerEntitlementRole workerEntitlementRole ->
            WorkerNameActionDateVO workerNameActionDateVO = new WorkerNameActionDateVO()
            workerNameActionDateVO.worker = workerEntitlementRole.worker
            workerNameActionDateVO.actionDate = getAppropriateDateOfProvisioningOfEntitlementsToWorker(workerEntitlementRole)
            workerNameActionDateVOs.add(workerNameActionDateVO)
        }
        return workerNameActionDateVOs
    }

    public Date getAppropriateDateOfProvisioningOfEntitlementsToWorker(WorkerEntitlementRole workerEntitlementRole) {
        List<ApsWorkflowTask> apsWorkflowTasks = ApsWorkflowTask.createCriteria().list {
            eq('workerEntitlementRoleId', workerEntitlementRole.id)
            eq('status', WorkflowTaskStatus.COMPLETE)
            ilike('response', 'CONFIRM')
            inList('workflowType', [ApsWorkflowType.ROLE_ACCESS_REQUEST, ApsWorkflowType.ROLE_ACCESS_REQUEST_FOR_CONTRACTOR])
            eq('nodeName', 'Pending Approval from Entitlement Provisioner')
        }
        return apsWorkflowTasks ? apsWorkflowTasks.min { it.lastUpdated }.lastUpdated : workerEntitlementRole.dateCreated
    }

    public WorkerEntitlementArchive createWorkerEntitlementArchiveEntry(Entitlement entitlement, WorkerNameActionDateVO workerNameActionDateVO) {
        WorkerEntitlementArchive workerEntitlementArchive = new WorkerEntitlementArchive()
        workerEntitlementArchive.workerId = workerNameActionDateVO.worker.id
        workerEntitlementArchive.entitlementId = entitlement?.id
        workerEntitlementArchive.workerFirstName = workerNameActionDateVO.worker.firstName
        workerEntitlementArchive.workerMiddleName = workerNameActionDateVO.worker.middleName
        workerEntitlementArchive.workerLastName = workerNameActionDateVO.worker.lastName
        workerEntitlementArchive.workerSlid = workerNameActionDateVO.worker?.slid
        workerEntitlementArchive.entitlementName = entitlement.name
        workerEntitlementArchive.entitlementAlias = entitlement.alias
        workerEntitlementArchive.entitlementOrigin = entitlement?.origin?.name
        workerEntitlementArchive.entitlementPolicyType = EntitlementPolicy.get(entitlement?.type)?.name
        workerEntitlementArchive.notes = entitlement.notes
        workerEntitlementArchive.actionType = CareConstants.ACCESS_REQUEST
        workerEntitlementArchive.actionDate = workerNameActionDateVO.actionDate
        workerEntitlementArchive.entitlementAttributes = entitlement.jsonifyEntitlementAttributes()
        workerEntitlementArchive.s()
        return workerEntitlementArchive
    }

    SharedAccountEntitlementVO createSharedAccountEntitlementVO(String line) {
        SharedAccountEntitlementVO sharedAccountEntitlementVO = new SharedAccountEntitlementVO()
        List<String> tokenizedString = line?.tokenize('|')*.trim()
        sharedAccountEntitlementVO.entitlementName = tokenizedString.get(0) ?: null
        sharedAccountEntitlementVO.entitlementAlias = tokenizedString.get(1) ?: null
        sharedAccountEntitlementVO.entitlementNotes = tokenizedString.get(2) ?: null
        sharedAccountEntitlementVO.roleName = tokenizedString.get(3) ?: null
        return sharedAccountEntitlementVO
    }

    public Map<Entitlement, List<EntitlementRole>> createOrUpdateEntitlementsFromSharedAccountEntitlementVOAndAssignToRole(List<SharedAccountEntitlementVO> sharedAccountEntitlementVOs, Long entitlementPolicyType, RoleOwner owner, SecurityRole gatekeeper, Origin origin, String attributeName, Boolean setLastPasswordChangeAttributeToCurrentDate) {
        Map<Entitlement, List<EntitlementRole>> sharedAccountEntitlementAddedToRoles = [:]
        sharedAccountEntitlementVOs.each { SharedAccountEntitlementVO sharedAccountEntitlementVO ->
            EntitlementRole entitlementRole = EntitlementRole.findByName(sharedAccountEntitlementVO.roleName.trim())
            Entitlement entitlement = Entitlement.findByName(sharedAccountEntitlementVO.entitlementName.trim()) ? Entitlement.findByName(sharedAccountEntitlementVO.entitlementName.trim(), [lock: true]) : new Entitlement(isExposed: true, name: sharedAccountEntitlementVO.entitlementName)
            entitlement.alias = sharedAccountEntitlementVO.entitlementAlias
            entitlement.notes = sharedAccountEntitlementVO.entitlementNotes
            entitlement.origin = origin
            entitlement.owner = owner
            if (!entitlement.gatekeepers.contains(gatekeeper)) {
                entitlement.gatekeepers.add(gatekeeper)
            }
            entitlement.type = entitlementPolicyType
            entitlement.isApproved = true
            if (!entitlement.entitlementAttributes.any { it.keyName.equalsIgnoreCase(attributeName) }) {
                entitlement.entitlementAttributes.add(new EntitlementAttribute(keyName: attributeName, value: 'true'))
            }
            if (setLastPasswordChangeAttributeToCurrentDate) {
                EntitlementAttribute entitlementAttribute = entitlement.entitlementAttributes.find { it?.keyName?.equals(CareConstants.LAST_PASSWORD_CHANGE_ATTRIBUTE) } ?: new EntitlementAttribute(keyName: CareConstants.LAST_PASSWORD_CHANGE_ATTRIBUTE)
                entitlementAttribute.value = new Date().format('MM/dd/yyyy')
                entitlement.entitlementAttributes.add(entitlementAttribute)
            }
            entitlement.entitlementAttributes = entitlement.entitlementAttributes.findAll { it?.keyName && it?.value }
            entitlement.gatekeepers = entitlement.gatekeepers.unique()
            entitlement.s()
            if (entitlementRole && !entitlementRole?.entitlements?.contains(entitlement)) {
                entitlementRole.addToEntitlements(entitlement)
                entitlementRole.s()
            }
            sharedAccountEntitlementAddedToRoles.put(entitlement, (sharedAccountEntitlementAddedToRoles.get(entitlement) ? sharedAccountEntitlementAddedToRoles.get(entitlement) << entitlementRole : [entitlementRole]))
        }
        return sharedAccountEntitlementAddedToRoles
    }

    public void createEntitlementRoleAndAssignEntitlements(String line, List<String> entitlementNames) {
        List<String> tokenizedString = line.split("\\|")
        EntitlementRole role = new EntitlementRole()
        role.name = tokenizedString.get(0)
        role.owner = RoleOwner.count() ? RoleOwner.getAll().first() : null
        role.gatekeepers = SecurityRole.getAll()
        role.status = EntitlementStatus.ACTIVE
        List<Entitlement> entitlementList = []
        tokenizedString.eachWithIndex { String s, int index ->
            if (s.equalsIgnoreCase('x')) {
                entitlementList.add(Entitlement.findByName(entitlementNames.get(index)))
            }
        }
        role.entitlements = entitlementList
        //role.notes = "Entitlement Role with Entitlements - ${role.entitlements?.join(', ')}"
        role.tags = "Misc."
        entitlementRoleService.save(role)
        sleep(1000)
        if (role) {
            autoApproveRoleApsWorkflowTask(role)
        }
    }

    public void createEntitlementsFromStringNames(List<String> entitlementNames, boolean isCyber = false) {
        entitlementNames.eachWithIndex { String entitlementName, int index ->
            if (index != 0) {
                Entitlement entitlement = new Entitlement(isExposed: true)
                entitlement.name = entitlementName
                entitlement.alias = entitlementName
                entitlement.origin = Origin.findByName("Manual")
                entitlement.owner = RoleOwner.count() ? RoleOwner.getAll().first() : null
                entitlement.gatekeepers = SecurityRole.getAll()
                entitlement.provisioners = SecurityRole.getAll()
                entitlement.deProvisioners = SecurityRole.getAll()
                if (isCyber) {
                    entitlement.type = EntitlementPolicy.findByName("Cyber CIP").id
                } else {
                    if (entitlementName.contains('(CIP-AAM)')) {
                        entitlement.type = EntitlementPolicy.findByName("Physical CIP").id
                    } else {
                        entitlement.type = EntitlementPolicy.findByName("Physical").id
                    }
                }
                entitlementService.saveEntitlement(entitlement)
                sleep(1000)
                if (entitlement) {
                    autoApproveEntitlementApsWorkflowTask(entitlement)
                }
            }
        }
    }

    private void autoApproveEntitlementApsWorkflowTask(Entitlement entitlement) {
        List<ApsWorkflowTask> apsWorkflowTasks = ApsWorkflowTask.findAllByEntitlementIdAndNodeName(entitlement?.id, "Get approval from Entitlement Owner")
        if (apsWorkflowTasks) {
            VersioningContext.set(UUID.randomUUID().toString())
            Map responseElements = ['accessJustification': 'Approved by system during creation of entitlements from csv file', 'userAction': 'APPROVE']
            ApsWorkflowUtilService.sendResponseElements(apsWorkflowTasks.last(), responseElements)
        }
    }

    def createCyberEntitlementsAndAssignToBusinessRolesFromCsvFile = {
        File csvFile = new File(System.getProperty("java.io.tmpdir"), "APS_Roles_with_cyber.csv")
        List<String> entitlementNames = []
        if (csvFile) {
            csvFile.eachLine { String line, int index ->
                if (index == 1) {
                    entitlementNames = line.tokenize("|")
                    createEntitlementsFromStringNames(entitlementNames, true)
                } else {
                    findEntitlementRoleAndAssignEntitlements(line, entitlementNames)
                }
            }
        }
    }

    public void findEntitlementRoleAndAssignEntitlements(String line, List<String> entitlementNames) {
        EntitlementRole role = null
        List<String> tokenizedString = line.split("\\|")
        List<String> nameEntryList = tokenizedString.getAt(0).tokenize('-')
        nameEntryList = nameEntryList*.trim()
        String roleNumber = nameEntryList.last()

        List<EntitlementRole> entitlementRoleList = EntitlementRole.findAllByNameIlike("%${roleNumber}%")
        entitlementRoleList.each { EntitlementRole entitlementRole ->
            List<String> tokens = entitlementRole.name.tokenize('-')*.trim()
            if (tokens.last().toInteger() == roleNumber.toInteger()) {
                role = entitlementRole
            }
        }

        if (role) {
            List<Entitlement> entitlementList = []
            tokenizedString.eachWithIndex { String s, int index ->
                if (s.equalsIgnoreCase('x')) {
                    entitlementList.add(Entitlement.findByName(entitlementNames.get(index)))
                }
            }
            if (entitlementList) {
                role.entitlements.addAll(entitlementList)
                entitlementRoleService.save(role)
                sleep(1000)
                autoApproveRoleApsWorkflowTask(role)
            }
        }
    }

    def postReqToTim = {
        String timResponse
//        timResponse = timService.createPerson(new Employee(person: new Person(firstName: 'John', lastName: 'Doe', slid: "UID1")), ["Tim Role1", "Tim Role2"])
//        timResponse = timService.addRolesToPerson("UID1", ["Tim Role2"])
//        timResponse = timService.removeRolesFromPerson("UID1", ["Tim Role1"])
//        timResponse = timService.isProvisioningRequired("UID1", "Tim Role1")
//        timResponse = timService.isDeProvisioningRequired("UID1", "Tim Role2")
//        timResponse = timService.getPeople("", "UID1")
//        timResponse = timService.getRequestStatus(12345)
//        timResponse = timService.getRoles()
        timResponse = timService.getRoles("UID1")
//        timResponse = timService.suspendPerson("UID1")
        render "Response from Tim : ${timResponse}"
    }

    def createNewWorkerAuditReport = {
        Worker worker = params.workerId ? Worker.get(params.long('workerId')) : null
        if (worker) {
            List<WorkerEntitlementArchive> workerEntitlementArchiveList = WorkerEntitlementArchive.findAllByWorkerId(worker.id).sort { it.actionDate }
            List<DataFile> evidenceDocuments = []
            File file
            WorkerCertificationArchive workerCertificationArchive
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
            List<String> headers = ["ID", "Cyber CIP Access", "Physical CIP Access", "Active OC Entitlements", "Active Certifications", "Evidence Name/s", "First Name", "Last Name", "Notes", "Entitlement Origin", "Action Date", "Entitlement Alias", "Entitlement Name", "De-provisioner SLID", "Entitlement Policy Type", "Action Type", "Provisioner SLID", "User Response", "Worker SLID", "Entitlement Attributes"]
            headers.eachWithIndex { String header, int cellNumber ->
                cell = row.createCell(cellNumber++)
                cell.setCellValue(header)
            }
            List<WorkerCertificationArchive> sortedWorkerCertificationArchiveList = workerEntitlementArchiveList ? WorkerCertificationArchive.findAllByWorkerIdAndDateCreatedLessThanEquals(worker.id, workerEntitlementArchiveList.max { it?.actionDate }.actionDate).sort { it.dateCreated } : null
            workerEntitlementArchiveList.eachWithIndex { WorkerEntitlementArchive workerEntitlementArchive, int index ->
                List<DataFile> evidenceList = (workerEntitlementArchive?.apsWorkflowTaskId && workerEntitlementArchive?.evidenceIds) ? ApsDataFile.findAllByIdInList(workerEntitlementArchive?.evidenceIds?.tokenize(',')*.toLong()) : ((workerEntitlementArchive?.centralWorkflowTaskId && workerEntitlementArchive?.evidenceIds) ? CentralDataFile.findAllByIdInList(workerEntitlementArchive?.evidenceIds?.tokenize(',')*.toLong()) : null)
                if (evidenceList) {
                    evidenceDocuments?.addAll(evidenceList)
                    evidenceDocuments = evidenceDocuments?.unique { it?.fileName }
                }
                row = sheet.createRow(++index)
                int cellNumber = 0
                cell = row.createCell(cellNumber++)
                cell.setCellValue(workerEntitlementArchive.apsWorkflowTaskId ? (workerEntitlementArchive.apsWorkflowTaskId + " (APS Task)") : (workerEntitlementArchive.centralWorkflowTaskId ? (workerEntitlementArchive.centralWorkflowTaskId + " (Central Task)") : ""))
                cell.setCellStyle(fontStyle);
                cell = row.createCell(cellNumber++)
                WorkerAuditReportVO workerAuditReportVO = getWorkerAuditReportVO(workerEntitlementArchiveList, workerEntitlementArchive)
                Map<String, List<Boolean>> hasCyberCipOrPhysicalCipAccess = workerAuditReportVO.whetherCyberCipOrPhysicalCipAccess
                cell.setCellValue(hasCyberCipOrPhysicalCipAccess.get('hasCyberCipAccess').any { it } ? 'Yes' : 'No')
                cell = row.createCell(cellNumber++)
                cell.setCellValue(hasCyberCipOrPhysicalCipAccess.get('hasPhysicalCipAccess').any { it } ? 'Yes' : 'No')
                cell = row.createCell(cellNumber++)
                cell.setCellValue(workerAuditReportVO?.activeOuterCircleEntitlements ? workerAuditReportVO?.activeOuterCircleEntitlements?.join(',') : "")
                cell = row.createCell(cellNumber++)
                workerCertificationArchive = sortedWorkerCertificationArchiveList?.findAll { it.dateCreated <= workerEntitlementArchive.actionDate } ? sortedWorkerCertificationArchiveList.findAll { it.dateCreated <= workerEntitlementArchive.actionDate }.last() : null
                cell.setCellValue(workerCertificationArchive ? workerCertificationArchive.certificationNames : "")
                cell = row.createCell(cellNumber++)
                cell.setCellValue(evidenceDocuments*.fileName?.join(','))
                cell = row.createCell(cellNumber++)
                cell.setCellValue(workerEntitlementArchive.workerFirstName)
                cell = row.createCell(cellNumber++)
                cell.setCellValue(workerEntitlementArchive.workerLastName)
                cell = row.createCell(cellNumber++)
                cell.setCellValue(workerEntitlementArchive.notes)
                cell.setCellStyle(dateCellStyle)
                cell = row.createCell(cellNumber++)
                cell.setCellValue(workerEntitlementArchive.entitlementOrigin)
                cell = row.createCell(cellNumber++)
                cell.setCellValue((Date) workerEntitlementArchive.actionDate)
                cell.setCellStyle(dateCellStyle)
                cell = row.createCell(cellNumber++)
                cell.setCellValue(workerEntitlementArchive.entitlementAlias)
                cell = row.createCell(cellNumber++)
                cell.setCellValue(workerEntitlementArchive.entitlementName)
                cell = row.createCell(cellNumber++)
                cell.setCellValue(workerEntitlementArchive.entitlementDeProvisionerSlid)
                cell = row.createCell(cellNumber++)
                cell.setCellValue(workerEntitlementArchive.entitlementPolicyType)
                cell = row.createCell(cellNumber++)
                cell.setCellValue(workerEntitlementArchive.actionType)
                cell.setCellStyle(dateCellStyle)
                cell = row.createCell(cellNumber++)
                cell.setCellValue(workerEntitlementArchive.entitlementProvisionerSlid)
                cell = row.createCell(cellNumber++)
                cell.setCellValue(workerEntitlementArchive.userResponse)
                cell = row.createCell(cellNumber++)
                cell.setCellValue(workerEntitlementArchive.workerSlid)
                cell = row.createCell(cellNumber++)
                cell.setCellValue(workerEntitlementArchive.entitlementAttributes)
                cell = row.createCell(cellNumber++)
            }
            try {
                file = new File(System.getProperty('java.io.tmpdir'), "${worker.firstName + '_' + (worker.middleName ?: "") + '_' + worker.lastName}.xlsx")
                FileOutputStream out = new FileOutputStream(file);
                workbook.write(out);
                out.close();
                String fileName
                createZipFile(evidenceDocuments, file.absolutePath)
                fileName = file.absolutePath.replace(".xlsx", ".zip")
                File zipFile = new File(fileName)
                byte[] fileContent = zipFile.bytes
                response.setContentLength(fileContent.size())
                response.setHeader("Content-disposition", "attachment; filename=" + fileName.substring(fileName.lastIndexOf(System.getProperty("file.separator")) + 1))
                response.setContentType(AppUtil.getMimeContentType(fileName.tokenize(".").last().toString()))
                OutputStream outputStream = response.getOutputStream()
                outputStream.write(fileContent)
                outputStream.flush()
                outputStream.close()
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            render "Please enter a valid worker ID."
        }
    }

    public WorkerAuditReportVO getWorkerAuditReportVO(List<WorkerEntitlementArchive> workerEntitlementArchiveList, WorkerEntitlementArchive workerEntitlementArchive) {
        WorkerAuditReportVO workerAuditReportVO = new WorkerAuditReportVO()
        Map<String, List<Boolean>> whetherCyberCipOrPhysicalCipAccess = [:]
        workerEntitlementArchiveList = workerEntitlementArchiveList?.findAll { it.actionDate <= workerEntitlementArchive.actionDate }
        List<WorkerEntitlementArchive> filteredWorkerEntitlementArchives = []
        workerEntitlementArchiveList?.each { WorkerEntitlementArchive archive ->
            Map<String, String> attributesMap = new Gson().fromJson(archive?.entitlementAttributes, Map.class)
            if (attributesMap.keySet().containsAll(['Physical', 'CIP', 'CIP Outer']) || attributesMap.keySet().containsAll(['Cyber', 'CIP', 'CIP Outer'])) {
                filteredWorkerEntitlementArchives.add(archive)
            }
        }

        Map<String, List<WorkerEntitlementArchive>> archivesGroupedByEntitlementId = filteredWorkerEntitlementArchives?.groupBy { it.entitlementId }
        archivesGroupedByEntitlementId.each { String entitlementId, List<WorkerEntitlementArchive> workerEntitlementArchives ->
            WorkerEntitlementArchive latestWorkerEntitlementArchiveBasedOnActionDate = workerEntitlementArchives?.sort { it.actionDate }?.last()
            if (latestWorkerEntitlementArchiveBasedOnActionDate.actionType.equals(CareConstants.ACCESS_REQUEST)) {
                workerAuditReportVO.addOuterCircleEntitlement(latestWorkerEntitlementArchiveBasedOnActionDate.entitlementName)
                Map<String, String> attributes = new Gson().fromJson(latestWorkerEntitlementArchiveBasedOnActionDate?.entitlementAttributes as String, Map.class)
                if (attributes?.get('Cyber')?.equalsIgnoreCase('true') && attributes?.get('CIP')?.equalsIgnoreCase('true') && attributes?.get('CIP Outer')?.equalsIgnoreCase('true')) {
                    List<Boolean> values = whetherCyberCipOrPhysicalCipAccess?.get('hasCyberCipAccess') ?: []
                    values?.add(true)
                    whetherCyberCipOrPhysicalCipAccess?.put('hasCyberCipAccess', values)
                }
                if (attributes?.get('Physical')?.equalsIgnoreCase('true') && attributes?.get('CIP')?.equalsIgnoreCase('true') && attributes?.get('CIP Outer')?.equalsIgnoreCase('true')) {
                    List<Boolean> values = whetherCyberCipOrPhysicalCipAccess?.get('hasPhysicalCipAccess') ?: []
                    values?.add(true)
                    whetherCyberCipOrPhysicalCipAccess?.put('hasPhysicalCipAccess', values)
                }
            }
        }
        workerAuditReportVO.whetherCyberCipOrPhysicalCipAccess = whetherCyberCipOrPhysicalCipAccess
        return workerAuditReportVO
    }

    def createCriticalAssetAuditReportForADate = {
        Date startReportDate = Date.parse('MM/dd/yyyy hh:mm', params.start ?: '11/01/2012 00:00')
        Date endReportDate = Date.parse('MM/dd/yyyy hh:mm', params.end ?: '11/01/2012 23:59')
        Map<Worker, List<ArchivedEntitlementVO>> finalEntries = createFinalEntries(startReportDate, endReportDate)
        File file = createFileForADate(finalEntries)
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
    }

    def createCriticalAssetAuditReportForADateRange = {
        Date startReportDate = Date.parse('MM/dd/yyyy hh:mm', params.start ?: '11/01/2012 00:00')
        Date endReportDate = Date.parse('MM/dd/yyyy hh:mm', params.end ?: '03/31/2013 23:59')
        Map<Worker, List<ArchivedEntitlementVO>> finalEntries = createFinalEntries(startReportDate, endReportDate)
        File file = createFileForADateRange(finalEntries, startReportDate)
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

    def triggerTimEntitlementWorkerFileFeedService = {
        FeedRun feedRun = new TimEntitlementWorkerFileFeedService().execute()
        render "Feed Executed : <a href='${g.createLink(controller: 'feedRun', action: 'show', id: feedRun.id)}'>View Details</a>"
    }

    def batchDumpingOfApsWorkflowTasks = {
        List<WorkflowTask> apsWorkflowTasks = fetchFilteredApsWorkflowTasks()
        List<WorkflowTask> centralWorkflowTasks = fetchFilteredCentralWorkflowTasks()
        List<ApsWorkflowTask> apsWorkflowTasksToBeProcessed = []
        List<CentralWorkflowTask> centralWorkflowTasksToBeProcessed = []
        apsWorkflowTasks.each { ApsWorkflowTask apsWorkflowTask ->
            WorkerEntitlementArchive workerEntitlementArchive
            try {
                workerEntitlementArchive = WorkerEntitlementArchive.createCriteria().get {
                    eq('workerId', apsWorkflowTask.worker.id)
                    eq('entitlementId', apsWorkflowTask.entitlementId)
                    if (apsWorkflowTask?.actionDate) {
                        eq('actionDate', apsWorkflowTask.actionDate)
                    } else {
                        isNull('actionDate')
                    }
                    if (apsWorkflowTask.isProvisionerTask()) {
                        eq('actionType', CareConstants.ACCESS_REQUEST)
                    } else if (apsWorkflowTask.isDeprovisionerTask()) {
                        eq('actionType', CareConstants.REVOKE_REQUEST)
                    }
                    if (apsWorkflowTask?.response) {
                        eq('userResponse', apsWorkflowTask.response)
                    } else {
                        isNull('userResponse')
                    }
                }
            } catch (Exception e) {
                List<WorkerEntitlementArchive> workerEntitlementArchiveList = WorkerEntitlementArchive?.createCriteria()?.list {
                    eq('workerId', apsWorkflowTask.worker.id)
                    eq('entitlementId', apsWorkflowTask.entitlementId)
                    if (apsWorkflowTask?.actionDate) {
                        eq('actionDate', apsWorkflowTask.actionDate)
                    } else {
                        isNull('actionDate')
                    }
                    if (apsWorkflowTask.isProvisionerTask()) {
                        eq('actionType', CareConstants.ACCESS_REQUEST)
                    } else if (apsWorkflowTask.isDeprovisionerTask()) {
                        eq('actionType', CareConstants.REVOKE_REQUEST)
                    }
                    if (apsWorkflowTask?.response) {
                        eq('userResponse', apsWorkflowTask.response)
                    } else {
                        isNull('userResponse')
                    }
                }
                workerEntitlementArchive = workerEntitlementArchiveList ? ((workerEntitlementArchiveList?.first()?.apsWorkflowTaskId || workerEntitlementArchiveList?.first()?.centralWorkflowTaskId) ? workerEntitlementArchiveList.last() : workerEntitlementArchiveList.first()) : null
            }

            if (!workerEntitlementArchive && apsWorkflowTask.entitlement) {
                apsWorkflowTasksToBeProcessed.add(apsWorkflowTask)
            } else if (workerEntitlementArchive) {
                workerEntitlementArchive.apsWorkflowTaskId = apsWorkflowTask.id
                workerEntitlementArchive.evidenceIds = apsWorkflowTask.documents*.id.join(',')
                if (workerEntitlementArchive?.entitlementAttributes?.length() <= 2 && apsWorkflowTask.entitlement) {
                    // Fill the matched worker entitlement archive records with the current entitlement attributes. We did not have versioning on attributes so this is the best way forward.
                    workerEntitlementArchive.entitlementAttributes = apsWorkflowTask.entitlement.jsonifyEntitlementAttributes()
                }
                workerEntitlementArchive.s()
            }
        }

        apsWorkflowTasksToBeProcessed.each {
            ApsWorkflowTask apsWorkflowTask ->
                WorkerEntitlementArchive workerEntitlementArchive = workerEntitlementArchiveService.createWorkerEntitlementArchiveEntry(apsWorkflowTask, [:])
                Entitlement entitlement = versioningService.getObjectOnDate(apsWorkflowTask.entitlement, apsWorkflowTask.actionDate ?: apsWorkflowTask.dateCreated) as Entitlement
                workerEntitlementArchive.entitlementPolicyType = EntitlementPolicy?.get(entitlement?.type)?.name
                workerEntitlementArchive.s()
        }

        centralWorkflowTasks.each { CentralWorkflowTask centralWorkflowTask ->
            WorkerEntitlementArchive workerEntitlementArchive
            List<Entitlement> entitlementsGrantedInThisCentralWorkflowTask = getEntitlementsFromWorkerEntitlementRole(centralWorkflowTask.workerEntitlementRole)
            entitlementsGrantedInThisCentralWorkflowTask.each { Entitlement entitlement ->
                try {
                    workerEntitlementArchive = WorkerEntitlementArchive.createCriteria().get {
                        eq('workerId', centralWorkflowTask.worker.id)
                        inList('entitlementId', entitlement.id)
                        if (centralWorkflowTask?.effectiveStartDate) {
                            eq('actionDate', centralWorkflowTask?.effectiveStartDate)
                        }
                        if (centralWorkflowTask.isAccessRequestedTask()) {
                            eq('actionType', CareConstants.ACCESS_REQUEST)
                        } else if (centralWorkflowTask.isAccessRevokedTask()) {
                            eq('actionType', CareConstants.REVOKE_REQUEST)
                        }
                    }
                } catch (Exception e) {
                    List<WorkerEntitlementArchive> workerEntitlementArchiveList = WorkerEntitlementArchive?.createCriteria()?.list {
                        eq('workerId', centralWorkflowTask.worker.id)
                        inList('entitlementId', getEntitlementsFromWorkerEntitlementRole(centralWorkflowTask.workerEntitlementRole))
                        if (centralWorkflowTask?.effectiveStartDate) {
                            eq('actionDate', centralWorkflowTask?.effectiveStartDate)
                        }
                        if (centralWorkflowTask.isAccessRequestedTask()) {
                            eq('actionType', CareConstants.ACCESS_REQUEST)
                        } else if (centralWorkflowTask.isAccessRevokedTask()) {
                            eq('actionType', CareConstants.REVOKE_REQUEST)
                        }
                    }
                    workerEntitlementArchive = workerEntitlementArchiveList ? ((workerEntitlementArchiveList?.first()?.apsWorkflowTaskId || workerEntitlementArchiveList?.first()?.centralWorkflowTaskId) ? workerEntitlementArchiveList.last() : workerEntitlementArchiveList.first()) : null
                }

                if (!workerEntitlementArchive) {
                    centralWorkflowTasksToBeProcessed.add(centralWorkflowTask)
                    workerEntitlementArchive = workerEntitlementArchiveService.createWorkerEntitlementArchiveEntry(centralWorkflowTask, [:], entitlement)
                    Entitlement versionedEntitlement = versioningService.getObjectOnDate(entitlement, centralWorkflowTask.effectiveStartDate) as Entitlement
                    workerEntitlementArchive.entitlementPolicyType = EntitlementPolicy?.get(versionedEntitlement?.type)?.name
                    workerEntitlementArchive.s()
                } else if (workerEntitlementArchive) {
                    workerEntitlementArchive.centralWorkflowTaskId = centralWorkflowTask.id
                    workerEntitlementArchive.evidenceIds = centralWorkflowTask.documents*.id.join(',')
                    if (workerEntitlementArchive?.entitlementAttributes?.length() <= 2) {
                        // Fill the matched worker entitlement archive records with the current entitlement attributes. We did not have versioning on attributes so this is the best way forward.
                        workerEntitlementArchive.entitlementAttributes = entitlement.jsonifyEntitlementAttributes()
                    }
                    workerEntitlementArchive.s()
                }
            }
        }
        render "Batch Dumping completed. Worker entitlement archive records created from APS tables: " + apsWorkflowTasksToBeProcessed.size()
        render "<br/><br/>"
        render "Batch Dumping completed. Worker entitlement archive records created from Central (access granted/revoked by feed) tables: " + centralWorkflowTasksToBeProcessed.size()
    }

    public List<Entitlement> getEntitlementsFromWorkerEntitlementRole(WorkerEntitlementRole workerEntitlementRole) {
        return (EntitlementRole?.findById(workerEntitlementRole?.entitlementRole?.id)?.allEntitlements?.flatten()) as List
    }

    public List<ApsWorkflowTask> fetchFilteredApsWorkflowTasks() {
        List<String> nodeNameList = ['Pending Approval from Entitlement Provisioner', 'Entitlement Revoke Request', 'TIM Request', 'Provisioner Task', 'Deprovisioner Task']
        List<ApsWorkflowTask> filteredApsWorkflowTasks = ApsWorkflowTask.createCriteria().list {
            inList('nodeName', nodeNameList)
            eq('status', WorkflowTaskStatus.COMPLETE)
            eq('response', 'CONFIRM')
        }
        return filteredApsWorkflowTasks
    }

    public List<CentralWorkflowTask> fetchFilteredCentralWorkflowTasks() {
        List<String> nodeNameList = ['Access Granted By Feed', 'Access Revoked By Feed']
        List<CentralWorkflowTask> filteredCentralWorkflowTasks = CentralWorkflowTask.createCriteria().list {
            inList('nodeName', nodeNameList)
            eq('status', WorkflowTaskStatus.COMPLETE)
        }
        return filteredCentralWorkflowTasks
    }

    def completeDeprovisionerTasksWithNonExistentEntitlement = {
        List<ApsWorkflowTask> apsWorkflowTasksToBeAutoCompleted = []
        List<ApsWorkflowTask> apsWorkflowTasks = ApsWorkflowTask.findAllByNodeNameInListAndStatus(['Entitlement Revoke Request', 'Deprovisioner Task'], WorkflowTaskStatus.NEW)
        apsWorkflowTasks.each { ApsWorkflowTask apsWorkflowTask ->
            if (!apsWorkflowTask.entitlement) {
                apsWorkflowTasksToBeAutoCompleted.add(apsWorkflowTask)
            }
        }
        println "Tasks to be auto-completed: " + apsWorkflowTasksToBeAutoCompleted*.id
        apsWorkflowTasksToBeAutoCompleted.each { ApsWorkflowTask task ->
            VersioningContext.set(UUID.randomUUID().toString())
            Map responseElements = ['accessJustification': 'Approved by system during auto confirmation of bulk tasks', 'userAction': 'AUTO CONFIRM']
            ApsWorkflowUtilService.sendResponseElements(task, responseElements)
        }
    }
}

class WorkerNameActionDateVO {
    Worker worker
    Date actionDate
}

class WorkerAuditReportVO {
    Map<String, List<Boolean>> whetherCyberCipOrPhysicalCipAccess
    List<String> activeOuterCircleEntitlements = []

    List<String> addOuterCircleEntitlement(String entitlementName) {
        activeOuterCircleEntitlements.add(entitlementName)
        return activeOuterCircleEntitlements
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

class WorkerCertificationVO {
    List<WorkerCertificationNameAndDateVO> workerCertificationNameAndDateVOs
}

class WorkerCertificationNameAndDateVO {
    String name
    String formattedText
}

class AllWorkflowsVO {
    String workerName
    String workerSlid
    String badgeNumber
    String workerNumber
    String businessUnit
    String reportDate = "N/A"
    List<EachWorkflowVO> accessRequestAndRevokeWorkflows = []
    String workerStatus
    String lastStatusChange
}

class EachWorkflowVO {
    String workflowName
    String revokeType
    String accessTypes
    String effectiveDateTime
    String completedDateTime
    String requesterName = 'N/A'
    String requesterSlid = 'N/A'
    String requesterMessage = 'N/A'
    String requesterResponse = 'N/A'
    List<AttachedFilesVO> requesterAttachedFiles = []
    String supervisorName = 'N/A'
    String supervisorSlid = 'N/A'
    String supervisorMessage = 'N/A'
    String supervisorResponse = 'N/A'
    List<AttachedFilesVO> supervisorAttachedFiles = []
    List<WorkerCertificationVO> workerCertificationVOs = []
    String supervisorApprovalDateTime
    String roleName
    String approverName = 'N/A'
    String approverSlid = 'N/A'
    String approvalMessage = 'N/A'
    String approvalResponse = 'N/A'
    List<AttachedFilesVO> approverAttachedFiles = []
//    Date approverCreateDateTime
    List<TaskDetailsVO> taskDetailsVOs = []
    String requesterCreateDateTime
    String approvalDateTime
    boolean isAccessRequestWorkflow = false
    boolean isAccessRevokeWorkflow = false
    String requestHeading
}

class RevocationEvidencePackageVO {
    String workerName
    String workerSlid
    String badgeNumber
    String workerNumber
    String supervisorName
    String revokeType
    Date effectiveDateTime
    Date completedDateTime
    String businessUnit
    String requesterName = 'N/A'
    String requesterSlid
    Date createDateTime
    String approverName = 'N/A'
    String approverSlid = 'N/A'
    Date approvalDateTime
    String approvalMessage = 'N/A'
    String approvalResponse = 'N/A'
    String justification = 'N/A'
    String roleName
    List<AttachedFilesVO> requesterAttachedFiles = []
    String requesterNotes = "N/A"
    List<AttachedFilesVO> approverAttachedFiles = []
    Date approverCreateDateTime
    List<TaskDetailsVO> taskDetailsVOs = []
    String reportDate = "N/A"
    String complianceText = ""
}

class TaskDetailsVO {
    String entitlementHeading
    String entitlementName
    String roleName
    String type
    String status
    String createDateTime
    String completedDateTime
    String provisionMethod
    String provisionerName
    String textForProvisionMethod
    String textForProvisionerName
    List<AttachedFilesVO> attachedFiles = []
    String notes
    List<WorkerCertificationVO> entitlementCertificationVOs = []
}

class AttachedFilesVO {
    List<FileInfoVO> fileInfoVOs = []
}

class FileInfoVO {
    String fileName
    String link

    FileInfoVO() {}

    FileInfoVO(String fileNameAndLink) {
        fileName = fileNameAndLink
        link = fileNameAndLink
    }
}

class EntitlementRoleToEmployeesVO {
    String entitlementRole
    List<WorkerNameVO> employeeNames = []

    EntitlementRoleToEmployeesVO(CcEntitlementRole ccEntitlementRole, List<Employee> employees) {
        entitlementRole = ccEntitlementRole.name
        employees.each { Employee employee ->
            employeeNames << new WorkerNameVO("${employee.person.lastName} ${employee.person.firstName} ${employee.person.middleName ?: ''}")
        }
    }
}

class WorkerNameVO {
    String name

    WorkerNameVO(String name) {
        this.name = name
    }
}

class SharedAccountEntitlementVO {
    String entitlementName
    String entitlementAlias
    String entitlementNotes
    String roleName
}