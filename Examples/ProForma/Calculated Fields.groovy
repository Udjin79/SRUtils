/*
 * Created 2023.
 * @author ProForma
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.ProForma

import com.atlassian.jira.bc.issue.properties.IssuePropertyService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.entity.property.EntityProperty
import com.atlassian.jira.entity.property.EntityPropertyService
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import groovy.json.JsonSlurper

////If used in a transition, comment next line:
Issue issue = ComponentAccessor.getIssueManager().getIssueObject("TEST-1")
String formName = "Form Name"
ArrayList<String> fieldNames = ["Field One", "Field Two"]
String fieldDestination = "Destination Field"
ApplicationUser loggedInUser = ComponentAccessor.jiraAuthenticationContext.getLoggedInUser()
IssuePropertyService issuePropertyService = ComponentAccessor.getComponentOfType(IssuePropertyService.class)

Closure getFormIds = { def property, String name ->
	def slurper = new JsonSlurper()
	def parsedJson = slurper.parseText(property.value)
	ArrayList ids = []
	parsedJson.forms.each() { form ->
		String id
		if (form.name.equals(name)) {
			id = form.id.toString()
		}
		if (id) {
			ids.add(Long.valueOf(id))
		}
	}
	return ids
}

Closure<String> getFieldId = { def property, String fieldName ->
	def slurper = new JsonSlurper()
	def parsedJson = slurper.parseText(property.value)
	for (def question : parsedJson.design.questions) {
		if (fieldName.equals(question.value.label)) {
			return question.key.toString()
		}
	}
}

Closure<String> getFieldValue = { def property, String fieldName ->
	def slurper = new JsonSlurper()
	def parsedJson = slurper.parseText(property.value)
	for (def answer : parsedJson.state.answers) {
		String fieldId = getFieldId(property, fieldName)
		if (fieldId.equals(answer.key)) {
			return answer.value.text.toString()
		}
	}
}

List<EntityProperty> allProperties = issuePropertyService.getProperties(loggedInUser, issue.id)
ArrayList forms = []
for (def property : allProperties) {
	if (property.key.equals("proforma.forms")) {
		forms = getFormIds(property, formName)
	}
}

for (def formId : forms) {
	Double result = 0
	for (def property : allProperties) {
		if (property.key.equals("proforma.forms" + ".i" + formId) && property.value.contains("\"schemaVersion\":8")) {
			for (def fieldName : fieldNames) {
				if (getFieldValue(property, fieldName)) {
					result = result + getFieldValue(property, fieldName).toDouble()
				}
			}
		}
	}
	
	for (def property : allProperties) {
		if (property.key.equals("proforma.forms" + ".i" + formId) && property.value.contains("\"schemaVersion\":8")) {
			String toReplace = "\"" + getFieldId(property, fieldDestination) + "\":{\"text\":\"" + getFieldValue(property, fieldDestination) + "\"}"
			String replacement = "\"" + getFieldId(property, fieldDestination) + "\":{\"text\":\"" + result.toString() + "\"}"
			if (!toReplace.contains("null")) {
				EntityPropertyService.SetPropertyValidationResult validationResult =
						issuePropertyService.validateSetProperty(
								loggedInUser,
								issue.id,
								new EntityPropertyService.PropertyInput(property.value.replace(toReplace, replacement), property.key))
				if (validationResult.isValid()) {
					issuePropertyService.setProperty(loggedInUser, validationResult)
				}
			} else {
				EntityPropertyService.SetPropertyValidationResult validationResult =
						issuePropertyService.validateSetProperty(
								loggedInUser,
								issue.id,
								new EntityPropertyService.PropertyInput(property.value.replace("\"answers\":{", "\"answers\":{" + replacement + ", "), property.key))
				if (validationResult.isValid()) {
					issuePropertyService.setProperty(loggedInUser, validationResult)
				}
			}
		}
	}
}

