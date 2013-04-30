-- dump the current shema (i.e. back up )

-- modify the scheam version table to remove the version 34 and 35.1 / and also remove the 37and 38 if you are there and make the version number 33.2 the current version by changing the version colmun to true for version 33.2

-- run gradle migrateDB (i.e. from the project path )

-- run the sql script(i.e. found in db/newmigration/migration_script.sql ) to modify the indexes

========================================================================
# TO RUN the new migration script found in the migration module
========================================================================

first run(i.e. if the table is created already an error msg will be displayed that says it's already initialized)

> gradle setupmigrationDB

# >>> creates and initializes the custom table  "migration_schema_version" for our custom migrations under the migration module path
      ( Note : this command needs to run once only to create the table create)

> gradle migratemoduleDB

# runs all the migrations(scripts) under the custom path modules\migration\src\main\resources\db\migration
     (Note : run this command when ever you have an addition to the custom migrations )

