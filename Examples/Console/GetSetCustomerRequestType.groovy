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
import com.atlassian.jira.user.ApplicationUser

MutableIssue issue = ComponentAccessor.issueManager.getIssueByCurrentKey('TEST-1234')

IssueManager issueManager = ComponentAccessor.getIssueManager()
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

CustomField crtField = customFieldManager.getCustomFieldObject(10001)
//def existingRequestValue = issue.getCustomFieldValue(crtField)

def setRequestValue = crtField.getCustomFieldType().getSingularObjectFromString('test/66e08e40-1df0-125f-9942-62fbc379d312')

issue.setCustomFieldValue(crtField, setRequestValue)
issueManager.updateIssue(user, issue, EventDispatchOption.DO_NOT_DISPATCH, false)
