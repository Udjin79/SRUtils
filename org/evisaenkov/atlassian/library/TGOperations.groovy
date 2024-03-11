/*
 * Copyright (c) 2022.
 * @author Evgeniy Isaenkov
 */

package org.evisaenkov.atlassian.library

import groovy.json.JsonSlurper
import org.evisaenkov.atlassian.library.Variables
import java.util.regex.Matcher
import java.util.regex.Pattern

class TGOperations {
	def sendToTelegram(String chatId, String text, String replyId = null, String tgToken = null) {
		String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&reply_to_message_id=%s&parse_mode=markdown&text=%s"
		String apiToken = tgToken ? tgToken : Variables.TG_TOKEN
		urlString = String.format(urlString, apiToken, chatId, replyId, normalizeText(text))
		
		try {
			URL url = new URL(urlString)
			URLConnection conn = url.openConnection()
			
			StringBuilder sb = new StringBuilder()
			InputStream is = new BufferedInputStream(conn.getInputStream())
			BufferedReader br = new BufferedReader(new InputStreamReader(is))
			String inputString = ''
			while ((inputString = br.readLine()) != null) {
				sb.append(inputString)
			}
			Map response = new JsonSlurper().parseText(sb.toString()) as Map
			return response['result']['message_id']
		} catch (Exception e) {
			return e.message
		}
	}
	
	String normalizeText(String messageText) {
		// Convert JIRA url format to TG format
		String urlsRegex = "\\[(.*?)\\|(.*?)\\]"
		Pattern urlPattern = Pattern.compile(urlsRegex, Pattern.MULTILINE);
		Matcher urlMatcher = urlPattern.matcher(messageText)
		String urlsSubst = '[$1]($2)'
		String normalizedUrls = urlMatcher.replaceAll(urlsSubst)
		// Remove JIRA {} tags
		String tagsRegex = "\\{.*?\\}(.*?)\\{.*?\\}"
		Pattern tagsPattern = Pattern.compile(tagsRegex, Pattern.MULTILINE);
		Matcher tagsMatcher = tagsPattern.matcher(normalizedUrls)
		String tagsSubst = '$1'
		String normalizedTags = tagsMatcher.replaceAll(tagsSubst)
		// Convert JIRA symbols to TG HTTP request compatible
		String normalizedText = normalizedTags
				.replace(' ', '%20')
				.replace(' ', '%20')
				.replace('#', '%23')
				.replace('+', '%2b')
				.replace('$', '%24')
				.replace('&', '%26')
				.replace('\\', '\\\\')
				.replace('\r\n', '\n')
				.replace('\n', '%0A')
				.replace('%0A%0A', '%0A')
				.replace('_', '\\_')
				.replace('*', '\\*')
		return normalizedText
	}
	
}
