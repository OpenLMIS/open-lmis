-- add the name column, which was missing before
alter table configuration_settings
	add name varchar(250) null;

-- add the descriptions column
alter table configuration_settings
	add description varchar(1000) null;

-- add the group name column wich was missing before
alter table configuration_settings
	add groupName varchar(250) not null default('General');

-- add the display order column which was missing before.
alter table configuration_settings
	add displayOrder int not null default(1);

-- add the value types column,
-- the possible values for this column include but not limited to ..
-- TEXT_AREA, TEXT, OPTION, BOOLEAN, NUMBER, DECIMAL, EMAIL, PASSWORD
alter table configuration_settings
add valueType varchar(250) default('TEXT') not null;

-- Add the value options column
alter table configuration_settings
  add valueOptions varchar(1000);

-- copy over the name from the keys column
update configuration_settings set name = key;

-- convert the key column to an upper case text keys
update configuration_settings set key = UPPER(key);

-- now let's clean out the required fields
alter table configuration_settings
	alter column name set not null;