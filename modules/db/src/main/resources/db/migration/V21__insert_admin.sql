INSERT INTO roles
 (name, description) VALUES
 ('Admin', 'Admin');

 INSERT INTO role_rights
  (roleId, rightName) VALUES
  ((select id from roles where name = 'Admin'), 'UPLOADS'),
  ((select id from roles where name = 'Admin'), 'MANAGE_FACILITY'),
  ((select id from roles where name = 'Admin'), 'MANAGE_ROLE'),
  ((select id from roles where name = 'Admin'), 'MANAGE_SCHEDULE'),
  ((select id from roles where name = 'Admin'), 'CONFIGURE_RNR'),
  ((select id from roles where name = 'Admin'), 'MANAGE_USERS');

INSERT INTO users
  (userName, password, facilityId, firstName, lastName, email) VALUES
  ('Admin123', 'TQskzK3iLfbRVHeM1muvBCiKribfl6lh8+o91hb74G3OvsybvkzpPI4S3KIeWTXAiwlUU0iSxWi4wSuS8mokSA==', null, 'John', 'Doe', 'John_Doe@openlmis.com');

INSERT INTO role_assignments
  (userId, roleId) VALUES
  ((select id from users where userName='Admin123'), (select id from roles where name = 'Admin'));



