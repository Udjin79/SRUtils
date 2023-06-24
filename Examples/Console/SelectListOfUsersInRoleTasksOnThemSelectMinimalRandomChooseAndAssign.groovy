/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import org.evisaenkov.atlassian.library.IssueOperations
import org.evisaenkov.atlassian.library.RolesOperations
import java.security.SecureRandom

IssueOperations issueOperations = new IssueOperations()
RolesOperations rolesOperations = new RolesOperations()

SecureRandom rnd = new SecureRandom()

List<ApplicationUser> users = rolesOperations.getUsersInProjectRole('Service Desk Team', 'TEST')

Map<String, MutableIssue> usersWorkload = users.collectEntries { ApplicationUser user ->
	[user.getName(), issueOperations.getIssuesByJQL("assignee = ${user.getName()} AND project = TEST AND statusCategory != Done").size()]
}

List possibleAssignees = usersWorkload.findAll { Map.Entry<String, MutableIssue> entry ->
	entry.value == usersWorkload.values().min()
}.keySet() as List

log.warn('List of assignees and their tasks:')
log.warn(usersWorkload)
log.warn('Users with minimal tasks:')
log.warn(possibleAssignees)
log.warn('Who is winner now:')
log.warn(possibleAssignees[rnd.nextInt(possibleAssignees.size())])
