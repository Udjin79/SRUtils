/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.entity.property.EntityPropertyService.PropertyResult
import com.atlassian.jira.bc.issue.properties.IssuePropertyService

UserManager userManager = ComponentAccessor.getUserManager()
IssuePropertyService issuePropertyService = ComponentAccessor.getComponent(IssuePropertyService)

MutableIssue issue = ComponentAccessor.issueManager.getIssueObject("TEST-1")
ApplicationUser techUser = userManager.getUserByName("tester")

String propertyKey = "request.channel.type"
PropertyResult getPropertyResult = issuePropertyService.getProperty(techUser, issue.id, propertyKey)

if (getPropertyResult.isValid()) {
	log.warn(getPropertyResult.entityProperty)
} else {
	log.warn("Error getting property data: ${getPropertyResult.errorCollection}")
}
