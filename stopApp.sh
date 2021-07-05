#!/bin/sh

# This is a script to stop app via Docker Compose.

# Variables
CWD=$(dirname $0)
# TOP=$(readlink -f ${CWD})
TOP=$(pwd)

# Stop App
COMPOSE_HTTP_TIMEOUT=120 docker-compose -f $TOP/docker-compose.yml down

rm -f $TOP/controller-cloud.env

docker system prune -f