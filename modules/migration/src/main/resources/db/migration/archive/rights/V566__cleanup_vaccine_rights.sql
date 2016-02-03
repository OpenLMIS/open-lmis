delete from role_rights where rightname = 'MANAGE_VACCINE_QUANTIFICATION';
delete from role_rights where rightname = 'MANAGE_VACCINE_TARGETS';
delete from role_rights where rightname = 'MANAGE_VACCINE_SETTINGS';
delete from role_rights where rightname = 'MANAGE_IVD_SETTINGS';
delete from role_rights where rightname = 'MANAGE_MAOS_SETTINGS';

delete from rights where name = 'MANAGE_VACCINE_QUANTIFICATION';
delete from rights where name = 'MANAGE_VACCINE_TARGETS';
delete from rights where name = 'MANAGE_VACCINE_SETTINGS';
delete from rights where name = 'MANAGE_IVD_SETTINGS';
delete from rights where name = 'MANAGE_MAOS_SETTINGS';

INSERT INTO rights (name, rightType, description, displayNameKey, displayOrder) VALUES
 ('MANAGE_VACCINE_DISEASE_LIST','ADMIN','Permission to manage vaccine disease list', 'right.admin.vaccine.disease', 200);


 update rights set displayOrder = 30 where name = 'MANAGE_SETTING';
 update rights set displayOrder = 31 where name = 'MANAGE_SUPPLYLINE';
 update rights set displayOrder = 32 where name = 'MANAGE_ELMIS_INTERFACE';
 update rights set displayOrder = 33 where name = 'CONFIGURE_HELP_CONTENT';
 update rights set displayOrder = 38 where name = 'ACCESS_ILS_GATEWAY';
 update rights set displayOrder = 40 where name = 'MANAGE_EQUIPMENT_SETTINGS';
 update rights set displayOrder = 41 where name = 'SERVICE_VENDOR_RIGHT';
 update rights set displayOrder = 42 where name = 'MANAGE_DONOR';
 update rights set displayOrder = 50 where name = 'MANAGE_SEASONALITY_RATIONING';
 update rights set displayOrder = 60 where name = 'MANAGE_DEMOGRAPHIC_PARAMETERS';


 update rights set displayOrder = 17 where name = 'DELETE_REQUISITION';
 update rights set displayOrder = 20 where name = 'MANAGE_EQUIPMENT_INVENTORY';
 update rights set displayOrder = 30 where name = 'MANAGE_DEMOGRAPHIC_ESTIMATES';