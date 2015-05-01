ALTER TABLE master_regimen_columns
ADD displayOrder INT NOT NULL DEFAULT (0);

-- migrate existing data
UPDATE master_regimen_columns SET displayOrder = 0 WHERE name = 'skipped';
UPDATE master_regimen_columns SET displayOrder = 1 WHERE name = 'code';
UPDATE master_regimen_columns SET displayOrder = 2 WHERE name = 'name';
UPDATE master_regimen_columns SET displayOrder = 3 WHERE name = 'patientsOnTreatment';
UPDATE master_regimen_columns SET displayOrder = 4 WHERE name = 'patientsToInitiateTreatment';
UPDATE master_regimen_columns SET displayOrder = 5 WHERE name = 'patientsStoppedTreatment';
UPDATE master_regimen_columns SET displayOrder = 6 WHERE name = 'remarks';

-- add the additional columns
INSERT INTO master_regimen_columns (name, label, visible, dataType, displayOrder)
    VALUES ('patientsOnTreatmentAdult', 'Patients On Treatment Adult', true, 'regimen.reporting.dataType.numeric', 8 ),
           ('patientsOnTreatmentChildren', 'Patients On Treatment Children', true, 'regimen.reporting.dataType.numeric', 9),
           ('patientsToInitiateTreatmentAdult', 'Patients To Initiate Treatment Adult', true, 'regimen.reporting.dataType.numeric', 10),
           ('patientsToInitiateTreatmentChildren', 'Patients To Initiate Treatment Children', true, 'regimen.reporting.dataType.numeric',11),
           ('patientsStoppedTreatmentAdult', 'Patients Stopped Treatment Adult', true, 'regimen.reporting.dataType.numeric', 12),
           ('patientsStoppedTreatmentChildren', 'Patients Stopped Treatment Children', true, 'regimen.reporting.dataType.numeric', 13);

-- add regimen column display order on program
ALTER TABLE program_regimen_columns
  ADD displayOrder INT NOT NULL DEFAULT (0);

ALTER TABLE regimen_line_items
ADD patientsontreatmentadult   INT,
ADD patientstoinitiatetreatmentadult INT,
ADD patientsstoppedtreatmentadult INT,
ADD patientsontreatmentchildren   INT,
ADD patientstoinitiatetreatmentchildren INT,
ADD patientsstoppedtreatmentchildren INT;