delete from rights where name = 'MANAGE_SUPPLYLINE';

INSERT INTO rights(name, righttype,description) VALUES
 ('MANAGE_SUPPLYLINE','ADMIN','Permission to create and edit Supply Line');
