INSERT INTO roles
 (id, name, description) VALUES
 (1, 'store in-charge', ''),
 (2, 'district pharmacist', '');

INSERT INTO role_rights
  (roleId, rightId) VALUES
  (1, 'VIEW_REQUISITION'),
  (1, 'CREATE_REQUISITION'),
  (2, 'VIEW_REQUISITION'),
  (2, 'UPLOADS'),
  (2, 'MANAGE_FACILITY'),
  (2, 'CONFIGURE_RNR');

INSERT INTO users
  (id, userName, password, role, facilityId) VALUES
  (200, 'User123', 'Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==','USER', 1);

INSERT INTO role_assignments
  (userId, roleId, programId) VALUES
  (100, 2, 1),
  (200, 1, 1);