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

Issue issueOrigin = ComponentAccessor.getIssueManager().getIssueObject("TEST-1")

Issue issueDestination = ComponentAccessor.getIssueManager().getIssueObject("TEST-2")
//// When using this script in a transition uncomment the next line:
//Issue issueDestination = issue

ApplicationUser loggedInUser = ComponentAccessor.jiraAuthenticationContext.getLoggedInUser()
IssuePropertyService issuePropertyService = ComponentAccessor.getComponentOfType(IssuePropertyService.class)

List<EntityProperty> allProperties = issuePropertyService.getProperties(loggedInUser, issueOrigin.id)
for (def property : allProperties) {
	if (property.key.contains("proforma.forms")) {
		EntityPropertyService.SetPropertyValidationResult result =
				issuePropertyService.validateSetProperty(
						loggedInUser,
						issueDestination.id,
						new EntityPropertyService.PropertyInput(property.value, property.key))
		if (result.isValid()) {
			issuePropertyService.setProperty(loggedInUser, result)
		}
	}
}