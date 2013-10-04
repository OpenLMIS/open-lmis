--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

DELETE FROM geographic_zones;

INSERT INTO geographic_zones
(code, name, levelId, parentId) values
('Root', 'Root', (SELECT id FROM geographic_levels WHERE code = 'country'), NULL);

INSERT INTO geographic_zones
(code, name, levelId, parentId) values
('Mozambique', 'Mozambique', (SELECT id FROM geographic_levels WHERE code = 'country'), NULL);


INSERT INTO geographic_zones
(code, name, levelId, parentId) values
('Arusha', 'Arusha',(SELECT id FROM geographic_levels WHERE code = 'state'), (SELECT id FROM geographic_zones WHERE code = 'Root'));


INSERT INTO geographic_zones
(code, name, levelId, parentId) values
('Dodoma', 'Dodoma',(SELECT id FROM geographic_levels WHERE code = 'province'), (SELECT id FROM geographic_zones WHERE code = 'Arusha'));

INSERT INTO geographic_zones
(code, name, levelId, parentId) values
('Ngorongoro', 'Ngorongoro', (SELECT id FROM geographic_levels WHERE code = 'district'), (SELECT id FROM geographic_zones WHERE code = 'Dodoma'));

INSERT INTO geographic_zones
(code, name, levelId, parentId) values
('Cabo Delgado Province', 'Cabo Delgado Province', (SELECT id FROM geographic_levels WHERE code = 'state'), (SELECT id FROM geographic_zones WHERE code = 'Mozambique')),
('Gaza Province', 'Gaza Province', (SELECT id FROM geographic_levels WHERE code = 'state'), (SELECT id FROM geographic_zones WHERE code = 'Mozambique')),
('Inhambane Province', 'Inhambane Province', (SELECT id FROM geographic_levels WHERE code = 'state'), (SELECT id FROM geographic_zones WHERE code = 'Mozambique'));

INSERT INTO geographic_zones
(code, name, levelId, parentId) values
('Norte', 'Norte', (SELECT id FROM geographic_levels WHERE code = 'province'), (SELECT id FROM geographic_zones WHERE code = 'Cabo Delgado Province'));

INSERT INTO geographic_zones
(code, name, levelId, parentId) values
('Centro', 'Centro', (SELECT id FROM geographic_levels WHERE code = 'province'), (SELECT id FROM geographic_zones WHERE code = 'Gaza Province'));

INSERT INTO geographic_zones
(code, name, levelId, parentId) values
('Sul', 'Sul', (SELECT id FROM geographic_levels WHERE code = 'province'), (SELECT id FROM geographic_zones WHERE code = 'Inhambane Province'));

INSERT INTO geographic_zones
(code, name, levelId, parentId) values
('District1', 'District1', (SELECT id FROM geographic_levels WHERE code = 'district'), (SELECT id FROM geographic_zones WHERE code = 'Norte')),
('District2', 'District2', (SELECT id FROM geographic_levels WHERE code = 'district'), (SELECT id FROM geographic_zones WHERE code = 'Norte')),
('District3', 'District3', (SELECT id FROM geographic_levels WHERE code = 'district'), (SELECT id FROM geographic_zones WHERE code = 'Norte')),
('District4', 'District4', (SELECT id FROM geographic_levels WHERE code = 'district'), (SELECT id FROM geographic_zones WHERE code = 'Centro')),
('District5', 'District5', (SELECT id FROM geographic_levels WHERE code = 'district'), (SELECT id FROM geographic_zones WHERE code = 'Centro')),
('District6', 'District6', (SELECT id FROM geographic_levels WHERE code = 'district'), (SELECT id FROM geographic_zones WHERE code = 'Centro')),
('District7', 'District7', (SELECT id FROM geographic_levels WHERE code = 'district'), (SELECT id FROM geographic_zones WHERE code = 'Sul')),
('District8', 'District8', (SELECT id FROM geographic_levels WHERE code = 'district'), (SELECT id FROM geographic_zones WHERE code = 'Sul')),
('District9', 'District9', (SELECT id FROM geographic_levels WHERE code = 'district'), (SELECT id FROM geographic_zones WHERE code = 'Sul'));



