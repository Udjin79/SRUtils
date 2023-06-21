/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package ConsoleExamples

import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.search.SearchResults
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.query.Query

import java.sql.Timestamp

UserManager userManager = ComponentAccessor.getUserManager()
JqlQueryParser jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
SearchService searchService = ComponentAccessor.getComponent(SearchService)
ChangeHistoryManager changeHistoryManager = ComponentAccessor.getChangeHistoryManager()
IssueManager issueManager = ComponentAccessor.getIssueManager()
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()

CustomField targetStatusTime = customFieldManager.getCustomFieldObject(12345)

// service account, that looks for changes in issue
ApplicationUser techUser = userManager.getUserByName('serviceAccount') as ApplicationUser
String targetStatusName = "Done"

// Receiving list of issues
Query query = jqlQueryParser.parseQuery('project = TEST')
SearchResults<Issue> search = searchService.search(techUser, query, PagerFilter.getUnlimitedFilter())

search.results.each { Issue issueObject ->
	MutableIssue issue = ComponentAccessor.issueManager.getIssueByCurrentKey(issueObject.key)
	Timestamp created = changeHistoryManager.getChangeItemsForField(issue, 'status').find {
		it.toString == targetStatusName
	}?.getCreated()
	
	long createdTime = created?.getTime()
	
	createdTime ? new Date(createdTime) : null
	try {
		issue.setCustomFieldValue(targetStatusTime, new Timestamp(createdTime))
		issueManager.updateIssue(techUser, issue, EventDispatchOption.DO_NOT_DISPATCH, false)
	} catch (Exception exc) {
		log.warn(exc.message)
	}
}
