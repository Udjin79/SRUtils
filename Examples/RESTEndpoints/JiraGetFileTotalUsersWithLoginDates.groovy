/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.RESTEndpoints


import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.util.JiraHome
import com.atlassian.jira.user.ApplicationUser
import com.onresolve.scriptrunner.runner.rest.common.CustomEndpointDelegate
import groovy.json.JsonBuilder
import groovy.transform.BaseScript
import org.evisaenkov.atlassian.library.UserOperations
import org.evisaenkov.atlassian.library.Variables

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response
import java.text.SimpleDateFormat

UserOperations userOperations = new UserOperations()
String tmpDir = Variables.JIRASM_TMP_DIR

@BaseScript CustomEndpointDelegate delegate
JiraGetFileTotalUsersWithLoginDates(httpMethod: 'GET', groups: ['jira-users']) { MultivaluedMap queryParams ->
	Integer query = queryParams.getFirst('query') as Integer
	String format = queryParams.getFirst('format') as String
	Collection usersList = userOperations.getAllActiveUsers()
	Collection users
	SimpleDateFormat csvDateFormat = new SimpleDateFormat('dd.MM.yyyy HH:mm', new Locale('ru'))
	JiraHome jiraHome = ComponentAccessor.getComponent(JiraHome)
	
	Map mapcsvLine = [:]
	Map rt = [:]
	String csvReturn = ''
	
	query = query == null ? 0 : query
	format = format == null ? 'file' : format
	Date outdatedDate = new Date() - query
	
	users = usersList.findAll { ApplicationUser user ->
		userOperations.getLoginInfoByUsername(user.name).getLastLoginTime() < outdatedDate.getTime()
	}
	
	int i = 0
	for (user in users) {
		List csvLine = []
		csvLine.add(user.name)
		csvLine.add(user.emailAddress)
		def lastLoginTime = userOperations.getLoginInfoByUsername(user.name).getLastLoginTime()
		if (lastLoginTime == null) {
			csvLine.add('null')
		} else {
			csvLine.add(csvDateFormat.format(new Date(lastLoginTime)))
		}
		if (csvLine.size() > 0) {
			mapcsvLine.put(i, csvLine)
		}
		i++
	}
	
	if (format == 'file') {
		csvReturn += 'username;email;lastlogindate\n'
		for (Map.Entry<Integer, List> entry : mapcsvLine.entrySet()) {
			csvReturn += entry.getValue()[0].toString() + ';'
			csvReturn += entry.getValue()[1].toString() + ';'
			csvReturn += entry.getValue()[2].toString()
			csvReturn += '\n'
		}
		
		Date datecsv = new Date()
		SimpleDateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd_HH-mm-ss', new Locale('ru'))
		File csvFile = new File(jiraHome.home, tmpDir + "jira_users_${dateFormat.format(datecsv)}.csv")
		csvFile.append(csvReturn)
		Response.ResponseBuilder response = Response.ok(csvReturn)
		response.header("Content-Disposition", "attachment;filename=" + csvFile.name)
		response.type(MediaType.APPLICATION_OCTET_STREAM_TYPE)
		csvFile.delete()
		return response.build()
	} else if (format == 'json') {
		rt = [
				users: mapcsvLine.collect { entry ->
					[
							login: entry.getValue()[0].toString(),
							email: entry.getValue()[1].toString(),
							date : entry.getValue()[2].toString()
					]
				},
				total: mapcsvLine.size()
		]
		
		return Response.ok(new JsonBuilder(rt).toString()).build()
	}
}
