#!/bin/bash
taskrunner="${TASKRUNNER:-siglus/taskrunner:0.0.2}"
pipelinename="${PIPELINE_NAME:-openlmis_portal}"
LOCAL_DOCKER_SCRIPT="docker run --rm -v $(pwd)/modules/openlmis-web:/openlmis-web -w /openlmis-web $taskrunner sh -c \"npm install && TZ=UTC npm run unit_test\""
CI_DOCKER_SCRIPT="docker run --rm --volumes-from $HOSTNAME -w /godata/pipelines/$pipelinename/modules/openlmis-web $taskrunner sh -c \"npm install && TZ=UTC npm run unit_test\""
LOCAL_SCRIPT="cd modules/openlmis-web && npm install && TZ=UTC npm run unit_test"

. ./task_executor.sh
