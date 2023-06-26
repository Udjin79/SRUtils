/*
 * Created 2023.
 * @author ProForma
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.ProForma

import com.atlassian.jira.bc.issue.properties.IssuePropertyService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.mail.Email
import com.atlassian.mail.server.SMTPMailServer
import groovy.json.JsonSlurper
import org.apache.commons.lang3.StringUtils

////If used in a transition, comment next line
Issue issue = ComponentAccessor.getIssueManager().getIssueObject("TEST-1")
String baseurl = ComponentAccessor.getApplicationProperties().getString("jira.baseurl")
String issueUrl = baseurl + "/browse/" + issue.key
String formName = "Form Name"
String subject = "Form Approval for issue: " + issue.key
String body = "Please check approval action for form " + formName + " in issue " + issueUrl
ApplicationUser loggedInUser = ComponentAccessor.jiraAuthenticationContext.getLoggedInUser()
IssuePropertyService issuePropertyService = ComponentAccessor.getComponentOfType(IssuePropertyService.class)

Closure sendEmail = { String mailAddress, String mailSubject, String mailBody ->
	SMTPMailServer mailServer = ComponentAccessor.getMailServerManager().getDefaultSMTPMailServer()
	if (mailServer) {
		Email email = new Email(mailAddress)
		email.setSubject(mailSubject)
		email.setBody(mailBody)
		ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader()
		Thread.currentThread().setContextClassLoader(SMTPMailServer.class.classLoader)
		mailServer.send(email)
		Thread.currentThread().setContextClassLoader(threadClassLoader)
	}
}

Closure<Long> getFormId = { def property, String name ->
	def slurper = new JsonSlurper()
	def parsedJson = slurper.parseText(property.value)
	String id
	parsedJson.forms.each() { form ->
		if (form.name.equals(name)) {
			id = form.id.toString()
			return
		}
	}
	if (id) {
		return Long.valueOf(id)
	}
}

Closure<String> getFieldId = { def property, String fieldName ->
	def slurper = new JsonSlurper()
	def parsedJson = slurper.parseText(property.value)
	for (def question : parsedJson.design.questions) {
		if (fieldName.equals(StringUtils.substringBetween(question.toString(), "label=", ","))) {
			return question.toString().substring(0, question.toString().indexOf("="))
		}
	}
}

Closure getFieldValue = { def property, String fieldName ->
	def slurper = new JsonSlurper()
	def parsedJson = slurper.parseText(property.value)
	String fieldId = getFieldId(property, fieldName)
	for (def answer : parsedJson.state.answers) {
		if (fieldId.equals(answer.toString().substring(0, answer.toString().indexOf("=")))) {
			return answer.value.users
		}
	}
}

Long approversFieldId = 10000l
CustomField approversField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(approversFieldId)
def approversValue = issue.getCustomFieldValue(approversField)

for (def approver : approversValue) {
	sendEmail(approver.getEmailAddress(), subject, body)
}

/*
String fieldName = "Approvers Field in Form"
List<EntityProperty> allProperties = issuePropertyService.getProperties(loggedInUser, issue.id)
def formId
for(def property : allProperties){
    if(property.key.equals("proforma.forms")){
           formId = getFormId(property, formName)
    }
}

def result
for(def property : allProperties){
    if(property.key.equals("proforma.forms" + ".i" + formId) && property.value.contains("\"schemaVersion\":8")){
        result = getFieldValue(property, fieldName)
    }
}

for(def user : result){
    sendEmail(ComponentAccessor.getUserManager().getUserByKey(user.id).getEmailAddress(), subject, body)
}
*/