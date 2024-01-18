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

// Instantiating the managers for field screens, workflows, and field screen schemes.
FieldScreenManager fieldScreenManager = ComponentAccessor.getFieldScreenManager()
WorkflowManager workflowManager = ComponentAccessor.getWorkflowManager()
FieldScreenSchemeManager fieldScreenSchemeManager = ComponentAccessor.getComponent(FieldScreenSchemeManager.class)

// Retrieving all field screens and workflows.
Collection<FieldScreen> fieldScreens = fieldScreenManager.getFieldScreens()
Collection<JiraWorkflow> workflows = workflowManager.getWorkflows()

// Initializing a report string and an index counter.
String report = ""
Integer index = 0

// Iterating over each field screen to check its associations.
fieldScreens.each { FieldScreen fieldScreen ->
	boolean hasScreanScheme = false
	boolean isWorkflowScreen = false
	
	// Checking if the field screen is used in any workflow actions.
	for (int i = 0; i < workflows.size(); i++) {
		if (workflows[i].getActionsForScreen(fieldScreen).size() > 0) {
			isWorkflowScreen = true
			break
		}
	}
	
	// Checking if the field screen is associated with any field screen schemes.
	if (fieldScreenSchemeManager.getFieldScreenSchemes(fieldScreen).size() > 0) {
		hasScreanScheme = true
	}
	
	// If the field screen is not used in any workflow or scheme, it's marked for deletion.
	if (!hasScreanScheme && !isWorkflowScreen) {
		report += "${fieldScreen.getName()}<br/>"
		index++
		
		// Removing field screen layout items and tabs before deleting the field screen itself.
		fieldScreen.getTabs().each { FieldScreenTab fieldScreenTab ->
			fieldScreenManager.removeFieldScreenLayoutItems(fieldScreenTab)
		}
		fieldScreenManager.removeFieldScreenTabs(fieldScreen)
		fieldScreenManager.removeFieldScreen(fieldScreen.getId())
	}
}

// Returning a report of the total number of field screens processed and their names.
return "Total: ${index}<br/>" + report
