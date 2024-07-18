import json
import re

# Load the JSON data from the uploaded file

with open("./input/SearchRequest.json", 'r', encoding = 'utf-8') as file:
    data = json.load(file)


# Define the function to filter out custom fields
def remove_fields_by_name_or_type(fields, names_to_remove, types_to_remove):
    return [
        field for field in fields
        if field['fieldName'] not in names_to_remove and field['fieldType'] not in types_to_remove
    ]


def rename_and_shift_text(text, old_prefix, new_prefix, shift_amount, threshold, lower_shift):
    pattern = rf"{old_prefix}-(\d+)"

    def replacement(match):
        number = int(match.group(1))
        if number < threshold:
            number += lower_shift
        else:
            number += shift_amount
        return f"{new_prefix}-{number}"

    return re.sub(pattern, replacement, text)


# Define the function to recursively rename and shift keys in the entire data structure
def rename_and_shift_keys(data, old_prefix, new_prefix, shift_amount, threshold = 50000, lower_shift = 1500):
    if isinstance(data, dict):
        for key, value in data.items():
            if isinstance(value, str):
                data[key] = rename_and_shift_text(value, old_prefix, new_prefix, shift_amount, threshold, lower_shift)
            else:
                rename_and_shift_keys(value, old_prefix, new_prefix, shift_amount, threshold, lower_shift)
    elif isinstance(data, list):
        for index, item in enumerate(data):
            if isinstance(item, str):
                data[index] = rename_and_shift_text(item, old_prefix, new_prefix, shift_amount, threshold, lower_shift)
            else:
                rename_and_shift_keys(item, old_prefix, new_prefix, shift_amount, threshold, lower_shift)
    return data


# Define the names and types to remove
types_to_remove = [
    "com.onresolve.jira.groovy.groovyrunner:scripted-field",
    "com.almworks.jira.structure:index-monitor-cf-type",
    "com.pyxis.greenhopper.jira:gh-lexo-rank",
    "com.pyxis.greenhopper.jira:gh-sprint"
]
names_to_remove = [
    "Approvals", "Epic Color", "Rank", "Rank (Obsolete)", "Story_Points", "Progress", "Units(WBSGantt)", "Target"
]

# Process each issue to remove the specified custom fields
for project in data['projects']:
    project['issues'] = rename_and_shift_keys(project['issues'], "OLD", "NEW", 5000)
    for issue in project['issues']:
        issue['customFieldValues'] = remove_fields_by_name_or_type(issue['customFieldValues'], names_to_remove,
                                                                   types_to_remove)

# Save the filtered data to a new JSON file
filtered_file_path = './output/filtered_issues.json'
with open(filtered_file_path, 'w', encoding = 'utf-8') as file:
    json.dump(data, file, ensure_ascii = False, indent = 4)
