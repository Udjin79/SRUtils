/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package org.evisaenkov.atlassian.library

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.attachment.Attachment
import com.atlassian.jira.util.AttachmentUtils
import com.atlassian.mail.Email
import com.atlassian.mail.queue.SingleMailQueueItem

import javax.mail.Multipart
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart

/**
 * Class for email operations with SR Jira
 * @author Evgeniy Isaenkov
 */
class EmailOperations {
	
	void sendEmail(String emailAddr, String copy, String subject, String body, String from) {
		Email email = new Email(emailAddr, copy, '')
		email.setSubject(subject)
		email.setBody(body)
		email.setMimeType('text/html; charset=utf-8')
		if (from) {
			email.setFrom(from)
		}
		SingleMailQueueItem smqi = new SingleMailQueueItem(email)
		ComponentAccessor.getMailQueue().addItem(smqi)
	}
	
	void sendEmail(String emailAddr, String copy, String subject, String body) {
		Email email = new Email(emailAddr, copy, '')
		email.setSubject(subject)
		email.setBody(body)
		email.setMimeType('text/html; charset=utf-8')
		SingleMailQueueItem smqi = new SingleMailQueueItem(email)
		ComponentAccessor.getMailQueue().addItem(smqi)
	}
	
	void sendEmail(String emailAddr, String subject, String body) {
		Email email = new Email(emailAddr)
		email.setSubject(subject)
		email.setBody(body)
		email.setMimeType('text/html; charset=utf-8')
		SingleMailQueueItem smqi = new SingleMailQueueItem(email)
		ComponentAccessor.getMailQueue().addItem(smqi)
	}
	
	/** send email
	 * @param emailAddr : "test@test.test"
	 * @param copy - string with list of email adresses.
	 * @param bcc - string with list of email adresses.
	 * @param subject - email subject.
	 * @param body - email body.
	 * @param from - sender email, by default - email address configured in Jira.
	 * @param replyTo - reply to email address.
	 * @param emailFormat - "text/html" or "text/plain" or ...
	 * @param attachments - list of attachments (if any)
	 * */
	void sendEmail(Map emailBody) {
		def (String emailAddr, String copy, String bcc, String subject, String body, String from, String replyTo, String emailFormat, List<Attachment> attachments, List<File> fileAttachments) =
		[emailBody['emailAddr'], emailBody['copy'], emailBody['bcc'], emailBody['subject'], emailBody['body'], emailBody['from'], emailBody['replyTo'], emailBody['emailFormat'], emailBody['attachments'] as List<Attachment>, emailBody['fileAttachments'] as List<File>]
		Email email = new Email(emailAddr, copy, bcc)
		email.setSubject(subject)
		Multipart multipart = new MimeMultipart("mixed")
		
		if (from) {
			email.setFrom(from)
		}
		if (replyTo) {
			email.setReplyTo(replyTo)
		}
		
		MimeBodyPart bodyPart = new MimeBodyPart()
		bodyPart.setContent(body, "${emailFormat ?: 'text/html'}; charset=utf-8")
		multipart.addBodyPart(bodyPart)
		
		attachments?.each {
			File attachment = AttachmentUtils.getAttachmentFile(it)
			
			MimeBodyPart attachmentPart = new MimeBodyPart()
			attachmentPart.attachFile(attachment)
			attachmentPart.setFileName(it.getFilename())
			multipart.addBodyPart(attachmentPart)
		}
		
		fileAttachments?.each { File attachment ->
			MimeBodyPart attachmentPart = new MimeBodyPart()
			attachmentPart.attachFile(attachment)
			attachmentPart.setFileName(attachment.getName())
			multipart.addBodyPart(attachmentPart)
		}
		
		email.setMultipart(multipart)
		email.setMimeType("multipart/mixed")
		SingleMailQueueItem smqi = new SingleMailQueueItem(email)
		ComponentAccessor.getMailQueue().addItem(smqi)
	}
}
