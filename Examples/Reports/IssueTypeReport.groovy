/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Reports

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.ConstantsManager
import com.atlassian.jira.issue.issuetype.IssueType

ConstantsManager constantsManager = ComponentAccessor.getConstantsManager()

List<String> issueTypes = []
for (IssueType issueType : constantsManager.getAllIssueTypeObjects()) {
	issueTypes.add("ID: ${issueType.getId()} NAME: ${issueType.getName()}")
}

return issueTypes