-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

Drop TABLE IF EXISTS program_rnr_columns;
CREATE TABLE program_rnr_columns(
    id SERIAL PRIMARY KEY ,
    masterColumnId INTEGER NOT NULL REFERENCES master_rnr_columns(id),
    programId INTEGER NOT NULL,
    label VARCHAR(200) NOT NULL,
    visible BOOLEAN NOT NULL,
    position int NOT NULL,
    source VARCHAR(1),
    formulaValidationRequired BOOLEAN,
    createdBy INTEGER,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modifiedBy INTEGER,
    modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (programId, masterColumnId)
);

CREATE INDEX program_id_index ON program_rnr_columns(programId);