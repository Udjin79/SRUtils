/*
 * Created 2024.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

import com.atlassian.jira.project.Project
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.user.ApplicationUser
import org.evisaenkov.atlassian.library.ProjectOperations
import org.evisaenkov.atlassian.library.RolesOperations
import org.evisaenkov.atlassian.library.UserOperations

ProjectOperations projectOperations = new ProjectOperations()
RolesOperations rolesOperations = new RolesOperations()
UserOperations userOperations = new UserOperations()

Collection<ProjectRole> roles = rolesOperations.getAllRoles()
List<Project> projects = projectOperations.getAllProjects()

String report = ""
Integer index = 0
Integer totalIndex = 0

projects.each { Project project ->
	roles.each { ProjectRole role ->
		Collection<ApplicationUser> users = rolesOperations.getUsersInProjectRole(project, role)
		users.each { ApplicationUser user ->
			try {
				if (!user.active) {
					rolesOperations.removeUserFromRole(user.username, role.name, project.key)
					index++
					totalIndex++
				}
			} catch (Exception e) {
				report += "Error: ${project.key}: ${user.username}<br/>"
			}
		}
	}
	report += "Project: ${project.key}. Removed: ${index}<br/>"
	index = 0
}
report += "Total removed: ${index}<br/>"
return report
