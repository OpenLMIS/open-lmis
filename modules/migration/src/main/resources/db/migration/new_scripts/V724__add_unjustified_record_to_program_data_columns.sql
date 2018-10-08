DELETE FROM program_data_columns WHERE code IN ('UNJUSTIFIED_HIVDETERMINE','UNJUSTIFIED_HIVUNIGOLD','UNJUSTIFIED_SYPHILLIS','UNJUSTIFIED_MALARIA');
INSERT INTO program_data_columns (code, supplementalProgramId) VALUES ('UNJUSTIFIED_HIVDETERMINE',1);
INSERT INTO program_data_columns (code, supplementalProgramId) VALUES ('UNJUSTIFIED_HIVUNIGOLD',1);
INSERT INTO program_data_columns (code, supplementalProgramId) VALUES ('UNJUSTIFIED_SYPHILLIS',1);
INSERT INTO program_data_columns (code, supplementalProgramId) VALUES ('UNJUSTIFIED_MALARIA',1);
