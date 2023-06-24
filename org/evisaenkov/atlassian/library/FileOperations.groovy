/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package org.evisaenkov.atlassian.library

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.util.JiraHome

import java.text.SimpleDateFormat

class FileOperations {
	
	JiraHome jiraHome = ComponentAccessor.getComponent(JiraHome)
	String tmpDir = Variables.JIRASM_TMP_DIR
	
	def base64ToFile(String base64str, String fileName, String extension) {
		try {
			new File(jiraHome.home, tmpDir + "${fileName}.${extension}").bytes = base64str.decodeBase64()
			File newFile = new File(jiraHome.home, tmpDir + "${fileName}.${extension}")
			return newFile
		} catch (Exception e) {
			return e.message
		}
	}
	
	def base64ToFile(String base64str, String fileNameAndExt) {
		try {
			new File(jiraHome.home, tmpDir + "${fileNameAndExt}").bytes = base64str.decodeBase64()
			File newFile = new File(jiraHome.home, tmpDir + "${fileNameAndExt}")
			return newFile
		} catch (Exception e) {
			return e.message
		}
	}
	
	File makeFile(String body, String fileName, String extension, boolean addTimestamp = false) {
		String timestamp = ''
		
		if (addTimestamp) {
			Date datecsv = new Date()
			SimpleDateFormat dateFormat = new SimpleDateFormat('yyyy-MM-dd_HH-mm-ss', new Locale('ru'))
			timestamp = "_${dateFormat.format(datecsv)}"
		}
		File txtFile = new File(jiraHome.home, tmpDir + "${fileName}${timestamp}.${extension}")
		txtFile.append(body)
		return txtFile
	}
}
