DELETE FROM configuration_settings WHERE key = 'LATE_REPORTING_DAYS';
INSERT INTO configuration_settings(key, value, name, description, groupname, valuetype)
values('LATE_REPORTING_DAYS',10,'Number of days to track late reporting','Number of days to track late reporting.','GENERAL','TEXT')