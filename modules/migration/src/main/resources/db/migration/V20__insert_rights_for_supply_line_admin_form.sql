delete from rights where name = 'MANAGE_SUPPLYLINE';

INSERT INTO rights(name, adminRight,description) VALUES
 ('MANAGE_SUPPLYLINE',TRUE,'Permission to create and edit Supply Line');
