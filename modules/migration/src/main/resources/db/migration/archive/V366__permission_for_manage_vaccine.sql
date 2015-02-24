delete from rights where name = 'MANAGE_VACCINE_SETTINGS';

INSERT INTO rights (name, rightType, description) VALUES
 ('MANAGE_VACCINE_SETTINGS','ADMIN','Permission to manage vaccines');