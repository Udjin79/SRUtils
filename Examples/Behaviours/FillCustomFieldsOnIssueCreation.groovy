/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Behaviours

/* Filling of custom multiline text fields on issue creation and setting one of them protected from changing */
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import com.onresolve.jira.groovy.user.FieldBehaviours
import com.onresolve.jira.groovy.user.FormField
import groovy.transform.BaseScript

@BaseScript FieldBehaviours fieldBehaviours

CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()

FormField descriptionForm = getFieldById('customfield_11234')
FormField warningForm = getFieldById('customfield_12234')

CustomField descriptionNote = customFieldManager.getCustomFieldObject(descriptionForm.getFieldId())
CustomField warningNote = customFieldManager.getCustomFieldObject(warningForm.getFieldId())

String descriptionValue = """*Template description title*
Template description body"""

String warningValue = """*Template warning title*
Template warning body"""

if (!underlyingIssue?.getCustomFieldValue(warningNote)) {
	warningForm.setFormValue(warningValue).setReadOnly(true)
}

if (!underlyingIssue?.getCustomFieldValue(descriptionNote)) {
	descriptionForm.setFormValue(descriptionValue)
}
