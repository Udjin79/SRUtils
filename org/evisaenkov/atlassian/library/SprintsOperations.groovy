/*
 * Copyright (c) 2022-2023.
 * @author Evgeniy Isaenkov
 */

package org.evisaenkov.atlassian.library

import com.atlassian.greenhopper.model.rapid.RapidView
import com.atlassian.greenhopper.service.sprint.Sprint
import com.atlassian.greenhopper.service.sprint.SprintIssueService
import com.atlassian.greenhopper.service.sprint.SprintManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.onresolve.scriptrunner.runner.customisers.JiraAgileBean
import com.onresolve.scriptrunner.runner.customisers.WithPlugin

/**
 * Class for sprints operations with SR Jira
 * @author Evgeniy Isaenkov
 */
class SprintsOperations {
	
	private final UserOperations userOperations = new UserOperations()
	private final ApplicationUser techUser = userOperations.getTechUser() as ApplicationUser
	
	@WithPlugin('com.pyxis.greenhopper.jira')
	@JiraAgileBean
	SprintIssueService sprintIssueService
	@JiraAgileBean
	SprintManager sprintManager
	
	Sprint getSprint(Long id) {
		return sprintManager.getSprint(id).getValue()
	}
	
	void deleteSprint(Long id) {
		sprintManager.deleteSprint(getSprint(id))
	}
	
	def getSprintsForView(RapidView board) {
		return sprintManager.getSprintsForView(board).getValue()
	}
	
	def getSprintsForIssue(Issue issue) {
		return sprintIssueService.getSprintsForIssue(techUser, issue).getValue()
	}
	
	def moveIssuesToSprint(Issue issue, Sprint sprint) {
		sprintIssueService.moveIssuesToSprint(techUser, sprint, [issue] as Collection)
	}
	
	def removeAllIssuesFromSprint(Sprint sprint) {
		sprintIssueService.removeAllIssuesFromSprint(techUser, sprint)
	}
	
	def moveIssuesToBacklog(Collection<Issue> issueList) {
		sprintIssueService.moveIssuesToBacklog(techUser, issueList)
	}
	
}
