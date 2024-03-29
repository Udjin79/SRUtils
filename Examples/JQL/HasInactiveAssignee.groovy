/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.JQL

import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.jql.query.QueryCreationContext
import com.atlassian.jira.security.JiraAuthenticationContext
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.util.MessageSet
import com.atlassian.jira.util.MessageSetImpl
import com.atlassian.query.clause.TerminalClause
import com.atlassian.query.operand.FunctionOperand
import com.onresolve.jira.groovy.jql.AbstractScriptedJqlFunction
import com.onresolve.jira.groovy.jql.JqlQueryFunction
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery

class HasInactiveAssignee extends AbstractScriptedJqlFunction implements JqlQueryFunction {
	
	@Override
	String getDescription() {
		'Function to show only the inactive users'
	}
	
	@Override
	List<LinkedHashMap> getArguments() {
		[
				[
						'description': 'Subquery',
						'optional'   : false,
				]
		]
	}
	
	@Override
	String getFunctionName() {
		'hasInactiveAssignee'
	}
	
	String subquery
	//@Override
	MessageSet validate(User user, FunctionOperand operand, TerminalClause terminalClause) {
		def messageSet = new MessageSetImpl()
		return messageSet
	}
	
	@Override
	Query getQuery(QueryCreationContext queryCreationContext, FunctionOperand operand, TerminalClause terminalClause) {
		// Get the JiraAuthenticationContext and logged-in ApplicationUser
		JiraAuthenticationContext context = ComponentAccessor.getJiraAuthenticationContext()
		ApplicationUser applicationUser = context.getLoggedInUser()
		
		// Create a BooleanQuery builder
		BooleanQuery.Builder boolQueryBuilder = new BooleanQuery.Builder()
		
		// Get a list of issues using the provided subquery
		issues = getIssues(operand.args[0], applicationUser)
		
		// Iterate through each issue
		issues.each { Issue issue ->
			try {
				// Check if the assignee of the issue is inactive
				boolean active = issue.assignee.isActive()
				
				// If the assignee is inactive, add the issue to the BooleanQuery
				if (!active) {
					boolQueryBuilder.add(new TermQuery(new Term('issue_id', issue.id as String)), BooleanClause.Occur.SHOULD)
				}
			}
			catch (NullPointerException NPE) {
				// Handle any NullPointerException and log a warning message
				log.warn(NPE.message)
			}
		}
		
		// Build and return the BooleanQuery
		return boolQueryBuilder.build()
	}
}
