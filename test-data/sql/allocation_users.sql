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

-- Create new roles for requisition and fulfillment based rights
-- Admin, reports and allocation based right roles already exist
INSERT INTO roles (name)
VALUES ('All Requisition')
  ,('All Fulfillment')
;

-- Add new rights for Admin role
INSERT INTO role_rights (roleid, rightname)
VALUES ((SELECT id FROM roles WHERE name = 'Admin'), 'MANAGE_SETTING')
  ,((SELECT id FROM roles WHERE name = 'Admin'), 'MANAGE_EQUIPMENT_SETTINGS')
  ,((SELECT id FROM roles WHERE name = 'Admin'), 'MANAGE_DEMOGRAPHIC_PARAMETERS')
;

-- Add new rights for report viewing role
INSERT INTO role_rights (roleid, rightname)
VALUES
  ((SELECT id FROM roles WHERE name = 'View-Report'), 'VIEW_USER_SUMMARY_REPORT')
;

-- Add new rights for reporting role
INSERT INTO role_rights (roleid, rightname)
  SELECT (SELECT id FROM roles WHERE name = 'Reporting'), name
  FROM rights r
  WHERE r.righttype = 'REPORTING'
        AND r.name <> 'MANAGE_REPORT';

-- Add all requisition rights for new requisition role
INSERT INTO role_rights (roleid, rightname)
  SELECT (SELECT id FROM roles WHERE name = 'All Requisition'), name
  FROM rights r
  WHERE r.righttype = 'REQUISITION';

-- Add all fulfillment rights for fulfillment role
INSERT INTO role_rights (roleid, rightname)
  SELECT (SELECT id FROM roles WHERE name = 'All Fulfillment'), name
  FROM rights r
  WHERE r.righttype = 'FULFILLMENT';

-- Create new admin user for developer testing
-- Set its home facility to something in the system
INSERT INTO users (userName, password, facilityId, firstName, lastName, email, verified, active, restrictLogin)
VALUES ('devadmin', 'TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie', (SELECT id FROM facilities WHERE code = 'F11'), 'Dev', 'Admin', 'devadmin@openlmis.com', TRUE, TRUE, FALSE)
;

-- Assign new dev admin user roles for all right categories
INSERT INTO role_assignments (userid, roleid, programid, deliveryzoneid)
VALUES ((SELECT id FROM users WHERE username = 'devadmin'), (SELECT id FROM roles WHERE name = 'Admin'), NULL, NULL)
  ,((SELECT id FROM users WHERE username = 'devadmin'), (SELECT id FROM roles WHERE name = 'View-Report'), NULL, NULL)
  ,((SELECT id FROM users WHERE username = 'devadmin'), (SELECT id FROM roles WHERE name = 'Reporting'), NULL, NULL)
  ,((SELECT id FROM users WHERE username = 'devadmin'), (SELECT id FROM roles WHERE name = 'All Requisition'), (SELECT id FROM programs WHERE code = 'ESS_MEDS'), NULL)
  ,((SELECT id FROM users WHERE username = 'devadmin'), (SELECT id FROM roles WHERE name = 'FieldCoordinator'), (SELECT id FROM programs WHERE code = 'VACCINES'), (SELECT id FROM delivery_zones WHERE code = 'Norte'))
;

INSERT INTO fulfillment_role_assignments (userid, roleid, facilityid)
VALUES ((SELECT id FROM users WHERE username = 'devadmin'), (SELECT id FROM roles WHERE name = 'All Fulfillment'), (SELECT id FROM facilities WHERE code = 'F11'))
;
