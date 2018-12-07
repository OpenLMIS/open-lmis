DO
$do$
BEGIN
IF EXISTS (SELECT * FROM programs WHERE id = 3) THEN

INSERT INTO services(id, code, name, programid, active, createdby)
    VALUES (3, 'CPN', 'CPN', 6, true, NULL);

INSERT INTO services(
            id, code, name, programid, active, createdby)
    VALUES (4, 'Maternity', 'Maternity',  6, true, NULL);

INSERT INTO services(id, code, name, programid, active, createdby)
    VALUES (5, 'CCR', 'CCR', 6, true, NULL);

INSERT INTO services(
            id, code, name, programid, active, createdby)
    VALUES (6, 'Farmacy', 'Farmacy',  6, true, NULL);

END IF;
END
$do$
