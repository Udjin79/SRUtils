/*
 * Created 2024.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectCategory
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.project.UpdateProjectParameters
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

List<String> projectCategories = ["CategoryA", "CategoryB"]

Logger logger = LogManager.getLogger("my.logger")
ProjectManager projectManager = ComponentAccessor.getProjectManager()

// for each category string
projectCategories.each() { String category ->
	// get the category
	ProjectCategory projectCategory = projectManager.getProjectCategoryObjectByName(category)
	// get all projects for category
	Collection<Project> projects = projectManager.getProjectObjectsFromProjectCategory(projectCategory.getId())
	// for each project
	projects.each() { Project project ->
		if (project.getLeadUserName().contentEquals("john.doe@example.com")) {
			logger.info("Project:\t${project.key}.\tAssignee type:\t${project.assigneeType}")
			// get update params, set default assignee = unassigned ( option 3)
			UpdateProjectParameters updateProjectParameters = UpdateProjectParameters.forProject(project.getId()).assigneeType(3)
			projectManager.updateProject(updateProjectParameters)
		}
	}
}
