/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

import com.atlassian.crowd.embedded.api.Group
import org.evisaenkov.atlassian.library.GroupOperations
import org.evisaenkov.atlassian.library.UserOperations

GroupOperations groupOperations = new GroupOperations()
UserOperations userOperations = new UserOperations()

String groupName = 'testJiraGroup'

List users = ["tester1", "tester2", "tester3", "tester4", "tester5", "tester6"]

Group group = groupOperations.getGroup(groupName)

users.each { String username ->
	groupOperations.addUserToGroup(userOperations.getUserByUsername(username), group)
}
