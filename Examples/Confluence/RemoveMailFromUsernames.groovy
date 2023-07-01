/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Confluence

import com.atlassian.confluence.user.UserAccessor
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.user.UserManager
import com.atlassian.user.search.page.Pager

UserManager userManager = ComponentLocator.getComponent(UserManager)
UserAccessor userAccessor = ComponentLocator.getComponent(UserAccessor)

def userNames = userManager.userNames as Pager<String>
def affectedUserNames = userNames.findAll { String userName ->
	userName.contains('@')
} as List<String>

affectedUserNames.each { String userName ->
	log.warn "Old username = ${userName}"
	String newUserName = userName.substring(0, userName.indexOf('@'))
	log.warn "New username = ${newUserName}"
	userAccessor.renameUser(userAccessor.getUserByName(userName), newUserName)
}
