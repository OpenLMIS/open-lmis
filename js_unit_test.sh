#!/bin/bash
display_error_message () {
  printf "Usage: %s: [-d] [-e value]\n\t-d\t\trun test within docker containers\n\t-e(REQUIRED)\tenvironment: local or ci\n"
  exit 2;
}

taskrunner="${TASKRUNNER:-siglus/taskrunner:0.0.2}"
pipelinename="${PIPELINE_NAME:-openlmis_portal}"
declare -A envvalues=(
  [local]=1 [ci]=1
)
dockerflag=
envflag=
while getopts de: name
do
  case $name in
    d)    dockerflag=1;;
    e)    envflag=1
          enval="$OPTARG";;
    ?)    display_error_message
  esac
done

if [[ ! -z "$envflag" ]]; then
  [[ ! -n "${envvalues[$enval]}"  ]] && display_error_message
fi

if [[ -z "$dockerflag" && "$enval" == "ci" || -z "$envflag"  ]]; then
  display_error_message
fi

if [[ ! -z "$dockerflag" && "$enval" == "local"  ]]; then
  printf "INFO: Running tests within a docker container to local enviroment...\n"
  docker run --rm -v $(pwd)/modules/openlmis-web:/openlmis-web \
             -w /openlmis-web \
             $taskrunner sh -c "npm install && TZ=UTC npm run unit_test"
  exit 0;
fi

if [[ ! -z "$dockerflag" && "$enval" == "ci"  ]]; then
  printf "INFO: Running tests within a docker container to CI enviroment...\n"
  docker run --rm --volumes-from $HOSTNAME \
             -w /godata/pipelines/$pipelinename/modules/openlmis-web \
             $taskrunner sh -c "npm install && TZ=UTC npm run unit_test"
  exit 0;
fi

printf "Running tests locally...\n"
sh -c "cd modules/openlmis-web && npm install && TZ=UTC npm run unit_test"
