/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 *
 * Part of the Examples.Behaviours package in SRUtils, this script is tailored for use in JIRA.
 * It demonstrates the capability of ScriptRunner to manipulate field values based on user input.
 * Specifically, this script automatically sets the 'Summary' field of an issue based on the
 * values selected in the 'Component/s' field and a date field. The summary is formatted to
 * include the component name and the selected date, thereby creating a clear and informative
 * issue summary. This is particularly useful in scenarios such as tracking emergency work,
 * where quick and descriptive issue creation is essential.
 */

package Examples.Behaviours

import com.onresolve.jira.groovy.user.FieldBehaviours
import com.onresolve.jira.groovy.user.FormField
import groovy.transform.BaseScript

import java.text.SimpleDateFormat

@BaseScript FieldBehaviours fieldBehaviours; // Annotation for field behaviors script.

// Initialize SimpleDateFormat to format dates.
SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

// Retrieve the value from the field that triggered the change.
def cascade_val = getFieldById(getFieldChanged()).getValue();
// Format the date value to a string.
String stringDate = dateFormat.format(new Date(cascade_val.getTime()));

// Get the value from the 'Component/s' field.
String component = getFieldByName("Component/s").getValue().toString();
// Access the 'Summary' field.
FormField summary = getFieldByName("Summary");
// Set the summary field's value to a formatted string including the component name and the date.
summary.setFormValue("Emergency work ${component} ${stringDate}");
