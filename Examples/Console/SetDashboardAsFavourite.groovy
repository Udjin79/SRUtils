/*
 * Created 2024.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.favourites.FavouritesManager
import com.atlassian.jira.portal.PortalPage
import com.atlassian.jira.portal.PortalPageManager
import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

// Set the filter ID and group to share with here
Long dashboardId = 10123
Collection<String> shareWithGroups = ["jira-software-users"]

FavouritesManager favouritesManager = ComponentAccessor.getComponentOfType(FavouritesManager.class);
GroupManager groupManager = ComponentAccessor.getGroupManager()
PortalPageManager portalPageManager = ComponentAccessor.getComponentOfType(PortalPageManager.class);
PortalPage portalPage = portalPageManager.getPortalPageById(dashboardId)
Logger logger = LogManager.getLogger("my.logger")


UserManager userManager = ComponentAccessor.getUserManager()
shareWithGroups.each { String groupName ->
	logger.info(groupName)
	Group group = groupManager.getGroup(groupName)
	groupManager.getUserNamesInGroup(group).each { String userId ->
		logger.info("id: ${userId}")
		ApplicationUser user = userManager.getUserByKey(userId)
		logger.info("user: ${user}")
		if (user != null) {
			logger.info("Add ${portalPage.getName()} to user favourites for ${user.getDisplayName()}")
			favouritesManager.addFavourite(user, portalPage)
		}
	}
}