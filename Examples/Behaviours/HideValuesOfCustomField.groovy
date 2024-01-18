/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 *
 * This script is a component of the Examples.Behaviours package within the SRUtils project,
 * designed for JIRA. It focuses on dynamically modifying the options of a custom select field
 * based on specific conditions. The script fetches and filters the options of a specified custom
 * field, limiting them to a predefined set. This functionality is particularly useful for
 * scenarios where the available selections in a custom field need to be contextually relevant,
 * enhancing the user experience by presenting only pertinent choices.
 */

package Examples.Behaviours

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.customfields.manager.OptionsManager
import com.atlassian.jira.issue.customfields.option.Options
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.fields.config.FieldConfig
import com.onresolve.jira.groovy.user.FieldBehaviours
import com.onresolve.jira.groovy.user.FormField
import groovy.transform.BaseScript

@BaseScript FieldBehaviours fieldBehaviours; // Base script annotation for field behaviors.

// Retrieve the field that triggered the behavior.
FormField singleSelect = getFieldById(getFieldChanged());
// Access the Options Manager.
OptionsManager optionsManager = ComponentAccessor.getOptionsManager();

// Retrieve the custom field object by its name.
FormField cf = getFieldByName("FieldName");
CustomField cfField = customFieldManager.getCustomFieldObject(cf.getFieldId());
// Get the relevant configuration for the custom field in the current issue context.
FieldConfig cfConfig = cfField.getRelevantConfig(getIssueContext());
// Fetch the options set for the custom field.
Options cfOptions = optionsManager.getOptions(cfConfig);

// Filter and map the options to include only specific values.
Map<Object, Object> cfA = cfOptions.findAll {option ->
	option.value in ['A', 'B', 'C', 'D'] // Define the set of allowed values.
}.collectEntries {
	[(it.optionId.toString()): it.value] // Map the option ID to its value.
};

// Set the filtered options as field options for the custom field.
cf.setFieldOptions(cfA);
