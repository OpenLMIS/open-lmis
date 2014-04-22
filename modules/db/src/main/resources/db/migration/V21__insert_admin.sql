--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

INSERT INTO roles (name, description) VALUES ('Admin', 'Admin');

INSERT INTO role_rights (roleId, rightName) VALUES ((SELECT
                                                       id
                                                     FROM roles
                                                     WHERE name = 'Admin'), 'UPLOADS'),
((SELECT
    id
  FROM roles
  WHERE name = 'Admin'), 'MANAGE_FACILITY'),
((SELECT
    id
  FROM roles
  WHERE name = 'Admin'), 'MANAGE_ROLE'),
((SELECT
    id
  FROM roles
  WHERE name = 'Admin'), 'MANAGE_PROGRAM_PRODUCT'),
((SELECT
    id
  FROM roles
  WHERE name = 'Admin'), 'MANAGE_SCHEDULE'),
((SELECT
    id
  FROM roles
  WHERE name = 'Admin'), 'CONFIGURE_RNR'),
((SELECT
    id
  FROM roles
  WHERE name = 'Admin'), 'MANAGE_USER'),
((SELECT
    id
  FROM roles
  WHERE name = 'Admin'), 'VIEW_REPORT'),
((SELECT
    id
  FROM roles
  WHERE name = 'Admin'), 'MANAGE_REPORT'),
((SELECT
    id
  FROM roles
  WHERE name = 'Admin'), 'SYSTEM_SETTINGS'),
((SELECT
    id
  FROM roles
  WHERE name = 'Admin'), 'MANAGE_REGIMEN_TEMPLATE');

INSERT INTO users (userName, password, facilityId, firstName, lastName, email, verified, active, restrictLogin) VALUES
('Admin123', 'TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie', null, 'John', 'Doe', 'John_Doe@openlmis.com', TRUE, TRUE, FALSE);

INSERT INTO role_assignments (userId, roleId) VALUES
((SELECT
    id
  FROM users
  WHERE userName = 'Admin123'), (SELECT
                                   id
                                 FROM roles
                                 WHERE name = 'Admin'));



