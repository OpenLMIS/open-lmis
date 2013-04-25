-- dump the current shema (i.e. back up )

-- modify the scheam version table to remove the version 34 and 35.1 / and also remove the 37and 38 if you are there and make the version number 33.2 the current version by changing the version colmun to true for version 33.2

-- run gradle migrateDB (i.e. from the project path )

-- run the sql script(i.e. found in db/newmigration/migration_script.sql ) to modify the indexes