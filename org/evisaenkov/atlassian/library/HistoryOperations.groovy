/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package org.evisaenkov.atlassian.library

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.StatusManager
import com.atlassian.jira.entity.Entity
import com.atlassian.jira.entity.property.EntityPropertyType
import com.atlassian.jira.entity.property.JsonEntityPropertyManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.changehistory.ChangeHistory
import com.atlassian.jira.issue.changehistory.ChangeHistoryItem
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.status.Status
import com.atlassian.jira.ofbiz.OfBizDelegator
import org.evisaenkov.atlassian.library.CustomFieldsOperations

/**
 * Class for basic history operations with SR Jira
 * @author Evgeniy Isaenkov
 */
class HistoryOperations {
	
	ChangeHistoryManager changeHistoryManager = ComponentAccessor.getChangeHistoryManager()
	CustomFieldsOperations customFields = new CustomFieldsOperations()
	OfBizDelegator ofBizDelegator = ComponentAccessor.getOfBizDelegator()
	JsonEntityPropertyManager jsonEntityPropertyManager = ComponentAccessor.getComponent(JsonEntityPropertyManager)
	
	List<ChangeHistoryItem> getAllChangeItems(Issue issue) {
		return changeHistoryManager.getAllChangeItems(issue)
	}
	
	List<ChangeHistory> getChangeHistories(Issue issue) {
		return changeHistoryManager.getChangeHistories(issue)
	}
	
	List<ChangeHistory> getChangeHistoryForCustomField(Issue issue, String fieldName) {
		List<ChangeHistory> allChanges = changeHistoryManager.getChangeHistories(issue)
		List<ChangeHistory> cfChanges = []
		allChanges.each { ChangeHistory change ->
			if (fieldName.toLowerCase() == change.getChangeItemBeans().field[0].toLowerCase()) {
				cfChanges.add(change)
			}
		}
		return cfChanges
	}
	
	List<ChangeHistory> getChangeHistoryForCustomField(Issue issue, Long fieldId) {
		String cfName = customFields.getCustomFieldObject(fieldId)
		getChangeHistoryForCustomField(issue, cfName)
	}
	
	void removeAllHistory(Issue issue) {
		changeHistoryManager.removeAllChangeItems(issue)
	}
	
	void removeChangeElementOfGroup(Long id) {
		ofBizDelegator.removeByAnd(Entity.Name.CHANGE_ITEM, [id: id].asImmutable())
	}
	
	void removeChangeGroup(Long id) {
		ofBizDelegator.removeByAnd(Entity.Name.CHANGE_GROUP, [id: id].asImmutable())
		jsonEntityPropertyManager.deleteByEntity(EntityPropertyType.CHANGE_HISTORY_PROPERTY.getDbEntityName(), id)
	}
	
	def getPreviousAssignee(Issue issue) {
		return ComponentAccessor.changeHistoryManager.getChangeItemsForField(issue, 'assignee').max { it.created }?.from
	}
	
	def getPreviousReporter(Issue issue) {
		return ComponentAccessor.changeHistoryManager.getChangeItemsForField(issue, 'reporter').max { it.created }?.from
	}
	
	def getPreviousValue(CustomField customField, Issue issue) {
		return ComponentAccessor.changeHistoryManager.getChangeItemsForField(issue, customField.name).max { it.created }?.from
	}
	
	Status getPreviousStatus(Issue issue) {
		String id = ComponentAccessor.changeHistoryManager.getChangeItemsForField(issue, 'status').max { it.created }.from
		StatusManager statusManager = ComponentAccessor.getComponentOfType(StatusManager)
		return statusManager.getStatus(id)
	}
	
}
