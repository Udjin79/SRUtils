/*
 * Created 2024.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package CloudExamples.SR

String[] userList = [
		"user1@example.com",
		"user2@example.com",
		"user3@example.com",
		"user4@example.com",
		"user5@example.com"
]

String[] groups = ["TestGroup"]

userList.each() { String user ->
	def result2 = get('/rest/api/3/user/search?query=' + user)
			.header('Content-Type', 'application/json')
			.asObject(List)
	
	assert result2.status == 200
	
	logger.info(user + " result2: " + result2.body)
	result2.body.each() { userId ->
		String accountId = userId.accountId
		logger.info("accountId " + accountId)
		groups.each() { groupname ->
			String groupsUrl = "/rest/api/3/group/user?groupname=${groupname}"
			logger.info('url: ' + groupname + ' = ' + groupsUrl)
			
			def result3 = post(groupsUrl)
					.header('Content-Type', 'application/json')
					.body([
							accountId: accountId
					])
					.asString()
			
			assert result2.status == 200
			
		}
	}
}
