/*
 * Created 2024.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

import com.atlassian.confluence.pages.Page
import com.atlassian.confluence.pages.PageManager
import com.atlassian.confluence.spaces.Space
import com.atlassian.confluence.spaces.SpaceManager
import com.atlassian.sal.api.component.ComponentLocator

SpaceManager spaceManager = ComponentLocator.getComponent(SpaceManager)
PageManager pageManager = ComponentLocator.getComponent(PageManager)

List<Space> spaces = spaceManager.getAllSpaces()

spaces.each { Space space ->
	pageManager.getPages(space, true).each { Page page ->
		String oldPage = page.getBodyAsString()
		if (oldPage.contains('ac:name="ui-text-box"')) {
			String newPage = oldPage.replaceAll('ac:name="ui-text-box"', 'ac:name="info"')
			pageManager.saveNewVersion(page) { pageObject ->
				pageObject.setBodyAsString(newPage)
			}
		}
	}
}
