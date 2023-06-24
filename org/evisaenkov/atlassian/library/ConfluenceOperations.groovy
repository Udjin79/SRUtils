/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package org.evisaenkov.atlassian.library

import com.atlassian.confluence.mail.ConfluenceMailServerManager
import com.atlassian.confluence.pages.Page
import com.atlassian.confluence.pages.PageManager
import com.atlassian.confluence.security.login.LoginInfo
import com.atlassian.confluence.security.login.LoginManager
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal
import com.atlassian.confluence.user.ConfluenceUser
import com.atlassian.confluence.user.UserAccessor
import com.atlassian.mail.Email
import com.atlassian.mail.server.SMTPMailServer
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.sal.api.user.UserKey
import com.atlassian.user.GroupManager
import com.atlassian.user.UserManager
import com.comalatech.workflow.StateService
import com.comalatech.workflow.model.State
import com.onresolve.scriptrunner.runner.ScriptRunnerImpl
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
import com.atlassian.confluence.spaces.SpaceManager
import com.atlassian.confluence.spaces.Space

import java.util.regex.Matcher

class ConfluenceOperations {
	
	PageManager pageManager = ComponentLocator.getComponent(PageManager)
	UserManager userManager = ComponentLocator.getComponent(UserManager)
	UserAccessor userAccessor = ComponentLocator.getComponent(UserAccessor)
	ConfluenceMailServerManager confluenceMailServerManager = ComponentLocator.getComponent(ConfluenceMailServerManager)
	SMTPMailServer mailServer = confluenceMailServerManager.getDefaultSMTPMailServer()
	GroupManager groupManager = ComponentLocator.getComponent(GroupManager)
	LoginManager loginManager = ComponentLocator.getComponent(LoginManager)
	SpaceManager spaceManager = ComponentLocator.getComponent(SpaceManager)
	
	Page getPageByID(Long pageId) {
		return pageManager.getPage(pageId)
	}
	
	Space getSpace(String spaceKey) {
		return spaceManager.getSpace(spaceKey)
	}
	
	List<Space> getAllSpaces() {
		return spaceManager.getAllSpaces()
	}
	
	Space getSpace(Long spaceId) {
		return spaceManager.getSpace(spaceId)
	}
	
	List<Page> getAllPagesInSpace(String spaceKey) {
		Space space = getSpace(spaceKey)
		return pageManager.getPages(space, true)
	}
	
	ConfluenceUser getUserByUserName(String username) {
		return userAccessor.getUserByName(username)
	}
	
	ConfluenceUser getUserByUserKey(String userKey) {
		return userAccessor.getUserByKey(new UserKey(userKey))
	}
	
	ConfluenceUser getCurrentUser() {
		return AuthenticatedUserThreadLocal.get()
	}
	
	Collection<String> getAllUserNames() {
		return userAccessor.getUserNamesWithConfluenceAccess()
	}
	
	LoginInfo getLoginInfoByUsername(String username) {
		ConfluenceUser user = getUserByUserName(username)
		return loginManager.getLoginInfo(user)
	}
	
	boolean isUserActive(ConfluenceUser user) {
		return user['backingUser'].getClass().toString().contains('EmbeddedCrowdUser')
	}
	
	List<String> getRegexGroups(String regex, String messageBody) {
		Matcher matcher
		List<String> groups = null
		if ((matcher = messageBody =~ /${regex}/)) {
			groups = matcher.collect { Matcher match ->
				match[1]
			}
		}
		return groups
	}
	
	String getPageComalaState(Long pageId) {
		@WithPlugin('com.comalatech.workflow')
		StateService stateService = ScriptRunnerImpl.getOsgiService(StateService)
		State state = stateService.getCurrentState(pageManager.getPage(pageId))
		String stateName = ''
		if (state) {
			stateName = state.getState()
		}
		return stateName
	}
	
	String getPageComalaStateChangeDate(Long pageId) {
		@WithPlugin('com.comalatech.workflow')
		StateService stateService = ScriptRunnerImpl.getOsgiService(StateService)
		State state = stateService.getCurrentState(pageManager.getPage(pageId))
		String stateName = ''
		if (state) {
			stateName = state.getDate()
		}
		return stateName
	}
	
	void updatePage(Page page) {
		pageManager.saveContentEntity(page, null, null)
	}
	
	void sendEmail(String emailAddr, String subject, String body) {
		Email email = new Email(emailAddr)
		email.setSubject(subject)
		email.setBody(body)
		email.setMimeType('text/html; charset=utf-8')
		mailServer.send(email)
	}
	
}
