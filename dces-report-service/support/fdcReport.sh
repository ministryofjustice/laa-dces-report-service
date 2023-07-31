#!/bin/bash
FROM_DATE=$1
TO_DATE=$2
echo "calling between $FROM_DATE and $TO_DATE"
curl -G http://localhost:8089/report/fdcReport -H "fromDate:$FROM_DATE" -H "toDate:$TO_DATE"
