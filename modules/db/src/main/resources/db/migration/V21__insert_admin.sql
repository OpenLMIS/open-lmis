INSERT INTO roles
 (name, adminRole, description) VALUES
 ('Admin',TRUE,'Admin');

 INSERT INTO role_rights
  (roleId, rightName) VALUES
  ((select id from roles where name = 'Admin'), 'UPLOADS'),
  ((select id from roles where name = 'Admin'), 'MANAGE_FACILITY'),
  ((select id from roles where name = 'Admin'), 'MANAGE_ROLE'),
  ((select id from roles where name = 'Admin'), 'MANAGE_SCHEDULE'),
  ((select id from roles where name = 'Admin'), 'CONFIGURE_RNR'),
  ((select id from roles where name = 'Admin'), 'MANAGE_USERS');

INSERT INTO users
  (userName, password, facilityId, firstName, lastName, email, active) VALUES
  ('Admin123', 'TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie', null, 'John', 'Doe', 'John_Doe@openlmis.com', true);

INSERT INTO role_assignments
  (userId, roleId) VALUES
  ((select id from users where userName='Admin123'), (select id from roles where name = 'Admin'));



