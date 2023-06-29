/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Behaviours

import com.onresolve.jira.groovy.user.FieldBehaviours
import com.onresolve.jira.groovy.user.FormField
import java.text.SimpleDateFormat
import groovy.transform.BaseScript


@BaseScript FieldBehaviours fieldBehaviours
SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy")

def cascade_val = getFieldById(getFieldChanged()).getValue()
String stringDate = dateFormat.format(new Date(cascade_val.getTime()))

String component = getFieldByName('Component/s').getValue().toString()
FormField summary = getFieldByName('Summary')
summary.setFormValue("Emergency work ${component} ${stringDate}")

