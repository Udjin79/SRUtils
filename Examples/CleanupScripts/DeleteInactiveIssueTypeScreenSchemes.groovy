/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.CleanupScripts

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenSchemeManager

IssueTypeScreenSchemeManager issueTypeScreenSchemeManager = ComponentAccessor.getIssueTypeScreenSchemeManager()
String report = ""
Integer i = 0

Collection<IssueTypeScreenScheme> schemes = issueTypeScreenSchemeManager.getIssueTypeScreenSchemes()
schemes.each { IssueTypeScreenScheme scheme ->
	if (scheme.getName() != "Default Issue Type Screen Scheme" && issueTypeScreenSchemeManager.getProjects(scheme).size() == 0) {
		report += "${scheme.getName()}<br/>"
		issueTypeScreenSchemeManager.removeIssueTypeSchemeEntities(scheme)
		issueTypeScreenSchemeManager.removeIssueTypeScreenScheme(scheme)
		i++
	}
}

return "Total: ${i}<br/>" + report
