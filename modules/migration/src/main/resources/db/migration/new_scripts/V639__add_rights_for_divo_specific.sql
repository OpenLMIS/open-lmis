DELETE FROM rights where name = 'MASS_DISTRIBUTION';

INSERT INTO rights (name, rightType, displaynamekey, description) VALUES
 ('MASS_DISTRIBUTION','REQUISITION','right.mass.distribution','Permission to do mass distribution');


DELETE FROM rights WHERE name = 'MANAGE_SUPERVISED_EQUIPMENTS';

INSERT INTO rights (name, rightType,displaynamekey, description) VALUES
 ('MANAGE_SUPERVISED_EQUIPMENTS','REQUISITION','right.manage.supervised.equipments','Permission to manage equipment inventory for supervised facility');