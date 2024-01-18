package CloudExamples

if (!issue.fields.issuetype.subtask) {
    return
}

// Get the parent issue as a Map
def parent = (issue.fields as Map).parent as Map

// Retrieve all the subtasks of this issue's parent
def parentKey = parent.key

// Get the field ids
def fields = get('/rest/api/2/field')
        .asObject(List)
        .body as List<Map>

// Get the story points custom field to use in the script
def storyPointsField = fields.find { it.name == "Story Points" }.id
logger.info("The id of the story points field is: $storyPointsField")

// Note:  The search API is limited that to only be able to return a maximum of 50 results
def allSubtasks = get("/rest/api/2/search")
        .queryString("jql", "parent=${parentKey}")
        .queryString("fields", "parent,$storyPointsField")
        .asObject(Map)
        .body
        .issues as List<Map>

logger.info("Total subtasks for ${parentKey}: ${allSubtasks.size()}")

// Sum the estimates
def estimate = allSubtasks.collect { Map subtask ->
    subtask.fields[storyPointsField] ?: 0

}.sum()
logger.info("Summed estimate: ${estimate}")

// Store the summed estimate on the Story Points field of the parent issue
def summedEstimateField = fields.find { it.name == "Story Points" }.id

logger.info("Custom field ID to update: ${summedEstimateField}")

// Now update the parent issue
def result = put("/rest/api/2/issue/${parentKey}")
        .header('Content-Type', 'application/json')
        .body([
                fields: [
                        (summedEstimateField): estimate
                ]
        ])
        .asString()

// check that updating the parent issue worked
assert result.status >= 200 && result.status < 300