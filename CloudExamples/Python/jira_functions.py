import base64

import requests
from decouple import config
from loguru import logger

# Load environment variables from .env file
jira_base_url = config("ATLASSIAN_BASE_URL")
jira_api_token = config("ATLASSIAN_API_TOKEN")
jira_api_email = config("ATLASSIAN_API_EMAIL")

# Atlassian REST API headers
base64_credentials = base64.b64encode(
    f"{jira_api_email}:{jira_api_token}".encode()
).decode()
headers = {
    "Authorization": f"Basic {base64_credentials}",
    "Content-Type": "application/json",
}


def get_all_projects():
    cloud_api_url = f"{jira_base_url}/rest/api/3/project/search"
    projects = []
    start_at = 0
    max_results = 50
    while True:
        params = {"startAt": start_at, "maxResults": max_results}
        response = requests.get(cloud_api_url, headers=headers, params=params)
        if response.status_code == 200:
            data = response.json()
            for project in data["values"]:
                projects.append(project)
            total = data["total"]
            start_at += max_results
            if start_at >= total:
                break
        else:
            logger.error(f"Error fetching projects: {response.text}")
            raise Exception(f"Error fetching projects: {response.text}")
    return projects


def get_project_by_id(id):
    url = f"{jira_base_url}/rest/api/3/project/{id}"
    response = requests.get(url, headers=headers)
    if response.status_code == 200:
        data = response.json()
    else:
        logger.error(f"Error fetching project: {response.text}")
        raise Exception(f"Error fetching project: {response.text}")
    return data
