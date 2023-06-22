/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Jobs

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.login.LoginManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager

UserManager userManager = ComponentAccessor.getUserManager()
LoginManager loginManager = ComponentAccessor.getComponentOfType(LoginManager)

List<String> usersToCheck = ['tester1', 'tester2', 'tester3']

usersToCheck.each { String username ->
	ApplicationUser user = userManager.getUserByName(username) as ApplicationUser
	Integer failedCount = loginManager.getLoginInfo(username).currentFailedLoginCount
	if (failedCount > 0) {
		log.warn("Failed login count for ${username}: ${failedCount}")
		loginManager.resetFailedLoginCount(user)
	}
}