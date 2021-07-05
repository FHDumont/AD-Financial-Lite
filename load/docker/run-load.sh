#!/usr/bin/env bash

echo IMPACTED_TIER=$IMPACTED_TIER
echo RESOURCE_HOG=$RESOURCE_HOG
echo APP_BASE_URL=$APP_BASE_URL
(timeout 1200 node /src/index.js; exit 0)

if [ $RESOURCE_HOG == "Memory" ]
then
    curl "$APP_BASE_URL/getQuote?spike=memory&hogstatus=off"
fi