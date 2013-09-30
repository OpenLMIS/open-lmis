INSERT INTO roles
 (name, type, description) VALUES
 ('Report Role', 'REPORT', 'Reporting Role');


  INSERT INTO role_rights
  (roleId, rightName)
  select (select id from roles where name = 'Report Role'), name from rights where righttype = 'REPORT';

  -- allow the admin to see all reports
  INSERT INTO role_assignments
  (userId, roleId) VALUES
  ((select id from users where userName='Admin123'), (select id from roles where name = 'Report Role'));

  -- allow admin to see all the admin functions too.
  INSERT INTO role_rights
  (roleId, rightName)
  select (select id from roles where name = 'Admin'), name from rights where righttype = 'ADMIN' and name not in (select rightname from role_rights where roleid = (select id from roles where name = 'Admin') );

  DELETE from role_rights
  where roleId = 1 and rightName = 'UPLOAD_REPORT';