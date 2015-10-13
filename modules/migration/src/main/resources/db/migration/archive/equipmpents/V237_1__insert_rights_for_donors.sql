delete from rights where name = 'MANAGE_DONOR';

INSERT INTO rights(name, righttype, description) VALUES
 ('MANAGE_DONOR','ADMIN','Permission to manage donors.');