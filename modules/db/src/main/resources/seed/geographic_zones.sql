-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

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
('Dodoma', 'Dodoma',(SELECT id FROM geographic_levels WHERE code = 'district'), (SELECT id FROM geographic_zones WHERE code = 'Arusha'));

INSERT INTO geographic_zones
(code, name, levelId, parentId) values
('Ngorongoro', 'Ngorongoro', (SELECT id FROM geographic_levels WHERE code = 'city'), (SELECT id FROM geographic_zones WHERE code = 'Dodoma'));

INSERT INTO geographic_zones
(code, name, levelId, parentId) values
('Cabo Delgado Province', 'Cabo Delgado Province', (SELECT id FROM geographic_levels WHERE code = 'state'), (SELECT id FROM geographic_zones WHERE code = 'Mozambique')),
('Gaza Province', 'Gaza Province', (SELECT id FROM geographic_levels WHERE code = 'state'), (SELECT id FROM geographic_zones WHERE code = 'Mozambique')),
('Inhambane Province', 'Inhambane Province', (SELECT id FROM geographic_levels WHERE code = 'state'), (SELECT id FROM geographic_zones WHERE code = 'Mozambique'));

INSERT INTO geographic_zones
(code, name, levelId, parentId) values
('Norte', 'Norte', (SELECT id FROM geographic_levels WHERE code = 'district'), (SELECT id FROM geographic_zones WHERE code = 'Cabo Delgado Province'));

INSERT INTO geographic_zones
(code, name, levelId, parentId) values
('Centro', 'Centro', (SELECT id FROM geographic_levels WHERE code = 'district'), (SELECT id FROM geographic_zones WHERE code = 'Gaza Province'));

INSERT INTO geographic_zones
(code, name, levelId, parentId) values
('Sul', 'Sul', (SELECT id FROM geographic_levels WHERE code = 'district'), (SELECT id FROM geographic_zones WHERE code = 'Inhambane Province'));

INSERT INTO geographic_zones
(code, name, levelId, parentId) values
('District1', 'District1', (SELECT id FROM geographic_levels WHERE code = 'city'), (SELECT id FROM geographic_zones WHERE code = 'Norte')),
('District2', 'District2', (SELECT id FROM geographic_levels WHERE code = 'city'), (SELECT id FROM geographic_zones WHERE code = 'Norte')),
('District3', 'District3', (SELECT id FROM geographic_levels WHERE code = 'city'), (SELECT id FROM geographic_zones WHERE code = 'Norte')),
('District4', 'District4', (SELECT id FROM geographic_levels WHERE code = 'city'), (SELECT id FROM geographic_zones WHERE code = 'Centro')),
('District5', 'District5', (SELECT id FROM geographic_levels WHERE code = 'city'), (SELECT id FROM geographic_zones WHERE code = 'Centro')),
('District6', 'District6', (SELECT id FROM geographic_levels WHERE code = 'city'), (SELECT id FROM geographic_zones WHERE code = 'Centro')),
('District7', 'District7', (SELECT id FROM geographic_levels WHERE code = 'city'), (SELECT id FROM geographic_zones WHERE code = 'Sul')),
('District8', 'District8', (SELECT id FROM geographic_levels WHERE code = 'city'), (SELECT id FROM geographic_zones WHERE code = 'Sul')),
('District9', 'District9', (SELECT id FROM geographic_levels WHERE code = 'city'), (SELECT id FROM geographic_zones WHERE code = 'Sul'));



