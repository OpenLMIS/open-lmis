-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DROP TABLE IF EXISTS program_product_price_history;
CREATE TABLE program_product_price_history (
    id SERIAL PRIMARY KEY,
    programProductId INTEGER REFERENCES program_products(id) NOT NULL,
    price NUMERIC(20,2) DEFAULT 0,
    pricePerDosage NUMERIC(20,2) DEFAULT 0,
    source VARCHAR(50),
    startDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    endDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modifiedBy INTEGER,
    modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    createdBy INTEGER,
    createdDate TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE INDEX uc_program_product_price_history_programProductId ON program_product_price_history(programProductId);
