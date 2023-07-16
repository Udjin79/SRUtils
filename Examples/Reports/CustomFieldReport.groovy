/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Reports

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField

CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()

List<String> cFields = []
for (CustomField customField : customFieldManager.getCustomFieldObjects()) {
	cFields.add("ID: ${customField.getId()} NAME: ${customField.getName()} TYPE: ${customField.getCustomFieldType().getName()}")
}

return cFields
