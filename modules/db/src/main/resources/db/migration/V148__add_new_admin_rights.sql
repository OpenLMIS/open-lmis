--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

INSERT INTO rights (name, rightType, description, displayOrder, displayNameKey) VALUES
('VIEW_SYNCHRONIZED_DATA', 'ADMIN', 'Permission to view synchronized data', 28, 'right.view.synchronized.data'),
('EDIT_SYNCHRONIZED_DATA', 'ADMIN', 'Permission to edit synchronized data', 29, 'right.edit.synchronized.data');

INSERT INTO role_rights (roleId, rightName) VALUES
((SELECT id FROM roles WHERE name = 'Admin'), 'VIEW_SYNCHRONIZED_DATA'),
((SELECT id FROM roles WHERE name = 'Admin'), 'EDIT_SYNCHRONIZED_DATA');
