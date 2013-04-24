-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

CREATE TABLE programs (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(50),
    description VARCHAR(50),
    active BOOLEAN,
    templateConfigured BOOLEAN,
    lastModifiedBy INTEGER,
    lastModifiedDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    createdBy INTEGER,
    createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    budgetingApplies BOOLEAN,
    usesDar BOOLEAN
);
