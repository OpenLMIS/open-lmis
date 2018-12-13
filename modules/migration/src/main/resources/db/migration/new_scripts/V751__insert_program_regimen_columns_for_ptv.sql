DO
$do$
BEGIN
IF EXISTS (SELECT * FROM programs WHERE id = 6) THEN

INSERT INTO program_regimen_columns(programid, name, label, visible, datatype) VALUES (6, 'code', 'header.code', TRUE, 'regimen.reporting.dataType.text');
INSERT INTO program_regimen_columns(programid, name, label, visible, datatype) VALUES (6, 'name', 'header.name', TRUE, 'regimen.reporting.dataType.text');
INSERT INTO program_regimen_columns(programid, name, label, visible, datatype) VALUES (6, 'patientsOnTreatment', 'Number of patients on treatment', TRUE, 'regimen.reporting.dataType.numeric');

END IF;
END
$do$