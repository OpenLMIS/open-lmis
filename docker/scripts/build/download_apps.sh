#!/usr/bin/env bash
#use openlmis web app as root war
rm -fr /usr/local/tomcat/webapps/ROOT
wget -O /usr/local/tomcat/webapps/ROOT.war http://10.71.21.139:8000/ROOT.war
# wget -O /usr/local/tomcat/webapps/ROOT.war --auth-no-challenge --http-user=user_ansible --no-check-certificate --http-password=b5c5990592a49e0df211b0e704d3bdd0 \
# https://52.69.16.156:8080/job/Lmis-cubes/lastSuccessfulBuild/artifact/open-lmis/modules/openlmis-web/build/libs/openlmis-web.war

#download migration jars
mkdir -p /usr/local/tomcat/webapps/db/
wget -O /usr/local/tomcat/webapps/db/db.jar --auth-no-challenge --http-user=user_ansible --no-check-certificate --http-password=b5c5990592a49e0df211b0e704d3bdd0 \
https://52.69.16.156:8080/job/Lmis-cubes/lastSuccessfulBuild/artifact/open-lmis/modules/db/build/libs/db.jar

wget -O /usr/local/tomcat/webapps/db/migration.jar --auth-no-challenge --http-user=user_ansible --no-check-certificate --http-password=b5c5990592a49e0df211b0e704d3bdd0 \
https://52.69.16.156:8080/job/Lmis-cubes/lastSuccessfulBuild/artifact/open-lmis/modules/migration/build/libs/migration.jar

#unzip migration jars and add one extra migration file that creates atomfeed schema
unzip /usr/local/tomcat/webapps/db/db.jar -d /opt/flyway/sql/db
unzip /usr/local/tomcat/webapps/db/migration.jar -d /opt/flyway/sql/migration
echo "CREATE SCHEMA atomfeed;" > /opt/flyway/sql/db/V1__create_atomfeed_schema.sql

#put cubes package in /app/cubes
# mkdir -p /app/cubes && wget -O /app/cubes/cubes.zip --auth-no-challenge --http-user=user_ansible --no-check-certificate --http-password=b5c5990592a49e0df211b0e704d3bdd0 \
# https://52.69.16.156:8080/job/Lmis-cubes/lastSuccessfulBuild/artifact/cubes.zip
#
# cd /app/cubes && unzip ./cubes.zip && rm cubes.zip
# #install cubes app dependencies
# pip install -r /app/cubes/bin/requirements.txt

#add properties file that will contain db credentials
mkdir -p /usr/local/tomcat/extra_properties
wget -O /usr/local/tomcat/extra_properties/app.properties https://raw.githubusercontent.com/SIGLUS/openlmis-devops/master/deployment/configuration/files/env/$ENV/app.properties
wget -O /usr/local/tomcat/extra_properties/atomfeed.properties https://raw.githubusercontent.com/SIGLUS/openlmis-devops/master/deployment/configuration/files/env/$ENV/atomfeed.properties
wget -O /usr/local/tomcat/extra_properties/default.properties https://raw.githubusercontent.com/SIGLUS/openlmis-devops/master/deployment/configuration/files/env/$ENV/default.properties
wget -O /usr/local/tomcat/extra_properties/mailing.properties https://raw.githubusercontent.com/SIGLUS/openlmis-devops/master/deployment/configuration/files/env/$ENV/mailing.properties

#add SIMAM template files
# mkdir -p /app/tomcat/openlmis/emailattachment/template
# mkdir -p /app/tomcat/openlmis/emailattachment/cache
# mkdir -p /app/tomcat/openlmis/tmp
#
# wget -O /app/tomcat/openlmis/emailattachment/template/template_Simam_import_Regimen.xlsx https://github.com/SIGLUS/open-lmis/raw/2.0-moz/modules/export/src/main/resources/templates/template_Simam_import_Regimen.xlsx
# wget -O /app/tomcat/openlmis/emailattachment/template/template_Simam_import_Regimen_EMPTY.xlsx https://github.com/SIGLUS/open-lmis/raw/2.0-moz/modules/export/src/main/resources/templates/template_Simam_import_Regimen_EMPTY.xlsx
# wget -O /app/tomcat/openlmis/emailattachment/template/template_Simam_import_Requi.xlsx https://github.com/SIGLUS/open-lmis/raw/2.0-moz/modules/export/src/main/resources/templates/template_Simam_import_Requi.xlsx
