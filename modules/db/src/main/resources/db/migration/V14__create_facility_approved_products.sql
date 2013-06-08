-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

DROP TABLE IF EXISTS facility_approved_products;
CREATE TABLE facility_approved_products (
    id SERIAL PRIMARY KEY,
    facilityTypeId INTEGER REFERENCES facility_types(id) NOT NULL,
    programProductId INTEGER REFERENCES program_products(id) NOT NULL,
    maxMonthsOfStock INTEGER NOT NULL,
    createdBy INTEGER,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modifiedBy INTEGER,
    modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (facilityTypeId, programProductId)
);

CREATE INDEX i_facility_approved_product_programProductId_facilityTypeId ON facility_approved_products(programProductId, facilityTypeId);
