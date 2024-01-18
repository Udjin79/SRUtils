/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.CleanupScripts

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.StatusManager
import com.atlassian.jira.issue.status.Status
import com.atlassian.jira.workflow.JiraWorkflow
import com.atlassian.jira.workflow.WorkflowManager

// Get the WorkflowManager and StatusManager components
WorkflowManager workflowManager = ComponentAccessor.getWorkflowManager()
StatusManager statusManager = ComponentAccessor.getComponent(StatusManager)

// Get all statuses in Jira
Collection<Status> allStatuses = statusManager.getStatuses()
Collection<JiraWorkflow> workflows = workflowManager.getWorkflows()
Set<Status> activeStatus = []
Integer i = 0
String report = ""

// Iterate through all workflows and collect their linked statuses
workflows.each { JiraWorkflow workflow ->
	activeStatus.addAll(workflow.getLinkedStatusObjects())
}

// Create a list of inactive statuses by finding the difference
Collection<Status> inactiveStatus = allStatuses - activeStatus

// Iterate over each inactive status and remove it
inactiveStatus.each { Status status ->
	statusManager.removeStatus(status.id)
	report += "${status.getName()}<br/>"
	i++
}

// Return the total count of removed statuses and the report
return "Total: ${i}<br/>" + report
