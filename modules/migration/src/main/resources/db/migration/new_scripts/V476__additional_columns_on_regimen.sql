ALTER TABLE regimen_line_items
  ADD patientsontreatmentadult   INT,
  ADD patientstoinitiatetreatmentadult INT,
  ADD patientsstoppedtreatmentadult INT,
  ADD patientsontreatmentchildren   INT,
  ADD patientstoinitiatetreatmentchildren INT,
  ADD patientsstoppedtreatmentchildren INT;


INSERT INTO master_regimen_columns (name, label, visible, dataType)
    VALUES ('patientsOnTreatmentAdult', 'Patients On Treatment Adult', true, 'regimen.reporting.dataType.numeric'),
           ('patientsOnTreatmentChildren', 'Patients On Treatment Adult', true, 'regimen.reporting.dataType.numeric'),
           ('patientsToInitiateTreatmentAdult', 'Patients To Initiate Treatment Adult', true, 'regimen.reporting.dataType.numeric'),
           ('patientsToInitiateTreatmentChildren', 'Patients To Initiate Treatment Children', true, 'regimen.reporting.dataType.numeric'),
           ('patientsStoppedTreatmentAdult', 'Patients Stopped Treatment Adult', true, 'regimen.reporting.dataType.numeric'),
           ('patientsStoppedTreatmentChildren', 'Patients Stopped Treatment Children', true, 'regimen.reporting.dataType.numeric');

ALTER TABLE program_regimen_columns
ADD displayOrder INT NOT NULL DEFAULT (0);