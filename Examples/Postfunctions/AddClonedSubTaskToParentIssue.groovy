/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Postfunctions

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.ConstantsManager
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser

IssueManager issueManager = ComponentAccessor.getIssueManager()
ConstantsManager constantsManager = ComponentAccessor.getConstantsManager()

MutableIssue issue = issue as MutableIssue
MutableIssue parentIssue = issueManager.getIssueObject(issue.key)
ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

// Set exact name of sub task you're trying to create
String issueType = "Sub-Bug"

MutableIssue newIssue = ComponentAccessor.getIssueFactory().getIssue()
newIssue.setProjectObject(issue.getProjectObject())
newIssue.setIssueType(constantsManager.allIssueTypeObjects.findByName(issueType))
newIssue.setSummary("BUG: ${issue.summary}")
newIssue.setDescription(issue.description)
newIssue.setAssignee(issue.assignee)
newIssue.setReporter(user)
newIssue.setLabels(null)
newIssue.setComponent(null)
newIssue.setPriorityId('3')
newIssue.setDueDate(null)
ComponentAccessor.issueManager.createIssueObject(user, newIssue)

ComponentAccessor.subTaskManager.createSubTaskIssueLink(parentIssue, newIssue, user)
