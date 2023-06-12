/*
 * Copyright (c) 2023.
 * @author Evgeniy Isaenkov
 */

package org.evisaenkov.atlassian.library

import com.atlassian.greenhopper.manager.rapidview.BoardAdminManager
import com.atlassian.greenhopper.model.rapid.BoardAdmin
import com.atlassian.greenhopper.model.rapid.RapidView
import com.atlassian.greenhopper.service.rapid.view.RapidViewService
import com.atlassian.jira.user.ApplicationUser
import com.onresolve.scriptrunner.runner.customisers.JiraAgileBean
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
import org.evisaenkov.atlassian.library.UserOperations
import com.atlassian.greenhopper.model.rapid.BoardAdmin.Type

/**
 * Class for boards operations with SR Jira
 * @author Evgeniy Isaenkov
 */
class BoardsOperations {
	
	private final UserOperations userOperations = new UserOperations()
	private final ApplicationUser techUser = userOperations.getTechUser() as ApplicationUser
	
	@WithPlugin('com.pyxis.greenhopper.jira')
	@JiraAgileBean
	RapidViewService rapidViewService
	@JiraAgileBean
	BoardAdminManager boardAdminManager
	
	RapidView getBoard(Long rapidBoardId) {
		return rapidViewService.getRapidView(techUser, rapidBoardId).value
	}
	
	List<RapidView> getBoardsForUser(Object user) {
		if (user instanceof String) {
			user = userOperations.getUser(user)
		}
		return rapidViewService.getRapidViews((ApplicationUser) user).value
	}
	
	Set<RapidView> getAllBoards() {
		Set<RapidView> allRapidViews = []
		List<ApplicationUser> allUsers = userOperations.getAllUsers() as List
		allUsers.each { ApplicationUser user ->
			allRapidViews += getBoardsForUser(user)
		}
		return allRapidViews
	}
	
	void setRapidViewOwner(Object board, Object newOwner) {
		if (board instanceof Long) {
			board = getBoard(board)
		}
		if (newOwner instanceof String && !newOwner.contains('JIRAUSER')) {
			newOwner = userOperations.getUser(newOwner)
		}
		if (newOwner instanceof ApplicationUser) {
			newOwner = newOwner.key
		}
		RapidView updatedView = RapidView.builder((RapidView) board).owner((String) newOwner).build()
		rapidViewService.update(techUser, updatedView)
	}
	
	void changeBoardFilter(Object board, Long filterId) {
		if (board instanceof Long) {
			board = getBoard(board)
		}
		RapidView updatedView = RapidView.builder((RapidView) board).savedFilterId(filterId).build()
		rapidViewService.update(techUser, updatedView)
	}
	
	List<BoardAdmin> getBoardAdmins(Object board) {
		if (board instanceof Long) {
			board = getBoard(board)
		}
		return boardAdminManager.getBoardAdmins((RapidView) board)
	}
	
	BoardAdmin createBoardAdminObject(String adminKey, String type) {
		return BoardAdmin.builder().key(adminKey).type(type.toLowerCase() == 'group' ? Type.GROUP : Type.USER).build()
	}
	
	void setBoardAdmins(List<BoardAdmin> newBoardAdmins, Object board) {
		if (board instanceof Long) {
			board = getBoard(board)
		}
		boardAdminManager.updateBoardAdmin((RapidView) board, newBoardAdmins)
	}
	
	void addBoardAdmins(List<BoardAdmin> newBoardAdmins, Object board) {
		if (board instanceof Long) {
			board = getBoard(board)
		}
		boardAdminManager.appendBoardAdmin((RapidView) board, newBoardAdmins)
	}
}
