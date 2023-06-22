/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Behaviours

import com.atlassian.crowd.embedded.api.Group

/* Запрет на создание типов задач в зависимости от группы */

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.issuetype.IssueType
import com.atlassian.jira.user.ApplicationUser
import com.onresolve.jira.groovy.user.FieldBehaviours
import com.onresolve.jira.groovy.user.FormField
import groovy.transform.BaseScript

import static com.atlassian.jira.issue.IssueFieldConstants.ISSUE_TYPE

@BaseScript FieldBehaviours fieldBehaviours
Collection<IssueType> allIssueTypes = ComponentAccessor.constantsManager.allIssueTypeObjects

FormField issueTypeField = getFieldById(ISSUE_TYPE)
ArrayList availableIssueTypes = []

ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

Group lawers = ComponentAccessor.groupManager.getGroup('Lawers')
Group tops = ComponentAccessor.groupManager.getGroup('Top')

List<String> whiteList = []

whiteList.addAll(ComponentAccessor.groupManager.getUserNamesInGroup(tops))
whiteList.addAll(ComponentAccessor.groupManager.getUserNamesInGroup(lawers))

if (whiteList.unique().contains(user.getName())) {
	availableIssueTypes.addAll(allIssueTypes.findAll { it.name in ['Task', 'Epic', 'Classified Task'] })
} else {
	availableIssueTypes.addAll(allIssueTypes.findAll { it.name in ['Task'] })
}

issueTypeField.setFieldOptions(availableIssueTypes)
