package CloudExamples

if (!issue.fields.issuetype.subtask) {
    return
}

// Retrieve all the subtasks of this issue's parent
def parentKey = issue.fields.parent.key
def allSubtasks = get("/rest/api/2/search")
        .queryString("jql", "parent=${parentKey}")
        .queryString("fields", "[]")
        .asObject(Map)
        .body
        .issues as List<Map>
logger.info("Total subtasks for ${parentKey}: ${allSubtasks.size()}")

// Get the field ids
def fields = get('/rest/api/2/field')
        .asObject(List)
        .body as List<Map>

def subtaskCount = fields.find { it.name == "Subtask Count" }.id
logger.info("Custom field ID to update: ${subtaskCount}")

// Now update the parent issue
def result = put("/rest/api/2/issue/${parentKey}")
        .header('Content-Type', 'application/json')
        .body([
                fields: [
                        (subtaskCount): allSubtasks.size()
                ]
        ])
        .asString()

// check that updating the parent issue worked
assert result.status >= 200 && result.status < 300