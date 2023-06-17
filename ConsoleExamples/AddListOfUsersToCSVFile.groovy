/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package ConsoleExamples

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.AttachmentManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.attachment.CreateAttachmentParamsBean
import com.atlassian.jira.user.ApplicationUser
import org.evisaenkov.atlassian.library.IssueOperations
import org.evisaenkov.atlassian.library.UserOperations
import org.evisaenkov.atlassian.library.GroupOperations

import java.text.SimpleDateFormat

IssueOperations issueOperations = new IssueOperations()
UserOperations userOperations = new UserOperations()
GroupOperations groupOperations = new GroupOperations()

Collection<ApplicationUser> userList = userOperations.getAllActiveUsers()
SimpleDateFormat df = new SimpleDateFormat('dd/MM/yy hh:mm', new Locale('ru'))

// Enter issue name where the attachment will be added
MutableIssue issue = issueOperations.getIssue("TEST-1234")

LinkedHashMap<Object, List> mapcsvLine = [:]
String csvReturn = ''

int i = 0

for (ApplicationUser user in userList) {
	List csvLine = []
	csvLine.add(user.getUsername())
	csvLine.add(user.getEmailAddress())
	csvLine.add(user.getDirectoryId().toString())
	Long lastLoginTime = userOperations.getLoginInfoByUsername(user.getUsername()).getLastLoginTime()
	if (lastLoginTime == null) {
		csvLine.add('null')
	} else {
		Date date = new Date(lastLoginTime)
		csvLine.add(df.format(date))
	}
	csvLine.add(user.isActive().toString())
	csvLine.add(groupOperations.getGroupsForUser(user.getUsername()))
	if (csvLine.size() > 0) {
		mapcsvLine.put(i, csvLine)
	}
	i++
}

csvReturn += 'username;email;directoryId;Lastlogindate;status;groups \n'
for (Map.Entry<Object, List> entry : mapcsvLine.entrySet()) {
	csvReturn += entry.getValue()[0].toString() + ';'
	csvReturn += entry.getValue()[1].toString() + ';'
	csvReturn += entry.getValue()[2].toString() + ';'
	csvReturn += entry.getValue()[3].toString() + ';'
	csvReturn += entry.getValue()[4].toString() + ';'
	csvReturn += entry.getValue()[5].toString()
	csvReturn += ' \n '
}
/* Below line will export the file in this folder "/jira/home-data/export/" */
Date datecsv = new Date()
SimpleDateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd_HH-mm-ss', new Locale('en'))
File csvFile = new File('/Export_Users_' + dateFormat.format(datecsv) + '.csv')
csvFile.append(csvReturn)

// Use below block to add the attachment to the issue specified above
AttachmentManager attachmentManager = ComponentAccessor.getAttachmentManager()
ApplicationUser reporter = userOperations.getCurrentUser()
CreateAttachmentParamsBean bean = new CreateAttachmentParamsBean.Builder()
		.file(new File(csvFile.getAbsolutePath()))
		.filename(csvFile.name)
		.contentType('csv')
		.author(reporter)
		.issue(issue)
		.build()
attachmentManager.createAttachment(bean)

csvFile.delete()
