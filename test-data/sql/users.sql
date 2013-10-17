--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

INSERT INTO roles
(name, description) VALUES
('Store In-Charge', ''),
('LMU', ''),
('LMU In-Charge', ''),
('FacilityHead', ''),
('Medical-Officer', ''),
('View-Report', ''),
('Shipment', '');

INSERT INTO role_rights
(roleId, rightName) VALUES
((SELECT id FROM roles WHERE name = 'Store In-Charge'), 'VIEW_REQUISITION'),
((SELECT id FROM roles WHERE name = 'Store In-Charge'), 'CREATE_REQUISITION'),
((SELECT id FROM roles WHERE name = 'Medical-Officer'), 'VIEW_REQUISITION'),
((SELECT id FROM roles WHERE name = 'Medical-Officer'), 'APPROVE_REQUISITION'),
((SELECT id FROM roles WHERE name = 'FacilityHead'), 'AUTHORIZE_REQUISITION'),
((SELECT id FROM roles WHERE name = 'FacilityHead'), 'VIEW_REQUISITION'),
((SELECT id FROM roles WHERE name = 'LMU'), 'VIEW_REQUISITION'),
((SELECT id FROM roles WHERE name = 'LMU'), 'APPROVE_REQUISITION'),
((SELECT id FROM roles WHERE name = 'LMU In-Charge'), 'CONVERT_TO_ORDER'),
((SELECT id FROM roles WHERE name = 'LMU In-Charge'), 'VIEW_ORDER'),
((SELECT id FROM roles WHERE name = 'Shipment'), 'FACILITY_FILL_SHIPMENT'),
((SELECT id FROM roles WHERE name = 'View-Report'), 'VIEW_REPORT');


INSERT INTO VENDORS (name, active) VALUES ('commTrack', TRUE);

INSERT INTO users
(userName, password, vendorId, facilityId, firstName, lastName, email, verified, active) VALUES
('StoreInCharge', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',
 (SELECT id FROM vendors WHERE name = 'openLmis'), (SELECT id FROM facilities WHERE code = 'F10'), 'Fatima', 'Doe', 'Fatima_Doe@openlmis.com', TRUE, TRUE),

('FacilityHead', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',
 (SELECT id FROM vendors WHERE name = 'openLmis'),  (SELECT id FROM facilities WHERE code = 'F10'), 'Jane', 'Doe', 'Kutt_Doe@openlmis.com', TRUE, TRUE),

('FacilityInCharge', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',
 (SELECT id FROM vendors WHERE name = 'openLmis'), (SELECT id FROM facilities WHERE code = 'F10'), 'Jane', 'Doe', 'Jane_Doe@openlmis.com', FALSE, TRUE),

('MedicalOfficer', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',
 (SELECT id FROM vendors WHERE name = 'openLmis'), (SELECT id FROM facilities WHERE code = 'F10'), 'John', 'Doe', 'Joh_Doe@openlmis.com', TRUE, TRUE),

('lmu', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',
 (SELECT id FROM vendors WHERE name = 'openLmis'), (SELECT id FROM facilities WHERE code = 'F10'), 'Frank', 'Doe', 'Frank_Doe@openlmis.com', TRUE, TRUE),

('StoreHead', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',
 (SELECT id FROM vendors WHERE name = 'openLmis'), (SELECT id FROM facilities WHERE code = 'F10'), 'Frank', 'Doe', 'Bhann_Doe@openlmis.com', FALSE, TRUE),

('superuser', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',
 (SELECT id FROM vendors WHERE name = 'openLmis'), (SELECT id FROM facilities WHERE code = 'F10'), 'Maafi-de', 'Doe', 'Maafi_de_Doe@openlmis.com', TRUE, TRUE),

('lmuincharge', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',
 (SELECT id FROM vendors WHERE name = 'openLmis'), (SELECT id FROM facilities WHERE code = 'F10'), 'Jake', 'Doe', 'Jake_Doe@openlmis.com', TRUE, TRUE);

