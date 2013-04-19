-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

delete from dosage_units;
INSERT INTO dosage_units
(code, displayOrder) VALUES
('mg',1),
('ml',2),
('each',3),
('cc',4),
('gm',5),
('mcg',6),
('IU',7);

