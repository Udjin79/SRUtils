package Examples.Behaviours

import com.onresolve.jira.groovy.user.FieldBehaviours
import com.onresolve.jira.groovy.user.FormField
import groovy.transform.BaseScript

@BaseScript FieldBehaviours fieldBehaviours

/*
 * Needs to be attached to Required field. In
 * In this example is used number field, but it's just example of logic
 */

final String ERROR_MESSAGE = "This error message is shown when the field is not empty and has invalid value"

FormField numField = getFieldById(getFieldChanged())
Float numFieldValue = (Float) numField?.getValue()

if (numFieldValue != null && numFieldValue > 1000) {
	numField?.setValid(false)
	numField?.setError(ERROR_MESSAGE)
} else {
	numField?.setValid(true)
	numField?.clearError()
}