INSERT INTO USERS
(id, userName, password, facilityId, firstName, lastName, vendorId, verified, active) VALUES
(700, 'commTrack', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',
 (SELECT id FROM facilities WHERE code = 'F10'), 'CommTrack', 'Doe', (SELECT id FROM vendors WHERE name = 'commTrack'), TRUE, TRUE);


INSERT INTO supervisory_nodes
(parentId, facilityId, name, code) VALUES
(null, (SELECT id FROM facilities WHERE code = 'F10'), 'Node 1', 'N1');

INSERT INTO supervisory_nodes
(parentId, facilityId, name, code) VALUES
((SELECT id FROM supervisory_nodes WHERE code = 'N1'), (SELECT id FROM facilities WHERE code = 'F11'), 'Node 2', 'N2'),
(null, (SELECT id FROM facilities WHERE code = 'F11'), 'Node 3', 'N3');


INSERT INTO role_assignments
(userId, roleId, programId, supervisoryNodeId) VALUES
((SELECT ID FROM USERS WHERE username = 'StoreInCharge'), (SELECT id FROM roles WHERE name = 'Store In-Charge'), 3, null),
((SELECT id FROM USERS WHERE username = 'StoreInCharge'), (SELECT id FROM roles WHERE name = 'Store In-Charge'), 2, null),
((SELECT ID FROM USERS WHERE username = 'StoreInCharge'), (SELECT id FROM roles WHERE name = 'Store In-Charge'), 4, null),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'Store In-Charge'), 3, null),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'FacilityHead'), 4, null),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'Store In-Charge'), 2, null),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'FacilityHead'), 3, null),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'Store In-Charge'), 4, null),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'FacilityHead'), 2, null),
((SELECT ID FROM USERS WHERE username = 'StoreInCharge'), (SELECT id FROM roles WHERE name = 'Store In-Charge'), 2, (SELECT id FROM supervisory_nodes WHERE code ='N3')),
((SELECT ID FROM USERS WHERE username = 'StoreInCharge'), (SELECT id FROM roles WHERE name = 'Store In-Charge'), 4, (SELECT id FROM supervisory_nodes WHERE code = 'N3')),
((SELECT ID FROM USERS WHERE username = 'StoreHead'), (SELECT id FROM roles WHERE name = 'Store In-Charge'), 1, (SELECT id FROM supervisory_nodes WHERE code = 'N3')),
((SELECT ID FROM USERS WHERE username = 'FacilityInCharge'), (SELECT id FROM roles WHERE name = 'FacilityHead'), 1, (SELECT id FROM supervisory_nodes WHERE code = 'N3')),
((SELECT ID FROM USERS WHERE username = 'FacilityHead'), (SELECT id FROM roles WHERE name = 'FacilityHead'), 3, (SELECT id FROM supervisory_nodes WHERE code = 'N2')),
((SELECT ID FROM USERS WHERE username = 'FacilityHead'), (SELECT id FROM roles WHERE name = 'FacilityHead'), 2, (SELECT id FROM supervisory_nodes WHERE code = 'N2')),
((SELECT ID FROM USERS WHERE username = 'FacilityHead'), (SELECT id FROM roles WHERE name = 'FacilityHead'), 4, (SELECT id FROM supervisory_nodes WHERE code = 'N2')),
((SELECT ID FROM USERS WHERE username = 'FacilityHead'), (SELECT id FROM roles WHERE name = 'FacilityHead'), 2, (SELECT id FROM supervisory_nodes WHERE code = 'N3')),
((SELECT ID FROM USERS WHERE username = 'FacilityHead'), (SELECT id FROM roles WHERE name = 'FacilityHead'), 4, (SELECT id FROM supervisory_nodes WHERE code = 'N3')),
((SELECT ID FROM USERS WHERE username = 'MedicalOfficer'), (SELECT id FROM roles WHERE name = 'Medical-Officer'), 3, (SELECT id FROM supervisory_nodes WHERE code = 'N2')),
((SELECT ID FROM USERS WHERE username = 'MedicalOfficer'), (SELECT id FROM roles WHERE name = 'Medical-Officer'), 2, (SELECT id FROM supervisory_nodes WHERE code = 'N2')),
((SELECT ID FROM USERS WHERE username = 'MedicalOfficer'), (SELECT id FROM roles WHERE name = 'Medical-Officer'), 4, (SELECT id FROM supervisory_nodes WHERE code = 'N2')),
((SELECT ID FROM USERS WHERE username = 'MedicalOfficer'), (SELECT id FROM roles WHERE name = 'Medical-Officer'), 2, (SELECT id FROM supervisory_nodes WHERE code = 'N3')),
((SELECT ID FROM USERS WHERE username = 'MedicalOfficer'), (SELECT id FROM roles WHERE name = 'Medical-Officer'), 4, (SELECT id FROM supervisory_nodes WHERE code = 'N3')),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'Medical-Officer'), 4, (SELECT id FROM supervisory_nodes WHERE code = 'N1')),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'Medical-Officer'), 4, (SELECT id FROM supervisory_nodes WHERE code = 'N2')),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'Medical-Officer'), 4, (SELECT id FROM supervisory_nodes WHERE code = 'N3')),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'Medical-Officer'), 3, (SELECT id FROM supervisory_nodes WHERE code = 'N1')),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'Medical-Officer'), 2, (SELECT id FROM supervisory_nodes WHERE code = 'N1')),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'Medical-Officer'), 2, (SELECT id FROM supervisory_nodes WHERE code = 'N2')),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'Medical-Officer'), 2, (SELECT id FROM supervisory_nodes WHERE code = 'N3')),
((SELECT ID FROM USERS WHERE username = 'lmu'), (SELECT id FROM roles WHERE name = 'LMU'), 3, (SELECT id FROM supervisory_nodes WHERE code = 'N1')),
((SELECT ID FROM USERS WHERE username = 'lmu'), (SELECT id FROM roles WHERE name = 'LMU'), 2, (SELECT id FROM supervisory_nodes WHERE code = 'N1')),
((SELECT ID FROM USERS WHERE username = 'lmu'), (SELECT id FROM roles WHERE name = 'LMU'), 4, (SELECT id FROM supervisory_nodes WHERE code = 'N1')),
((SELECT ID FROM USERS WHERE username = 'lmu'), (SELECT id FROM roles WHERE name = 'LMU'), 2, (SELECT id FROM supervisory_nodes WHERE code = 'N3')),
((SELECT ID FROM USERS WHERE username = 'lmu'), (SELECT id FROM roles WHERE name = 'LMU'), 4, (SELECT id FROM supervisory_nodes WHERE code = 'N3')),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'LMU'), 4, (SELECT id FROM supervisory_nodes WHERE code = 'N1')),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'LMU'), 3, (SELECT id FROM supervisory_nodes WHERE code = 'N1')),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'LMU'), 2, (SELECT id FROM supervisory_nodes WHERE code = 'N1')),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'LMU'), 2, (SELECT id FROM supervisory_nodes WHERE code = 'N2')),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'LMU'), 2, (SELECT id FROM supervisory_nodes WHERE code = 'N3')),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'Admin'), null, null),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'View-Report'), null, null),
((SELECT ID FROM USERS WHERE username = 'commTrack'), (SELECT id FROM roles WHERE name = 'Store In-Charge'), 2, null),
((SELECT ID FROM USERS WHERE username = 'commTrack'), (SELECT id FROM roles WHERE name = 'FacilityHead'), 2, null);

INSERT INTO fulfillment_role_assignments
(userId, roleId, facilityId) VALUES
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'LMU In-Charge'), (SELECT id FROM facilities WHERE code ='F10')),
((SELECT ID FROM USERS WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'Shipment'), (SELECT id FROM facilities WHERE code ='F11')),
((SELECT ID FROM USERS WHERE username = 'lmuincharge'), (SELECT id FROM roles WHERE name = 'LMU In-Charge'), (SELECT id FROM facilities WHERE code ='F10')),
((SELECT ID FROM USERS WHERE username = 'lmuincharge'), (SELECT id FROM roles WHERE name = 'Shipment'), (SELECT id FROM facilities WHERE code ='F11'));

