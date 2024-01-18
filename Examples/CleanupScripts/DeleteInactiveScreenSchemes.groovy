/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.CleanupScripts

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.fields.screen.FieldScreenScheme
import com.atlassian.jira.issue.fields.screen.FieldScreenSchemeManager
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenSchemeManager

// Get the FieldScreenSchemeManager and IssueTypeScreenSchemeManager instances
FieldScreenSchemeManager fieldScreenSchemeManager = ComponentAccessor.getComponent(FieldScreenSchemeManager.class)
IssueTypeScreenSchemeManager issueTypeScreenSchemeManager = ComponentAccessor.getIssueTypeScreenSchemeManager()

// Get a collection of all FieldScreenSchemes
Collection<FieldScreenScheme> fieldScreenSchemes = fieldScreenSchemeManager.getFieldScreenSchemes()

// Initialize a report string and a counter variable
String report = ""
Integer i = 0

// Loop through each FieldScreenScheme
fieldScreenSchemes.each { FieldScreenScheme fieldScreenScheme ->
	// Check if there are no IssueTypeScreenSchemes associated with the FieldScreenScheme
	if (issueTypeScreenSchemeManager.getIssueTypeScreenSchemes(fieldScreenScheme).size() == 0) {
		// Add the FieldScreenScheme name to the report and remove it
		report += "${fieldScreenScheme.getName()}<br/>"
		fieldScreenSchemeManager.removeFieldSchemeItems(fieldScreenScheme)
		fieldScreenSchemeManager.removeFieldScreenScheme(fieldScreenScheme)
		i++
	}
}

// Return the total count of removed FieldScreenSchemes and the report
return "Total: ${i}<br/>" + report
