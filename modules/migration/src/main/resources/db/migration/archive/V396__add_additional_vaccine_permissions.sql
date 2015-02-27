delete from rights where name = 'MANAGE_VACCINE_QUANTIFICATION';
delete from rights where name = 'MANAGE_VACCINE_TARGETS';

INSERT INTO rights(name, righttype,description, displayNameKey) VALUES
 ('MANAGE_VACCINE_TARGETS','ADMIN','Permission to manage vaccine targets.', 'right.admin.vaccine.targets');

 INSERT INTO rights(name, righttype,description, displayNameKey) VALUES
 ('MANAGE_VACCINE_QUANTIFICATION','ADMIN','Permission to manage vaccine quantification.', 'right.admin.vaccine.quantifications');