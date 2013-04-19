-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

delete from facility_operators;
INSERT INTO facility_operators
(code,      text,      displayOrder) VALUES
('MoH',     'MoH',     1),
('NGO',     'NGO',     2),
('FBO',     'FBO',     3),
('Private', 'Private', 4);
