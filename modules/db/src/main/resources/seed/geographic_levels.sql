-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

delete from geographic_levels;
INSERT INTO geographic_levels
(code, name, levelNumber) VALUES
('country', 'Country', 1),
('state', 'State', 2),
('district', 'District', 3),
('city', 'City', 4);
