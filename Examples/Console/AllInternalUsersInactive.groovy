/*
 * Created 2024.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console


/*
Find all inactive internal users
Example of accessing User Attributes
*/

import com.atlassian.crowd.embedded.api.CrowdService
import com.atlassian.crowd.embedded.api.UserWithAttributes
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

Logger logger = LogManager.getLogger("my.logger")

CrowdService crowdService = ComponentAccessor.crowdService
GroupManager groupManager = ComponentAccessor.getGroupManager()
UserManager userManager = ComponentAccessor.getUserManager()

Collection<ApplicationUser> users = userManager.getAllApplicationUsers()
logger.info("Users list size = ${users.size()}")
users.each { ApplicationUser user ->
	Date d = null
	if (user != null && user.directoryId == 1 && !user.active) {
		List<String> usergroups = groupManager.getGroupNamesForUser(user)
		UserWithAttributes userPlus = crowdService.getUserWithAttributes(user.getName())
		String lastLoginMillis = userPlus.getValue('login.lastLoginMillis')
		if (lastLoginMillis?.isNumber()) {
			d = new Date(Long.parseLong(lastLoginMillis))
		}
		logger.info("internal:-," + user.getDisplayName() + "," + user.emailAddress + "," + d + "," + (user.active ? "active" : "inactive") + ",Groups:" + usergroups.toString())
	}
}
