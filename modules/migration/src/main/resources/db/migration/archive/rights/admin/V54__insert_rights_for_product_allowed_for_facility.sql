delete from rights where name = 'MANAGE_PRODUCT_ALLOWED_FOR_FACILITY';

INSERT INTO rights(name, righttype, description) VALUES
 ('MANAGE_PRODUCT_ALLOWED_FOR_FACILITY','ADMIN','Permission to manage products allowed for facilities.');