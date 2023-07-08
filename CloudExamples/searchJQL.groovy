/*
 * Created 2023.
 * @author ScriptRunner for Jira on Cloud
 * @github https://github.com/Udjin79/SRUtils
 */

package CloudExamples

//ScriptRunner for Jira on Cloud - fetch issue keys based on a jql
def resp = get("/rest/api/3/search?jql=project=CT")
		.header("Content-Type", "application/json")
		.asObject(Map)

'${resp.status}: ' + resp.body.issues*.key
