/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Confluence

@Grapes([
		@Grab("com.atlassian.jira:jira-api:8.0.0"),
		/*
		Many transitive dependencies of the Jira API will either be provided by the host Confluence application or simply aren't needed for this script. Since several of them won't be resolvable with the default configuration, we exclude them with the below annotations.
		 */
		@GrabExclude("com.atlassian.annotations:atlassian-annotations"),
		@GrabExclude("jta:jta"),
		@GrabExclude("log4j:log4j"),
		@GrabExclude("webwork:pell-multipart-request"),
		@GrabExclude("org.codehaus.jackson:jackson-core-asl"),
		@GrabExclude("org.codehaus.jackson:jackson-mapper-asl"),
		@GrabExclude("com.atlassian.sal:sal-api"),
		@GrabExclude("com.atlassian.gadgets#atlassian-gadgets-api"),
])
import com.atlassian.confluence.user.AuthenticatedUserImpersonator
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal
import com.atlassian.jira.bc.issue.comment.CommentService
import com.atlassian.jira.bc.issue.comment.CommentService.CommentParameters.CommentParametersBuilder
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.comments.Comment
import com.atlassian.jira.issue.search.SearchResults
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.query.Query
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.user.User
import com.atlassian.user.UserManager
import com.onresolve.scriptrunner.remote.RemoteControl
import com.atlassian.jira.user.util.UserManager as JiraUserManager

def page = event.page
def (title, pageId, spaceKey) = [page.title, page.id, page.space.key]
String currentUserKey = AuthenticatedUserThreadLocal.get().name
log.debug "Current user is ${currentUserKey}"
User serviceAccountUser = ComponentLocator.getComponent(UserManager).getUser('serviceaccount') //change this to a user with admin permissions on your remote Jira instance

def messages = AuthenticatedUserImpersonator.REQUEST_AGNOSTIC.asUser({
	RemoteControl.forPrimaryJiraAppLink().exec {
		log.debug "Beginning attempt to create comment in Jira"
		CommentService commentService = ComponentAccessor.getComponent(CommentService)
		JqlQueryParser jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
		SearchService searchService = ComponentAccessor.getComponent(SearchService)
		ApplicationUser user = ComponentAccessor.getComponent(JiraUserManager).getUserByName(currentUserKey)
		log.debug "Will seearch & create comments as ${user.name}"
		String jql = "issueFunction in linkedIssuesOfRemote('query', 'pageId=${pageId}')"
		log.debug "Searching with JQL query ${jql}"
		Query query = jqlQueryParser.parseQuery(jql)
		SearchResults<Issue> results = searchService.search(user, query, PagerFilter.unlimitedFilter)
		List<Issue> issues = results.results
		issues.collect { Issue issue ->
			log.debug "Found issue ${issue.key}; attempting to create comment as ${user.name}"
			def commentParameters = new CommentParametersBuilder()
					.author(user)
					.body("Page ${title} has been updated") // Customize this comment to contain the information you need
					.issue(issue)
					.build()
			def commentCreateValidationResult = commentService.validateCommentCreate(user, commentParameters)
			if (commentCreateValidationResult.valid) {
				Comment comment = commentService.create(user, commentCreateValidationResult, true)
				String message = "Created comment ${comment.id} on issue ${issue.key} in repsonse to update of page '${title}' in the ${spaceKey} space"
				log.debug message
				return message
			}
			log.error "Could not create comment as ${user.name}"
			commentCreateValidationResult.errorCollection.errorMessages.each {
				log.error it
			}
			commentCreateValidationResult.warningCollection.warnings.each {
				log.warn it
			}
		}
	}
}, serviceAccountUser)

messages.each {
	log.debug messages
}