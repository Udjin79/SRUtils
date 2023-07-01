/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

//For each project in a category add a group as role actor to each of listed roles
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.ProjectCategory
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.security.roles.ProjectRoleActor
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.LogManager
import com.atlassian.jira.bc.projectroles.ProjectRoleService
import com.atlassian.jira.util.SimpleErrorCollection

Logger logger = LogManager.getLogger("my.logger")

List<String> roleNames = ['Project Managers']
Collection<String> actorCollection = ['My PMs']

ProjectRoleService projectRoleService = ComponentAccessor.getComponent(ProjectRoleService)
ProjectManager projectManager = ComponentAccessor.getProjectManager()
ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
SimpleErrorCollection errorCollection = new SimpleErrorCollection()

List projectCategories = ["Developers", "Products", "Projects"]

projectCategories.each { String categoryName ->
	ProjectCategory category = projectManager.getProjectCategoryObjectByName(categoryName)
	Collection<Project> projectsInCategory = projectManager.getProjectObjectsFromProjectCategory(category.getId())
	projectsInCategory.each { Project project ->
		roleNames.each() { String roleName ->
			ProjectRole role = projectRoleManager.getProjectRole((String) roleName)
			projectRoleService.addActorsToProjectRole(actorCollection,
					role,
					project,
					ProjectRoleActor.GROUP_ROLE_ACTOR_TYPE,
					errorCollection)
			logger.info("Added ${actorCollection} to ${role} / ${project}")
		}
	}
}
