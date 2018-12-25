#!/usr/bin/env bash

#install prerequisite for cubes
apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EF0F382A1A7B6500
apt-get update && apt-get -y install postgresql-client \
                                     ruby

#install flyway
mkdir -p /opt/flyway/
cd /opt/flyway/
wget https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/4.0.3/flyway-commandline-4.0.3-linux-x64.tar.gz
tar -xvzf flyway-commandline-4.0.3-linux-x64.tar.gz -C . --strip-components=1
rm flyway-commandline-4.0.3-linux-x64.tar.gz
