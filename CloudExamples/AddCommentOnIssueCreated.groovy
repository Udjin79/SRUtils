package CloudExamples

String projectKey = 'SUPPORT'

// Restrict this script to only run against issues in one project
if (issue.fields.project.key != projectKey) {
    return
}

def author = issue.fields.creator.displayName

// Add a plain-text comment, see https://docs.atlassian.com/jira/REST/cloud/#api/2/issue/{issueIdOrKey}/comment-addComment
// for more details
def commentResp = post("/rest/api/2/issue/${issue.key}/comment")
        .header('Content-Type', 'application/json')
        .body([
                body: """Thank you ${author} for creating a support request.

We'll respond to your query within 24hrs.

In the meantime, please read our documentation: http://example.com/documentation"""
        ])
        .asObject(Map)

assert commentResp.status == 201