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
import com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.RendererManager
import com.atlassian.jira.issue.fields.renderer.JiraRendererPlugin
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.sal.api.net.Request
import com.atlassian.sal.api.net.Response
import com.atlassian.sal.api.net.ResponseException
import com.atlassian.sal.api.net.ResponseHandler
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

import java.text.SimpleDateFormat
import java.util.regex.Pattern


class UnclassifiedOperations {
	UserOperations userOperations = new UserOperations()
	
	String timestampFormat(Object timestamp, String formatPattern = 'dd-MM-yyyy HH:mm', String locale = getLocale().split('_').first()) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatPattern, new Locale(locale))
		return dateFormat.format(timestamp)
	}
	
	Date stringToDate(String dateString, String formatPattern = 'dd-MM-yyyy HH:mm') {
		Date timestamp = Date.parse(formatPattern, dateString)
		return timestamp
	}
	
	String getLocale(String username = null) {
		ApplicationUser user = username ? userOperations.getUser(username) : userOperations.getCurrentUser()
		return ComponentAccessor.localeManager.getLocaleFor(user)
	}
	
	String getUrlJSON(String url) {
		def jsonSlurper = new JsonSlurper()
		return jsonSlurper.parseText(url.toURL().text)
	}
	
	boolean isValidRegex(String value, String regex) {
		Pattern pattern = Pattern.compile(regex)
		return pattern.matcher(value).matches()
	}
	
	List getVariablesListFromWikiPage(Long pageId, Integer columns = 1, boolean htmlFormat = false) {
		ApplicationLinkService applicationLinkService = ComponentAccessor.getComponent(ApplicationLinkService)
		ApplicationLink confLink = applicationLinkService.getApplicationLinks(ConfluenceApplicationType)?.first()
		ApplicationLinkRequestFactory authenticatedRequestFactory = confLink.createAuthenticatedRequestFactory()
		Integer i = 0
		while (i < 10) {
			try {
				String requestUrl = "rest/api/content/${pageId}?expand=body.storage"
				ApplicationLinkRequest request = authenticatedRequestFactory
						.createRequest(Request.MethodType.GET, requestUrl)
						.addHeader('Content-Type', 'application/json')
				String myResult = request.execute()
				Map page = new JsonSlurper().parseText(myResult) as Map
				String html = page['body']['storage']['value']
				Document doc = Jsoup.parse(html)
				Element table = doc.select('table').first()
				List<String> rows = htmlFormat ? table.select('td').iterator()*.html() as List : table.select('td').iterator()*.text() as List
				if (htmlFormat) {
					rows = rows.collect { String row ->
						row.replace('<br>', '\n').replace('</p>', '\n').replaceAll("<[^>]*>", "")
					}
				}
				i = 10
				return rows.collate(columns)
			} catch (e) {
				i++
			}
		}
	}
	
	Map getWikiPageData(Long pageId) {
		ApplicationUser currentUser = userOperations.getCurrentUser()
		userOperations.impersonateUser(userOperations.getTechUser())
		ApplicationLinkService applicationLinkService = ComponentAccessor.getComponent(ApplicationLinkService)
		ApplicationLink confLink = applicationLinkService.getApplicationLinks(ConfluenceApplicationType)?.first()
		ApplicationLinkRequestFactory authenticatedRequestFactory = confLink.createAuthenticatedRequestFactory()
		
		String requestUrl = "rest/api/content/${pageId}"
		ApplicationLinkRequest request = authenticatedRequestFactory
				.createRequest(Request.MethodType.GET, requestUrl)
				.addHeader('Content-Type', 'application/json')
		String myResult = request.execute()
		Map page = new JsonSlurper().parseText(myResult) as Map
		userOperations.impersonateUser(currentUser)
		return page
	}
	
	void setWikiPageData(Long pageId, String pageBody) {
		ApplicationUser currentUser = userOperations.getCurrentUser()
		userOperations.impersonateUser(userOperations.getTechUser())
		ApplicationLinkService applicationLinkService = ComponentAccessor.getComponent(ApplicationLinkService)
		ApplicationLink confLink = applicationLinkService.getApplicationLinks(ConfluenceApplicationType)?.first()
		ApplicationLinkRequestFactory authenticatedRequestFactory = confLink.createAuthenticatedRequestFactory()
		
		Map pageData = getWikiPageData(pageId) as Map
		LinkedHashMap paramsBody = [
				type   : "page",
				title  : pageData['title'],
				version: [
						number: ((Integer) pageData['version']['number']) + 1
				],
				space  : [
						key: pageData['space']['key']
				],
				body   : [
						storage: [
								value         : pageBody,
								representation: "storage"
						],
				],
		]
		
		String requestUrl = "rest/api/content/${pageId}"
		authenticatedRequestFactory
				.createRequest(Request.MethodType.PUT, requestUrl)
				.addHeader('Content-Type', 'application/json')
				.setRequestBody(new JsonBuilder(paramsBody).toPrettyString())
				.execute(new ResponseHandler<Response>() {
					@Override
					void handle(Response response) throws ResponseException {
						if (response.statusCode != HttpURLConnection.HTTP_OK) {
							throw new Exception(response.getResponseBodyAsString())
						}
					}
				})
		userOperations.impersonateUser(currentUser)
	}
	
	Map createWikiPage(String spaceKey, Integer pageId, String pageTitle, String pageBody) {
		ApplicationUser currentUser = userOperations.getCurrentUser()
		userOperations.impersonateUser(userOperations.getTechUser())
		ApplicationLinkService applicationLinkService = ComponentAccessor.getComponent(ApplicationLinkService)
		ApplicationLink confLink = applicationLinkService.getApplicationLinks(ConfluenceApplicationType)?.first()
		ApplicationLinkRequestFactory authenticatedRequestFactory = confLink.createAuthenticatedRequestFactory()
		
		
		LinkedHashMap paramsBody = [
				type     : "page",
				title    : pageTitle,
				ancestors: [
						[
								"id": pageId
						]
				],
				space    : [
						key: spaceKey
				],
				body     : [
						storage: [
								value         : pageBody,
								representation: "storage"
						]
				]
		]
		Map result
		String requestUrl = "rest/api/content/"
		authenticatedRequestFactory
				.createRequest(Request.MethodType.POST, requestUrl)
				.addHeader('Content-Type', 'application/json')
				.setRequestBody(new JsonBuilder(paramsBody).toPrettyString())
				.execute(new ResponseHandler<Response>() {
					@Override
					void handle(Response response) throws ResponseException {
						result = new JsonSlurper().parseText(response.responseBodyAsString) as Map
						if (response.statusCode != HttpURLConnection.HTTP_OK) {
							throw new Exception(response.getResponseBodyAsString())
						}
					}
				})
		userOperations.impersonateUser(currentUser)
		return result
	}
	
	String renderWikiMarkup(MutableIssue issue, String data) {
		RendererManager rendererManager = ComponentAccessor.rendererManager
		JiraRendererPlugin renderer = rendererManager.getRendererForType("atlassian-wiki-renderer")
		String output = renderer.render(data, issue.getIssueRenderContext())
		return output
	}
}