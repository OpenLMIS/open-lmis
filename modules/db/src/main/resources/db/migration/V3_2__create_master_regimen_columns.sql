-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

Drop TABLE IF EXISTS master_regimen_columns;
CREATE TABLE master_regimen_columns (
    name varchar(100) NOT NULL,
    label varchar(100) NOT NULL,
    visible boolean NOT NULL,
    dataType varchar(50) NOT NULL
);