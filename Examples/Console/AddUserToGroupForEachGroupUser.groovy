/*
 * Created 2024.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

/*
  Used where users had been entered with wrong domain name
  This only fixes the user groups memberships as a time saving operation
  Take the users in source groups
  For each member, change domain and add to source group and license group.
*/
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


// the name of the  group
// format :
// "Source group":"license group"
Map<String, String> groups = [
		"Accounts Team": "jira-software-users",
		"Dev Team"     : "jira-software-users",
		"QA Team"      : "jira-software-users",
]

Logger logger = LogManager.getLogger("my.logger")
GroupManager groupManager = ComponentAccessor.groupManager
UserManager userManager = ComponentAccessor.getUserManager()

groups.each() { Map.Entry<String, String> group ->
	Group groupIn = groupManager.getGroup(group.key)
	if (groupIn) {
		logger.info("source : " + groupIn.name)
		Group license = groupManager.getGroup(group.value)
		Collection<ApplicationUser> inUsers = groupManager.getUsersInGroup(groupIn)
		inUsers.each() { ApplicationUser user ->
			if (user.username.contains("domain1")) {
				String newName = user.username.replaceFirst("domain1.com", "domain2.com")
				ApplicationUser user_2 = userManager.getUserByName(newName)
				if (user_2) {
					logger.info("change " + user_2.username + " to " + newName)
					groupManager.addUserToGroup(user_2, groupIn)
					groupManager.addUserToGroup(user_2, license)
				}
			}
		}
	}
}
    
