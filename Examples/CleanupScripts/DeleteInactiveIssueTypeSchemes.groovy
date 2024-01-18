/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.CleanupScripts

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.fields.config.FieldConfigScheme
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager

// Obtaining the IssueTypeSchemeManager instance.
IssueTypeSchemeManager issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager()

// Retrieving all field configuration schemes available in the Jira instance.
List<FieldConfigScheme> fieldConfigSchemes = issueTypeSchemeManager.getAllSchemes()
String report = ""

// Counter for the number of schemes processed.
Integer i = 0

// Iterating through each field configuration scheme.
fieldConfigSchemes.each { FieldConfigScheme scheme ->
	
	// Checking if the scheme is not the default one and is not associated with any projects.
	if (scheme.getName() != "Default Issue Type Scheme" && scheme.getAssociatedProjectObjects().size() == 0) {
		// Appending the scheme name to the report.
		report += "${scheme.getName()}<br/>"
		
		// Incrementing the counter.
		i++
		
		// Deleting the scheme as it's unused.
		issueTypeSchemeManager.deleteScheme(scheme)
	}
}

// Returning a report containing the total number of deleted schemes and their names.
return "Total: ${i}<br/>" + report
