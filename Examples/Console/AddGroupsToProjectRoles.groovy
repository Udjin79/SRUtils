/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.security.roles.ProjectRoleActor
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.LogManager
import com.atlassian.jira.bc.projectroles.ProjectRoleService
import com.atlassian.jira.util.SimpleErrorCollection

//Example, how to add a group to all listed roles in all listed projects
ProjectRoleService projectRoleService = ComponentAccessor.getComponent(ProjectRoleService)
ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
ProjectManager projectManager = ComponentAccessor.getProjectManager()

Logger logger = LogManager.getLogger("my.logger")

List<String> roleNames = ["Clients", "Project Managers", "Project Members", "Administrators"]
List<String> projectNames = ["PROJECT_A", "PROJECT_B", "PROJECT_C"]
Collection<String> actorCollection = ["testGroup"]

SimpleErrorCollection errorCollection = new SimpleErrorCollection()

projectNames.each() { String projectName ->
	Project project = projectManager.getProjectByCurrentKey(projectName)
	roleNames.each() { String roleName ->
		ProjectRole role = projectRoleManager.getProjectRole(roleName)
		projectRoleService.addActorsToProjectRole(actorCollection,
				role,
				project,
				ProjectRoleActor.GROUP_ROLE_ACTOR_TYPE,
				errorCollection)
		logger.info("Added ${actorCollection} to ${role} / ${project}")
	}
}
 
