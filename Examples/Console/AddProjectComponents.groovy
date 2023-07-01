/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

//add listed components to listed projects
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.project.component.ProjectComponentManager
import com.atlassian.jira.project.ProjectManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.LogManager

Logger logger = LogManager.getLogger("my.logger")

ProjectComponentManager projectComponentManager = ComponentAccessor.getProjectComponentManager()
ProjectManager projectManager = ComponentAccessor.getProjectManager()

List<String> projectNames = ["PROJECT_A", "PROJECT_B", "PROJECT_C"]
List<String> components = ["Component_A", "Component_B", "Component_C"]

projectNames.each { String projectName ->
	Long projectId = projectManager.getProjectByCurrentKey(projectName).getId()
	components.each { String componentName ->
		projectComponentManager.create(componentName, null, null, 0, projectId)
		logger.info("${projectId} / ${componentName}")
	}
}

