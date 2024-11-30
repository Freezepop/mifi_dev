#!/usr/bin/python3

import pytest
from redirect_flask_app import app

@pytest.fixture
def client():
    app.config["TESTING"] = True
    return app.test_client()

def test_redirect_with_url(client):
    response = client.get("/redirect?url=https://example.com")
    assert response.status_code == 302
    assert response.location == "https://example.com"

def test_redirect_without_url(client):
    response = client.get("/redirect")
    assert response.status_code == 400
    assert b'No URL for redirection' in response.data