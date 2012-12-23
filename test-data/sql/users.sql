INSERT INTO roles
 (id, name, description) VALUES
 (2, 'store in-charge', ''),
 (3, 'district pharmacist', '');

INSERT INTO role_rights
  (roleId, rightId) VALUES
  (2, 'VIEW_REQUISITION'),
  (2, 'CREATE_REQUISITION'),
  (3, 'VIEW_REQUISITION'),
  (3, 'UPLOADS'),
  (3, 'MANAGE_FACILITY'),
  (3, 'CONFIGURE_RNR');

INSERT INTO users
  (id, userName, password, facilityId) VALUES
  (200, 'User123', 'Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==', (SELECT id FROM facilities WHERE code = 'F1756'));

INSERT INTO supervisory_nodes
  (parentId, facilityId, name, code) VALUES
  (null, (SELECT id FROM facilities WHERE code = 'F1756'), 'Node 1', 'N1');

INSERT INTO role_assignments
  (userId, roleId, programId, supervisoryNodeId) VALUES
  (200, 2, 1, null),
  (200, 2, 1, (SELECT id from supervisory_nodes WHERE code = 'N1'));
