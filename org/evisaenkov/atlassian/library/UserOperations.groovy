/*
 * Copyright (c) 2022.
 * @author Evgeniy Isaenkov
 */

package org.evisaenkov.atlassian.library

import com.atlassian.jira.bc.security.login.LoginInfo
import com.atlassian.jira.bc.user.search.UserSearchParams
import com.atlassian.jira.bc.user.search.UserSearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.login.LoginManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.UserFilter
import com.atlassian.jira.user.util.UserManager
import org.evisaenkov.atlassian.library.Variables

/**
 * Class for making users Jira requests
 * @author Evgeniy Isaenkov
 */
class UserOperations {
	
	private final UserManager userManager = ComponentAccessor.getUserManager()
	private final UserSearchService userSearchService = ComponentAccessor.getComponent(UserSearchService.class)
	
	ApplicationUser getUser(String username) {
		if (username && username.contains('JIRA')) {
			getUserByKey(username)
		} else if (username) {
			getUserByUsername(username)
		} else {
			return null
		}
	}
	
	ApplicationUser getUserByUsername(String username) {
		return userManager.getUserByName(username) as ApplicationUser
	}
	
	ApplicationUser getUserByKey(String userkey) {
		return userManager.getUserByKey(userkey) as ApplicationUser
	}
	
	ApplicationUser getUserByEmail(String email) {
		Iterable<ApplicationUser> users = userSearchService.findUsersByEmail(email)
		if (users) {
			return users.first()
		} else {
			return null
		}
	}
	
	List getUsersByEmail(String email) {
		Iterable<ApplicationUser> users = userSearchService.findUsersByEmail(email)
		List usersList = []
		users.each { ApplicationUser user ->
			usersList.add(user)
		}
		return usersList
	}
	
	ApplicationUser getTechUser() {
		return userManager.getUserByName(Variables.SERVICE_ACCOUNT) as ApplicationUser
	}
	
	ApplicationUser getCurrentUser() {
		return ComponentAccessor.jiraAuthenticationContext.getLoggedInUser() as ApplicationUser
	}
	
	void impersonateUser(ApplicationUser user) {
		ComponentAccessor.jiraAuthenticationContext.setLoggedInUser(user)
	}
	
	void impersonateUser(String username) {
		ApplicationUser user = getUser(username)
		ComponentAccessor.jiraAuthenticationContext.setLoggedInUser(user)
	}
	
	Collection getUsersInGroup(String group) {
		return ComponentAccessor.getGroupManager().getUsersInGroup(group)
	}
	
	Collection<ApplicationUser> getAllUsers() {
		return userManager.getAllApplicationUsers()
	}
	
	Collection<ApplicationUser> getAllActiveUsers() {
		UserSearchService userSearchService = ComponentAccessor.getComponent(UserSearchService)
		
		boolean allowEmptyQuery = true
		boolean includeActive = true
		boolean includeInactive = false
		boolean canMatchEmail = false
		def userFilter = null
		def projectIds = null
		
		UserSearchParams allUserParams = new UserSearchParams(allowEmptyQuery, includeActive, includeInactive, canMatchEmail, userFilter as UserFilter, projectIds as Set<Long>)
		return userSearchService.findUsers('', allUserParams)
	}
	
	Collection<ApplicationUser> getUsersByQuery(boolean allowEmptyQuery = true, boolean includeActive = true, boolean includeInactive = true, boolean canMatchEmail = false, UserFilter userFilter = null, Set<Long> projectIds = null) {
		UserSearchService userSearchService = ComponentAccessor.getComponent(UserSearchService)
		UserSearchParams allUserParams = new UserSearchParams(allowEmptyQuery, includeActive, includeInactive, canMatchEmail, userFilter as UserFilter, projectIds as Set<Long>)
		return userSearchService.findUsers('', allUserParams)
	}
	
	LoginInfo getLoginInfoByUsername(String userName) {
		LoginManager loginManager = ComponentAccessor.getComponentOfType(LoginManager)
		return loginManager.getLoginInfo(userName)
	}
	
	boolean isLocalUser(String userName) {
		ApplicationUser user = getUser(userName)
		return user.getDirectoryId() == 1
	}
	
	void resetFailedLoginCount(Object user) {
		LoginManager loginManager = ComponentAccessor.getComponentOfType(LoginManager)
		if (user instanceof ApplicationUser) {
			loginManager.resetFailedLoginCount(user)
		} else if (user instanceof String) {
			loginManager.resetFailedLoginCount(getUser(user))
		}
	}
}
