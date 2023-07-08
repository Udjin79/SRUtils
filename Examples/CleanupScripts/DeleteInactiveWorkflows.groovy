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

WorkflowManager workflowManager = ComponentAccessor.getWorkflowManager()
WorkflowSchemeManager workflowSchemeManager = ComponentAccessor.getWorkflowSchemeManager()
String report = ""
Integer i = 0

Collection<JiraWorkflow> workflows = workflowManager.getWorkflows()
workflows.removeAll(workflowManager.getActiveWorkflows())

workflows.each { JiraWorkflow workflow ->
	if (workflowSchemeManager.getSchemesForWorkflow(workflow).size() == 0) {
		report += "${workflow.getName()}<br/>"
		workflowManager.deleteWorkflow(workflow)
		i++
	}
}

return "Total: ${i}<br/>" + report
