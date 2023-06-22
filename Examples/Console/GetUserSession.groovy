/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

import com.atlassian.applinks.api.ApplicationLink
import com.atlassian.applinks.api.ApplicationLinkRequest
import com.atlassian.applinks.api.ApplicationLinkRequestFactory
import com.atlassian.applinks.api.ApplicationLinkResponseHandler
import com.atlassian.applinks.api.ApplicationLinkService
import com.atlassian.applinks.api.application.jira.JiraApplicationType
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.sal.api.net.Response
import com.atlassian.sal.api.net.ResponseException
import groovy.json.JsonSlurper

import static com.atlassian.sal.api.net.Request.MethodType.GET

ApplicationLinkService appLinkService = ComponentLocator.getComponent(ApplicationLinkService)
ApplicationLink appLink = appLinkService.getPrimaryApplicationLink(JiraApplicationType)

ApplicationLinkRequestFactory applicationLinkRequestFactory = appLink.createAuthenticatedRequestFactory()
ApplicationLinkRequest request = applicationLinkRequestFactory.createRequest(GET, "/rest/auth/1/session")

def handler = new ApplicationLinkResponseHandler<Map>() {
	@Override
	Map credentialsRequired(Response response) throws ResponseException {
		return null
	}
	
	@Override
	Map handle(Response response) throws ResponseException {
		assert response.statusCode == 200
		new JsonSlurper().parseText(response.getResponseBodyAsString()) as Map
	}
}

Map sessionDetails = request.execute(handler)
log.debug("Making the request as: " + sessionDetails)