/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package ConsoleExamples

import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import org.evisaenkov.atlassian.library.CustomFieldsOperations
import org.evisaenkov.atlassian.library.IssueOperations
import org.evisaenkov.atlassian.library.UserOperations
import com.atlassian.jira.event.issue.IssueEvent

CustomFieldsOperations customFieldsOperations = new CustomFieldsOperations()
IssueOperations issueOperations = new IssueOperations()
UserOperations userOperations = new UserOperations()
IssueEvent event = event as IssueEvent

MutableIssue issue = issueOperations.getIssue(event.issue.key)
String cFieldValue = customFieldsOperations.getCustomFieldValue(issue, "Platform")

def change = event?.getChangeLog()?.getRelated("ChildChangeItem")?.find {
	it.field == "Platform"
}

if (change) {
	if (cFieldValue == "web") {
		String userNameWeb = "Web-Dev"
		ApplicationUser userWeb = userOperations.getUser(userNameWeb)
		issue.setReporter(userWeb)
		issueOperations.updateIssue(issue)
	}
	if (cFieldValue == "android") {
		String userNameAndroid = "Android-Dev"
		ApplicationUser userAndroid = userOperations.getUser(userNameAndroid)
		issue.setReporter(userAndroid)

	}
	issueOperations.updateIssue(issue)
}
