/*
 * Created 2024.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package evisaenkov.atlassian.library
/*
 * Created 2024.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */



import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.worklog.Worklog

/**
 * Class for worklog operations used with SR Jira
 * @author Evgeniy Isaenkov
 */

class WorklogOperations {
	List<Worklog> getWorklogsByIssue(Issue issue) {
		List<Worklog> worklogs = ComponentAccessor.worklogManager.getByIssue(issue)
		return worklogs
	}
}
