/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */
package org.evisaenkov.atlassian.library

import com.atlassian.applinks.api.ApplicationLink
import com.atlassian.applinks.api.ApplicationLinkRequest
import com.atlassian.applinks.api.ApplicationLinkRequestFactory
import com.atlassian.applinks.api.ApplicationLinkService
import com.atlassian.applinks.api.application.jira.JiraApplicationType
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.bc.project.component.ProjectComponent
import com.atlassian.jira.bc.project.component.ProjectComponentManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.ConstantsManager
import com.atlassian.jira.config.IssueTypeManager
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.issue.issuetype.IssueType
import com.atlassian.jira.issue.label.Label
import com.atlassian.jira.issue.search.SearchResults
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.query.Query
import com.atlassian.sal.api.net.Request.MethodType
import groovy.json.JsonSlurper
import org.evisaenkov.atlassian.library.UserOperations

import java.sql.Timestamp

/**
 * Class for issue operations with SR Jira
 * @author Evgeniy Isaenkov
 */

class IssueOperations {
	
	UserOperations userOperations = new UserOperations()
	ProjectManager projectManager = ComponentAccessor.getProjectManager()
	ProjectComponentManager projectComponentManager = ComponentAccessor.getProjectComponentManager()
	ConstantsManager constantsManager = ComponentAccessor.getConstantsManager()
	CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
	IssueManager issueManager = ComponentAccessor.getIssueManager()
	JqlQueryParser jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser) as JqlQueryParser
	SearchService searchService = ComponentAccessor.getComponent(SearchService)
	ApplicationUser techUser = userOperations.getTechUser()
	IssueTypeManager issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class)
	
	String makeIssue(Map issueBody, Map cfBody = [:]) {
		/**
		 * Example of issueBody
		 * ['project':'VMB',
		 * 'issueType':'Task',
		 * 'summary':'Summary Text',
		 * 'description':'Description Text',
		 * 'assignee':'evisaenkov',
		 * 'reporter':'evisaenkov']
		 * Example of cfBody
		 * [11123:'qweasd',
		 * 11321:'asdqwe']
		 */
		Collection components = []
		Project projectObject = projectManager.getProjectObjByKey(issueBody['project'] as String)
		if (issueBody['components']) {
			if (issueBody['components'] instanceof List) {
				issueBody['components'].each { String component ->
					components.add(projectComponentManager.findByComponentName(projectObject.getId(), component))
				}
			} else {
				components.add(projectComponentManager.findByComponentName(projectObject.getId(), issueBody['components'] as String))
			}
		}
		
		MutableIssue newIssue = ComponentAccessor.getIssueFactory().getIssue()
		newIssue.setProjectObject(projectObject)
		newIssue.setIssueType(constantsManager.allIssueTypeObjects.findByName(issueBody['issueType'] as String))
		newIssue.setSummary(issueBody['summary'] as String)
		newIssue.setDescription(issueBody.containsKey('description') ? issueBody['description'] as String : '')
		newIssue.setAssignee(issueBody.containsKey('assignee') ? userOperations.getUser(issueBody['assignee'] as String) : null)
		newIssue.setReporter(userOperations.getUser(issueBody['reporter'] as String))
		newIssue.setLabels(issueBody.containsKey('labels') ? issueBody['labels'] as Set : null)
		newIssue.setComponent(components.size() > 0 ? components : null)
		newIssue.setPriorityId(issueBody.containsKey('priority') ? (String) issueBody['priority'] : '3')
		newIssue.setDueDate(issueBody.containsKey('duedate') ? issueBody['duedate'] as Timestamp : null)
		ComponentAccessor.issueManager.createIssueObject(techUser, newIssue)
		
		if (!cfBody.isEmpty()) {
			cfBody.each { key, value ->
				CustomField tmpCF = customFieldManager.getCustomFieldObject((Long) key)
				newIssue.setCustomFieldValue(tmpCF, cfBody[key])
			}
			issueManager.updateIssue(techUser, newIssue, EventDispatchOption.DO_NOT_DISPATCH, false)
		}
		
		return newIssue.key
	}
	
	String makeSubIssue(String parentIssueKey, Map issueBody, Map cfBody = [:]) {
		String subIssueKey = makeIssue(issueBody, cfBody)
		Issue subIssue = getIssue(subIssueKey)
		Issue parentIssue = getIssue(parentIssueKey)
		ComponentAccessor.subTaskManager.createSubTaskIssueLink(parentIssue, subIssue, techUser)
		
		return subIssueKey
	}
	
	void updateIssue(MutableIssue issue, Boolean sendMail = false) {
		issueManager.updateIssue(techUser, issue, EventDispatchOption.DO_NOT_DISPATCH, sendMail)
		reindexIssue(issue)
	}
	
	void reindexIssue(Issue issue) {
		IssueIndexingService issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
		issueIndexingService.reIndex(issue)
	}
	
	MutableIssue getIssue(Long id) {
		return ComponentAccessor.getIssueManager().getIssueObject(id) as MutableIssue
	}
	
	MutableIssue getIssue(String issueKey) {
		return ComponentAccessor.getIssueManager().getIssueObject(issueKey) as MutableIssue
	}
	
	Map getRemoteIssueData(String issueKey) {
		JsonSlurper jsonSlurper = new JsonSlurper()
		ApplicationLinkService appLinkService = ComponentAccessor.getComponent(ApplicationLinkService)
		ApplicationLink appLink = appLinkService.getPrimaryApplicationLink(JiraApplicationType)
		ApplicationLinkRequestFactory applicationLinkRequestFactory = appLink.createAuthenticatedRequestFactory()
		ApplicationLinkRequest request = applicationLinkRequestFactory.createRequest(MethodType.GET, "/rest/api/2/issue/${issueKey}")
		String response = request.execute()
		return jsonSlurper.parseText(response) as Map
	}
	
	List<MutableIssue> getIssuesByJQL(String jql) {
		Query query = jqlQueryParser.parseQuery(jql)
		SearchResults search = searchService.search(techUser, query, PagerFilter.getUnlimitedFilter())
		return search.results.collect { Issue issue ->
			getIssue(issue.key as String)
		}
	}
	
	Set<Label> getLabels(Issue issue) {
		return issue.getLabels()
	}
	
	Collection<ProjectComponent> getComponents(Issue issue) {
		return issue.getComponents()
	}
	
	Collection<IssueType> getAllIssueTypes() {
		Collection<IssueType> allIssueTypes = ComponentAccessor.constantsManager.allIssueTypeObjects
		return allIssueTypes
	}
	
	IssueType getIssueTypeById(String issueTypeId) {
		IssueType issueType = (IssueType) issueTypeManager.getIssueTypes().find {
			it.getId() == issueTypeId
		}
		return issueType
	}
	
	IssueType getIssueTypeByName(String issueTypeName) {
		IssueType issueType = (IssueType) issueTypeManager.getIssueTypes().find {
			it.getName().equalsIgnoreCase(issueTypeName)
		}
		return issueType
	}
	
	MutableIssue getIssueEpic(MutableIssue issue) {
		if (issue.issueType.name != "Epic" && issue.getCustomFieldValue(customFieldManager.getCustomFieldObjectsByName('Epic Link').first())) {
			return issue.getCustomFieldValue(customFieldManager.getCustomFieldObjectsByName('Epic Link').first()) as MutableIssue
		}
		return null
	}
	
	MutableIssue getParentIssue(Issue subTask) {
		return getIssue(subTask.getParentObject().key)
	}
}
