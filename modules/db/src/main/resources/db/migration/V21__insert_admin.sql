INSERT INTO roles
 (name, description) VALUES
 ('admin', '');

 INSERT INTO role_rights
  (roleId, rightId) VALUES
  ((select id from roles where name = 'admin'), 'UPLOADS'),
  ((select id from roles where name = 'admin'), 'MANAGE_FACILITY'),
  ((select id from roles where name = 'admin'), 'CONFIGURE_RNR');

INSERT INTO users
  (userName, password, role, facilityId) VALUES
  ('Admin123', 'TQskzK3iLfbRVHeM1muvBCiKribfl6lh8+o91hb74G3OvsybvkzpPI4S3KIeWTXAiwlUU0iSxWi4wSuS8mokSA==','ADMIN', null);

INSERT INTO role_assignments
  (userId, roleId) VALUES
  ((select id from users where userName='Admin123'), (select id from roles where name = 'admin'));



