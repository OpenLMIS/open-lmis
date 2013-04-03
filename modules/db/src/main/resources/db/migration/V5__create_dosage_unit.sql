-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DROP TABLE IF EXISTS dosage_units;
CREATE TABLE dosage_units (
    id SERIAL PRIMARY KEY,
    code varchar(20),
    displayOrder INTEGER
);

