DO
$do$
BEGIN
IF EXISTS (SELECT * FROM programs WHERE id in (1,2,3,4,5)) THEN
INSERT INTO reports_type(
            id, code, programid, name, description)
    VALUES (1, 'MMIA', 1, 'MMIA', 'MMIA');
INSERT INTO reports_type(
            id, code, programid, name, description)
    VALUES (2, 'TEST_KIT', 3, 'RAPID TEST', 'RAPID TEST');
INSERT INTO reports_type(
            id, code, programid, name, description)
    VALUES (3, 'PTV', 6, 'PTV', 'PTV');
INSERT INTO reports_type(
            id, code, programid, name, description)
    VALUES (4, 'VIA', 10, 'Balance Requisition', 'Balance Requisition');
 INSERT INTO reports_type(
            id, code, programid, name, description)
    VALUES (5, 'MALARIA', 5, 'AL', 'AL');
END IF;
END
$do$