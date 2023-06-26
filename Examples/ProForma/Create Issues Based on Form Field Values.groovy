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
import com.atlassian.jira.issue.issuetype.IssueType
import com.atlassian.jira.issue.priority.Priority
import com.atlassian.jira.project.Project
import com.atlassian.jira.user.ApplicationUser
import groovy.json.JsonSlurper

////If used in a transition, comment next line:
Issue issue = ComponentAccessor.getIssueManager().getIssueObject("TEST-1")
String formName = "Form Name"
String fieldName = "Issue Creation Dropdown Field"
ApplicationUser loggedInUser = ComponentAccessor.jiraAuthenticationContext.getLoggedInUser()
IssuePropertyService issuePropertyService = ComponentAccessor.getComponentOfType(IssuePropertyService.class)
IssueService issueService = ComponentAccessor.issueService
ConstantsManager constantsManager = ComponentAccessor.constantsManager

Closure createIssue = { String projectKey, String summary, String priorityName, String issueTypeName ->
	Project project = ComponentAccessor.projectManager.getProjectObjByKey(projectKey)
	IssueType issueType = constantsManager.allIssueTypeObjects.findByName(issueTypeName)
	Priority priority = constantsManager.priorities.findByName(priorityName)
	
	IssueInputParameters issueInputParameters = issueService.newIssueInputParameters().with {
		setProjectId(project.id)
		setIssueTypeId(issueType.id)
		setReporterId(loggedInUser.getUsername())
		setSummary(summary)
		setPriorityId(priority.id)
	}
	
	IssueService.CreateValidationResult validationResult = issueService.validateCreate(loggedInUser, issueInputParameters)
	if (validationResult.isValid()) {
		IssueService.IssueResult issueResult = issueService.create(loggedInUser, validationResult)
	} else {
		log.error("Error while creating issue from ProForma Form: " + validationResult.errorCollection)
	}
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
	if (property.key.equals("proforma.forms" + ".i" + formId)) {
		def choiceValues = getCheckboxFieldValues(property, fieldName)
		def fieldInfo = getCheckboxFieldInfo(property, fieldName)
		for (def info : fieldInfo) {
			if (info.label.equals("Story") && choiceValues.contains(info.id)) {
				createIssue("PS", "Summary for Story", "Medium", "Change")
			} else if (info.label.equals("Problem") && choiceValues.contains(info.id)) {
				createIssue("PS", "Summary for Problem", "Medium", "Problem")
			} else if (info.label.equals("Task") && choiceValues.contains(info.id)) {
				createIssue("PS", "Summary for Task", "Medium", "Task")
			}
		}
	}
}