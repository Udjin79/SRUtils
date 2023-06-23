/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager

UserManager userManager = ComponentAccessor.getUserManager()
GroupManager groupManager = ComponentAccessor.getGroupManager()

ApplicationUser userA = userManager.getUserByName("A") as ApplicationUser
ApplicationUser userB = userManager.getUserByName("B") as ApplicationUser

Collection<Group> groups = groupManager.getGroupsForUser(userA)

groups.each { Group group ->
	groupManager.addUserToGroup(userB, group)
}
