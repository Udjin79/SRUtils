/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package ConsoleExamples

import com.atlassian.confluence.pages.Page
import com.atlassian.confluence.pages.PageManager
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.confluence.spaces.Space
import com.atlassian.confluence.spaces.SpaceManager

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

SpaceManager spaceManager = ComponentLocator.getComponent(SpaceManager)
PageManager pageManager = ComponentLocator.getComponent(PageManager)

String spaceKey = 'TESTING'
String tableBody
Integer counter = 1
Space space = spaceManager.getSpace(spaceKey)

Page page = pageManager.getPage(space.key, 'TestPage')
Document doc = Jsoup.connect('https://www.atlassian.com/trust/security/advisories').get()

String tableHeader = '''
<table>
  <colgroup> <col/> <col/> </colgroup>
  <tbody>
    <tr>
      <th>№</th>
      <th>Name of vulnerability</th>
    </tr>'''
String tableFooter = '''
  </tbody>
</table>'''

Elements articles = doc.getElementsByClass('row')
articles.each { Element article ->
	if (article.getElementsByClass('heading').select('h3').size() > 0 && article.getElementsByClass('component component--textblock').select('a').size() > 0) {
		tableBody += """
    <tr>
      <td colspan="2" style="text-align: center;">${article.getElementsByClass('heading').select('h3').first().text()}</td>
    </tr>
    """
		Elements blocks = article.getElementsByClass('component component--textblock').select('a')
		blocks.each { Element block ->
			tableBody += """
        <tr>
      <td style="text-align: center;">${counter}</td>
      <td>
        <a href="${block.attr('href')}">${block.text()}</a>
      </td>
    </tr>
    """
			counter += 1
		}
	}
}

String tableFullBody = tableHeader + tableBody + tableFooter

pageManager.saveNewVersion(page) { pageObject ->
	pageObject.setBodyAsString(tableFullBody)
}
