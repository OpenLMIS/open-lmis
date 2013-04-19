-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

Drop TABLE IF EXISTS master_rnr_columns;
CREATE TABLE master_rnr_columns (
    id SERIAL PRIMARY KEY,
    name varchar(200) NOT NULL UNIQUE,
    position integer  NOT NULL,
    source VARCHAR(1) NOT NULL,
    sourceConfigurable boolean NOT NULL,
    label varchar(200),
    formula varchar(200),
    indicator varchar(3) not null,
    used boolean NOT NULL,
    visible boolean NOT NULL,
    mandatory boolean NOT NULL,
    description varchar(250)
);