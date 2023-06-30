/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Listeners

import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager

UserManager userManager = ComponentAccessor.getUserManager()
IssueManager issueManager = ComponentAccessor.getIssueManager()
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
IssueIndexingService issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)

MutableIssue issue = issueManager.getIssueObject(event.getIssue().getKey())

CustomField cField = customFieldManager.getCustomFieldObject("customfield_29500")
CustomField team = customFieldManager.getCustomFieldObject("customfield_28112")
CustomField epicLink = customFieldManager.getCustomFieldObject("Epic Link")

Map temp = issue.getCustomFieldValue(cField) as Map
String cFieldValue = temp.get(null).toString()

Map variables = [
		"COBOL/DBA/Mainframe Team": ["GOXFUNC-5611", "x-cteam", "COBOL/DBA/Mainframe Team"],
		"BASIS-Team"              : ["GOXFUNC-6635", "x-bteam", "Backdesk Team", 11201],
		"UCD Team"                : ["GOXFUNC-6488", "x-uteam", "UCD Team"],
		"Java Team"               : ["GOXFUNC-6633", "x-jteam", "Java Team"],
		"KM-Tools Team"           : ["GOXFUNC-6634", "x-kteam", "KM-Tools Team", 11201],
		"PL/Infra/KM"             : ["GOXFUNC-5435", "x-pteam", "Production Line Team"],
		"Others"                  : [null, "x-pteam", "Other"]
]

if (cFieldValue in variables.keySet()) {
	ApplicationUser assignee = userManager.getUserByName(variables[cFieldValue][1])
	issue.setAssignee(assignee)
	if (variables[cFieldValue][3]) {
		issue.setPriorityId(variables[cFieldValue][3])
	}
	issue.setCustomFieldValue(team, variables[cFieldValue][2])
	if (variables[cFieldValue][0]) {
		MutableIssue epicIssue = issueManager.getIssueObject(variables[cFieldValue][0])
		issue.setCustomFieldValue(epicLink, epicIssue)
	}
	ApplicationUser user = userManager.getUserByName("x-bteam")
	issueManager.updateIssue(user, issue, EventDispatchOption.DO_NOT_DISPATCH, false)
	issueIndexingService.reIndex(issue)
}
