#!/bin/sh

# This is a script to restart app via Docker Compose.

# Variables
CWD=$(dirname $0)
TOP=$(readlink -f ${CWD})

# Stop App
COMPOSE_HTTP_TIMEOUT=120 docker-compose -f $TOP/docker-compose.yml down

docker system prune -f -a --volumes

rm -Rf agent-repo/AppServerAgent
rm -Rf agent-repo/MachineAgent

./build-all.sh

# Start App
COMPOSE_HTTP_TIMEOUT=120 docker-compose -f $TOP/docker-compose.yml up -d