/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package org.evisaenkov.atlassian.library

import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueFieldConstants
import com.atlassian.jira.issue.attachment.Attachment
import com.atlassian.jira.issue.attachment.TemporaryAttachmentId
import com.atlassian.jira.issue.attachment.TemporaryWebAttachment
import com.atlassian.jira.issue.attachment.TemporaryWebAttachmentManager
import webwork.action.ActionContext
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.util.JiraHome
import com.atlassian.jira.issue.AttachmentManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.attachment.CreateAttachmentParamsBean
import com.atlassian.jira.user.ApplicationUser
import org.evisaenkov.atlassian.library.UserOperations
import java.text.SimpleDateFormat

/**
 * Class for attachments operations with SR Jira
 * @author Evgeniy Isaenkov
 */
class AttachmentsOperations {
	
	AttachmentManager attachmentManager = ComponentAccessor.getAttachmentManager()
	JiraHome jiraHome = ComponentAccessor.getComponent(JiraHome)
	String tmpDir = Variables.JIRASM_TMP_DIR
	UserOperations userOperations = new UserOperations()
	ApplicationUser techUser = userOperations.getTechUser()
	
	def getAttachments(Issue issue) {
		return attachmentManager.getAttachments(issue)
	}
	
	void deleteAttachment(Attachment attachment) {
		attachmentManager.deleteAttachment(attachment)
	}
	
	void deleteTemporaryAttachment(TemporaryAttachmentId attachmentId) {
		attachmentManager.deleteTemporaryAttachment(attachmentId)
	}
	
	List getAttachmentsOnIssueCreation() {
		TemporaryWebAttachmentManager temporaryAttachmentUtil = ComponentAccessor.getComponent(TemporaryWebAttachmentManager)
		String formToken = ActionContext.getRequest()?.getParameter(IssueFieldConstants.FORM_TOKEN)
		List<TemporaryWebAttachment> tempWebAttachments
		
		if (formToken) {
			tempWebAttachments = temporaryAttachmentUtil.getTemporaryWebAttachmentsByFormToken(formToken)
			return tempWebAttachments
		} else {
			return []
		}
		
	}
	
	void addAttachment(MutableIssue issue, String fileBody, ApplicationUser author = techUser, String fileName, String extension) {
		Date currentDate = new Date()
		SimpleDateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd_HH-mm-ss', new Locale('ru'))
		File newFile = new File(jiraHome.home, tmpDir + "${fileName}_${issue.key}_${dateFormat.format(currentDate)}.${extension}")
		newFile.append(fileBody)
		
		CreateAttachmentParamsBean bean = new CreateAttachmentParamsBean.Builder()
				.file(new File(newFile.getAbsolutePath()))
				.filename(newFile.name)
				.contentType(extension)
				.author(author)
				.issue(issue)
				.build()
		attachmentManager.createAttachment(bean)
		
		newFile.delete()
	}
	
	void addAttachment(MutableIssue issue, File newFile, ApplicationUser author = techUser) {
		String extension = ""
		int i = newFile.name.lastIndexOf('.')
		if (i > 0) {
			extension = newFile.name.substring(i + 1)
		}
		CreateAttachmentParamsBean bean = new CreateAttachmentParamsBean.Builder()
				.file(new File(newFile.getAbsolutePath()))
				.filename(newFile.name)
				.contentType(extension)
				.author(author)
				.issue(issue)
				.build()
		attachmentManager.createAttachment(bean)
		
		newFile.delete()
	}
}
