#!/bin/bash
display_error_message () {
  printf "Usage: %s: [-d] [-e value]\n\t-d\t\trun tasks within docker containers\n\t-e(REQUIRED)\tenvironment: local or ci\n"
  exit 2;
}

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
  printf "INFO: Running tasks within a docker container to local enviroment...\n"
  sh -c "$LOCAL_DOCKER_SCRIPT"
  exit 0;
fi

if [[ ! -z "$dockerflag" && "$enval" == "ci"  ]]; then
  printf "INFO: Running tasks within a docker container to CI enviroment...\n"
  sh -c "$CI_DOCKER_SCRIPT"
  exit 0;
fi

printf "Running tests locally...\n"
sh -c "$LOCAL_SCRIPT"
