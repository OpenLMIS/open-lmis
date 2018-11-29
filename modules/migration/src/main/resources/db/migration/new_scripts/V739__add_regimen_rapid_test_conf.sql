DO
$do$
BEGIN
IF EXISTS (SELECT * FROM programs WHERE id = 3) THEN
DELETE FROM program_regimen_columns WHERE programId = 3;
INSERT INTO program_regimen_columns (programId, name, label, visible, dataType, displayOrder) VALUES (3, 'patientsOnTreatment', 'Number of patients on treatment', true, 'regimen.reporting.dataType.numeric', 3);
UPDATE programs SET regimenTemplateConfigured = true WHERE id = 3;
END IF;
END
$do$