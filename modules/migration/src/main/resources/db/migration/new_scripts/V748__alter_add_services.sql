DO
$do$
BEGIN
IF EXISTS (SELECT * FROM programs WHERE id = 3) THEN
INSERT INTO services(
            id, code, name, programid, active, createdby)
    VALUES (7, 'GENERAL_WARD', 'GENERAL_WARD', 3, true, 1);

INSERT INTO services(
            id, code, name, programid, active, createdby)
    VALUES (8, 'ACC_EMERGENCY', 'ACC_EMERGENCY',  3, true, 1);

INSERT INTO services(
            id, code, name, programid, active, createdby)
    VALUES (9, 'MOBILE_UNIT', 'MOBILE_UNIT',  3, true, 1);

INSERT INTO services(
            id, code, name, programid, active, createdby)
    VALUES (10, 'LABORATORY', 'LABORATORY',  3, true, 1);

INSERT INTO services(
            id, code, name, programid, active, createdby)
    VALUES (11, 'UATS', 'UATS',  3, true, 1);

INSERT INTO services(
            id, code, name, programid, active, createdby)
    VALUES (12, 'PNCTL', 'PNCTL',  3, true, 1);

INSERT INTO services(
            id, code, name, programid, active, createdby)
    VALUES (13, 'PAV', 'PAV',  3, true, 1);

INSERT INTO services(
            id, code, name, programid, active, createdby)
    VALUES (14, 'DENTAL_WARD', 'DENTAL_WARD',  3, true, 1);
END IF;
END
$do$
