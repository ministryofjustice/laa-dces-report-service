#!/bin/sh
FROM_DATE=$1
TO_DATE=$2

SERVER_PORT=8089

API_ENDPOINT="api/internal/v1/dces/report/fdc"
REQUEST_URI="localhost:${SERVER_PORT}/$API_ENDPOINT/$FROM_DATE/$TO_DATE"

echo "$REQUEST_URI"
curl -G $REQUEST_URI -i