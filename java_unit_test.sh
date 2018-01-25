#!/bin/bash
set -e

taskrunner="${TASKRUNNER:-siglus/taskrunner:0.0.2}"
pipelinename="${PIPELINE_NAME:-openlmis_portal}"
dockernet="${DOCKER_NET:-gocd_default}"
dbserver="${DB_SERVER:-db}"
gradleexec="./gradlew clean :modules:db:setupDB :modules:migration:setupExtensions test -x :modules:openlmis-web:less -PdatabaseHostName=$dbserver"
LOCAL_DOCKER_SCRIPT="docker run --rm -v $(pwd):/openlmis -w /openlmis $taskrunner $gradleexec"
CI_DOCKER_SCRIPT="docker run --rm --net $dockernet --volumes-from $HOSTNAME -w /godata/pipelines/$pipelinename $taskrunner $gradleexec"
LOCAL_SCRIPT="$gradleexec"

. ./task_executor.sh
