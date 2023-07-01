/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Confluence

import com.atlassian.confluence.pages.Page
import com.atlassian.confluence.pages.PageManager
import com.atlassian.sal.api.component.ComponentLocator
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

PageManager pageManager = ComponentLocator.getComponent(PageManager)
Page page = pageManager.getPage('scriptrunner', 'Change Status')

Document soup = Jsoup.parse(page.bodyAsString)
soup.outputSettings.prettyPrint(false) // prevents whitespaces in the macro parameters

// Check, that exactly first status macro should be changed
Element statusMacro = soup.selectFirst("ac|structured-macro[ac:name='status']")

// Change title
Element statusMacroTitle = statusMacro.selectFirst("ac|parameter[ac:name='title']")
statusMacroTitle.text('OK')

// Change colour
Element statusMacroValue = statusMacro.selectFirst("ac|parameter[ac:name='colour']")
statusMacroValue.text('Green')

pageManager.saveNewVersion(page) { Page newVersion ->
	newVersion.bodyAsString = soup.toString()
}
