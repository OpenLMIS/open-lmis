#!/bin/bash
taskrunner="${TASKRUNNER:-siglus/taskrunner:0.0.2}"
pipelinename="${PIPELINE_NAME:-openlmis_portal}"
dockernet="${DOCKER_NET:-gocd_default}"
dbserver="${DB_SERVER:-db}"
LOCAL_DOCKER_SCRIPT="docker run --rm -v $(pwd):/openlmis -w /openlmis $taskrunner ./gradlew :modules:db:setupDB :modules:migration:setupExtensions -PdatabaseHostName=$dbserver"
CI_DOCKER_SCRIPT="docker run --rm --net $dockernet --volumes-from $HOSTNAME -w /godata/pipelines/$pipelinename $taskrunner ./gradlew :modules:db:setupDB :modules:migration:setupExtensions -PdatabaseHostName=$dbserver"
LOCAL_SCRIPT="./gradlew :modules:db:setupDB :modules:migration:setupExtensions -PdatabaseHostName=$dbserver"

. ./task_executor.sh
