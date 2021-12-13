import com.force5solutions.care.workflow.ApsWorkflowTask
import com.force5solutions.care.workflow.ApsWorkflowTaskType

List<ApsWorkflowTask> tasks = ApsWorkflowTask.findAllByNodeName('Initial Task')

tasks.each { ApsWorkflowTask task ->
    task.type = ApsWorkflowTaskType.SYSTEM_APS
    task.s()
}