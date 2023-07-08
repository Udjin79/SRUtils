/*
 * Created 2023.
 * @author ScriptRunner for Jira on Cloud
 * @github https://github.com/Udjin79/SRUtils
 */

package CloudExamples

def response = get('/rest/api/3/issue/cts-12')
		.header('Content-Type', 'application/json')
		.asObject(Map)

logger.info("Issue id: " + response.body.id)
logger.info("Priority name: " + response.body.fields.priority.name)

return response.body
