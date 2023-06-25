/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.RESTEndpoints

import com.atlassian.confluence.user.ConfluenceUser
import com.onresolve.scriptrunner.runner.rest.common.CustomEndpointDelegate
import groovy.json.JsonBuilder
import groovy.transform.BaseScript
import org.evisaenkov.atlassian.library.ConfluenceOperations
import org.evisaenkov.atlassian.library.Variables

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response
import java.text.SimpleDateFormat

ConfluenceOperations confluenceOperations = new ConfluenceOperations()
String tmpDir = Variables.CONFLUENCE_TMP_DIR


@BaseScript CustomEndpointDelegate delegate
WikiGetFileTotalUsersWithLoginDates(httpMethod: 'GET', groups: ['jira-users']) { MultivaluedMap queryParams ->
	Integer query = queryParams.getFirst('query') as Integer
	String format = queryParams.getFirst('format') as String
	Collection usersList = confluenceOperations.getAllUserNames()
	Collection users
	SimpleDateFormat csvDateFormat = new SimpleDateFormat('dd.MM.yyyy HH:mm', new Locale('ru'))
	
	Map mapcsvLine = [:]
	Map rt = [:]
	String csvReturn = ''
	
	query = query == null ? 0 : query
	format = format == null ? 'file' : format
	Date outdatedDate = new Date() - query
	
	users = usersList.findAll { String userName ->
		ConfluenceUser user = confluenceOperations.getUserByUserName(userName)
		confluenceOperations.isUserActive(user) && confluenceOperations.getLoginInfoByUsername(userName).getLastSuccessfulLoginDate() < outdatedDate
	}
	
	int i = 0
	for (username in users) {
		ConfluenceUser user = confluenceOperations.getUserByUserName(username)
		List csvLine = []
		csvLine.add(username)
		csvLine.add(user.email)
		def lastLoginTime = confluenceOperations.getLoginInfoByUsername(username).getLastSuccessfulLoginDate()
		if (lastLoginTime == null) {
			csvLine.add('null')
		} else {
			csvLine.add(csvDateFormat.format(lastLoginTime))
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
		File csvFile = new File(tmpDir + "wiki_users_${dateFormat.format(datecsv)}.csv")
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
