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
 (name, type, description) VALUES
 ('Admin','ADMIN','Admin');

 INSERT INTO role_rights
  (roleId, rightName) VALUES
  ((select id from roles where name = 'Admin'), 'UPLOADS'),
  ((select id from roles where name = 'Admin'), 'MANAGE_FACILITY'),
  ((select id from roles where name = 'Admin'), 'MANAGE_ROLE'),
  ((select id from roles where name = 'Admin'), 'MANAGE_PROGRAM_PRODUCT'),
  ((select id from roles where name = 'Admin'), 'MANAGE_SCHEDULE'),
  ((select id from roles where name = 'Admin'), 'CONFIGURE_RNR'),
  ((select id from roles where name = 'Admin'), 'MANAGE_USER'),
  ((select id from roles where name = 'Admin'), 'VIEW_REPORT'),
  ((select id from roles where name = 'Admin'), 'MANAGE_REPORT'),
  ((select id from roles where name = 'Admin'), 'CONFIGURE_EDI'),
  ((select id from roles where name = 'Admin'), 'MANAGE_REGIMEN_TEMPLATE');

INSERT INTO users
  (userName, password,vendorId, facilityId, firstName, lastName, email, verified, active) VALUES
  ('Admin123', 'TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie',(SELECT id from vendors where name='openLmis'), null, 'John', 'Doe', 'John_Doe@openlmis.com', true, true);

INSERT INTO role_assignments
  (userId, roleId) VALUES
  ((select id from users where userName='Admin123'), (select id from roles where name = 'Admin'));



