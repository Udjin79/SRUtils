/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package org.evisaenkov.atlassian.library

import com.atlassian.jira.bc.projectroles.ProjectRoleService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.security.roles.ProjectRoleActor
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.project.AssigneeTypes
import com.atlassian.jira.util.SimpleErrorCollection
import org.evisaenkov.atlassian.library.UserOperations

/**
 * Class for ProjectOperations with SR Jira
 * @author Evgeniy Isaenkov
 */
class ProjectOperations {
	ProjectManager projectManager = ComponentAccessor.getProjectManager()
	UserOperations userOperations = new UserOperations()
	ProjectRoleService projectRoleService = ComponentAccessor.getComponent(ProjectRoleService)
	ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
	
	List getAllProjects() {
		return projectManager.getProjectObjects()
	}
	
	Project getProject(String projectKey) {
		return projectManager.getProjectObjByKey(projectKey)
	}
	
	Project getProject(Long projectId) {
		return projectManager.getProjectObj(projectId)
	}
	
	Map getProjectData(String projectKey) {
		Map projectData = [:]
		Project project = getProject(projectKey)
		projectData.put("ID", project.id)
		projectData.put("Key", project.key)
		projectData.put("Name", project.name)
		projectData.put("Lead", project.getProjectLead())
		projectData.put("Components", project.components)
		if (project.projectCategory) {
			projectData.put("Category", project.projectCategory)
		}
		return projectData
	}
	
	ApplicationUser getProjectLead(String projectKey) {
		Project prj = projectManager.getProjectObjByKey(projectKey)
		return prj.getProjectLead()
	}
	
	void setProjectLead(String projectKey, ApplicationUser projectLead) {
		Project prj = getProject(projectKey)
		String name = prj.name
		String description = prj.description
		String leadKey = projectLead.key
		String url = prj.url
		projectManager.updateProject(prj, name, description, leadKey, url, AssigneeTypes.UNASSIGNED)
	}
	
	void addGroupToProjectRole(String projectKey, String projectRoleName, String groupName) {
		SimpleErrorCollection errorCollection = new SimpleErrorCollection()
		Project project = getProject(projectKey)
		ProjectRole role = projectRoleManager.getProjectRole(projectRoleName)
		Collection<String> actorCollection = [groupName]
		projectRoleService.addActorsToProjectRole(actorCollection,
				role,
				project,
				ProjectRoleActor.GROUP_ROLE_ACTOR_TYPE,
				errorCollection)
	}
	
	void addUserToProjectRole(String projectKey, String projectRoleName, String userName) {
		SimpleErrorCollection errorCollection = new SimpleErrorCollection()
		Project project = getProject(projectKey)
		ProjectRole role = projectRoleManager.getProjectRole(projectRoleName)
		Collection<String> actorCollection = [userOperations.getUser(userName).key]
		projectRoleService.addActorsToProjectRole(actorCollection,
				role,
				project,
				ProjectRoleActor.USER_ROLE_ACTOR_TYPE,
				errorCollection)
	}
}