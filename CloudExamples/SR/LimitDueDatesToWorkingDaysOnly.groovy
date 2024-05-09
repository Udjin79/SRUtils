/*
 * Created 2024.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

const changedField = getChangeField();
const duedateField = getFieldById("customfield_10123");

if (changedField.getName() == "Due Date") {
	const date = new Date(changedField.getValue());
	const dayOfWeek = date.getDay();
	
	if ([0, 6].includes(dayOfWeek)) {
		duedateField.setValue(null);
	}
}

