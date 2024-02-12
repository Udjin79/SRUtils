import base64

import requests
from decouple import config
from loguru import logger

# Load environment variables from .env file
cloud_base_url = config("ATLASSIAN_BASE_URL")
cloud_api_token = config("ATLASSIAN_API_TOKEN")
cloud_api_email = config("ATLASSIAN_API_EMAIL")

# Atlassian REST API headers
base64_credentials = base64.b64encode(
    f"{cloud_api_email}:{cloud_api_token}".encode()
).decode()
headers = {
    "Authorization": f"Basic {base64_credentials}",
    "Content-Type": "application/json",
}


def get_cloud_users():
    cloud_api_url = f"{cloud_base_url}/rest/api/3/users/search"
    users = []
    start_at = 0
    max_results = 50

    while True:
        print(start_at)
        params = {"startAt": start_at, "maxResults": max_results}
        response = requests.get(cloud_api_url, headers=headers, params=params)
        if response.status_code == 200:
            data = response.json()
            if data:
                for user in data:
                    groups = get_cloud_user_groups(user["accountId"])
                    user.update({"groups": groups})
                    users.append(user)
                start_at += max_results
            else:
                break
        else:
            logger.error(f"Error fetching users: {response.text}")
            raise Exception(f"Error fetching users: {response.text}")
    return users


def get_cloud_groups():
    cloud_api_url = f"{cloud_base_url}/rest/api/3/group/bulk"
    groups = []
    start_at = 0
    max_results = 50
    while True:
        params = {"startAt": start_at, "maxResults": max_results}
        response = requests.get(cloud_api_url, headers=headers, params=params)
        if response.status_code == 200:
            data = response.json()
            for group in data["values"]:
                users = get_cloud_group_users(group["groupId"])
                group.update({"users": users})
                groups.append(group)
            total = data["total"]
            start_at += max_results
            if start_at >= total:
                break
        else:
            logger.error(f"Error fetching groups: {response.text}")
            raise Exception(f"Error fetching groups: {response.text}")
    return groups


def get_cloud_group_users(group_id):
    cloud_api_url = f"{cloud_base_url}/rest/api/3/group/member?groupId={group_id}"
    users = []
    start_at = 0
    max_results = 50
    while True:
        params = {
            "startAt": start_at,
            "maxResults": max_results,
            "includeInactiveUsers": True,
        }
        response = requests.get(cloud_api_url, headers=headers, params=params)
        if response.status_code == 200:
            data = response.json()
            users.extend(data["values"])
            total = data["total"]
            start_at += max_results
            if start_at >= total:
                break
        else:
            logger.error(f"Error fetching users: {response.text}")
            raise Exception(f"Error fetching users: {response.text}")
    return users


def get_cloud_user_groups(account_id):
    cloud_api_url = f"{cloud_base_url}/rest/api/3/user/groups"
    groups = []
    params = {
        "accountId": account_id,
    }
    response = requests.get(cloud_api_url, headers=headers, params=params)
    if response.status_code == 200:
        data = response.json()
        groups.extend(data)
    else:
        logger.error(f"Error fetching groups: {response.text}")
        raise Exception(f"Error fetching groups: {response.text}")
    return groups


def get_user_by_email(data, email):
    for item in data:
        if "emailAddress" in item and item["emailAddress"] == email:
            return item["accountId"]
    return None
