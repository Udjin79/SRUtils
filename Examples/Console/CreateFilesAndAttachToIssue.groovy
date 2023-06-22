/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

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
File file = new File(jiraHome.home, tmpDir + 'test.txt') //location JIRA_HOME/tmpDir/test.txt

try {
	file.write('qqqwwweee')
	
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
