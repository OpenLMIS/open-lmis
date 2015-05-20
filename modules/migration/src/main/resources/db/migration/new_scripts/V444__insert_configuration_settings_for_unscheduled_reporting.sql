DELETE FROM configuration_settings WHERE key = 'UNSCHEDULED_REPORTING_CUT_OFF_DATE';
INSERT INTO configuration_settings(key, value, name, description, groupname, valuetype)
values('UNSCHEDULED_REPORTING_CUT_OFF_DATE',30,'Cut-off date to track unscheduled reporting','Cut-off date to track unscheduled reporting.','R & R','TEXT');

