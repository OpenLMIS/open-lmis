-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

Drop TABLE IF EXISTS program_regimen_columns;
CREATE TABLE program_regimen_columns (
    id SERIAL PRIMARY KEY,
    programId INTEGER NOT NULL REFERENCES programs(id),
    name varchar(100) NOT NULL,
    label varchar(100) NOT NULL,
    visible boolean NOT NULL,
    dataType varchar(50) NOT NULL,
    createdBy INTEGER,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modifiedBy INTEGER,
    modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX i_program_regimens_name ON program_regimen_columns(programId, name);


