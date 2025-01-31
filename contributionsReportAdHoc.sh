#!/bin/sh
REPORT_TITLE=$1
FROM_DATE=$2
TO_DATE=$3

CONFIG_FILE="./dces-report-service/src/main/resources/application.yaml"
SERVER_PORT=$(egrep -e '^server:+$' $CONFIG_FILE -A 1 | grep "port:" | awk -F: '{print $2}')

API_ENDPOINT="api/internal/v1/dces/report/contributions"
REQUEST_URI="localhost:${SERVER_PORT:1}/$API_ENDPOINT/$REPORT_TITLE/$FROM_DATE/$TO_DATE"

echo "$REQUEST_URI"
curl -G $REQUEST_URI -i