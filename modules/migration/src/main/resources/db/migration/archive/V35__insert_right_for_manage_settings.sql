delete from rights where name = 'MANAGE_SETTING';

INSERT INTO rights(name, righttype, description) VALUES
 ('MANAGE_SETTING','ADMIN','Permission to configure settings.');