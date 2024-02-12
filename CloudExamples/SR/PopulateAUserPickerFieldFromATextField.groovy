package CloudExamples

import java.util.regex.Matcher
import java.util.regex.Pattern

// Get  all custom fields
def customFields = get("/rest/api/3/field")
        .asObject(List)
        .body
        .findAll { (it as Map).custom } as List<Map>

// Get the ID for your user picker field field
def userPickerField = customFields.find { it.name == 'Demo User Picker' }?.id // Change to name of your field here

// Get the current issue summary and todays date
def descriptionVal = issue.fields.description

// Define the regular expression pattern used to find the user in the description text
def regex = '(?<=The owner of this ticket is:).*'
Pattern pattern = Pattern.compile(regex)
Matcher matcher = pattern.matcher(descriptionVal)

// Extract the name of the user after the text The owner of this ticket is:
if (matcher.find()) {
    def user = matcher.group(0)

// Find the user details for the user matched from the description
    def userSearchRequest = get("/rest/api/3/user/search")
            .queryString("query", user)
            .asObject(List)

// Assert that the API call to the  user search API returned a success response
    assert userSearchRequest.status == 200

// Get the accountId for the user returned by the search
    def userAccountId = userSearchRequest.body.accountId[0]

// Update the issue to set the user picker field to the returned user
    def setUserPickerField = put('/rest/api/3/issue/' + issue.key)
            .header('Content-Type', 'application/json')
            .body([
                    fields: [
                            (userPickerField): [id: userAccountId],
                    ]
            ])
            .asString()
}