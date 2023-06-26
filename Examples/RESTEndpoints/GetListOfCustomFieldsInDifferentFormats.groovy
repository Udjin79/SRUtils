/*
 * Created 2023.
 * @author Peter-Dave Sheehan
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.RESTEndpoints

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.fields.CustomField
import com.onresolve.scriptrunner.runner.rest.common.CustomEndpointDelegate
import groovy.json.JsonBuilder
import groovy.transform.BaseScript
import groovy.xml.MarkupBuilder

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response

@BaseScript CustomEndpointDelegate delegate
extractCustomFields(httpMethod: "GET", groups: ['jira-users']) { MultivaluedMap queryParams, String body ->
	List<Map> listOfFieldMaps = ComponentAccessor.customFieldManager.customFieldObjects.collect { CustomField cf ->
		[
				idLong : cf.idAsLong,
				idText : cf.id,
				cfname : cf.name,
				typeKey: cf.customFieldType.key
		]
	}
	
	def format = queryParams.getFirst('format') ?: 'json'
	switch (format) {
		case 'json':
			return Response.ok(new JsonBuilder(listOfFieldMaps).toPrettyString()).build()
			break
		case 'html':
			def writer = new StringWriter()
			def builder = new MarkupBuilder(writer)
			builder.table {
				thead {
					tr {
						listOfFieldMaps[0].keySet().each { th it }
					}
				}
				tbody {
					listOfFieldMaps.each { row ->
						tr {
							listOfFieldMaps[0].keySet().each { td row[it] }
						}
					}
				}
			}
			return Response.ok().type(MediaType.TEXT_HTML).entity(writer.toString()).build()
			break
		case 'csv':
			def csv = new StringBuffer()
			listOfFieldMaps[0].keySet().each { csv << it + ',' }
			csv << "\n"
			listOfFieldMaps.each { row ->
				listOfFieldMaps[0].keySet().each { csv << row[it] + ',' }
				csv << "\n"
			}
			return Response.ok().type(MediaType.TEXT_PLAIN).entity(csv.toString()).build()
			break
		default:
			return Response.notAcceptable(null).entity([error: "Invalid format. Only json,csv and html currently supported "]).build()
	}
}