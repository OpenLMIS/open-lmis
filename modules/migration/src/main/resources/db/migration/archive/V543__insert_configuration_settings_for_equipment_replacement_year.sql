DELETE FROM configuration_settings WHERE key = 'EQUIPMENT_REPLACEMENT_YEAR';
INSERT INTO configuration_settings(key, value, name, description, groupname, valuetype)
values('EQUIPMENT_REPLACEMENT_YEAR',11,'Standard Years For Equipment Replacement','Standard Years For Equipment Replacement.','GENERAL','TEXT');


DELETE FROM configuration_settings WHERE key = 'YEAR_OF_EQUIPMENT_REPLACEMENT';
INSERT INTO configuration_settings(key, value, name, description, groupname, valuetype)
values('YEAR_OF_EQUIPMENT_REPLACEMENT',5,'Number Of Years For Equipment Plan','Number Of Years For Equipment Plan.','GENERAL','TEXT');

