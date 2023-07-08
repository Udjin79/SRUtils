/*
 * Created 2023.
 * @author ScriptRunner for Jira on Cloud
 * @github https://github.com/Udjin79/SRUtils
 */

package CloudExamples

post('/rest/api/3/issue/bt-2/worklog')
		.header('Content-Type', 'application/json')
		.body(
				[
						timeSpent: "20m",
						comment  : [
								type   : "doc",
								version: 1,
								content: [
										[
												type   : "paragraph",
												content: [
														[
																text: "This is from SR4J on Cloud.",
																type: "text"
														]
												]
										]
								]
						]
				])
		.asString().body
