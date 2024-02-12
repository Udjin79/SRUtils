import base64

import requests
from decouple import config
from loguru import logger

# Load environment variables from .env file
wiki_base_url = config("ATLASSIAN_BASE_URL")
wiki_api_token = config("ATLASSIAN_API_TOKEN")
wiki_api_email = config("ATLASSIAN_API_EMAIL")

# Atlassian REST API headers
base64_credentials = base64.b64encode(
    f"{wiki_api_email}:{wiki_api_token}".encode()
).decode()
headers = {
    "Authorization": f"Basic {base64_credentials}",
    "Content-Type": "application/json",
}


def get_all_spaces():
    spaces = []
    url = f"{wiki_base_url}/wiki/api/v2/spaces"
    while True:
        response = requests.get(url, headers=headers)
        if response.status_code == 200:
            data = response.json()
            spaces.extend(data["results"])
            if data["_links"].get("next"):
                url = f'{wiki_base_url}{data["_links"]["next"]}'
            else:
                break
        else:
            break
    return spaces


def get_space_by_id(id):
    url = f"{wiki_base_url}/wiki/api/v2/spaces/{id}/?description-format=plain"
    response = requests.get(url, headers=headers)
    if response.status_code == 200:
        data = response.json()
    else:
        logger.error(f"Error fetching spaces: {response.text}")
        raise Exception(f"Error fetching spaces: {response.text}")
    return data


def get_all_pages_by_space(space_id):
    pages = []
    url = f"{wiki_base_url}/wiki/api/v2/spaces/{space_id}/pages"
    while True:
        response = requests.get(url, headers=headers)
        if response.status_code == 200:
            data = response.json()
            pages.extend(data["results"])
            if data["_links"].get("next"):
                url = f'{wiki_base_url}{data["_links"]["next"]}'
            else:
                break
        else:
            break
    return pages


def get_all_pages():
    pages = []
    url = f"{wiki_base_url}/wiki/api/v2/pages"
    while True:
        response = requests.get(url, headers=headers)
        if response.status_code == 200:
            data = response.json()
            pages.extend(data["results"])
            if data["_links"].get("next"):
                url = f'{wiki_base_url}{data["_links"]["next"]}'
            else:
                break
        else:
            break
    return pages


def get_page_by_id(id):
    url = f"{wiki_base_url}/wiki/api/v2/pages/{id}"
    response = requests.get(url, headers=headers)
    if response.status_code == 200:
        data = response.json()
    else:
        logger.error(f"Error fetching pages: {response.text}")
        raise Exception(f"Error fetching pages: {response.text}")
    return data
