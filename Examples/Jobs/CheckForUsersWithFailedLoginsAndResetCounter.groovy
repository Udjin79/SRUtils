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

// Get the UserManager and LoginManager components
UserManager userManager = ComponentAccessor.getUserManager()
LoginManager loginManager = ComponentAccessor.getComponentOfType(LoginManager)

// Define a list of usernames to check for failed login attempts
List<String> usersToCheck = ['tester1', 'tester2', 'tester3']

// Iterate through the list of usernames
usersToCheck.each { String username ->
	// Get the ApplicationUser by username
	ApplicationUser user = userManager.getUserByName(username) as ApplicationUser
	
	// Get the current failed login count for the user
	Integer failedCount = loginManager.getLoginInfo(username).currentFailedLoginCount
	
	// Check if there are failed login attempts
	if (failedCount > 0) {
		// Log a warning message with the failed login count
		log.warn("Failed login count for ${username}: ${failedCount}")
		
		// Reset the failed login count for the user
		loginManager.resetFailedLoginCount(user)
	}
}
