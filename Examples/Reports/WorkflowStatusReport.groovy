/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Reports

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.ConstantsManager
import com.atlassian.jira.issue.status.Status

ConstantsManager constantsManager = ComponentAccessor.getConstantsManager()

List<String> statuses = []
for (Status status : constantsManager.getStatuses()) {
	statuses.add("ID: ${status.getId()} NAME: ${status.getName()} CATEGORY: ${status.getStatusCategory().getName()} COLOR: ${status.getStatusCategory().getColorName()}")
}

return statuses
