#!/bin/sh
FROM_DATE=$1
TO_DATE=$2

API_PORT=$(printenv DCES_SERVER_PORT)
API_ENDPOINT="api/internal/v1/dces/report/fdc"
REQUEST_URI="localhost:${API_PORT}/$API_ENDPOINT/$FROM_DATE/$TO_DATE"

echo "$REQUEST_URI"
curl -G $REQUEST_URI -i