/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 *
 * This script is a part of the Examples.Behaviours package within the SRUtils project,
 * aimed at enhancing the functionality of JIRA. It specifically focuses on customizing
 * the behavior of the issue link type field. The script restricts the available issue link
 * types based on predefined lists of allowed inward and outward link names. This ensures
 * that users can only create issue links of specified types, thereby maintaining a controlled
 * and organized issue linking structure within JIRA projects.
 */

package Examples.Behaviours

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.link.IssueLinkType
import com.atlassian.jira.issue.link.IssueLinkTypeManager
import com.onresolve.jira.groovy.user.FieldBehaviours
import com.onresolve.jira.groovy.user.FormField
import groovy.transform.BaseScript

@BaseScript FieldBehaviours fieldBehaviours; // Annotation for field behaviors script.

FormField linkTypesField = getFieldById("issuelinks-linktype"); // Accessing the issue link type field.

// Define lists of allowed outward and inward link type names.
ArrayList<String> allowedOutwardTypesNames = ['blocks', 'relates to', 'causes'];
ArrayList<String> allowedInwardTypesNames = ['is blocked by', 'relates to', 'is caused by'];

// Access the Issue Link Type Manager.
IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager);
// Retrieve all issue link types.
Collection<IssueLinkType> allLinkTypes = issueLinkTypeManager.getIssueLinkTypes(false);

// Filter and map the outward allowed link types.
Map<Object, Object> outwardAllowedLinks = allLinkTypes.findAll { linkType ->
	linkType.outward in allowedOutwardTypesNames
}.collectEntries { linkType ->
	[(linkType.outward): linkType.outward]
};

// Filter and map the inward allowed link types.
Map<Object, Object> inwardAllowedLinks = allLinkTypes.findAll { linkType ->
	linkType.inward in allowedInwardTypesNames
}.collectEntries { linkType ->
	[(linkType.inward): linkType.inward]
};

// Combine maps of allowed link direction names.
def allowedLinks = outwardAllowedLinks + inwardAllowedLinks;
log.debug("Allowed Links = $allowedLinks");

// Set the allowed links as options for the 'issuelinks-linktype' field.
// The options are structured as [blocks: 'blocks', relates to: 'relates to'] to match the field's HTML structure.
linkTypesField.setFieldOptions(allowedLinks);
