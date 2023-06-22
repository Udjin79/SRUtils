/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Behaviours

// Limit issue types, depending from user Role in project
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.IssueFieldConstants.ISSUE_TYPE
import com.atlassian.jira.issue.issuetype.IssueType
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.user.ApplicationUser
import com.onresolve.jira.groovy.user.FieldBehaviours
import com.onresolve.jira.groovy.user.FormField
import groovy.transform.BaseScript

@BaseScript FieldBehaviours fieldBehaviours
ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
Collection<IssueType> allIssueTypes = ComponentAccessor.constantsManager.allIssueTypeObjects

ApplicationUser user = ComponentAccessor.jiraAuthenticationContext.getLoggedInUser()
FormField issueTypeField = getFieldById(ISSUE_TYPE)
ArrayList availableIssueTypes = []

//Check Role
ArrayList<String> remoteUsersRoles = projectRoleManager.getProjectRoles(user, issueContext.projectObject)*.name

if ('Lawers' in remoteUsersRoles) {
	availableIssueTypes.addAll(allIssueTypes.findAll { it.name in ['Task', 'Experiment', 'Classified Tasks'] })
} else {
	availableIssueTypes.addAll(allIssueTypes.findAll { it.name in ['Task'] })
}

issueTypeField.setFieldOptions(availableIssueTypes)
