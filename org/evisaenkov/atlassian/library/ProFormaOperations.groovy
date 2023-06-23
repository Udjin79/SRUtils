/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package org.evisaenkov.atlassian.library

import com.atlassian.jira.bc.issue.properties.IssuePropertyService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.entity.property.EntityProperty
import com.atlassian.jira.issue.Issue
import groovy.json.JsonSlurper
import org.evisaenkov.atlassian.library.UserOperations

class ProFormaOperations {
	
	UserOperations userOperations = new UserOperations()
	
	Map getProformaAnswers(Issue issue, Integer formId = 1) {
		IssuePropertyService issuePropertyService = ComponentAccessor.getComponentOfType(IssuePropertyService)
		List<EntityProperty> allProperties = issuePropertyService.getProperties(userOperations.getTechUser(), issue.id)
		Map parsedJson = [:]
		for (def property : allProperties) {
			if ((property.key == "proforma.forms.i${formId}") && property.value.contains('\"schemaVersion\":8')) {
				JsonSlurper jsonSlurper = new JsonSlurper()
				parsedJson = jsonSlurper.parseText(property.value) as Map
			}
		}
		return parsedJson.state.answers
	}
}
