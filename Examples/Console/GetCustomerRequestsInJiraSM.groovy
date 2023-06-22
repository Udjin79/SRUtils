/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

import com.atlassian.servicedesk.api.ServiceDesk
import com.atlassian.servicedesk.api.util.paging.PagedResponse
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
import com.onresolve.scriptrunner.runner.customisers.PluginModule
import com.atlassian.servicedesk.api.ServiceDeskManager
import com.atlassian.servicedesk.api.requesttype.RequestTypeService
import org.evisaenkov.atlassian.library.ProjectOperations
import com.atlassian.servicedesk.api.requesttype.RequestType
import com.atlassian.servicedesk.api.requesttype.RequestTypeQuery

@WithPlugin("com.atlassian.servicedesk")
@PluginModule ServiceDeskManager serviceDeskManager
@PluginModule RequestTypeService requestTypeService

ProjectOperations projectOperations = new ProjectOperations()

ServiceDesk serviceDesk = serviceDeskManager.getServiceDeskForProject(projectOperations.getProject('TEST'))
RequestTypeQuery.Builder queryBuilder = requestTypeService.newQueryBuilder()
queryBuilder.requestOverrideSecurity(true)
queryBuilder.serviceDesk(serviceDesk.id)
PagedResponse<RequestType> allRequestTypes = requestTypeService.getRequestTypes(null, queryBuilder.build())

return allRequestTypes.results