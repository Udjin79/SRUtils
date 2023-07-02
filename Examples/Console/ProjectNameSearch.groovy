/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.LogManager

Logger logger = LogManager.getLogger("my.logger")

ProjectManager projectManager = ComponentAccessor.getProjectManager()

List<String> projectKeys = ["TEST_A", "TEST_B"]
List<Project> allProjects = projectManager.getProjectObjects()
List<Project> matchProjects = []

allProjects.each { Project project ->
	projectKeys.each { String projectKey ->
		if (project.getName().contains(projectKey)) {
			matchProjects.add(project)
		}
	}
}

logger.info("Found projects: ${matchProjects.toString()}")