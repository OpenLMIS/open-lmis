delete from rights where name = 'SERVICE_VENDOR_RIGHT';

INSERT INTO rights (name, rightType, description) VALUES
 ('SERVICE_VENDOR_RIGHT','ADMIN','Permission to use system as service Vendor');