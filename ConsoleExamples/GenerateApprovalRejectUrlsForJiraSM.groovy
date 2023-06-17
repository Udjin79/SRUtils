/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package ConsoleExamples

/**
 * Generates approval/reject URLs for issues with approvals in Jira SM.
 * Can be used, when you need to send custom approval letters, or send them to external services (Slack, Telegram...)
 */
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.servicedesk.plugins.approvals.internal.customfield.ApprovalsCFValue
import org.evisaenkov.atlassian.library.IssueOperations
import org.evisaenkov.atlassian.library.CustomFieldsOperations

IssueOperations issueOperations = new IssueOperations()
CustomFieldsOperations customFieldsOperations = new CustomFieldsOperations()

MutableIssue issue = issueOperations.getIssue('TEST-1234')

Integer approvalId = (customFieldsOperations.getCustomFieldValue(issue, 10100) as ApprovalsCFValue).approvals.first().id

String approveUrl = "https://jira.example.com/servicedesk/customer/user/approval-action/${issue.key}/${approvalId}/approve/"
String rejectUrl = "https://jira.example.com/servicedesk/customer/user/approval-action/${issue.key}/${approvalId}/reject/"
