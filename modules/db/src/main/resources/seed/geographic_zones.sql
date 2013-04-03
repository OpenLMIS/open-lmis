-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

delete from geographic_zones;
INSERT INTO geographic_zones
(code, name, levelId, parent) values
('Root', 'Root', (SELECT id FROM geographic_levels WHERE code = 'country'), (SELECT id FROM geographic_levels WHERE code = 'country')),
('Arusha', 'Arusha',(SELECT id FROM geographic_levels WHERE code = 'state'), (SELECT id FROM geographic_levels WHERE code = 'country')),
('Dodoma', 'Dodoma', (SELECT id FROM geographic_levels WHERE code = 'district'), (SELECT id FROM geographic_levels WHERE code = 'state')),
('Ngorongoro', 'Ngorongoro', (SELECT id FROM geographic_levels WHERE code = 'city'), (SELECT id FROM geographic_levels WHERE code = 'district'));
