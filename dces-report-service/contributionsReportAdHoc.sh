#!/bin/sh
FROM_DATE=$1
TO_DATE=$2

API_PORT=$(printenv LAA_DCES_REPORT_SERVICE_SERVICE_PORT_HTTP)
API_ENDPOINT="api/internal/v1/dces/report/contributions"
REQUEST_URI="localhost:${API_PORT}/$API_ENDPOINT/$FROM_DATE/$TO_DATE"

echo "$REQUEST_URI"
curl $REQUEST_URI -i
echo "  \n"