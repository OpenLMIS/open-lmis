delete from rights where name = 'MANAGE_EQUIPMENT_SETTINGS';
delete from rights where name = 'MANAGE_EQUIPMENT_INVENTORY';


INSERT INTO rights (name, rightType, description) VALUES
 ('MANAGE_EQUIPMENT_SETTINGS','ADMIN','Permission to manage equipment settings');

INSERT INTO rights (name, rightType, description) VALUES
 ('MANAGE_EQUIPMENT_INVENTORY','ADMIN','Permission to manage equipment inventory for each facility');