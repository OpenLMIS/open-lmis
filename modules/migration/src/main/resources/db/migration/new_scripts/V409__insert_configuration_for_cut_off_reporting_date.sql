
DELETE FROM configuration_settings WHERE key = 'DISTRICT_REPORTING_CUT_OFF_DATE';
INSERT INTO configuration_settings(key, value, name, description, groupname, valuetype)
values('DISTRICT_REPORTING_CUT_OFF_DATE',14,'Cut-off date to track distict timeliness reporting','Cut-off date to track distict timeliness reporting.','GENERAL','TEXT');

DELETE FROM configuration_settings WHERE key = 'MSD_ZONE_REPORTING_CUT_OFF_DATE';
INSERT INTO configuration_settings(key, value, name, description, groupname, valuetype)
values('MSD_ZONE_REPORTING_CUT_OFF_DATE',21,'Cut-off date to track MSD Zone Timeliness Reporting','Cut-off date to track MSD Zone Timeliness Reporting.','GENERAL','TEXT')
