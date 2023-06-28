/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Fields

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.changehistory.ChangeHistory
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager

import java.sql.Timestamp

ChangeHistoryManager changeHistoryManager = ComponentAccessor.getChangeHistoryManager()

String fieldName = "Status"
String statusName = "In Progress"

Issue issue = issue as Issue

List<ChangeHistory> allChanges = changeHistoryManager.getChangeHistories(issue)
List<ChangeHistory> cfChanges = []
Timestamp firstChange = null

allChanges.each { ChangeHistory change ->
	if (fieldName.toLowerCase() == change.getChangeItemBeans().field[0].toLowerCase()) {
		cfChanges.add(change)
	}
}

if (cfChanges) {
// Additional sorting by date
	cfChanges = cfChanges.sort {
		it.getTimePerformed()
	}
	
	firstChange = cfChanges.findAll {
		it.getChangeItemBeans().toString.first() == statusName
	}.first().timePerformed
}

return firstChange