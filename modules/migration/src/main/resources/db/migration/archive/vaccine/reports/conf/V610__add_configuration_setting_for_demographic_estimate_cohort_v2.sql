delete from configuration_settings where key = 'VACCINE_DEMOGRAPHIC_ESTIMATE_COHORT_ID';
INSERT INTO configuration_settings (key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES ('VACCINE_DEMOGRAPHIC_ESTIMATE_COHORT_ID', '4', 'Surving Infants estimate id', 'Used in vaccine reports for calculating coverage', 'VACCINE', '1', 'TEXT', NULL, 't');
