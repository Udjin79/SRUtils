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

FieldScreenSchemeManager fieldScreenSchemeManager = ComponentAccessor.getComponent(FieldScreenSchemeManager.class)
IssueTypeScreenSchemeManager issueTypeScreenSchemeManager = ComponentAccessor.getIssueTypeScreenSchemeManager()
Collection<FieldScreenScheme> fieldScreenSchemes = fieldScreenSchemeManager.getFieldScreenSchemes()
String report = ""
Integer i = 0

fieldScreenSchemes.each { FieldScreenScheme fieldScreenScheme ->
	if (issueTypeScreenSchemeManager.getIssueTypeScreenSchemes(fieldScreenScheme).size() == 0) {
		report += "${fieldScreenScheme.getName()}<br/>"
		fieldScreenSchemeManager.removeFieldSchemeItems(fieldScreenScheme)
		fieldScreenSchemeManager.removeFieldScreenScheme(fieldScreenScheme)
		i++
	}
}

return "Total: ${i}<br/>" + report
