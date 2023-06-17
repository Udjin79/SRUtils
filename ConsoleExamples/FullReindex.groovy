/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package ConsoleExamples

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.task.context.Context
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.task.context.Contexts

IssueIndexingService issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
boolean notifyCluster = true
boolean useBackgroundReindexing = false
Context context = Contexts.builder().build()

issueIndexingService.reIndexAll(context, useBackgroundReindexing, notifyCluster)

