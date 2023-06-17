/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package ConsoleExamples

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.util.JiraHome
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.attachment.CreateAttachmentParamsBean
import com.atlassian.jira.user.ApplicationUser

IssueManager issueManager = ComponentAccessor.getIssueManager()
ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
Issue issue = issueManager.getIssueObject('TEST-1234')
String tmpDir = '/tmp/'

JiraHome jiraHome = ComponentAccessor.getComponent(JiraHome)
File file = new File(jiraHome.home, tmpDir + 'test.txt') << new URL('https://en.wikipedia.org/wiki/Main_Page').getText('UTF-8')

try {
	CreateAttachmentParamsBean bean = new CreateAttachmentParamsBean.Builder()
			.file(file)
			.filename(file.name)
			.contentType('text/plain')
			.author(user)
			.issue(issue)
			.build()
	ComponentAccessor.attachmentManager.createAttachment(bean)
} finally {
	file.delete()
}
