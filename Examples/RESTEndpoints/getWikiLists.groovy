/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.RESTEndpoints

import com.atlassian.confluence.pages.Page
import com.atlassian.confluence.pages.PageManager
import com.atlassian.sal.api.component.ComponentLocator
import com.onresolve.scriptrunner.runner.rest.common.CustomEndpointDelegate
import groovy.json.JsonBuilder
import groovy.transform.BaseScript
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response

@BaseScript CustomEndpointDelegate delegate
getWikiLists(httpMethod: 'GET', groups: ['confluence-users']) { MultivaluedMap queryParams ->
	Integer query = queryParams.getFirst('query') as Integer
	Map rt = [:]
	
	PageManager pageManager = ComponentLocator.getComponent(PageManager)
	Page resultPage = pageManager.getPage(query)
	
	String html = resultPage.getBodyAsString()
	Document doc = Jsoup.parse(html)
	Element table = doc.select('table').first()
	Integer columns = table.select('th').iterator().size()
	List iterator = table.select('td').iterator()*.text() as List
	iterator.collate(columns).each { group ->
		rt[group[0]] = group[1..columns - 1]
	}
	
	return Response.ok(new JsonBuilder(rt).toString()).build()
}
