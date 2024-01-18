/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.CleanupScripts

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.scheme.Scheme
import com.atlassian.jira.workflow.WorkflowSchemeManager

// Get the WorkflowSchemeManager component
WorkflowSchemeManager schemeManager = ComponentAccessor.workflowSchemeManager

// Initialize report strings, an errors string, and a counter variable
String report = ""
String errors = ""
Integer i = 0

// Iterate through each scheme
schemeManager.schemeObjects.each { Scheme scheme ->
	try {
		// Check if the scheme is not used by any projects
		if (schemeManager.getProjectsUsing(schemeManager.getWorkflowSchemeObj(scheme.id)).size() == 0) {
			// Add the scheme name to the report and delete the scheme
			report += "${scheme.getName()}<br/>"
			schemeManager.deleteScheme(scheme.id)
			i++
		}
	}
	catch (Exception e) {
		// Handle any errors that occur during the process and add them to the errors string
		errors += ("Error: ${e.message}<br/>")
	}
}

// Return the total count of deleted schemes, the report, and any errors
return "Total: ${i}<br/>" + report + "<br/>" + errors
