INSERT INTO roles
 (name, description) VALUES
 ('store in-charge', ''),
 ('district pharmacist', ''),
 ('medical-officer', '');

INSERT INTO role_rights
  (roleId, rightName) VALUES
  ((select id from roles where name='store in-charge'), 'VIEW_REQUISITION'),
  ((select id from roles where name='store in-charge'), 'CREATE_REQUISITION'),
  ((select id from roles where name='medical-officer'), 'VIEW_REQUISITION'),
  ((select id from roles where name='medical-officer'), 'AUTHORIZE_REQUISITION'),
  ((select id from roles where name='store in-charge'), 'APPROVE_REQUISITION'),
  ((select id from roles where name='district pharmacist'), 'UPLOADS'),
  ((select id from roles where name='district pharmacist'), 'MANAGE_FACILITY'),
  ((select id from roles where name='district pharmacist'), 'CONFIGURE_RNR');


INSERT INTO users
  (id, userName, password, facilityId, firstName, lastName, email) VALUES
  (200, 'User123', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie', (SELECT id FROM facilities WHERE code = 'F10'), 'Jane', 'Doe', 'Jane_Doe@openlmis.com'),
  (300, 'User234', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie', (SELECT id FROM facilities WHERE code = 'F10'), 'Lokesh', 'Agrawal', 'lokeshag@thoughtworks.com');

INSERT INTO supervisory_nodes
  (parentId, facilityId, name, code) VALUES
  (null, (SELECT id FROM facilities WHERE code = 'F10'), 'Node 1', 'N1');

 INSERT INTO supervisory_nodes
  (parentId, facilityId, name, code) VALUES
  ((select id from  supervisory_nodes where code ='N1'), (SELECT id FROM facilities WHERE code = 'F11'), 'Node 2', 'N2');

INSERT INTO role_assignments
  (userId, roleId, programId, supervisoryNodeId) VALUES
  (200, (SELECT id FROM roles WHERE name = 'store in-charge'), 1, null),
  (200, (SELECT id FROM roles WHERE name = 'store in-charge'), 1, (SELECT id from supervisory_nodes WHERE code = 'N1')),
  (300, (SELECT id FROM roles WHERE name = 'medical-officer'), 1, (SELECT id from supervisory_nodes WHERE code = 'N2'));
