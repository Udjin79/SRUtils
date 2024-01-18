/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 *
 * This script is part of the Examples.Behaviours package in the SRUtils project. Its primary
 * functionality is to control the availability of specific issue types in JIRA based on the
 * user's group membership. It utilizes the JIRA Software Server API and ScriptRunner for custom
 * field behavior. The script identifies users in specific groups ('Lawyers' and 'Top') and
 * grants them access to a wider range of issue types, while restricting others to a default set.
 */

package Examples.Behaviours

import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.issuetype.IssueType
import com.atlassian.jira.user.ApplicationUser
import com.onresolve.jira.groovy.user.FieldBehaviours
import com.onresolve.jira.groovy.user.FormField
import groovy.transform.BaseScript

import static com.atlassian.jira.issue.IssueFieldConstants.ISSUE_TYPE

@BaseScript FieldBehaviours fieldBehaviours; // Base script annotation for field behaviors.

// Retrieve all available issue types.
Collection<IssueType> allIssueTypes = ComponentAccessor.constantsManager.allIssueTypeObjects;

// Access the issue type field.
FormField issueTypeField = getFieldById(ISSUE_TYPE);

// Initialize a list to store available issue types.
ArrayList<IssueType> availableIssueTypes = new ArrayList<>();

// Get the currently logged-in user.
ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();

// Access groups 'Lawyers' and 'Top'.
Group lawyers = ComponentAccessor.groupManager.getGroup("Lawyers");
Group tops = ComponentAccessor.groupManager.getGroup("Top");

// Create a list to store usernames in the 'Lawyers' and 'Top' groups.
List<String> whiteList = new ArrayList<>();
whiteList.addAll(ComponentAccessor.groupManager.getUserNamesInGroup(tops));
whiteList.addAll(ComponentAccessor.groupManager.getUserNamesInGroup(lawyers));

// Check if the current user is in the whitelist.
if (whiteList.contains(user.getName())) {
	// If the user is in the whitelist, allow access to specific issue types.
	availableIssueTypes.addAll(allIssueTypes.findAll { it.name in ['Task', 'Epic', 'Classified Task'] });
} else {
	// If the user is not in the whitelist, restrict to 'Task' issue type only.
	availableIssueTypes.addAll(allIssueTypes.findAll { it.name in ['Task'] });
}

// Set the available issue types as field options.
issueTypeField.setFieldOptions(availableIssueTypes);
