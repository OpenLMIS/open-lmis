-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DROP TABLE IF EXISTS program_products;
CREATE TABLE program_products (
    id SERIAL PRIMARY KEY,
    programId INTEGER REFERENCES programs(id) NOT NULL,
    productId INTEGER REFERENCES products(id) NOT NULL,
    dosesPerMonth INTEGER NOT NULL,
    active BOOLEAN NOT NULL,
    currentPrice NUMERIC(20,2) DEFAULT 0,
    modifiedBy INTEGER,
    modifiedDate TIMESTAMP,
    createdBy INTEGER,
    createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    UNIQUE (productId, programId)
);
