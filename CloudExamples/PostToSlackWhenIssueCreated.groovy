package CloudExamples

// Specify the key of the issue to get the fields from
def issueKey = issue.key

// Get the issue summary
def summary = issue.fields.summary

// Get the issue description
def description = issue.fields.description

// Specify the name of the slack room to post to
def channelName = '<ChannelNameHere>'

// Specify the name of the user who will make the post
def username = '<UsernameHere>'

// Specify the message metadata
Map msg_meta = [channel: channelName, username: username, icon_emoji: ':rocket:']

// Specify the message body which is a simple string
Map msg_dets = [text: "A new issue was created with the details below: \n Issue key = ${issueKey} \n Issue Sumamry = ${summary} \n Issue Description = ${description}"]

// Post the constructed message to slack
def postToSlack = post('https://slack.com/api/chat.postMessage')
        .header('Content-Type', 'application/json')
        .header('Authorization', "Bearer ${SLACK_API_TOKEN}") // Store the API token as a script variable named SLACK_API_TOKEN
        .body(msg_meta + msg_dets)
        .asObject(Map)
        .body

assert postToSlack: "Failed to create Slack message check the logs tab for more details"