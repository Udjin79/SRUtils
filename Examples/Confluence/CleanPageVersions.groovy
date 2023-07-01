/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Confluence

import com.atlassian.confluence.pages.Page
import com.atlassian.confluence.pages.PageManager
import com.atlassian.confluence.spaces.Space
import com.atlassian.confluence.spaces.SpaceManager
import com.atlassian.sal.api.component.ComponentLocator

PageManager pageManager = ComponentLocator.getComponent(PageManager)
SpaceManager spaceManager = ComponentLocator.getComponent(SpaceManager)

Space space = spaceManager.getSpace("TESTSPACE")
List<Page> pages = pageManager.getPages(space, true) // boolean currentOnly

int versionsToLeave = 0
ArrayList removedPagesTitles = []

pages.each { Page page ->
	int previousVersion = page.getPreviousVersion()
	if (previousVersion > versionsToLeave) {
		for (int i = 1; i <= (previousVersion - versionsToLeave); i++) {
			try {
				def pageForRemoval = (Page) pageManager.getPageByVersion(page, i)
				removedPagesTitles << "title: " + pageForRemoval.displayTitle + " - version: " + pageForRemoval.version
				pageForRemoval.remove(pageManager)
			} catch (NullPointerException e) {
				log.error(e.message)
			}
		}
	}
}

log.warn(removedPagesTitles.size())