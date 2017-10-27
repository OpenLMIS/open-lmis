#!/usr/bin/env bash

#use openlmis web app as root war
rm -fr /usr/local/tomcat/webapps/ROOT
cp /libs/openlmis-web.war /usr/local/tomcat/webapps/ROOT.war

#download migration jars
mkdir -p /usr/local/tomcat/webapps/db/
cp /libs/db.jar /usr/local/tomcat/webapps/db/
cp /libs/migration.jar /usr/local/tomcat/webapps/db/

#unzip migration jars and add one extra migration file that creates atomfeed schema
unzip /usr/local/tomcat/webapps/db/db.jar -d /opt/flyway/sql/db
unzip /usr/local/tomcat/webapps/db/migration.jar -d /opt/flyway/sql/migration
echo "CREATE SCHEMA atomfeed;" > /opt/flyway/sql/db/V1__create_atomfeed_schema.sql

#add properties file that will contain db credentials
mkdir -p /usr/local/tomcat/extra_properties
cp -R /configuration/*.properties /usr/tomcat/extra_properties/
