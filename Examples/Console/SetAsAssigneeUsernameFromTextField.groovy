/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager

MutableIssue issue = issue as MutableIssue

UserManager userManager = ComponentAccessor.getUserManager()
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
IssueManager issueManager = ComponentAccessor.getIssueManager()
IssueIndexingService issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)

// Set Customfield name or ID
CustomField cField = customFieldManager.getCustomFieldObject(12345)
String username = issue.getCustomFieldValue(cField)

ApplicationUser user = userManager.getUserByName(username)
ApplicationUser currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
// Set default user login
ApplicationUser defaultUser = userManager.getUserByName("defaultUser")

if (user) {
	issue.setAssignee(user)
} else {
	issue.setAssignee(defaultUser)
}

issueManager.updateIssue(currentUser, issue, EventDispatchOption.DO_NOT_DISPATCH, false)
issueIndexingService.reIndex(issue)
