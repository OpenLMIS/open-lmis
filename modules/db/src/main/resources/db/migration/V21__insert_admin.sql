-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

INSERT INTO roles
 (name, type, description) VALUES
 ('Admin','ADMIN','Admin');

 INSERT INTO role_rights
  (roleId, rightName) VALUES
  ((select id from roles where name = 'Admin'), 'UPLOADS'),
  ((select id from roles where name = 'Admin'), 'MANAGE_FACILITY'),
  ((select id from roles where name = 'Admin'), 'MANAGE_ROLE'),
  ((select id from roles where name = 'Admin'), 'MANAGE_PROGRAM_PRODUCT'),
  ((select id from roles where name = 'Admin'), 'MANAGE_SCHEDULE'),
  ((select id from roles where name = 'Admin'), 'CONFIGURE_RNR'),
  ((select id from roles where name = 'Admin'), 'MANAGE_USER'),
  ((select id from roles where name = 'Admin'), 'VIEW_REPORT'),
  ((select id from roles where name = 'Admin'), 'MANAGE_REPORT'),
  ((select id from roles where name = 'Admin'), 'MANAGE_REGIMEN_TEMPLATE');

INSERT INTO users
  (userName, password,vendorId, facilityId, firstName, lastName, email, verified, active) VALUES
  ('Admin123', 'TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie',(SELECT id from vendors where name='openLmis'), null, 'John', 'Doe', 'John_Doe@openlmis.com', true, true);

INSERT INTO role_assignments
  (userId, roleId) VALUES
  ((select id from users where userName='Admin123'), (select id from roles where name = 'Admin'));



