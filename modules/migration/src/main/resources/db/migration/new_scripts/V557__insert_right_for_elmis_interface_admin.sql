DELETE FROM Rights where name='MANAGE_ELMIS_INTERFACE';

INSERT INTO rights (name, rightType, displaynamekey, description) VALUES
 ('MANAGE_ELMIS_INTERFACE','ADMIN','right.admin.elmis.interface','Permission to manage ELMIS interface apps setting');
