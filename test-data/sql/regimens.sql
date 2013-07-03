-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DELETE FROM regimens;

INSERT INTO regimens (programid, categoryid, code, name, active, displayorder) VALUES
(2, 1, '001', 'REGIMEN1', TRUE, 1),
(2, 1, '002', 'REGIMEN2', TRUE, 2),
(2, 1, '003', 'REGIMEN3', TRUE, 3),
(2, 2, '004', 'REGIMEN4', TRUE, 4),
(2, 2, '005', 'REGIMEN5', TRUE, 5);