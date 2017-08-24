#!/usr/bin/env bash

#replace placeholders in properties files and ini file
#using the env vars provided with "docker run"
ruby /scripts/run/replace_configs.rb

#run migration
sh /scripts/run/migration.sh

#start cubes
# sh /app/cubes/bin/start.sh

#setenv.sh will make sure tomcat pickup the extra properties files
cp /scripts/run/setenv.sh /usr/local/tomcat/bin/setenv.sh

#start tomcat
catalina.sh run
