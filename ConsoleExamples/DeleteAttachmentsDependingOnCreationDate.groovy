/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package ConsoleExamples

import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.attachment.Attachment
import com.atlassian.jira.issue.search.SearchResults
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.query.Query

JqlQueryParser jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser) as JqlQueryParser
SearchService searchService = ComponentAccessor.getComponent(SearchService)
ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
Calendar cal = Calendar.getInstance()

String jqlQuery = "project = TEST AND attachments is not EMPTY"

cal.set(2023, 0, 01, 0, 0, 0)
Date fromDate = cal.getTime()
cal.clear()
cal.set(2023, 0, 31, 23, 59, 59)
Date toDate = cal.getTime()

Query query = jqlQueryParser.parseQuery(jqlQuery)
SearchResults search = searchService.search(user, query, PagerFilter.getUnlimitedFilter())
List<Issue> results = search.results

results.each { Issue issue ->
	List<Attachment> attachments = ComponentAccessor.attachmentManager.getAttachments(issue)
	attachments.each { Attachment attachment ->
		if (attachment.created >= fromDate.toTimestamp() && attachment.created <= toDate.toTimestamp()) {
			ComponentAccessor.attachmentManager.deleteAttachment(attachment)
		}
	}
}
