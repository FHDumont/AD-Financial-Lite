#!/bin/sh

# This is a script to start app via Docker Compose.

# Variables
CWD=$(dirname $0)
TOP=$(readlink -f ${CWD})

#Backup Files
# Not Needed as shouldn't be user altered. cp $TOP/docker-compose.yml $TOP/ad-fin-cloud/docker-compose.yml.backup
cp -f $TOP/controller.env $TOP/ad-fin-cloud/controller.env.backup

#Create and Edit secondary Env file
cp -f $TOP/ad-fin-cloud/controller.env.backup $TOP/controller-cloud.env
sed -r -i "s/^[#]*\s*DISABLE_CLOUD_APP=.*/DISABLE_CLOUD_APP=0/" $TOP/controller-cloud.env
AppNAME=$(sed -rn 's/^APPLICATION_NAME=([^\n]+)$/\1/p' $TOP/controller-cloud.env)
sed -r -i "s/^[#]*\s*APPLICATION_NAME=.*/APPLICATION_NAME=${AppNAME}-Cloud/" $TOP/controller-cloud.env

#Import the Cloud Version of docker-compose.yml
cp -f $TOP/ad-fin-cloud/docker-compose-cloud.yml $TOP/docker-compose.yml

# Start App
COMPOSE_HTTP_TIMEOUT=120 docker-compose -f $TOP/docker-compose.yml up -d