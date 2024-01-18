# Jira Username and Password
$JIRA_USERNAME = "user"
$JIRA_PASSWORD = "password"

# Base64 encode credentials for basic authentication
$base64AuthInfo = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes(("${JIRA_USERNAME}:${JIRA_PASSWORD}")))

$headers = @{
    "Authorization" = "Basic ${base64AuthInfo}"
    "Content-Type" = "application/json"
}

    # Get all fields from Jira
    $fieldsResponse = Invoke-RestMethod -Uri "http://localhost:8080/rest/api/2/customFields?startAt1&maxResults=1000&search=zzz" -Headers $headers -Method 'GET'
    $fields = $fieldsResponse.values
                Write-Host "Custom Field ID $CF_ID (Name: '$fields') is empty"
    # Iterate over each custom field
    foreach ($field in $fields) {
	try {
        $CF_ID = $field.numericId
        $FIELD_NAME = $field.name
	# Check if $CF_ID is not null
        if ($CF_ID) {
			
            # Check if the field is empty (by querying an issue with that field)
            $issueData = Invoke-RestMethod -Uri "http://localhost:8080/rest/api/2/search?jql=cf[$CF_ID] is not EMPTY" -Headers $headers -Method 'GET'

            # Check if the field is truly empty
            if ($issueData.total -eq 0) {
                Write-Host "Custom Field ID $CF_ID (Name: '$FIELD_NAME') is empty"
            } else {
                Write-Host "Custom Field ID $CF_ID (Name: '$FIELD_NAME') is not empty"
            }
        } else {
            Write-Host "Custom Field ID is null or not defined for '$FIELD_NAME'"
        }
	} catch {
		Write-Host "Failed to retrieve field $CF_ID data from Jira. Error: $_"
	}
    }
