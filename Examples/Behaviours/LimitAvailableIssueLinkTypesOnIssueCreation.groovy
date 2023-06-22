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
FormField linkTypesField = getFieldById('issuelinks-linktype')

ArrayList<String> allowedOutwardTypesNames = ['blocks', 'relates to', 'causes']
ArrayList<String> allowedInwardTypesNames = ['is blocked by', 'relates to', 'is caused by']

IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager)
Collection<IssueLinkType> allLinkTypes = issueLinkTypeManager.getIssueLinkTypes(false)

// Get the outward link names you want
Map<Object, Object> outwardAllowedLinks = allLinkTypes.findAll { linkType ->
	linkType.outward in allowedOutwardTypesNames
}.collectEntries { linkType ->
	[(linkType.outward): linkType.outward]
}
// Get the inward link names you want
Map<Object, Object> inwardAllowedLinks = allLinkTypes.findAll { linkType ->
	linkType.inward in allowedInwardTypesNames
}.collectEntries { linkType ->
	[(linkType.inward): linkType.inward]
}

// Combine maps of allowed link direction names
def allowedLinks = outwardAllowedLinks + inwardAllowedLinks
log.debug("Allowed Links = $allowedLinks")

// The options for the 'issuelinks-linktype' field have to be set in this structure: [blocks:blocks, relates to:relates to]
// because the html structure of the field uses the actual link direction name as the value property.
linkTypesField.setFieldOptions(allowedLinks)
