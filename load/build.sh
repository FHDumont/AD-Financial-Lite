#!/bin/bash

if [ -z "$1" ] || [ $1 == "load" ]
then
    docker build -t fin-load -f docker/Dockerfile ./docker
else
    docker build -t fin-cron-load -f docker/Dockerfile-cron ./docker
fi 
