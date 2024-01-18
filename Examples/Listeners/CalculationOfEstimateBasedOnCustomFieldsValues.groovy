/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Listeners

import com.atlassian.greenhopper.service.sprint.Sprint
import com.atlassian.greenhopper.service.sprint.SprintIssueService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager
import com.onresolve.scriptrunner.runner.customisers.JiraAgileBean
import com.onresolve.scriptrunner.runner.customisers.WithPlugin

import java.sql.Timestamp

@WithPlugin('com.pyxis.greenhopper.jira')
@JiraAgileBean
SprintIssueService sprintIssueService

IssueManager issueManager = ComponentAccessor.getIssueManager()
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
UserManager userManager = ComponentAccessor.getUserManager()
IssueIndexingService issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)

// Configuring main variables
def changeItem = event.changeLog.getRelated("ChildChangeItem")
MutableIssue issue = issueManager.getIssueObject(event.getIssue().id) as MutableIssue
Sprint sprint
// Configuring service user, which will make changes
ApplicationUser user = userManager.getUserByName("serviceAccount")
// Configuring date string format, which we receive in change data
String formatPattern = "yyyy-MM-dd'T'HH:mm:ssZ"
List fieldNames = ["timeoriginalestimate", "Start Date", "Target End"]

if (changeItem['field'] && changeItem['field'].first() in fieldNames) {
    String fieldName = changeItem['field'].first()
    //    set ID's of your custom fields
    CustomField startDate = customFieldManager.getCustomFieldObject(11111)
    CustomField targetEnd = customFieldManager.getCustomFieldObject(11112)
    Long startDateValue = (issue.getCustomFieldValue(startDate) as Timestamp).getTime()
    Long targetEndValue = (issue.getCustomFieldValue(targetEnd) as Timestamp).getTime()
    def sprintData = sprintIssueService.getActiveSprintForIssue(user, issue)
    if (sprintData) {
        sprint = sprintData.getValue().first()
    }
    Long originalEstimate = issue.getOriginalEstimate()

    if (sprint && (!startDateValue || !targetEndValue)) {
        issue.setCustomFieldValue(startDate, sprint.getStartDate().getMillis())
        issue.setCustomFieldValue(targetEnd, sprint.getEndDate().getMillis())
        issue.setOriginalEstimate((targetEndValue - startDateValue) / 1000 as Long)
    }

    if (startDateValue && targetEndValue && originalEstimate && fieldName == "Start Date") {
        Long changeOld = (Date.parse(formatPattern, (String) changeItem['oldvalue'].first()) as Date).getTime()
        Long changeNew = (Date.parse(formatPattern, (String) changeItem['newvalue'].first()) as Date).getTime()
        Long diff = changeNew - changeOld
        issue.setCustomFieldValue(targetEnd, targetEndValue + diff)
    }

    if (startDateValue && targetEndValue && originalEstimate && fieldName == "Target End") {
        Long changeOld = (Date.parse(formatPattern, (String) changeItem['oldvalue'].first()) as Date).getTime()
        Long changeNew = (Date.parse(formatPattern, (String) changeItem['newvalue'].first()) as Date).getTime()
        Long diff = changeNew - changeOld
        issue.setOriginalEstimate(issue.originalEstimate + (diff / 1000) as Long)
    }

    if (!originalEstimate && startDateValue && targetEndValue) {
        issue.setOriginalEstimate((targetEndValue - originalEstimate) / 1000 as Long)
    }

    issueManager.updateIssue(user, issue, EventDispatchOption.DO_NOT_DISPATCH, false)
    issueIndexingService.reIndex(issue)
}

