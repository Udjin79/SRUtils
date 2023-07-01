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

SpaceManager spaceManager = ComponentLocator.getComponent(SpaceManager)
PageManager pageManager = ComponentLocator.getComponent(PageManager)

String spaceKey = 'TEST'

Space space = spaceManager.getSpace(spaceKey)
Page spaceHomePage = space.getHomePage()

Page parentPage = pageManager.getPage(spaceHomePage.id)

Page page = new Page()
page.setSpace(space)
page.setParentPage(parentPage)
page.setTitle('Title')
page.setBodyAsString('Page body')

parentPage.addChild(page)

pageManager.saveContentEntity(page, null, null)
