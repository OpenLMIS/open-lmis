DO
$do$
BEGIN
IF EXISTS (SELECT * FROM programs WHERE id = 3) THEN
INSERT INTO regimens(programid, categoryid, code, name, active, displayorder, createdby, modifiedby, iscustom) VALUES (3, 1, 175, 'HIV Determine Consumo', TRUE, 105, NULL, NULL, TRUE );
INSERT INTO regimens(programid, categoryid, code, name, active, displayorder, createdby, modifiedby, iscustom) VALUES (3, 1, 176, 'Sífilis Teste Rápido Consumo', TRUE, 106, NULL, NULL, TRUE);
INSERT INTO regimens(programid, categoryid, code, name, active, displayorder, createdby, modifiedby, iscustom) VALUES (3, 1, 177, 'Sífilis Teste Rápido Injustificado', TRUE, 107, NULL, NULL, TRUE);
INSERT INTO regimens(programid, categoryid, code, name, active, displayorder, createdby, modifiedby, iscustom) VALUES (3, 1, 178, 'HIV Unigold Consumo', TRUE, 108, NULL, NULL, TRUE);
INSERT INTO regimens(programid, categoryid, code, name, active, displayorder, createdby, modifiedby, iscustom) VALUES (3, 1, 179, 'HIV Unigold Positivos +', TRUE, 109, NULL, NULL, TRUE);
INSERT INTO regimens(programid, categoryid, code, name, active, displayorder, createdby, modifiedby, iscustom) VALUES (3, 1, 180, 'HIV Determine Positivos +', TRUE, 110, NULL, NULL, TRUE);
INSERT INTO regimens(programid, categoryid, code, name, active, displayorder, createdby, modifiedby, iscustom) VALUES (3, 1, 181, 'HIV Unigold Injustificado', TRUE, 111, NULL, NULL, TRUE);
INSERT INTO regimens(programid, categoryid, code, name, active, displayorder, createdby, modifiedby, iscustom) VALUES (3, 1, 182, 'Malaria Teste Rápido Injustificado', TRUE, 112, NULL, NULL, TRUE);
INSERT INTO regimens(programid, categoryid, code, name, active, displayorder, createdby, modifiedby, iscustom) VALUES (3, 1, 183, 'HIV Determine Injustificado', TRUE, 113, NULL, NULL, TRUE);
INSERT INTO regimens(programid, categoryid, code, name, active, displayorder, createdby, modifiedby, iscustom) VALUES (3, 1, 184, 'Sífilis Teste Positivos +', TRUE, 114, NULL, NULL, TRUE);
INSERT INTO regimens(programid, categoryid, code, name, active, displayorder, createdby, modifiedby, iscustom) VALUES (3, 1, 185, 'Malaria Teste Positivos +', TRUE, 115, NULL, NULL, TRUE);
INSERT INTO regimens(programid, categoryid, code, name, active, displayorder, createdby, modifiedby, iscustom) VALUES (3, 1, 186, 'Malaria Teste Rápido Consumo', TRUE, 116, NULL, NULL, TRUE);
END IF;
END
$do$