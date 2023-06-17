/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package org.evisaenkov.atlassian.library

import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItem
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemBuilder
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.context.IssueContextImpl
import com.atlassian.jira.issue.customfields.manager.OptionsManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.fields.config.FieldConfig
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.managedconfiguration.ConfigurationItemAccessLevel
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemService
import org.evisaenkov.atlassian.library.IssueOperations

/**
 * Class for customfields operations with SR Jira
 * @author Evgeniy Isaenkov
 */

class CustomFieldsOperations {
	
	IssueOperations issueOperations = new IssueOperations()
	ManagedConfigurationItemService managedConfigurationItemService = ComponentAccessor.getComponent(ManagedConfigurationItemService)
	
	void updateCustomFieldValueWithIssueUpdate(String issueKey, Long customFieldId, newValue) {
		CustomField customField = getCustomFieldObject(customFieldId)
		MutableIssue issue = issueOperations.getIssue(issueKey)
		issue.setCustomFieldValue(customField, newValue)
		issueOperations.updateIssue(issue)
	}
	
	CustomField getCustomFieldObject(Long fieldId) {
		return ComponentAccessor.getCustomFieldManager().getCustomFieldObject(fieldId)
	}
	
	CustomField getCustomFieldObject(String fieldName) {
		return ComponentAccessor.getCustomFieldManager().getCustomFieldObject(fieldName)
	}
	
	def getCustomFieldValue(Issue issue, Long fieldId) {
		return issue.getCustomFieldValue(ComponentAccessor.getCustomFieldManager().getCustomFieldObject(fieldId))
	}
	
	def getCustomFieldValue(Issue issue, String fieldName) {
		return issue.getCustomFieldValue(ComponentAccessor.getCustomFieldManager().getCustomFieldObject(fieldName))
	}
	
	List getCustomFieldContext(Long fieldId, Long projectId, String issueTypeId) {
		OptionsManager optionsManager = ComponentAccessor.getOptionsManager()
		
		def issueContext = new IssueContextImpl(projectId, issueTypeId)
		FieldConfig fieldConfig = getCustomFieldObject(fieldId).getRelevantConfig(issueContext)
		return optionsManager.getOptions(fieldConfig)
	}
	
	List<CustomField> getAllCustomFieldObjects() {
		return ComponentAccessor.getCustomFieldManager().getCustomFieldObjects() as List<CustomField>
	}
	
	void unlockLockedCustomField(String customFieldName) {
		CustomField cf = getCustomFieldObject(customFieldName)
		if (cf) {
			ManagedConfigurationItem mci = managedConfigurationItemService.getManagedCustomField(cf)
			if (mci)
			
			{
				managedConfigurationItemService.removeManagedConfigurationItem(mci)
			}
		}
	}
	
	void lockLockedCustomField(String customFieldName) {
		CustomField cf = getCustomFieldObject(customFieldName)
		
		if (cf) {
			ManagedConfigurationItem mci = managedConfigurationItemService.getManagedCustomField(cf)
			if (mci)
			
			{
				ManagedConfigurationItemBuilder managedConfigurationItemBuilder = mci.newBuilder()
				ManagedConfigurationItem updatedMci = managedConfigurationItemBuilder.setManaged(true).setConfigurationItemAccessLevel(ConfigurationItemAccessLevel.LOCKED).build()
				managedConfigurationItemService.updateManagedConfigurationItem(updatedMci)
			}
		}
	}
	
}
