/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.CleanupScripts

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenSchemeManager

// Getting the IssueTypeScreenSchemeManager instance to manage issue type screen schemes.
IssueTypeScreenSchemeManager issueTypeScreenSchemeManager = ComponentAccessor.getIssueTypeScreenSchemeManager()
String report = ""
Integer i = 0

// Retrieving all issue type screen schemes available in the system.
Collection<IssueTypeScreenScheme> schemes = issueTypeScreenSchemeManager.getIssueTypeScreenSchemes()

// Iterating through each issue type screen scheme.
schemes.each { IssueTypeScreenScheme scheme ->
	// Checking if the scheme is not the default one and is not associated with any project.
	if (scheme.getName() != "Default Issue Type Screen Scheme" && issueTypeScreenSchemeManager.getProjects(scheme).size() == 0) {
		// Appending the scheme name to the report for tracking.
		report += "${scheme.getName()}<br/>"
		
		// Removing all scheme entities and the scheme itself as it's no longer needed.
		issueTypeScreenSchemeManager.removeIssueTypeSchemeEntities(scheme)
		issueTypeScreenSchemeManager.removeIssueTypeScreenScheme(scheme)
		
		// Incrementing the counter for each removed scheme.
		i++
	}
}

// Returning a report with the total count of removed schemes and their names.
return "Total: ${i}<br/>" + report
