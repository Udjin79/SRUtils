/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Listeners

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager

IssueManager issueManager = ComponentAccessor.getIssueManager()
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
UserManager userManager = ComponentAccessor.getUserManager()
IssueIndexingService issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)

def changeItem = event.changeLog.getRelated("ChildChangeItem")
MutableIssue issue = issueManager.getIssue(event.getIssue().id) as MutableIssue

// Set field names
String approverFieldName = "Approved By"
String approvedDateFieldName = "Approved Date"

CustomField approvedDateField = customFieldManager.getCustomFieldObject(approvedDateFieldName)
// Set service account, that will make changes in issues
ApplicationUser user = userManager.getUserByName('serviceAccount')

if (changeItem['field'].first() == approverFieldName) {
	Date currentDate = new Date()
	issue.setCustomFieldValue(approvedDateField, currentDate.toTimestamp())
	issueManager.updateIssue(user, issue, EventDispatchOption.DO_NOT_DISPATCH, false)
	issueIndexingService.reIndex(issue)
}