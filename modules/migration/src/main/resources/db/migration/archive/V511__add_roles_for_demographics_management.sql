DELETE FROM rights where name = 'MANAGE_DEMOGRAPHIC_ESTIMATES';

INSERT INTO rights (name, rightType, displaynamekey, description) VALUES
 ('MANAGE_DEMOGRAPHIC_ESTIMATES','REQUISITION','right.manage.demographic.estimates','Permission to manage demographic estimates');


DELETE FROM rights WHERE name = 'MANAGE_DEMOGRAPHIC_PARAMETERS';

INSERT INTO rights (name, rightType, displaynamekey, description) VALUES
 ('MANAGE_DEMOGRAPHIC_PARAMETERS','ADMIN','right.manage.demographic.parameters','Permission to manage demographic parameters');