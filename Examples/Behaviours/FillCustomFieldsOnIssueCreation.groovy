/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 *
 * This script, part of the Examples.Behaviours package in SRUtils, is designed for JIRA.
 * Its primary function is to automatically populate two custom multiline text fields
 * when an issue is created. One of these fields is set to be protected from changes post-creation.
 * This enhances the issue creation process by pre-filling important fields with template data,
 * ensuring consistency and saving time. Additionally, protecting one of the fields from further
 * edits ensures the integrity of the pre-defined information.
 */

package Examples.Behaviours

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import com.onresolve.jira.groovy.user.FieldBehaviours
import com.onresolve.jira.groovy.user.FormField
import groovy.transform.BaseScript

@BaseScript FieldBehaviours fieldBehaviours; // Annotation for field behaviors script.

// Access the CustomFieldManager.
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();

// Retrieve form fields for custom fields.
FormField descriptionForm = getFieldById("customfield_11234");
FormField warningForm = getFieldById("customfield_12234");

// Get the CustomField objects using their IDs.
CustomField descriptionNote = customFieldManager.getCustomFieldObject(descriptionForm.getFieldId());
CustomField warningNote = customFieldManager.getCustomFieldObject(warningForm.getFieldId());

// Define template values for the description and warning fields.
String descriptionValue = """*Template description title*
Template description body""";

String warningValue = """*Template warning title*
Template warning body""";

// Check if the underlying issue does not already have a value for the warning note.
if (!underlyingIssue?.getCustomFieldValue(warningNote)) {
	// If not, set the template value and make the field read-only.
	warningForm.setFormValue(warningValue).setReadOnly(true);
}

// Similarly, check for the description note field.
if (!underlyingIssue?.getCustomFieldValue(descriptionNote)) {
	// If not, set the template value for the description field.
	descriptionForm.setFormValue(descriptionValue);
}
