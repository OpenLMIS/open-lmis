delete from configuration_settings where key = 'VACCINE_DROPOUT_DTP';
INSERT INTO configuration_settings (key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) 
VALUES ('VACCINE_DROPOUT_DTP', 'V010', 'DTP product code', 'Used in vaccine reports for calculating dropout rate', 'VACCINE', '1', 'TEXT', NULL, 't');

delete from configuration_settings where key = 'VACCINE_DROPOUT_BCG';
INSERT INTO configuration_settings (key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) 
VALUES ('VACCINE_DROPOUT_BCG', 'V001', 'BCG product code', 'Used in vaccine reports for calculating dropout rate', 'VACCINE', '1', 'TEXT', NULL, 't');

delete from configuration_settings where key = 'VACCINE_DROPOUT_MR';
INSERT INTO configuration_settings (key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) 
VALUES ('VACCINE_DEMOGRAPHIC_ESTIMATE_COHORT_ID', 'V009', 'MR product code', 'Used in vaccine reports for calculating dropout rate', 'VACCINE', '1', 'TEXT', NULL, 't');
 