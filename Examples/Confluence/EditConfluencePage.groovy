/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Confluence

import com.atlassian.confluence.pages.Page
import com.atlassian.confluence.pages.PageManager
import com.atlassian.sal.api.component.ComponentLocator

PageManager pageManager = ComponentLocator.getComponent(PageManager)

// Define page
Page outputPage = pageManager.getPage(12345678)

// Get current text
String pageBody = outputPage.getBodyAsString()

// Add new text
String appendedText = "<p>This is added text!<br/>>Hello world!</p>"

// Build new body
String outputPageBody = pageBody + appendedText

//Saving the page
pageManager.saveContentEntity(outputPage.setBodyAsString(outputPageBody), null, null)
