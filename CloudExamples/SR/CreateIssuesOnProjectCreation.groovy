package CloudExamples

def projectKey = project.key as String
logger.info(projectKey)

def issueTypesReq = get("/rest/api/2/issuetype").asObject(List)
assert issueTypesReq.status == 200
def taskType = issueTypesReq.body.find { it.name == "Task" }
assert taskType // Must have an issue type named Task

// create two issues

post("/rest/api/2/issue")
        .header("Content-Type", "application/json")
        .body(
                [
                        fields: [
                                summary    : "Create Confluence space associated to the project",
                                description: "Don't forget to do this!.",
                                project    : [
                                        id: project.id
                                ],
                                issuetype  : [
                                        id: taskType.id
                                ]
                        ]
                ])
        .asString()

post("/rest/api/2/issue")
        .header("Content-Type", "application/json")
        .body(
                [
                        fields: [
                                summary    : "Bootstrap connect add-on",
                                description: "Some other task",
                                project    : [
                                        id: project.id
                                ],
                                issuetype  : [
                                        id: taskType.id
                                ]
                        ]
                ])
        .asString()

// example of bulk update:
post("/rest/api/2/issue/bulk")
        .header("Content-Type", "application/json")
        .body(
                [
                        issueUpdates: [
                                [
                                        fields: [
                                                summary    : "Bulk task one",
                                                description: "First example of a bulk update",
                                                project    : [
                                                        id: project.id
                                                ],
                                                issuetype  : [
                                                        id: taskType.id
                                                ]
                                        ]
                                ],
                                [
                                        fields: [
                                                summary    : "Bulk task two",
                                                description: "2nd example of a bulk update",
                                                project    : [
                                                        id: project.id
                                                ],
                                                issuetype  : [
                                                        id: taskType.id
                                                ]
                                        ]
                                ]
                        ]
                ])
        .asString()