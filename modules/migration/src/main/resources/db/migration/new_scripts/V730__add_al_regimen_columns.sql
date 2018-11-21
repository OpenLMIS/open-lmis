DELETE FROM program_regimen_columns WHERE programId = 5;
INSERT INTO program_regimen_columns (programId, name, label, visible, dataType, displayOrder) VALUES (5, 'patientsOnTreatment', 'Number of patients on treatment', true, 'regimen.reporting.dataType.numeric', 3);
INSERT INTO program_regimen_columns (programId, name, label, visible, dataType, displayOrder) VALUES (5, 'hf', 'hf', true, 'regimen.reporting.dataType.numeric', 4);
INSERT INTO program_regimen_columns (programId, name, label, visible, dataType, displayOrder) VALUES (5, 'chw', 'chw', true, 'regimen.reporting.dataType.numeric', 5);
