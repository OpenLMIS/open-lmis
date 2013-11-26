--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

INSERT INTO roles (name) VALUES ('FieldCoordinator');

INSERT INTO role_rights (roleId, rightName) VALUES
((SELECT
    id
  FROM roles
  WHERE name = 'FieldCoordinator'), 'MANAGE_DISTRIBUTION');


INSERT INTO users
(userName, password, facilityId, firstName, lastName, email, verified, active, restrictLogin) VALUES
('FieldCoordinator', 'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',
 (SELECT
    id
  FROM facilities
  WHERE code = 'F10'), 'Field', 'Coordinator', 'fieldcoordinator@openlmis.com', TRUE, TRUE, FALSE);

INSERT INTO role_assignments (userId, roleId, deliveryZoneId, programId) VALUES
((SELECT
    id
  FROM USERS
  WHERE username = 'FieldCoordinator'),
 (SELECT
    id
  FROM ROLES
  WHERE name = 'FieldCoordinator'),
 (SELECT
    id
  FROM DELIVERY_ZONES
  WHERE code = 'Norte'),
 (SELECT
    id
  FROM programs
  WHERE name = 'VACCINES')),

((SELECT
    id
  FROM USERS
  WHERE username = 'FieldCoordinator'),
 (SELECT
    id
  FROM roles
  WHERE name = 'FieldCoordinator'),
 (SELECT
    id
  FROM delivery_zones
  WHERE code = 'Centro'),
 (SELECT
    id
  FROM programs
  WHERE name = 'VACCINES')),

((SELECT
    id
  FROM USERS
  WHERE username = 'FieldCoordinator'),
 (SELECT
    id
  FROM roles
  WHERE name = 'FieldCoordinator'),
 (SELECT
    id
  FROM delivery_zones
  WHERE code = 'Sul'),
 (SELECT
    id
  FROM programs
  WHERE name = 'VACCINES')),

((SELECT
    id
  FROM USERS
  WHERE username = 'superuser'),
 (SELECT
    id
  FROM roles
  WHERE name = 'FieldCoordinator'),
 (SELECT
    id
  FROM delivery_zones
  WHERE code = 'Norte'),
 (SELECT
    id
  FROM programs
  WHERE name = 'VACCINES')),

((SELECT
    id
  FROM USERS
  WHERE username = 'superuser'),
 (SELECT
    id
  FROM roles
  WHERE name = 'FieldCoordinator'),
 (SELECT
    id
  FROM delivery_zones
  WHERE code = 'Centro'),
 (SELECT
    id
  FROM programs
  WHERE name = 'VACCINES')),

((SELECT
    id
  FROM USERS
  WHERE username = 'superuser'),
 (SELECT
    id
  FROM roles
  WHERE name = 'FieldCoordinator'),
 (SELECT
    id
  FROM delivery_zones
  WHERE code = 'Sul'),
 (SELECT
    id
  FROM programs
  WHERE name = 'VACCINES')),

((SELECT
    id
  FROM USERS
  WHERE username = 'FieldCoordinator'),
 (SELECT
    id
  FROM roles
  WHERE name = 'FieldCoordinator'),
 (SELECT
    id
  FROM delivery_zones
  WHERE code = 'DZ1'),
 (SELECT
    id
  FROM programs
  WHERE name = 'VACCINES'));

