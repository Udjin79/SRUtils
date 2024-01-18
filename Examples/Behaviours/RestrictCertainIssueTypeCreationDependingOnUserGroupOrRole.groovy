/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 *
 * This script is part of the Examples.Behaviours package in the SRUtils project.
 * It is designed for JIRA and implements custom behavior for issue type fields
 * based on the user's role within a project. It uses the JIRA Software Server API and ScriptRunner.
 * The primary functionality involves restricting the available issue types for users
 * based on their roles. For instance, users with the 'Lawyers' role have access to a broader
 * range of issue types compared to other users.
 *
 */

package Examples.Behaviours

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.issuetype.IssueType
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.user.ApplicationUser
import com.onresolve.jira.groovy.user.FieldBehaviours
import com.onresolve.jira.groovy.user.FormField
import groovy.transform.BaseScript

import static com.atlassian.jira.issue.IssueFieldConstants.ISSUE_TYPE

@BaseScript FieldBehaviours fieldBehaviours; // Annotation to define the base script for field behaviours.
ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager); // Accessing the ProjectRoleManager.
Collection<IssueType> allIssueTypes = ComponentAccessor.constantsManager.allIssueTypeObjects; // Retrieving all issue types.

ApplicationUser user = ComponentAccessor.jiraAuthenticationContext.getLoggedInUser(); // Getting the currently logged-in user.
FormField issueTypeField = getFieldById(ISSUE_TYPE); // Getting the issue type field.
ArrayList<IssueType> availableIssueTypes = new ArrayList<>(); // Creating a list to store available issue types.

// Retrieving the roles of the current user in the context of the current project.
ArrayList<String> remoteUsersRoles = new ArrayList<>(projectRoleManager.getProjectRoles(user, issueContext.projectObject)*.name);

// Conditional logic to determine available issue types based on user role.
if (remoteUsersRoles.contains("Lawyers")) {
	// For users with the 'Lawyers' role, add specific issue types to the available list.
	availableIssueTypes.addAll(allIssueTypes.findAll { it.name in ['Task', 'Experiment', 'Classified Tasks'] });
} else {
	// For other users, restrict the available issue types to 'Task' only.
	availableIssueTypes.addAll(allIssueTypes.findAll { it.name in ['Task'] });
}

// Setting the options for the issue type field based on the available issue types.
issueTypeField.setFieldOptions(availableIssueTypes);
