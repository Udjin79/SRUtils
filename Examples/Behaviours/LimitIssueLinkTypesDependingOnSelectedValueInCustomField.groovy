/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 *
 * This script is a part of the Examples.Behaviours package, specifically tailored for JIRA.
 * It dynamically modifies the visibility and requirement status of the 'Связанные задачи'
 * (Linked Issues) field based on the selection in another custom field. Additionally,
 * it restricts the types of issue links that can be selected, depending on the value chosen
 * in the decision field. This implementation enhances the user interface in JIRA by providing
 * a more context-sensitive and streamlined issue creation or editing process.
 */

package Examples.Behaviours

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.link.IssueLinkType
import com.atlassian.jira.issue.link.IssueLinkTypeManager
import com.onresolve.jira.groovy.user.FieldBehaviours
import com.onresolve.jira.groovy.user.FormField
import groovy.transform.BaseScript

@BaseScript FieldBehaviours fieldBehaviours; // Annotation for field behaviors script.

FormField fieldToShow = getFieldByName("Связанные задачи"); // Access the 'Linked Issues' field.
FormField decisionField = getFieldById(getFieldChanged()); // Get the field that triggered the change.

String selectedOption = decisionField.getValue() as String; // Retrieve the selected value from the decision field.
boolean triggerValue = selectedOption in ["value1", "value2"]; // Determine if the trigger condition is met.

FormField linkTypesField = getFieldById("issuelinks-linktype"); // Get the issue link type field.
IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager); // Access the Issue Link Type Manager.
Collection<IssueLinkType> allLinkTypes = issueLinkTypeManager.getIssueLinkTypes(false); // Retrieve all issue link types.
def allowedInwardTypesNames; // Declare a variable to store allowed inward link types.

// Set allowed inward link types based on the selected option.
if (selectedOption == "value1") {
	allowedInwardTypesNames = ["relates to"]; // Specify allowed types for 'value1'.
} else if (selectedOption == "value2") {
	allowedInwardTypesNames = ["is child task of"]; // Specify allowed types for 'value2'.
}

// Create a map of allowed inward link types.
Map<Object, Object> inwardAllowedLinks = allLinkTypes.findAll { linkType ->
	linkType.inward in allowedInwardTypesNames
}.collectEntries { linkType ->
	[(linkType.inward): linkType.inward]
};

// Set the allowed link types as options in the link types field.
linkTypesField.setFieldOptions(inwardAllowedLinks);

// Control the visibility and requirement of the 'Linked Issues' field.
fieldToShow.setHidden(!triggerValue);
fieldToShow.setRequired(triggerValue);
