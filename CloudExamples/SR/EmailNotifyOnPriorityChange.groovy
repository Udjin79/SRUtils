package CloudExamples

import groovy.xml.MarkupBuilder

def priorityChange = changelog?.items.find { it['field'] == 'priority' }

if (!priorityChange) {
    logger.info("Priority was not updated")
    return
}
logger.info("Priority changed from {} to {}", priorityChange.fromString, priorityChange.toString)
if (priorityChange.toString == "Highest") {
    def writer = new StringWriter()
    // Note that markup builder will result in static type errors as it is dynamically typed.
    // These can be safely ignored
    def markupBuilder = new MarkupBuilder(writer)
    markupBuilder.div {
        p {
            // update url below:
            a(href: "http://myjira.atlassian.net/issue/${issue.key}", issue.key)
            span(" has had priority changed from ${priorityChange.fromString} to ${priorityChange.toString}")
        }
        p("You're important so we thought you should know")
    }
    def htmlMessage = writer.toString()
    def textMessage = new XmlSlurper().parseText(htmlMessage).text()

    logger.info("Sending email notification for issue {}", issue.key)
    def resp = post("/rest/api/2/issue/${issue.id}/notify")
            .header("Content-Type", "application/json")
            .body([
                    subject : 'Priority Increased',
                    textBody: textMessage,
                    htmlBody: htmlMessage,
                    to      : [
                            reporter: issue.fields.reporter != null, // bug - 500 error when no reporter
                            assignee: issue.fields.assignee != null, // bug - 500 error when no assignee
                            watchers: true,
                            voters  : true,
                            users   : [[
                                               name: 'admin'
                                       ]],
                            groups  : [[
                                               name: 'jira-administrators'
                                       ]]
                    ]
            ])
            .asString()
    assert resp.status == 204
}