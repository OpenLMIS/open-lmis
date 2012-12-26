INSERT INTO roles
 (name, description) VALUES
 ('store in-charge', ''),
 ('district pharmacist', '');

INSERT INTO role_rights
  (roleId, rightId) VALUES
  (2, 'CREATE_REQUISITION'),
  (3, 'UPLOADS'),
  (3, 'MANAGE_FACILITY'),
  (3, 'CONFIGURE_RNR');

INSERT INTO users
  (id, userName, password, facilityId) VALUES
  (200, 'User123', 'Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==', (SELECT id FROM facilities WHERE code = 'F1756'));

INSERT INTO supervisory_nodes
  (parentId, facilityId, name, code) VALUES
  (null, (SELECT id FROM facilities WHERE code = 'F1756'), 'Node 1', 'N1'),
  ((select id from  supervisory_nodes where code ='N1'), (SELECT id FROM facilities WHERE code = 'F1757'), 'Node 1', 'N2');

INSERT INTO role_assignments
  (userId, roleId, programId, supervisoryNodeId) VALUES
  (200, (SELECT id FROM roles WHERE name = 'store in-charge'), 1, null),
  (200, (SELECT id FROM roles WHERE name = 'store in-charge'), 1, (SELECT id from supervisory_nodes WHERE code = 'N1'));
