/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package org.evisaenkov.atlassian.library

import com.atlassian.jira.bc.projectroles.ProjectRoleService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.security.roles.ProjectRoleActor
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.util.SimpleErrorCollection
import org.evisaenkov.atlassian.library.ProjectOperations
import org.evisaenkov.atlassian.library.UserOperations


class RolesOperations {
	
	ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
	ProjectOperations projectOperations = new ProjectOperations()
	UserOperations userOperations = new UserOperations()
	
	Collection getAllRoles() {
		return projectRoleManager.getProjectRoles()
	}
	
	void addUserToRole(String username, String role, String projectKey) {
		ProjectRoleService projectRoleService = ComponentAccessor.getComponent(ProjectRoleService)
		SimpleErrorCollection errorCollection = new SimpleErrorCollection()
		
		ProjectRole projectRole = getProjectRole(role)
		Project project = projectOperations.getProject(projectKey)
		ApplicationUser user = userOperations.getUserByUsername(username)
		
		Collection<String> actorCollection = new ArrayList<>()
		actorCollection.add(user.getKey())
		
		projectRoleService.addActorsToProjectRole(actorCollection, projectRole, project, ProjectRoleActor.USER_ROLE_ACTOR_TYPE, errorCollection)
	}
	
	ProjectRole getProjectRole(String projectRoleName) {
		return projectRoleManager.getProjectRole(projectRoleName)
	}
	
	Collection<ApplicationUser> getUsersOnProjectRole(Project project, ProjectRole role) {
		return projectRoleManager.getProjectRoleActors(role, project)?.getApplicationUsers()
	}
	
	def getRolesForUser(ApplicationUser user, Project project) {
		return projectRoleManager.getProjectRoles(user, project)
	}
	
	boolean isUserInProjectRole(ApplicationUser user, String projectRoleName, Project project) {
		ProjectRole projectRole = getProjectRole(projectRoleName)
		return projectRoleManager.isUserInProjectRole(user, projectRole, project)
	}
	
}
