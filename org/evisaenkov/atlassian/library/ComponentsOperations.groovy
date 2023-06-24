/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package org.evisaenkov.atlassian.library

import com.atlassian.jira.bc.project.component.ProjectComponent
import com.atlassian.jira.bc.project.component.ProjectComponentManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.project.Project

class Components {
	ProjectComponentManager projectComponentManager = ComponentAccessor.getProjectComponentManager()
	
	Collection<ProjectComponent> getComponents(Project project) {
		return projectComponentManager.findAllForProject(project.id)
	}
	
	Collection<ProjectComponent> getComponents(Long projectId) {
		return projectComponentManager.findAllForProject(projectId)
	}
	
	/**
	 * @param name
	 * @param description
	 * @param leadUsername
	 * @param assigneeType
	 * @param projectId
	 * @return
	 * Example: components.createComponent('ComponentName', 'ComponentDescription', 'evisaenkov', 3, 10146)
	 */
	ProjectComponent createComponent(String name, String description, String leadUsername, Long assigneeType, Long projectId) {
		projectComponentManager.create(name, description, leadUsername, assigneeType, projectId)
	}
	
	def getComponent(String componentName, Long projectId) {
		return projectComponentManager.findByComponentName(projectId, componentName)
	}
	
	def getComponent(Long id) {
		return projectComponentManager.find(id)
	}
	
	boolean hasComponents(Issue issue) {
		return ((issue.components == null) || (issue.components.size() == 0))
	}
	
	def hasComponent(Issue issue, ProjectComponent component) {
		return issue.components.contains(component)
	}
	
	def hasComponent(Issue issue, String componentName) {
		return issue.components.name.contains(componentName)
	}
	
	def hasComponent(Issue issue, Long componentId) {
		return issue.components.id.contains(componentId)
	}
}