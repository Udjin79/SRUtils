/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package org.evisaenkov.atlassian.library

import com.atlassian.jira.bc.issue.link.RemoteIssueLinkService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.link.RemoteIssueLink
import com.atlassian.jira.issue.link.RemoteIssueLinkBuilder
import com.atlassian.jira.issue.link.RemoteIssueLinkManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.applinks.api.ApplicationLink
import com.atlassian.applinks.api.ApplicationLinkRequest
import com.atlassian.applinks.api.ApplicationLinkRequestFactory
import com.atlassian.applinks.api.ApplicationLinkService
import com.atlassian.applinks.api.application.jira.JiraApplicationType
import com.atlassian.sal.api.net.Response
import com.atlassian.sal.api.net.ResponseException
import com.atlassian.sal.api.net.ResponseHandler
import groovy.json.JsonBuilder
import com.atlassian.sal.api.net.Request.MethodType
import org.evisaenkov.atlassian.library.UserOperations
import org.evisaenkov.atlassian.library.Variables

/**
 * Class for links operations with SR Jira
 * @author Evgeniy Isaenkov
 */
class Links {
	
	UserOperations userOperations = new UserOperations()
	
	CustomField getEpicLink(Issue issue) {
		return ComponentAccessor.customFieldManager.getCustomFieldObjects(issue).find { it.name == 'Epic Link' }
	}
	
	List getInwardLinks(Issue issue) {
		return ComponentAccessor.getIssueLinkManager().getInwardLinks(issue.id) as List
	}
	
	List getOutwardLinks(Issue issue) {
		return ComponentAccessor.getIssueLinkManager().getOutwardLinks(issue.id) as List
	}
	
	Set getAllLinkedIssues(Issue issue) {
		Set allLinkedIssue = [] as Set
		allLinkedIssue.addAll(getInwardIssues(issue))
		allLinkedIssue.addAll(getOutwardIssues(issue))
		return allLinkedIssue
	}
	
	List getInwardIssues(Issue issue) {
		return ComponentAccessor.getIssueLinkManager().getInwardLinks(issue.id)*.getSourceObject()
	}
	
	List getOutwardIssues(Issue issue) {
		return ComponentAccessor.getIssueLinkManager().getOutwardLinks(issue.id)*.getDestinationObject()
	}
	
	Set getAllLinkedIssues(Issue issue, long linkTypeId) {
		Set allLinkedIssue = [] as Set
		allLinkedIssue.addAll(getInwardIssues(issue, linkTypeId))
		allLinkedIssue.addAll(getOutwardIssues(issue, linkTypeId))
		return allLinkedIssue
	}
	
	List getInwardIssues(Issue issue, long linkTypeId) {
		return ComponentAccessor.getIssueLinkManager().getInwardLinks(issue.id).findAll {
			it.getLinkTypeId() == linkTypeId
		}*.getSourceObject()
	}
	
	List getOutwardIssues(Issue issue, long linkTypeId) {
		return ComponentAccessor.getIssueLinkManager().getOutwardLinks(issue.id).findAll {
			it.getLinkTypeId() == linkTypeId
		}*.getDestinationObject()
	}
	
	void linkIssues(Issue fromIssue, Issue toIssue, Long issueLinkTypeId, Long sequence, ApplicationUser user) {
		ComponentAccessor.issueLinkManager.createIssueLink(fromIssue.id, toIssue.id, issueLinkTypeId, sequence, user)
	}
	
	List getRemoteIssueLinks(Issue issue) {
		RemoteIssueLinkManager rilm = ComponentAccessor.getComponentOfType(RemoteIssueLinkManager)
		return rilm.getRemoteIssueLinksForIssue(issue)
	}
	
