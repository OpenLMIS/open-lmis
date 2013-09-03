
DELETE FROM configuration_settings WHERE key = 'INDICATOR_PRODUCTS';
INSERT INTO configuration_settings(key, value, name, groupname, valuetype)
values('INDICATOR_PRODUCTS','Indicator Products','Indicator Products','GENERAL','TEXT')