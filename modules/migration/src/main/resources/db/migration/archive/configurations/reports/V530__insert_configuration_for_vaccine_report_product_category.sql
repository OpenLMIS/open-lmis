DELETE FROM configuration_settings WHERE key = 'VACCINE_REPORT_VACCINE_CATEGORY_CODE';
INSERT INTO configuration_settings(key, value, name, description, groupname, valuetype)
values('VACCINE_REPORT_VACCINE_CATEGORY_CODE','Vaccine','Vaccine Product Category Code','Use this code to filter data from data source','VACCINE','TEXT');

DELETE FROM configuration_settings WHERE key = 'VACCINE_REPORT_VITAMINS_CATEGORY_CODE';
INSERT INTO configuration_settings(key, value, name, description, groupname, valuetype)
values('VACCINE_REPORT_VITAMINS_CATEGORY_CODE','vit','Syringes Product Category Code','Use this code to filter data from data source','VACCINE','TEXT');

DELETE FROM configuration_settings WHERE key = 'VACCINE_REPORT_SYRINGES_CATEGORY_CODE';
INSERT INTO configuration_settings(key, value, name, description, groupname, valuetype)
values('VACCINE_REPORT_SYRINGES_CATEGORY_CODE','Syringes and safety boxes','Syringes Product Category Code','Use this code to filter data from data source','VACCINE','TEXT');

