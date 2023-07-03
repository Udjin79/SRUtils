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

// Set space key, pages in which you're trying to list
Space space = spaceManager.getSpace("TEST")
// Set page ID, where you want to add list of pages in space
Page resultPage = pageManager.getPage(2752521)
String result = ""

String header = """<table style="">
  <colgroup>
    <col/>
    <col/>
    <col/>
    <col/>
    <col/>
  </colgroup>
  <thead>
    <tr>
      <th style="text-align: left;">
        <p>Page Name</p>
      </th>
      <th style="text-align: left;">
        <p>Creator Username</p>
      </th>
      <th style="text-align: left;">
        <p>Creator Full Name</p>
      </th>
      <th style="text-align: left;">
        <p>Last Modified</p>
      </th>
      <th style="text-align: left;">
        <p>Version</p>
      </th>
    </tr>
  </thead>
  <tbody>"""

String footer = """</tbody>
</table>"""

for (Page page : pageManager.getPages(space, true)) {
	if (page.getCreator() == null) {
		result += """<tr><td style="text-align: left;">${page.title}</td>
      <td style="text-align: left;">creator is empty</td>
      <td style="text-align: left;"></td>
      <td style="text-align: left;"></td>
      <td style="text-align: left;"></td></tr>"""
	} else {
		String username = page.getCreator().getName()
		String fullName = page.getCreator().getFullName()
		result += """<tr><td style="text-align: left;">${page.title}</td>
      <td style="text-align: left;">${username}</td>
      <td style="text-align: left;">${fullName}</td>
      <td style="text-align: left;">${page.getLastModificationDate()}</td>
      <td style="text-align: left;">${page.version}</td></tr>"""
	}
}

resultPage.setBodyAsString(header + result + footer)
pageManager.saveContentEntity(resultPage, null, null)
