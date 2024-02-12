package CloudExamples

if (!issue.fields.issuetype.subtask) {
    return
}

// Get the parent issue as a Map
def parent = (issue.fields as Map).parent as Map

// Retrieve all the subtasks of this issue's parent
def parentKey = parent.key

def allSubtasks = get("/rest/api/2/search")
        .queryString("jql", "parent=${parentKey}")
        .queryString("fields", "parent,timeestimate")
        .asObject(Map)
        .body
        .issues as List<Map>
logger.info("Total subtasks for ${parentKey}: ${allSubtasks.size()}")

// Sum the estimates
def estimate = allSubtasks.collect { Map subtask ->
    subtask.fields.timeestimate ?: 0
}.sum()
logger.info("Summed estimate: ${estimate}")

// Get the field ids
def fields = get('/rest/api/2/field')
        .asObject(List)
        .body as List<Map>

def summedEstimateField = fields.find { it.name == "Summed Subtask Estimate" }.id
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