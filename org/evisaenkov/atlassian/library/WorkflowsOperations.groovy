/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package org.evisaenkov.atlassian.library

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.status.Status
import com.atlassian.jira.workflow.JiraWorkflow

/**
 * Class for basic workflows operations with SR
 * @author Evgeniy Isaenkov
 */
class WorkflowsOperations {
	
	List<Status> getAllStatusesFor(MutableIssue issue) {
		ComponentAccessor.workflowManager.getWorkflow(issue).getLinkedStatusObjects()
	}
	
	Set<String> getAllStatusesIdsFor(MutableIssue issue) {
		return getWorkflow(issue).getLinkedStatusIds()
	}
	
	JiraWorkflow getWorkflow(MutableIssue issue) {
		ComponentAccessor.workflowManager.getWorkflow(issue)
	}
	
	List getLinkedActions(MutableIssue issue) {
		return ComponentAccessor.workflowManager.getWorkflow(issue).getLinkedStep(issue.status).getActions()
	}
}
