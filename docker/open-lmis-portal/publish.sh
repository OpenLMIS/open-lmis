#!/bin/bash
set -e
docker login --username $DOCKER_USERNAME --password $DOCKER_PASSWORD
docker push siglus/open-lmis-portal
