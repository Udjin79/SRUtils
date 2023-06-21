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

/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package ConsoleExamples

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser

Collection<ApplicationUser> getUsersInGroup(group) {
	return ComponentAccessor.getGroupManager().getUsersInGroup(group)
}

List<String> administrators = getUsersInGroup('jira-administrators').collect { ApplicationUser administrator ->
	administrator.name
}

log.warn(administrators)