/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

import com.atlassian.jira.project.Project
import com.atlassian.jira.security.roles.ProjectRole
import org.evisaenkov.atlassian.library.ProjectOperations
import org.evisaenkov.atlassian.library.RolesOperations
import org.evisaenkov.atlassian.library.UserOperations

RolesOperations rolesOperations = new RolesOperations()
ProjectOperations projectOperations = new ProjectOperations()

// Example, how to get roles with users
Map<String, List> rolesAndUsers = [:]
Project prj = projectOperations.getProject('OLD')
Collection<ProjectRole> rolesList = rolesOperations.getAllRoles()


rolesList.each { ProjectRole role ->
	List users = rolesOperations.getUsersOnProjectRole(prj, role).collect {
		if (it.active) {
			it.getUsername()
		}
	}
	rolesAndUsers.put(role.name, users)
}

// Example, how to add users to project role
String role = 'Service Desk Team'
String projectName = 'NEW'
List users = ['tester1', 'tester2', 'tester3', 'tester4', 'tester5']

users.each { String username ->
	rolesOperations.addUserToRole(username, role, projectName)
}
