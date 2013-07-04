-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

INSERT INTO roles
 (name, type) VALUES
 ('FieldCoordinator', 'ALLOCATION');

 INSERT INTO role_rights
 (roleId, rightName) VALUES
 ((select id from roles where name='FieldCoordinator'), 'MANAGE_DISTRIBUTION');


 INSERT INTO users
  (userName, password,vendorId, facilityId, firstName, lastName, email, active) VALUES
  ('FieldCoordinator', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie', (SELECT id FROM vendors WHERE name = 'openLmis'), (SELECT id FROM facilities WHERE code = 'F10'), 'Field', 'Coordinator','fieldcoordinator@openlmis.com', true);

 INSERT INTO role_assignments
  (userId, roleId, deliveryZoneId, programId) VALUES
  ((SELECT id FROM USERS WHERE username='FieldCoordinator'), (SELECT id FROM roles WHERE name = 'FieldCoordinator'), (SELECT id FROM delivery_zones WHERE code='Norte'), (SELECT name FROM programs WHERE name = 'VACCINES')),
  ((SELECT id FROM USERS WHERE username='FieldCoordinator'), (SELECT id FROM roles WHERE name = 'FieldCoordinator'), (SELECT id FROM delivery_zones WHERE code='Centro'),(SELECT name FROM programs WHERE name = 'VACCINES')),
  ((SELECT id FROM USERS WHERE username='FieldCoordinator'), (SELECT id FROM roles WHERE name = 'FieldCoordinator'), (SELECT id FROM delivery_zones WHERE code='Sul'), (SELECT name FROM programs WHERE name = 'VACCINES')),
  ((SELECT id FROM USERS WHERE username='FieldCoordinator'), (SELECT id FROM roles WHERE name = 'FieldCoordinator'), (SELECT id FROM delivery_zones WHERE code='DZ1'), (SELECT name FROM programs WHERE name = 'VACCINES'));


