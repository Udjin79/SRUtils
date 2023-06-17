package ConsoleExamples
/*
 * Copyright (c) 2022.
 * @author Evgeniy Isaenkov
 */

import com.atlassian.confluence.pages.Page
import com.atlassian.confluence.pages.PageManager
import com.atlassian.confluence.spaces.Space
import com.atlassian.confluence.internal.spaces.SpaceManagerInternal
import com.atlassian.sal.api.component.ComponentLocator
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.LogManager

Logger logger = LogManager.getLogger("org.evisaenkov.atlassian")
SpaceManagerInternal spaceManager = ComponentLocator.getComponent(SpaceManagerInternal)
PageManager pageManager = ComponentLocator.getComponent(PageManager)

List<Space> spaces = spaceManager.getAllSpaces()

spaces.each { Space space ->
	pageManager.getPages(space, true).each { Page page ->
		String oldPage = page.getBodyAsString()
		if (oldPage.contains('ac:name="ui-text-box"')) {
			logger.warn(page.getId())
			String newPage = oldPage.replaceAll('ac:name="ui-text-box"', 'ac:name="info"')
			pageManager.saveNewVersion(page) { pageObject ->
				pageObject.setBodyAsString(newPage)
			}
		}
	}
}
