DELETE FROM program_data_columns WHERE code IN ('UNJUSTIFIED_HIVDETERMINE','UNJUSTIFIED_HIVUNIGOLD','UNJUSTIFIED_SYPHILLIS','UNJUSTIFIED_MALARIA');
INSERT INTO program_data_columns (code, supplementalProgramId) VALUES ('UNJUSTIFIED_HIVDETERMINE',(SELECT id FROM supplemental_programs WHERE code = 'RAPID_TEST'));
INSERT INTO program_data_columns (code, supplementalProgramId) VALUES ('UNJUSTIFIED_HIVUNIGOLD',(SELECT id FROM supplemental_programs WHERE code = 'RAPID_TEST'));
INSERT INTO program_data_columns (code, supplementalProgramId) VALUES ('UNJUSTIFIED_SYPHILLIS',(SELECT id FROM supplemental_programs WHERE code = 'RAPID_TEST'));
INSERT INTO program_data_columns (code, supplementalProgramId) VALUES ('UNJUSTIFIED_MALARIA',(SELECT id FROM supplemental_programs WHERE code = 'RAPID_TEST'));
