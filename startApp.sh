#!/bin/sh

# This is a script to start app via Docker Compose.

# Variables
CWD=$(dirname $0)
# TOP=$(readlink -f ${CWD})
TOP=$(pwd)

#Backup Config Files
# Not Needed as shouldn't be user altered. cp $TOP/docker-compose.yml $TOP/ad-fin-cloud/docker-compose.yml.backup
cp -f $TOP/controller.env $TOP/ad-fin-cloud/controller.env.backup

#Import the NON-Cloud Version of docker-compose.yml
cp -f $TOP/ad-fin-cloud/docker-compose-original.yml $TOP/docker-compose.yml

# Start App
COMPOSE_HTTP_TIMEOUT=120 docker-compose -f $TOP/docker-compose.yml up -d
