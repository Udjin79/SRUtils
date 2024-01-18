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

// Get the ChangeHistoryManager component
ChangeHistoryManager changeHistoryManager = ComponentAccessor.getChangeHistoryManager()

// Define the field name and status name to search for
String fieldName = "Status"
String statusName = "In Progress"

// Get the current issue
Issue issue = issue as Issue

// Get a list of all change histories for the issue
List<ChangeHistory> allChanges = changeHistoryManager.getChangeHistories(issue)
List<ChangeHistory> cfChanges = []
Timestamp firstChange = null

// Iterate through all change histories and filter those related to the specified field
allChanges.each { ChangeHistory change ->
	if (fieldName.toLowerCase() == change.getChangeItemBeans().field[0].toLowerCase()) {
		cfChanges.add(change)
	}
}

// Check if there are relevant changes
if (cfChanges) {
	// Additional sorting by date to find the first change
	cfChanges = cfChanges.sort { ChangeHistory change ->
		change.getTimePerformed()
	}
	
	// Find the first change that matches the specified status
	firstChange = cfChanges.findAll { ChangeHistory change ->
		change.getChangeItemBeans().toString.first() == statusName
	}.first().timePerformed
}

// Return the timestamp of the first change to the specified status
return firstChange
