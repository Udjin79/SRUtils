/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package ConsoleExamples

import com.atlassian.jira.bc.project.component.ProjectComponent
import com.onresolve.jira.groovy.user.FieldBehaviours
import com.onresolve.jira.groovy.user.FormField
import groovy.transform.BaseScript

@BaseScript FieldBehaviours fieldBehaviours
FormField changeManager = getFieldById(getFieldChanged())
List<ProjectComponent> components = getFieldById("components").getValue() as List<ProjectComponent>
changeManager.clearError()

Map managers = [
		'Change Level1': ["users": ['User One', 'User Two'], "message": "is not a manager, please select a manager to approve the change"],
		'Change Level2': ["users": ['User Three', 'User Four'], "message": "is not a director, please select a director to approve the change"],
		'Change Level3': ["users": ['User Five', 'User Six'], "message": "is not a c-suite, select a c-suite to approve the change"]
]

List<String> intersections = components.name.intersect(managers.keySet())

if (changeManager && intersections) {
	intersections.each { String key ->
		if (!(changeManager.value.toString() in managers[key]['users'])) {
			changeManager.value ? changeManager.setError("$changeManager.value ${managers[key]['message']}") : changeManager.clearError()
		}
	}
}