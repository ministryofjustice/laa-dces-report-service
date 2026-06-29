#!/bin/bash

# Ensure the script stops if any command fails
set -euo pipefail

# Specify the vault and the document name in 1Password
VAULT="LAA Crime Apps"
DOCUMENT="EnvironmentVariables-DCESReportService-App"
APP_ENV_FILE="./app.env"

function cleanup()
{
    echo "Stopping Docker containers..."
    docker-compose down || true

    echo "Removing app.env file"
    rm -f "$APP_ENV_FILE"
}

trap cleanup EXIT

echo "Signing into 1Password..."
export OP_SESSION_ministryofjustice="$(op signin --account ministryofjustice --raw)"

echo "Fetching latest .env file from 1Password..."
op document get "$DOCUMENT" --vault "$VAULT" --output "$APP_ENV_FILE" --force

if [ ! -f "$APP_ENV_FILE" ]; then
    echo "Failed to retrieve .env file from 1Password."
    exit 1
fi

echo ".env file successfully retrieved."

echo "Starting Docker containers..."
docker-compose up --build
