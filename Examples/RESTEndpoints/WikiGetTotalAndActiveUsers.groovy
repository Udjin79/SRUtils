/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.RESTEndpoints

import com.onresolve.scriptrunner.runner.rest.common.CustomEndpointDelegate
import groovy.json.JsonBuilder
import groovy.transform.BaseScript
import com.atlassian.confluence.user.ConfluenceUser
import org.evisaenkov.atlassian.library.ConfluenceOperations

import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response

ConfluenceOperations confluenceOperations = new ConfluenceOperations()

@BaseScript CustomEndpointDelegate delegate
WikiGetTotalAndActiveUsers(httpMethod: 'GET', groups: ['confluence-users']) { MultivaluedMap queryParams ->
	Collection usersList = confluenceOperations.getAllUserNames()
	Date outdatedDate = new Date() - 90
	Map rt = [:]
	
	Collection users = usersList.findAll { userName ->
		ConfluenceUser user = confluenceOperations.getUserByUserName(userName)
		confluenceOperations.isUserActive(user) && confluenceOperations.getLoginInfoByUsername(userName).getLastSuccessfulLoginDate() > outdatedDate
	}
	
	rt = [
			total : usersList.size(),
			active: users.size()
	]
	
	return Response.ok(new JsonBuilder(rt).toString()).build()
}
