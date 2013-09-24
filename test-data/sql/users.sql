-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

INSERT INTO roles
 (name, type, description) VALUES
 ('Store In-Charge', 'REQUISITION', ''),
 ('LMU', 'REQUISITION', ''),
 ('LMU In-Charge', 'ADMIN', ''),
 ('FacilityHead', 'REQUISITION', ''),
 ('Medical-Officer', 'REQUISITION', ''),
 ('View-Report', 'ADMIN', '');

INSERT INTO role_rights
  (roleId, rightName) VALUES
  ((select id from roles where name='Store In-Charge'), 'VIEW_REQUISITION'),
  ((select id from roles where name='Store In-Charge'), 'CREATE_REQUISITION'),
  ((select id from roles where name='Medical-Officer'), 'VIEW_REQUISITION'),
  ((select id from roles where name='Medical-Officer'), 'APPROVE_REQUISITION'),
  ((select id from roles where name='FacilityHead'), 'AUTHORIZE_REQUISITION'),
  ((select id from roles where name='FacilityHead'), 'VIEW_REQUISITION'),
  ((select id from roles where name='LMU'), 'VIEW_REQUISITION'),
  ((select id from roles where name='LMU'), 'APPROVE_REQUISITION'),
  ((select id from roles where name='LMU In-Charge'), 'CONVERT_TO_ORDER'),
  ((select id from roles where name='LMU In-Charge'), 'VIEW_ORDER'),
  ((select id from roles where name='View-Report'), 'VIEW_REPORT');

INSERT INTO VENDORS (name, active) VALUES ('commTrack', true);

