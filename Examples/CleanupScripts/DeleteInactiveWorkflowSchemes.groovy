/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.CleanupScripts

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.scheme.Scheme
import com.atlassian.jira.workflow.WorkflowSchemeManager

WorkflowSchemeManager schemeManager = ComponentAccessor.workflowSchemeManager
String report = ""
String errors = ""
Integer i = 0

schemeManager.schemeObjects.each { Scheme scheme ->
	try {
		if (schemeManager.getProjectsUsing(schemeManager.getWorkflowSchemeObj(scheme.id)).size() == 0) {
			report += "${scheme.getName()}<br/>"
			schemeManager.deleteScheme(scheme.id)
			i++
		}
	}
	catch (Exception e) {
		errors += ("Error: ${e.message}<br/>")
	}
}

return "Total: ${i}<br/>" + report + "<br/>" + errors
