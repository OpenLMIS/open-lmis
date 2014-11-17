delete from rights where name = 'MANAGE_GEOGRAPHIC_ZONES';

INSERT INTO rights(name, righttype,description) VALUES
 ('MANAGE_GEOGRAPHIC_ZONES','ADMIN','Permission to manage geographic zones.');