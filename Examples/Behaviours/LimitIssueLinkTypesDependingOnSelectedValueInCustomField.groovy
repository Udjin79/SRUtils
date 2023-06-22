/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Behaviours

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.link.IssueLinkType
import com.atlassian.jira.issue.link.IssueLinkTypeManager
import com.onresolve.jira.groovy.user.FieldBehaviours
import com.onresolve.jira.groovy.user.FormField
import groovy.transform.BaseScript

@BaseScript FieldBehaviours fieldBehaviours
FormField fieldToShow = getFieldByName('Связанные задачи')
FormField decisionField = getFieldById(getFieldChanged())

String selectedOption = decisionField.getValue() as String
boolean triggerValue = selectedOption in ['value1', 'value2']

FormField linkTypesField = getFieldById('issuelinks-linktype')
IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager)
Collection<IssueLinkType> allLinkTypes = issueLinkTypeManager.getIssueLinkTypes(false)
def allowedInwardTypesNames

if (selectedOption == 'value1') {
	allowedInwardTypesNames = ['relates to']
} else if (selectedOption == 'value2') {
	allowedInwardTypesNames = ['is child task of']
}

Map<Object, Object> inwardAllowedLinks = allLinkTypes.findAll { linkType ->
	linkType.inward in allowedInwardTypesNames
}.collectEntries { linkType ->
	[(linkType.inward): linkType.inward]
}

linkTypesField.setFieldOptions(inwardAllowedLinks)
fieldToShow.setHidden(!triggerValue)
fieldToShow.setRequired(triggerValue)
