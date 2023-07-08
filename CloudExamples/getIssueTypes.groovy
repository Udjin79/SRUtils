/*
 * Created 2023.
 * @author ScriptRunner for Jira on Cloud
 * @github https://github.com/Udjin79/SRUtils
 */

package CloudExamples

def responseIssueTypes = get("/rest/api/3/issuetype").asObject(List)
def issueTypes = responseIssueTypes.body as List<Map>

//Get only standard issue type with name "Task"
return issueTypes.findAll { it.name == "Task" && !it.subtask }
