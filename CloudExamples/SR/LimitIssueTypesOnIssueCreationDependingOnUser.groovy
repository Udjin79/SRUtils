/*
 * Created 2024.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

const issueTypeField = getFieldById("issuetype")
const user = await makeRequest("/rest/api/2/myself");

const email = "user@example.com";
const removeAccessId = ["10000"]

if (user.body.emailAddress != email) {
	issueTypeField.setOptionsVisibility(removeAccessId, false)
}
