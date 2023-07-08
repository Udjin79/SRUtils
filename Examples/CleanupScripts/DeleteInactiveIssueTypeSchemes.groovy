/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.CleanupScripts

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.fields.config.FieldConfigScheme
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager

IssueTypeSchemeManager issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager()
List<FieldConfigScheme> fieldConfigSchemes = issueTypeSchemeManager.getAllSchemes()
String report = ""

Integer i = 0

fieldConfigSchemes.each { FieldConfigScheme scheme ->
	
	if (scheme.getName() != "Default Issue Type Scheme" && scheme.getAssociatedProjectObjects().size() == 0) {
		report += "${scheme.getName()}<br/>"
		i++
		issueTypeSchemeManager.deleteScheme(scheme)
	}
}

return "Total: ${i}<br/>" + report