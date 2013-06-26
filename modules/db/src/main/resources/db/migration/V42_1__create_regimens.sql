-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DROP TABLE IF EXISTS regimens;
CREATE TABLE regimens (
    id SERIAL PRIMARY KEY,
    programId INTEGER NOT NULl references programs(id),
    categoryId INTEGER NOT NULL references regimen_categories(id),
    code VARCHAR(50) NOT NULL,
    name VARCHAR(50) NOT NULL,
    active   BOOLEAN,
    displayOrder INTEGER NOT NULL,
    createdBy INTEGER,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modifiedBy INTEGER,
    modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX i_regimens_code_programId ON regimens(code, programId);







