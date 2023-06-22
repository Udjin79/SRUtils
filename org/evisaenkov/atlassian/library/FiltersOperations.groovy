/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package org.evisaenkov.atlassian.library

import com.atlassian.jira.bc.JiraServiceContextImpl
import com.atlassian.jira.bc.filter.SearchRequestService
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.fields.NavigableField
import com.atlassian.jira.issue.fields.layout.column.ColumnLayoutItem
import com.atlassian.jira.issue.fields.layout.column.ColumnLayoutManager
import com.atlassian.jira.issue.search.SearchRequest
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.query.Query
import org.evisaenkov.atlassian.library.UserOperations

/**
 * Class for operations with filters with SR Jira
 * @author Evgeniy Isaenkov
 */

class FiltersOperations {
	
	UserOperations userOperations = new UserOperations()
	SearchRequestService searchRequestService = ComponentAccessor.getComponent(SearchRequestService)
	JqlQueryParser jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
	
	void findAndReplaceInFilters(String find, String replaceTo) {
		searchTextByAllFilters(find).each { SearchRequest filter ->
			String newJql = filter.query?.getQueryString()?.replaceAll(find, replaceTo)
			updateUserFilter(filter, newJql, getOwner(filter))
		}
	}
	
	SearchRequest getFilter(long filterId, ApplicationUser user = userOperations.getTechUser()) {
		return searchRequestService.getFilter(new JiraServiceContextImpl(user), filterId)
	}
	
	def searchFiltersByColumnField(String fieldId, ApplicationUser user) {
		getAllFilters().findAll { SearchRequest filter ->
			getColumnFieldsFromFilter(filter, user).find { NavigableField field ->
				field.id == fieldId
			}
		}
	}
	
	def searchTextByAllFilters(String text) {
		getAllFilters().findAll { SearchRequest filter ->
			filter.query?.getQueryString()?.contains(text)
		}
	}
	
	Set<SearchRequest> getAllFilters() {
		Set<SearchRequest> filters = []
		userOperations.getAllUsers().each { ApplicationUser user ->
			filters.addAll(searchRequestService.getOwnedFilters(user))
		}
		return filters
	}
	
	Collection getUserFilters(ApplicationUser user) {
		return searchRequestService.getOwnedFilters(user)
	}
	
	Collection getUserFilters(String username) {
		ApplicationUser user = userOperations.getUser(username)
		return searchRequestService.getOwnedFilters(user)
	}
	
	void updateUserFilter(SearchRequest filter, String newJql, ApplicationUser user) {
		SearchService searchService = ComponentAccessor.getComponent(SearchService)
		Query newQuery = searchService.parseQuery(user, newJql).getQuery()
		if (newQuery) {
			filter.setQuery(newQuery)
		}
		searchRequestService.updateFilter(new JiraServiceContextImpl(user), filter)
	}
	
	def getColumnFieldsFromFilter(SearchRequest filter, ApplicationUser user) {
		ColumnLayoutManager columnLayoutManager = ComponentAccessor.getComponentOfType(ColumnLayoutManager)
		columnLayoutManager.getColumnLayout(user, filter).getColumnLayoutItems().collect { ColumnLayoutItem columnItem ->
			return columnItem.getNavigableField()
		}
	}
	
	ApplicationUser getOwner(SearchRequest filter) {
		return filter.getOwner()
	}
	
	void setOwner(long filterId, ApplicationUser newOwnerUser) {
		SearchRequest filter = getFilter(filterId, userOperations.getTechUser())
		filter.setOwner(newOwnerUser)
		updateFilter(filter)
	}
	
	void setNewQuery(long filterId, String jqlString) {
		SearchRequest filter = getFilter(filterId, userOperations.getTechUser())
		Query query = makeFilterQuery(jqlString)
		filter.setQuery(query)
		updateFilter(filter)
	}
	
	void updateFilter(SearchRequest filter) {
		searchRequestService.updateFilterOwner(new JiraServiceContextImpl(userOperations.getTechUser()), userOperations.getTechUser(), filter)
	}
	
	Query makeFilterQuery(String jqlString) {
		Query query = jqlQueryParser.parseQuery(jqlString)
		return query
	}
}
