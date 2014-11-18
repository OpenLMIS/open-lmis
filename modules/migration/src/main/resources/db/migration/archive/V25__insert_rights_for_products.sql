delete from rights where name = 'MANAGE_PRODUCT';

INSERT INTO rights(name, righttype,description) VALUES
 ('MANAGE_PRODUCT','ADMIN','Permission to manage products.');