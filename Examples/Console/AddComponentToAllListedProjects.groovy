/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.project.component.ProjectComponentManager
import com.atlassian.jira.project.ProjectManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.LogManager

Logger logger = LogManager.getLogger("my.logger")

ProjectComponentManager projectComponentManager = ComponentAccessor.getProjectComponentManager()
ProjectManager projectManager = ComponentAccessor.getProjectManager()

List<String> projects = ["PROJECT_A", "PROJECT_B", "PROJECT_C"]
List<String> components = ["Issue & Inquiry Only", "Registration", "Modification", "Update", "Static Page", "Promotion", "Home Page", "Footer"]

projects.each { String projectName ->
	Long projectId = projectManager.getProjectByCurrentKey(projectName).getId()
	components.each { String componentName ->
		projectComponentManager.create(componentName, null, null, 0, projectId)
		logger.info("${projectId} / ${componentName}")
	}
}

