/*
 * Copyright (c) 2023.
 * @author Evgeniy Isaenkov
 */

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.servicedesk.api.ServiceDeskManager
import com.atlassian.servicedesk.api.queue.QueueCreateParameters
import com.atlassian.servicedesk.api.queue.QueueQuery
import com.atlassian.servicedesk.api.queue.QueueService
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
import org.evisaenkov.atlassian.library.UserOperations
import org.evisaenkov.atlassian.library.ProjectOperations

/**
This script is used to mass clone Jira SM queue.
 When you have default queue to show requests for employee, if you have many employees in department, you can duplicate queue for every person.
 */

@WithPlugin("com.atlassian.servicedesk")
UserOperations userOperations = new UserOperations()
ProjectOperations projectOperations = new ProjectOperations()

ApplicationUser currentUser = userOperations.getCurrentUser()
Project project = projectOperations.getProject("TEST")

QueueService queueService = ComponentAccessor.getOSGiComponentInstanceOfType(QueueService)
ServiceDeskManager serviceDeskManager = ComponentAccessor.getOSGiComponentInstanceOfType(ServiceDeskManager)

// Get Service Desk Id related to projects
int projectServiceDeskId = serviceDeskManager.getServiceDeskForProject(project).id

// Get source project queue and copy
QueueQuery query = queueService.newQueueQueryBuilder().serviceDeskId(1).queueId(100).build()
String username1 = "testUser1"
String username2 = "testUser2"

queueService.getQueues(currentUser, query).results.eachWithIndex { queue, index ->
	// Default JQL is removed because when a Queue is created 'project = <related key project>' is added by default
	String newJql = queue.jql.replace(username1, username2)
	// Replace source key project with destination key project in JQL
	QueueCreateParameters queueCreateParameters = queueService.newQueueCreateParameterBuilder(projectServiceDeskId, userOperations.getUserByUsername(username2).displayName)
			.jql(newJql)
			.fields(queue.fields)
			.order(index)
			.build()
	queueService.addQueue(currentUser, queueCreateParameters)
}