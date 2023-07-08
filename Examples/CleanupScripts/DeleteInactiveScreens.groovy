/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.CleanupScripts

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.fields.screen.FieldScreen
import com.atlassian.jira.issue.fields.screen.FieldScreenManager
import com.atlassian.jira.issue.fields.screen.FieldScreenSchemeManager
import com.atlassian.jira.issue.fields.screen.FieldScreenTab
import com.atlassian.jira.workflow.JiraWorkflow
import com.atlassian.jira.workflow.WorkflowManager

FieldScreenManager fieldScreenManager = ComponentAccessor.getFieldScreenManager()
WorkflowManager workflowManager = ComponentAccessor.getWorkflowManager()
FieldScreenSchemeManager fieldScreenSchemeManager = ComponentAccessor.getComponent(FieldScreenSchemeManager.class)

Collection<FieldScreen> fieldScreens = fieldScreenManager.getFieldScreens()
Collection<JiraWorkflow> workflows = workflowManager.getWorkflows()

String report = ""
Integer index = 0

fieldScreens.each { FieldScreen fieldScreen ->
	boolean hasScreanScheme = false
	boolean isWorkflowScreen = false
	
	for (int i = 0; i < workflows.size(); i++) {
		if (workflows[i].getActionsForScreen(fieldScreen).size() > 0) {
			isWorkflowScreen = true
			break
		}
	}
	
	if (fieldScreenSchemeManager.getFieldScreenSchemes(fieldScreen).size() > 0) {
		hasScreanScheme = true
	}
	
	if (!hasScreanScheme && !isWorkflowScreen) {
		report += "${fieldScreen.getName()}<br/>"
		index++
		
		fieldScreen.getTabs().each { FieldScreenTab fieldScreenTab ->
			fieldScreenManager.removeFieldScreenLayoutItems(fieldScreenTab)
		}
		fieldScreenManager.removeFieldScreenTabs(fieldScreen)
		fieldScreenManager.removeFieldScreen(fieldScreen.getId())
	}
}

return "Total: ${index}<br/>" + report