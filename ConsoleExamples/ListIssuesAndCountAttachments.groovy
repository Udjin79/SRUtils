/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package ConsoleExamples

import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.AttachmentManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.attachment.Attachment
import com.atlassian.jira.issue.search.SearchResults
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.query.Query

AttachmentManager attachmentManager = ComponentAccessor.getAttachmentManager()
JqlQueryParser jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser) as JqlQueryParser
SearchService searchService = ComponentAccessor.getComponent(SearchService)
UserManager userManager = ComponentAccessor.getUserManager()

//Username of user, that has rights to see issues
String username = "admin"
ApplicationUser techUser = userManager.getUserByName(username) as ApplicationUser

String jql = "project = TEST"
Query query = jqlQueryParser.parseQuery(jql)
SearchResults search = searchService.search(techUser, query, PagerFilter.getUnlimitedFilter())
List issues = search.results

String report = ""

issues.each { Issue issue ->
	List<Attachment> attachments = attachmentManager.getAttachments(issue)
	report += "Issue: ${issue.key}. Count: ${attachments ? attachments.size() : 0}\n"
}

log.warn(report)
