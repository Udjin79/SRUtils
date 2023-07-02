/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Validators

import com.atlassian.jira.issue.Issue

Issue epicIssue = cfValues['Epic Link'] as Issue

if (epicIssue) {
	String epicStatus = epicIssue.getStatus().getName()
	if (!(epicStatus in ["Open", "In Progress", "Ready"])) {
		return false
	}
}
return true