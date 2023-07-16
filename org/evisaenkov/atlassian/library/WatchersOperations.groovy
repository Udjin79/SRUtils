/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package org.evisaenkov.atlassian.library

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser

/**
 * Class for basic watchers operations with SR
 * @author Evgeniy Isaenkov
 */
class WatchersOperations {
	
	void startWatching(ApplicationUser user, Issue issue) {
		ComponentAccessor.watcherManager.startWatching(user, issue)
	}
	
	void startWatching(String username, Issue issue) {
		ApplicationUser user = ComponentAccessor.getUserManager().getUserByName(username)
		ComponentAccessor.watcherManager.startWatching(user, issue)
	}
	
	void stopWatching(ApplicationUser user, Issue issue) {
		ComponentAccessor.watcherManager.stopWatching(user, issue)
	}
	
	void stopWatching(String username, Issue issue) {
		ApplicationUser user = ComponentAccessor.getUserManager().getUserByName(username)
		ComponentAccessor.watcherManager.stopWatching(user, issue)
	}
	
	List<ApplicationUser> getAllWatchers(Issue issue) {
		return ComponentAccessor.watcherManager.getWatchers(issue, Locale.US)
	}
	
}