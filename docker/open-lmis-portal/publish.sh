#!/bin/bash
set -e
docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
docker push siglus/open-lmis-portal
