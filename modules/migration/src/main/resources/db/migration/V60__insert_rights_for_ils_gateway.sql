delete from rights where name = 'ACCESS_ILS_GATEWAY';

INSERT INTO rights(name, righttype, description) VALUES
 ('ACCESS_ILS_GATEWAY','ADMIN','Permission to access the ILS Gateway.');