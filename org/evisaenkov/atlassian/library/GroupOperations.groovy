/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package org.evisaenkov.atlassian.library

import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import org.evisaenkov.atlassian.library.UserOperations

/**
 * Class for group operations with SR Jira
 * @author Evgeniy Isaenkov
 */
class GroupOperations {
	
	UserOperations userOperations = new UserOperations()
	
	boolean isUserInGroup(ApplicationUser user, String groupName) {
		isUserInGroup(user, getGroup(groupName))
	}
	
	boolean isUserInGroup(String userName, String groupName) {
		isUserInGroup(userOperations.getUser(userName), getGroup(groupName))
	}
	
	boolean isUserInGroup(ApplicationUser user, Group group) {
		ComponentAccessor.getGroupManager().isUserInGroup(user, group)
	}
	
	Group getGroup(String groupName) {
		ComponentAccessor.getGroupManager().getGroup(groupName)
	}
	
	Collection<ApplicationUser> getUsersInGroup(Group group) {
		ComponentAccessor.getGroupManager().getUsersInGroup(group)
	}
	
	Collection<ApplicationUser> getUsersInGroup(String groupName) {
		getUsersInGroup(getGroup(groupName))
	}
	
	boolean isCurrentUserInGroup(Group group) {
		ComponentAccessor.getGroupManager().isUserInGroup(userOperations.getCurrentUser(), group)
	}
	
	def isCurrentUserInGroup(String groupName) {
		isUserInGroup(userOperations.getCurrentUser(), getGroup(groupName))
	}
	
	void addUserToGroup(ApplicationUser user, Group group) {
		ComponentAccessor.getGroupManager().addUserToGroup(user, group)
	}
	
	def addUserToGroup(String userName, String groupName) {
		ComponentAccessor.getGroupManager().addUserToGroup(userOperations.getUserByUsername(userName), getGroup(groupName))
	}
	
	def getAllGroups() {
		ComponentAccessor.getGroupManager().getAllGroups()
	}
	
	Collection<Group> getGroupsForUser(String username) {
		ComponentAccessor.getUserUtil().getGroupsForUser(username)
	}
	
	void deleteUserFromGroups(ApplicationUser user, Collection<Group> groups) {
		ComponentAccessor.getUserUtil().removeUserFromGroups(groups, user)
	}
	
	void deleteUserFromAllGroups(String user) {
		deleteUserFromGroups(user as ApplicationUser, getGroupsForUser(user))
	}
}
