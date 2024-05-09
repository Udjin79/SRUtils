/*
 * Created 2024.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

const issueTypeField = getFieldById("issuetype");
const user = await makeRequest("/rest/api/2/myself");
const { accountId } = user.body;
const userGroups= await makeRequest("/rest/api/2/user/groups?accountId=" + accountId);
const groupNames = userGroups.body.map(({ name }) => name);

// Select role for priority field to be displayed
const role = "Developer";

if (groupNames.includes(role)) {
	issueTypeField.setOptionsVisibility(["10001", "10004"], true)
}
