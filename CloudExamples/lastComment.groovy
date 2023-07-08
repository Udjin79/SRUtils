/*
 * Created 2023.
 * @author ScriptRunner for Jira on Cloud
 * @github https://github.com/Udjin79/SRUtils
 */

package CloudExamples

def issueKey = issue.key

def resp = get("/rest/api/3/issue/${issueKey}/comment")
		.header("Content-Type", "application/json")
		.asObject(Map)

return resp.body.comments ? resp.body.comments.last().body.content.content.text[0][0] : "No comments"
