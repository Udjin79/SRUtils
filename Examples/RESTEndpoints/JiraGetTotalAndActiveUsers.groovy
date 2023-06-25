/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.RESTEndpoints

import com.atlassian.jira.user.ApplicationUser
import com.onresolve.scriptrunner.runner.rest.common.CustomEndpointDelegate
import groovy.json.JsonBuilder
import groovy.transform.BaseScript
import org.evisaenkov.atlassian.library.UserOperations

import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response

UserOperations userOperations = new UserOperations()

@BaseScript CustomEndpointDelegate delegate
JiraGetTotalAndActiveUsers(httpMethod: 'GET', groups: ['jira-users']) { MultivaluedMap queryParams ->
	Collection usersList = userOperations.getAllActiveUsers()
	Date outdatedDate = new Date() - 90
	Map rt = [:]
	
	Collection users = usersList.findAll { ApplicationUser user ->
		userOperations.getLoginInfoByUsername(user.name).getLastLoginTime() > outdatedDate.getTime()
	}
	
	rt = [
			total : usersList.size(),
			active: users.size()
	]
	
	return Response.ok(new JsonBuilder(rt).toString()).build()
}
