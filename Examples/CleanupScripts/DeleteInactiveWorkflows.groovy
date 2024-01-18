/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.CleanupScripts

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.workflow.JiraWorkflow
import com.atlassian.jira.workflow.WorkflowManager
import com.atlassian.jira.workflow.WorkflowSchemeManager

// Get the WorkflowManager and WorkflowSchemeManager components
WorkflowManager workflowManager = ComponentAccessor.getWorkflowManager()
WorkflowSchemeManager workflowSchemeManager = ComponentAccessor.getWorkflowSchemeManager()

// Initialize a report string and a counter variable
String report = ""
Integer i = 0

// Get a collection of all Jira workflows
Collection<JiraWorkflow> workflows = workflowManager.getWorkflows()

// Remove active workflows from the list
workflows.removeAll(workflowManager.getActiveWorkflows())

// Iterate through each remaining workflow
workflows.each { JiraWorkflow workflow ->
	// Check if the workflow is not associated with any schemes
	if (workflowSchemeManager.getSchemesForWorkflow(workflow).size() == 0) {
		// Add the workflow name to the report and delete the workflow
		report += "${workflow.getName()}<br/>"
		workflowManager.deleteWorkflow(workflow)
		i++
	}
}

// Return the total count of deleted workflows and the report
return "Total: ${i}<br/>" + report