INSERT INTO users
  (userName, password,vendorId, facilityId, firstName, lastName, email, verified, active) VALUES
  ('StoreInCharge', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',(SELECT id FROM vendors WHERE name = 'openLmis'), (SELECT id FROM facilities WHERE code = 'F10'), 'Fatima', 'Doe', 'Fatima_Doe@openlmis.com', true, true),
  ('FacilityHead', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',(SELECT id FROM vendors WHERE name = 'openLmis') ,(SELECT id FROM facilities WHERE code = 'F10'), 'Jane', 'Doe', 'Kutt_Doe@openlmis.com', true, true),
  ('FacilityInCharge', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',(SELECT id FROM vendors WHERE name = 'openLmis') , (SELECT id FROM facilities WHERE code = 'F10'), 'Jane', 'Doe', 'Jane_Doe@openlmis.com', false, true),
  ('MedicalOfficer', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',(SELECT id FROM vendors WHERE name = 'openLmis'), (SELECT id FROM facilities WHERE code = 'F10'), 'John', 'Doe', 'Joh_Doe@openlmis.com', true, true),
  ('lmu', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',(SELECT id FROM vendors WHERE name = 'openLmis'), (SELECT id FROM facilities WHERE code = 'F10'), 'Frank', 'Doe', 'Frank_Doe@openlmis.com', true, true),
  ('StoreHead', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',(SELECT id FROM vendors WHERE name = 'openLmis'),  (SELECT id FROM facilities WHERE code = 'F10'), 'Frank', 'Doe', 'Bhann_Doe@openlmis.com', false, true),
  ('superuser', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',(SELECT id FROM vendors WHERE name = 'openLmis'),  (SELECT id FROM facilities WHERE code = 'F10'), 'Maafi-de', 'Doe', 'Maafi_de_Doe@openlmis.com', true, true),
  ('lmuincharge', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',(SELECT id FROM vendors WHERE name = 'openLmis'), (SELECT id FROM facilities WHERE code = 'F10'), 'Jake', 'Doe', 'Jake_Doe@openlmis.com', true, true);

INSERT INTO USERS
  (id, userName, password, facilityId, firstName, lastName, vendorId ,verified, active) VALUES
  (700, 'commTrack', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie', (SELECT id FROM facilities WHERE code = 'F10'), 'CommTrack', 'Doe',(SELECT id from vendors where name='commTrack'), true, true);


INSERT INTO supervisory_nodes
  (parentId, facilityId, name, code) VALUES
  (null, (SELECT id FROM facilities WHERE code = 'F10'), 'Node 1', 'N1');

 INSERT INTO supervisory_nodes
  (parentId, facilityId, name, code) VALUES
  ((select id from  supervisory_nodes where code ='N1'), (SELECT id FROM facilities WHERE code = 'F11'), 'Node 2', 'N2');

   INSERT INTO supervisory_nodes
  (parentId, facilityId, name, code) VALUES
  (null, (SELECT id FROM facilities WHERE code = 'F11'), 'Node 3', 'N3');

INSERT INTO role_assignments
  (userId, roleId, programId, supervisoryNodeId) VALUES
  ((SELECT ID FROM USERS WHERE username='StoreInCharge'),  (SELECT id FROM roles WHERE name = 'Store In-Charge'), 3, null),
  ((SELECT ID FROM USERS WHERE username='StoreInCharge'),  (SELECT id FROM roles WHERE name = 'Store In-Charge'), 2, null),
  ((SELECT ID FROM USERS WHERE username='StoreInCharge'),  (SELECT id FROM roles WHERE name = 'Store In-Charge'), 4, null),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'Store In-Charge'), 3, null),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'FacilityHead'), 4, null),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'Store In-Charge'), 2, null),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'FacilityHead'), 3, null),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'Store In-Charge'), 4, null),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'FacilityHead'), 2, null),
  ((SELECT ID FROM USERS WHERE username='StoreInCharge'),  (SELECT id FROM roles WHERE name = 'Store In-Charge'), 2, (SELECT id from supervisory_nodes WHERE code = 'N3')),
  ((SELECT ID FROM USERS WHERE username='StoreInCharge'),  (SELECT id FROM roles WHERE name = 'Store In-Charge'), 4, (SELECT id from supervisory_nodes WHERE code = 'N3')),
  ((SELECT ID FROM USERS WHERE username='StoreHead'),  (SELECT id FROM roles WHERE name = 'Store In-Charge'), 1, (SELECT id from supervisory_nodes WHERE code = 'N3')),
  ((SELECT ID FROM USERS WHERE username='FacilityInCharge'),  (SELECT id FROM roles WHERE name = 'FacilityHead'), 1, (SELECT id from supervisory_nodes WHERE code = 'N3')),
  ((SELECT ID FROM USERS WHERE username='FacilityHead'),   (SELECT id FROM roles WHERE name = 'FacilityHead'), 3, (SELECT id from supervisory_nodes WHERE code = 'N2')),
  ((SELECT ID FROM USERS WHERE username='FacilityHead'),   (SELECT id FROM roles WHERE name = 'FacilityHead'), 2, (SELECT id from supervisory_nodes WHERE code = 'N2')),
  ((SELECT ID FROM USERS WHERE username='FacilityHead'),   (SELECT id FROM roles WHERE name = 'FacilityHead'), 4, (SELECT id from supervisory_nodes WHERE code = 'N2')),
  ((SELECT ID FROM USERS WHERE username='FacilityHead'),   (SELECT id FROM roles WHERE name = 'FacilityHead'), 2, (SELECT id from supervisory_nodes WHERE code = 'N3')),
  ((SELECT ID FROM USERS WHERE username='FacilityHead'),   (SELECT id FROM roles WHERE name = 'FacilityHead'), 4, (SELECT id from supervisory_nodes WHERE code = 'N3')),
  ((SELECT ID FROM USERS WHERE username='MedicalOfficer'), (SELECT id FROM roles WHERE name = 'Medical-Officer'), 3, (SELECT id from supervisory_nodes WHERE code = 'N2')),
  ((SELECT ID FROM USERS WHERE username='MedicalOfficer'), (SELECT id FROM roles WHERE name = 'Medical-Officer'), 2, (SELECT id from supervisory_nodes WHERE code = 'N2')),
  ((SELECT ID FROM USERS WHERE username='MedicalOfficer'), (SELECT id FROM roles WHERE name = 'Medical-Officer'), 4, (SELECT id from supervisory_nodes WHERE code = 'N2')),
  ((SELECT ID FROM USERS WHERE username='MedicalOfficer'), (SELECT id FROM roles WHERE name = 'Medical-Officer'), 2, (SELECT id from supervisory_nodes WHERE code = 'N3')),
  ((SELECT ID FROM USERS WHERE username='MedicalOfficer'), (SELECT id FROM roles WHERE name = 'Medical-Officer'), 4, (SELECT id from supervisory_nodes WHERE code = 'N3')),
  ((SELECT ID FROM USERS WHERE username='superuser'),   (SELECT id FROM roles WHERE name = 'Medical-Officer'), 4, (SELECT id from supervisory_nodes WHERE code = 'N1')),
  ((SELECT ID FROM USERS WHERE username='superuser'),   (SELECT id FROM roles WHERE name = 'Medical-Officer'), 4, (SELECT id from supervisory_nodes WHERE code = 'N2')),
  ((SELECT ID FROM USERS WHERE username='superuser'),   (SELECT id FROM roles WHERE name = 'Medical-Officer'), 4, (SELECT id from supervisory_nodes WHERE code = 'N3')),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'Medical-Officer'), 3, (SELECT id from supervisory_nodes WHERE code = 'N1')),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'Medical-Officer'), 2, (SELECT id from supervisory_nodes WHERE code = 'N1')),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'Medical-Officer'), 2, (SELECT id from supervisory_nodes WHERE code = 'N2')),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'Medical-Officer'), 2, (SELECT id from supervisory_nodes WHERE code = 'N3')),
  ((SELECT ID FROM USERS WHERE username='lmu'),  (SELECT id FROM roles WHERE name = 'LMU'), 3, (SELECT id from supervisory_nodes WHERE code = 'N1')),
  ((SELECT ID FROM USERS WHERE username='lmu'),  (SELECT id FROM roles WHERE name = 'LMU'), 2, (SELECT id from supervisory_nodes WHERE code = 'N1')),
  ((SELECT ID FROM USERS WHERE username='lmu'),  (SELECT id FROM roles WHERE name = 'LMU'), 4, (SELECT id from supervisory_nodes WHERE code = 'N1')),
  ((SELECT ID FROM USERS WHERE username='lmu'),  (SELECT id FROM roles WHERE name = 'LMU'), 2, (SELECT id from supervisory_nodes WHERE code = 'N3')),
  ((SELECT ID FROM USERS WHERE username='lmu'),  (SELECT id FROM roles WHERE name = 'LMU'), 4, (SELECT id from supervisory_nodes WHERE code = 'N3')),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'LMU'), 4, (SELECT id from supervisory_nodes WHERE code = 'N1')),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'LMU'), 3, (SELECT id from supervisory_nodes WHERE code = 'N1')),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'LMU'), 2, (SELECT id from supervisory_nodes WHERE code = 'N1')),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'LMU'), 2, (SELECT id from supervisory_nodes WHERE code = 'N2')),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'LMU'), 2, (SELECT id from supervisory_nodes WHERE code = 'N3')),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'Admin'),null ,null ),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'LMU In-Charge'), null,null ),
  ((SELECT ID FROM USERS WHERE username='superuser'),  (SELECT id FROM roles WHERE name = 'View-Report'), null,null ),
  ((SELECT ID FROM USERS WHERE username='lmuincharge'), (SELECT id FROM roles WHERE name = 'LMU In-Charge'), null, (SELECT id from supervisory_nodes WHERE code = 'N1')),
  ((SELECT ID FROM USERS WHERE username='lmuincharge'), (SELECT id FROM roles WHERE name = 'LMU In-Charge'), null, (SELECT id from supervisory_nodes WHERE code = 'N1')),
  ((SELECT ID FROM USERS WHERE username='lmuincharge'), (SELECT id FROM roles WHERE name = 'LMU In-Charge'), null, (SELECT id from supervisory_nodes WHERE code = 'N1')),
  ((SELECT ID FROM USERS WHERE username='commTrack'),  (SELECT id FROM roles WHERE name = 'Store In-Charge'), 2, null),
  ((SELECT ID FROM USERS WHERE username='commTrack'),  (SELECT id FROM roles WHERE name = 'FacilityHead'), 2, null);

