DO
$do$
BEGIN
IF EXISTS (SELECT * FROM programs WHERE id = 3) THEN
INSERT INTO services(
            id, code, name, programid, active, createdby)
    VALUES (1, 'MATERNITY', 'MATERNITY', 3, true, 1);

INSERT INTO services(
            id, code, name, programid, active, createdby)
    VALUES (2, 'APES', 'APES',  3, true, 1);
END IF;
END
$do$
