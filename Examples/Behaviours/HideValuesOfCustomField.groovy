/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
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

@BaseScript FieldBehaviours fieldBehaviours
FormField singleSelect = getFieldById(getFieldChanged())
OptionsManager optionsManager = ComponentAccessor.getOptionsManager()

FormField cf = getFieldByName('FieldName')
CustomField cfField = customFieldManager.getCustomFieldObject(cf.getFieldId())
FieldConfig cfConfig = cfField.getRelevantConfig(getIssueContext())
Options cfOptions = optionsManager.getOptions(cfConfig)
Map<Object, Object> cfA = cfOptions.findAll {
	it.value in ['A', 'B', 'C', 'D']
}.collectEntries {
	[(it.optionId.toString()): it.value]
}
cf.setFieldOptions(cfA)
