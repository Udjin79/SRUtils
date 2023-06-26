/*
 * Created 2023.
 * @author ProForma
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.ProForma

import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.bc.issue.properties.IssuePropertyService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.ConstantsManager
import com.atlassian.jira.entity.property.EntityProperty
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueInputParameters
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.issuetype.IssueType
import com.atlassian.jira.issue.priority.Priority
import com.atlassian.jira.user.ApplicationUser
import groovy.json.JsonSlurper

////If used in a transition, comment next line
Issue issue = ComponentAccessor.getIssueManager().getIssueObject("ISSUE-1")
String formName = "Form Name"
String fieldName = "Field Name"
ApplicationUser loggedInUser = ComponentAccessor.jiraAuthenticationContext.getLoggedInUser()
IssuePropertyService issuePropertyService = ComponentAccessor.getComponentOfType(IssuePropertyService.class)
IssueService issueService = ComponentAccessor.issueService
ConstantsManager constantsManager = ComponentAccessor.constantsManager

Closure createSubtask = { Issue parentIssue, String summary, String priorityName, String subtaskType ->
	Collection<IssueType> subtaskIssueTypes = constantsManager.allIssueTypeObjects.findAll { it.subTask }
	IssueType subTaskIssueType = subtaskIssueTypes.findByName(subtaskType)
	Priority priority = constantsManager.priorities.findByName(priorityName)
	
	IssueInputParameters issueInputParameters = issueService.newIssueInputParameters().with {
		setProjectId(parentIssue.projectObject.id)
		setIssueTypeId(subTaskIssueType.id)
		setReporterId(loggedInUser.getUsername())
		setSummary(summary)
		setPriorityId(priority.id)
	}
	
	IssueService.CreateValidationResult validationResult = issueService.validateSubTaskCreate(loggedInUser, parentIssue.id, issueInputParameters)
	IssueService.IssueResult issueResult = issueService.create(loggedInUser, validationResult)
	MutableIssue subtask = issueResult.issue
	ComponentAccessor.subTaskManager.createSubTaskIssueLink(parentIssue, subtask, loggedInUser)
}

Closure<Long> getFormId = { def property, String name ->
	def slurper = new JsonSlurper()
	def parsedJson = slurper.parseText(property.value)
	String id
	parsedJson.forms.each() { form ->
		if (form.name.equals(name)) {
			id = form.id.toString()
			return
		}
	}
	if (id) {
		return Long.valueOf(id)
	}
}

Closure<String> getFieldId = { def property, String field ->
	def slurper = new JsonSlurper()
	def parsedJson = slurper.parseText(property.value)
	for (def question : parsedJson.design.questions) {
		if (field.equals(question.value.label)) {
			return question.key
		}
	}
}

Closure getCheckboxFieldValues = { def property, String field ->
	def slurper = new JsonSlurper()
	def parsedJson = slurper.parseText(property.value)
	for (def answer : parsedJson.state.answers) {
		String fieldId = getFieldId(property, field)
		if (fieldId.equals(answer.key)) {
			return answer.value.choices
		}
	}
}

Closure getCheckboxFieldInfo = { def property, String field ->
	def slurper = new JsonSlurper()
	def parsedJson = slurper.parseText(property.value)
	for (def question : parsedJson.design.questions) {
		if (field.equals(question.value.label)) {
			return question.value.choices
		}
	}
}

List<EntityProperty> allProperties = issuePropertyService.getProperties(loggedInUser, issue.id)
def formId
for (def property : allProperties) {
	if (property.key.equals("proforma.forms")) {
		formId = getFormId(property, formName)
	}
}

for (def property : allProperties) {
	if (property.key.equals("proforma.forms" + ".i" + formId) && property.value.contains("\"schemaVersion\":8")) {
		def choiceValues = getCheckboxFieldValues(property, fieldName)
		def fieldInfo = getCheckboxFieldInfo(property, fieldName)
		for (def info : fieldInfo) {
			if (info.label.equals("Create First Subtask") && choiceValues.contains(info.id)) {
				createSubtask(issue, "First Sub-task", "Medium", "Sub-task")
			} else if (info.label.equals("Create Second Subtask") && choiceValues.contains(info.id)) {
				createSubtask(issue, "Second Sub-task", "Medium", "Sub-task")
			}
		}
	}
}