	/**
	 * Parameters for addRemoteIssueLink function
	 * @param issue - source issue
	 * @param remoteSystem - name of remote system, where remote issue/page is located. Variants: jirasm, jira, confluence
	 * @param requestBody - dictionary with request parameters
	 Map requestBody = [
	 'objectId' : remoteIssueId,
	 'summary' : remoteIssueSummary,
	 'relationship' : 'relates to',
	 'title' : remoteIssueKey,
	 'url' : "https://jira.local.domain/browse/${remoteIssueKey}"]
	 */
	void addRemoteIssueLink(Issue issue, String remoteSystem, Map requestBody, boolean reciprocal = true) {
		RemoteIssueLinkService remoteIssueLinkService = ComponentAccessor.getComponentOfType(RemoteIssueLinkService)
		ApplicationUser techUser = userOperations.getTechUser()
		Map systems = [
				'jirasm'    : [
						"appId=${Variables.JIRASM_GLOBAL_ID}&issueId=",
						"JiraSM",
						"com.atlassian.jira",
						"${Variables.JIRASM_URL}/browse/"
				],
				'jira'      : [
						"appId=${Variables.JIRA_GLOBAL_ID}&issueId=",
						"Jira",
						"com.atlassian.jira",
						"${Variables.JIRA_URL}/browse/"
				],
				'confluence': [
						"appId=${Variables.CONFLUENCE_GLOBAL_ID}&pageId=",
						"Confluence",
						"com.atlassian.confluence",
						"${Variables.CONFLUENCE_URL}/pages/viewpage.action?pageId="
				]
		]
		
		RemoteIssueLinkBuilder linkBuilder = new RemoteIssueLinkBuilder()
		linkBuilder.globalId(systems[remoteSystem][0].toString() + requestBody['objectId'])
		linkBuilder.issueId(issue.id)
		linkBuilder.summary(requestBody['summary'].toString())
		linkBuilder.applicationName(systems[remoteSystem][1].toString())
		linkBuilder.applicationType(systems[remoteSystem][2].toString())
		linkBuilder.relationship(requestBody['relationship'] ? requestBody['relationship'].toString() : 'relates to')
		linkBuilder.title(requestBody['title'].toString())
		linkBuilder.url(requestBody['url'].toString())
		RemoteIssueLink remoteIssueLink = linkBuilder.build()
		
		RemoteIssueLinkService.CreateValidationResult validationResult = remoteIssueLinkService.validateCreate(techUser, remoteIssueLink)
		if (validationResult.isValid()) {
			remoteIssueLinkService.create(techUser, validationResult)
		}
		
		if (reciprocal) {
			ApplicationUser currentUser = userOperations.getCurrentUser()
			userOperations.impersonateUser(techUser)
			try {
				ApplicationLinkService appLinkService = ComponentAccessor.getComponent(ApplicationLinkService)
				ApplicationLink appLink = appLinkService.getPrimaryApplicationLink(JiraApplicationType)
				ApplicationLinkRequestFactory applicationLinkRequestFactory = appLink.createAuthenticatedRequestFactory()
				
				List allSystems = ['JiraSM', 'Jira', 'Confluence']
				String currentSystem = (allSystems - appLinkService.getApplicationLinks().collect().name)[0].toLowerCase()
				
				String body = new JsonBuilder([
						application: [
								type: "${systems[currentSystem][2].toString()}",
								name: "${systems[currentSystem][1].toString()}"
						],
						object     : [
								url  : "${systems[currentSystem][3].toString()}",
								title: "${issue.key}"
						],
						globalId   : "${systems[currentSystem][0].toString()}${issue.id}"
				]).toString()
				
				ApplicationLinkRequest request = applicationLinkRequestFactory.createRequest(MethodType.POST, "/rest/api/2/issue/${requestBody['title'].toString()}/remotelink")
						.addHeader('Content-Type', 'application/json')
						.setEntity(body)
				request.execute(new ResponseHandler<Response>() {
					@Override
					void handle(Response response) throws ResponseException {
						if (response.statusCode > 300) {
							log.error("Error at the process of reciprocal linking of ${issue.key} and ${requestBody['title'].toString()}")
						}
					}
				})
			} catch (e) {
				log.warn(e.message)
			} finally {
				userOperations.impersonateUser(currentUser)
			}
		}
	}
	
	void removeIssueLink(long issueLinkId) {
		ApplicationUser techUser = userOperations.getTechUser()
		ComponentAccessor.getIssueLinkManager().with {
			IssueLink issueLink = getIssueLink(issueLinkId)
			if (issueLink) {
				removeIssueLink(issueLink, techUser)
			}
		}
	}
}
