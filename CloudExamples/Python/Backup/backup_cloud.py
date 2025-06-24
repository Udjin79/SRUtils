import json
import time

import requests
from decouple import config
from datetime import date
import threading

from requests.auth import HTTPBasicAuth

# Load environment variables from .env file
JIRA_BASE_URL = config("JIRA_BASE_URL")
API_TOKEN = config("API_TOKEN")
API_EMAIL = config("API_EMAIL")
JIRA_GUID = config("JIRA_GUID")

# Function to initiate a Jira Cloud backup
def initiate_backup():
    headers = {"Content-Type": "application/json", "Accept": "application/json"}

    backup_data = {"cbAttachments": "true", "exportToCloud": "true"}

    # Send a POST request to initiate the backup
    response = requests.post(
        f"{JIRA_BASE_URL}/rest/backup/1/export/runbackup",
        headers=headers,
        auth=HTTPBasicAuth(API_EMAIL, API_TOKEN),
        json=backup_data,
    )

    if response.status_code == 200:
        print("Backup initiated successfully.")
        time.sleep(5)
    else:
        print(
            f"Failed to initiate backup. Status code: {response.status_code}. Error: {response.text}"
        )


# Function to monitor the backup status and download when ready
def last_backup_status():
    headers = {"Accept": "application/json"}

    while True:
        task_id_response = requests.get(
            f"{JIRA_BASE_URL}/rest/backup/1/export/lastTaskId",
            headers=headers,
            auth=HTTPBasicAuth(API_EMAIL, API_TOKEN),
        )

        if task_id_response.status_code == 200:
            backup_id = task_id_response.json()
            if backup_id is not None:
                response = requests.get(
                    f"{JIRA_BASE_URL}/rest/backup/1/export/getProgress?taskId={backup_id}",
                    headers=headers,
                    auth=HTTPBasicAuth(API_EMAIL, API_TOKEN),
                )
                if response.status_code == 200:
                    backup_status = response.json()
                    status = backup_status.get("status", "Unknown")

                    if status == "Success":
                        result = backup_status.get("result", "Unknown")
                        print(
                            f"Backup ID: {backup_id}. Status: {status}\nResult: {result}"
                        )
                        download_backup_result(result)
                        break
                    elif status == "Failed":
                        print(f"Backup failed. Backup ID: {backup_id}")
                        break
                    else:
                        print(f"Backup ID: {backup_id}. Status: {status}")
                        time.sleep(10)
                        last_backup_status()
                        break
                else:
                    print(
                        f"Failed to retrieve backup status. Status code: {response.status_code}"
                    )
            else:
                print("Backup ID not found in response.")
        else:
            print(
                f"Failed to retrieve backup task ID. Status code: {task_id_response.status_code}"
            )


# Function to download the backup result
def download_backup_result(backup_id):
    headers = {}

    response = requests.get(
        f"{JIRA_BASE_URL}/plugins/servlet/{backup_id}",
        headers=headers,
        auth=HTTPBasicAuth(API_EMAIL, API_TOKEN),
    )
    if response.status_code == 200:
        now = date.today()
        with open(f"backup_results/backup_{now}.zip", "wb") as file:
            file.write(response.content)
        print(f"Backup result file downloaded for Backup ID: {backup_id}")
    else:
        print(
            f"Failed to download backup result file. Status code: {response.status_code}"
        )


def get_rules_json():
    headers = {}
    try:
        # Send a GET request to the URL to retrieve JSON data
        response = requests.get(
            f"{JIRA_BASE_URL}/gateway/api/automation/internal-api/jira/{JIRA_GUID}/pro/rest/GLOBAL/rule/export",
            headers=headers,
            auth=HTTPBasicAuth(API_EMAIL, API_TOKEN),
        )
        # Check if the request was successful (status code 200)
        if response.status_code == 200:
            return response.json()
        else:
            print(f"Failed to retrieve JSON data. Status code: {response.status_code}")
            return None
    except Exception as e:
        print(f"An error occurred: {str(e)}")
        return None


def save_rules_json_to_file(json_data):
    try:
        # Save the JSON data to a file
        now = date.today()
        with open(f"backup_results/rules_list_{now}.json", "w") as file:
            json.dump(json_data, file, indent=4)
        print(f"JSON data successfully saved")
    except Exception as e:
        print(f"An error occurred while saving to the file: {str(e)}")


# Example usage
if __name__ == "__main__":
    # Start initiate_backup
    initiate_backup()
    # Check the status of the backup and download when ready
    last_backup_status()

    json_data = get_rules_json()
    if json_data:
        save_rules_json_to_file(json_data)
