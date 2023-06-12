/*
 * Copyright (c) 2023.
 * @author Evgeniy Isaenkov
 */

package org.evisaenkov.atlassian.library

import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.IssueInputParameters
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.workflow.TransitionOptions
import org.evisaenkov.atlassian.library.UserOperations
import org.evisaenkov.atlassian.library.CommentsOperations
import org.evisaenkov.atlassian.library.IssueOperations

/**
 * Class for transitions operations with SR Jira
 * @author Evgeniy Isaenkov
 */
class TransitionsOperations {
	
	private final UserOperations userOperations = new UserOperations()
	private final CommentsOperations commentsOperations = new CommentsOperations()
	private final IssueOperations issueOperations = new IssueOperations()
	private final ApplicationUser techUser = userOperations.getTechUser() as ApplicationUser
	
	boolean doTransition(MutableIssue issue, int actionId, boolean skipConditions = false, boolean skipPermissions = false, boolean skipValidators = false, String username = techUser.username) {
		ApplicationUser user = userOperations.getUser(username) as ApplicationUser
		def builder = new TransitionOptions.Builder()
		
		if (skipConditions) {
			builder.skipConditions()
		}
		if (skipPermissions) {
			builder.skipPermissions()
		}
		if (skipValidators) {
			builder.skipValidators()
		}
		TransitionOptions transitionOptions = builder.build()
		IssueService issueService = ComponentAccessor.getIssueService()
		IssueInputParameters issueInputParameters = issueService.newIssueInputParameters()
		IssueService.TransitionValidationResult transitionValidationResult = issueService.validateTransition(user, issue.id, actionId, issueInputParameters, transitionOptions)
		if (transitionValidationResult.isValid()) {
			issueService.transition(user, transitionValidationResult)
			return true
		} else {
			return false
		}
	}
	
	def doTransitionWithCommentAndResolution(MutableIssue issue, int id, String username = techUser.username, String resolution, String comment = null) {
		ApplicationUser user = userOperations.getUser(username)
		doTransition(issue, id, true, true, true, user.username)
		if (comment != null) {
			commentsOperations.addComment(issue, comment)
		}
		issue.setResolutionId(resolution)
		issueOperations.updateIssue(issue)
	}
}
